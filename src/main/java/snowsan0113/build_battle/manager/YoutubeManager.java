package snowsan0113.build_battle.manager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import snowsan0113.build_battle.BuildBattle;
import snowsan0113.build_battle.util.ChatUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class YoutubeManager {

    private static YoutubeManager instance;

    //plugin
    private final BuildBattle plugin;
    private final FileConfiguration config;

    //youtube
    private final GameManager game_manager;
    private final String api_url_video;
    private final String api_url_livestream;
    private final String api_url_channel;
    private String api_key;
    private int get_time;
    private String livechatid;
    private BukkitTask task = null;

    public YoutubeManager() {
        plugin = BuildBattle.getPlugin(BuildBattle.class);
        config = plugin.getConfig();
        try {
            game_manager = GameManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //youtube
        api_key = config.getString("youtube.api_key");
        get_time = config.getInt("youtube.get_time");
        api_url_channel = "https://www.googleapis.com/youtube/v3/channels?key=%s&part=snippet&id=%s"; //API_KEY、USERID
        api_url_livestream = "https://www.googleapis.com/youtube/v3/liveChat/messages?key=%s&part=id,snippet&liveChatId=%s"; //API_KEY、LIVE_CHAT_ID
        api_url_video = "https://www.googleapis.com/youtube/v3/videos?key=%s&part=liveStreamingDetails&id=%s"; //API_KEY、LIVE_CHAT_ID
    }

    public static YoutubeManager getInstance() {
        if (instance == null) {
            instance = new YoutubeManager();
        }
        return instance;
    }

    public synchronized void startYoutube(String liveid) throws IOException {
        if (task == null) {
            JsonObject json = getJson(String.format(api_url_video, api_key, liveid));
            livechatid = json.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonObject("liveStreamingDetails").get("activeLiveChatId").getAsString();

            task = new BukkitRunnable() {
                String before_publish = "";
                @Override
                public void run() {
                    try {
                        YoutubeChat youtube_chat = getChat();
                        if (youtube_chat != null) {
                            String after_publish = youtube_chat.getPublish();
                            String name = youtube_chat.getUserName();
                            String comment = youtube_chat.getComment();

                            if (!before_publish.equalsIgnoreCase(after_publish)) {
                                before_publish = after_publish;
                                Bukkit.broadcastMessage(ChatColor.RED + "[Youtube]" + ChatColor.RESET + "<" + name + ">:" + comment);

                                if (game_manager.getStatus() == GameManager.GameStatus.RUNNING) {
                                    if (game_manager.getBuild().name().equalsIgnoreCase(comment)) {
                                        ChatUtil.sendGlobalMessage("==============" + "\n" +
                                                "Youtubeの" + name + "さんが正解を回答しました! " + "\n" +
                                                "正解は" + game_manager.getBuild().name() + "でした" + "\n" +
                                                "次のゲームまでお待ちください。" + "\n" +
                                                "============");
                                        game_manager.nextGame();
                                    }
                                }
                            }
                        }
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }.runTaskTimer(plugin, 0L, 20L * get_time);
        }
    }

    public YoutubeChat getChat() throws IOException {
        //Youtubeチャットから取得する
        JsonObject chat_json = getJson(String.format(api_url_livestream, api_key, livechatid));
        JsonArray chat_item = chat_json.getAsJsonArray("items");
        if (!chat_item.isEmpty()) {
            int size = chat_item.isEmpty() ? 0 : chat_item.size() - 1;
            JsonElement chat_snippet = chat_item.get(size).getAsJsonObject().get("snippet").getAsJsonObject();
            if (chat_snippet != null) {
                String publish = chat_snippet.getAsJsonObject().get("publishedAt").getAsString();
                String userid = chat_snippet.getAsJsonObject().get("authorChannelId").getAsString();
                String comment = chat_snippet.getAsJsonObject().get("displayMessage").getAsString();

                //チャンネルから取得する
                JsonObject channel_json = getJson(String.format(api_url_channel, api_key, userid));
                JsonElement channel_item = channel_json.get("items").getAsJsonArray().get(0);
                JsonElement channel_snippet = channel_item.getAsJsonObject().get("snippet");
                String name = channel_snippet.getAsJsonObject().get("title").getAsString();

                return new YoutubeChat(userid, name, comment, publish);
            }
        }

        return null;
    }

    private JsonObject getJson(String link) throws IOException {
        URL url = new URL(link);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder content = new StringBuilder();

        String inputLine;
        while((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();
        return  (new Gson()).fromJson(content.toString(), JsonObject.class);
    }

    public static class YoutubeChat {
        private final String userid;
        private final String username;
        private final String comment;
        private final String publish;

        public YoutubeChat(String userid, String username, String comment, String publish) {
            this.userid = userid;
            this.username = username;
            this.comment = comment;
            this.publish = publish;
        }

        public String getUserName() {
            return username;
        }

        public String getComment() {
            return comment;
        }

        public String getPublish() {
            return publish;
        }

        public String getUserid() {
            return userid;
        }
    }

}


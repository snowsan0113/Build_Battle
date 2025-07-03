package snowsan0113.build_battle.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snowsan0113.build_battle.BuildBattle;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BuildManager {

    private static BuildManager buildManager;
    private final Gson gson;

    private BuildManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public static BuildManager getInstance() {
        if (buildManager == null) {
            buildManager = new BuildManager();
        }
        return buildManager;
    }

    @Nullable
    public Build getBuild(String name) throws IOException {
        JsonObject json = getRawJson();
        if (json.has(name)) {
            JsonObject build_json = json.getAsJsonObject(name);

            int difficulty = build_json.get("difficulty").getAsInt();
            String genre = build_json.get("genre").getAsString();
            String hint = build_json.get("hint").getAsString();
            Bukkit.broadcastMessage(new Build(name, difficulty, genre, hint).toString());
            return new Build(name, difficulty, genre, hint);
        }
        return null;
    }

    public List<Build> getBuildList() throws IOException {
        JsonObject json = getRawJson();
        List<Build> list = new ArrayList<>();
        for (String keys : json.keySet()) {
            Build build = getBuild(keys);
            list.add(build);
        }
        System.out.println(list.toString());
        return list;
    }

    @Deprecated
    public String getObjectValue(String key) throws IOException {
        JsonObject raw_json = getRawJson();
        String[] keys = key.split("\\."); // 「.」で区切る
        JsonObject now_json = raw_json; //jsonを代入する
        for (int n = 0; n < keys.length - 1; n++) { //keyの1個前未満をループする。（keyが2個だと、1回だけ実行）
            now_json = now_json.getAsJsonObject(keys[n]); // jsonを代入する
        }

        return now_json.get(keys[keys.length - 1]).getAsString(); //key数 - 1（最後のキー）を取得する
    }

    @Deprecated
    public JsonObject getRawJson() throws IOException {
        createJson();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(getFile().toPath()), StandardCharsets.UTF_8))) {
            return gson.fromJson(reader, JsonObject.class);
        }
    }

    private void writeFile(String date) {
        try (BufferedWriter write = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(getFile().toPath()), StandardCharsets.UTF_8))) {
            write.write(date);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createJson() throws IOException {
        if (!getFile().exists()) {
            BuildBattle.getPlugin(BuildBattle.class).getDataFolder().mkdir();
            getFile().createNewFile();
            writeFile("{}");
            Bukkit.getLogger().info("JSONファイルが正常に作成されました。");
        }
    }

    public File getFile() {
        return new File(BuildBattle.getPlugin(BuildBattle.class).getDataFolder(), "build_data.json");
    }

    /**
     * @param name       建築物の名前
     * @param difficulty 建築物の難しさ
     * @param genre      建築物のジャンル
     * @param hint       建築物のヒント
     */
    public record Build(String name, int difficulty, String genre, String hint) {
        public @NotNull String toString() {
            return String.format("Build{name=%s,difficulty=%d,genre=%s,hint=%s}", name, difficulty, genre, hint);
        }
    }

}


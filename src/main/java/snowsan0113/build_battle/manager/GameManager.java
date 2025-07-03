package snowsan0113.build_battle.manager;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.Nullable;
import snowsan0113.build_battle.BuildBattle;
import snowsan0113.build_battle.util.ChatUtil;
import snowsan0113.build_battle.util.ConfigPath;

import java.io.IOException;
import java.util.*;

public class GameManager {

    //インスタンス
    private static GameManager instance;
    private static BuildManager build_instance;
    private static Random random;

    //プラグイン
    private BuildBattle plugin;
    private FileConfiguration config;

    //ゲーム
    private BukkitTask task; //ゲームタスク
    private GameStatus status; //ゲームの状態
    private int build_time; //建築する残り時間
    private int count_time; //カウント時間
    private World world; //ゲームのワールド

    //建築物
    private BuildManager.Build now_build; //現在の建築物
    private final List<BuildManager.Build> build_list; //残りの建築物

    //建築プレイヤー
    private OfflinePlayer build_player; //建築しているプレイヤー
    private final List<OfflinePlayer> build_player_list; //建築予定のプレイヤー
    private final List<OfflinePlayer> build_end_player_list; //建築が終了したプレイヤー

    private GameManager() throws IOException {
        //インスタンス
        random = new Random();
        build_instance = BuildManager.getInstance();

        //プラグイン
        this.plugin = BuildBattle.getPlugin(BuildBattle.class);
        this.config = plugin.getConfig();

        //configからの設定
        String world_name = config.getString(ConfigPath.WORLD_NAME.getPath());
        this.world = Bukkit.getWorld(world_name);
        this.count_time = config.getInt(ConfigPath.COUNT_TIME.getPath());
        this.build_time = config.getInt(ConfigPath.BUILD_TIME.getPath());

        //ゲーム設定
        this.build_player_list = new ArrayList<>();
        this.build_end_player_list = new ArrayList<>();
        this.build_list = new ArrayList<>(build_instance.getBuildList());
        this.status = GameStatus.WAIITNG;
    }

    public int startGame() {
        if (this.task == null) {
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (status == GameStatus.WAIITNG || status == GameStatus.CONNTING) {
                        if (count_time == 0) {
                            ChatUtil.sendGlobalMessage("ゲーム開始!");
                            build_player_list.addAll(Bukkit.getOnlinePlayers());
                            try {
                                nextGame();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            status = GameStatus.RUNNING;
                            for (Player online : Bukkit.getOnlinePlayers()) {
                                try {
                                    ScoreboardManager.getInstance(online.getUniqueId()).setScoreboard();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                        else {
                            String format = String.format("ゲーム開始まであと%d秒", count_time);
                            ChatUtil.sendGlobalMessage(format);
                            count_time--;
                        }
                    }
                    else if (status == GameStatus.RUNNING) {

                        for (Player online : Bukkit.getOnlinePlayers()) {
                            if (build_time <= 300) {
                                online.spigot().sendMessage(ChatMessageType.ACTION_BAR,  new TextComponent("難しさ：" + now_build.difficulty()));
                                if (build_time == 300) ChatUtil.sendGlobalMessage("=== ヒント!! ===" + "\n" +
                                        "難しさは" + ChatColor.YELLOW + now_build.difficulty());
                            }
                            if (build_time <= 200) {
                                online.spigot().sendMessage(ChatMessageType.ACTION_BAR,  new TextComponent("難しさ：" + now_build.difficulty() + ",ジャンル: " + now_build.genre()));
                                if (build_time == 200) ChatUtil.sendGlobalMessage("=== ヒント!! ===" + "\n" +
                                        "ジャンルは" + ChatColor.YELLOW + now_build.genre());
                            }
                            if (build_time <= 100) {
                                online.spigot().sendMessage(ChatMessageType.ACTION_BAR,  new TextComponent("難しさ：" + now_build.difficulty() + ",ジャンル: " + now_build.genre() + ",ヒント：" + now_build.hint()));
                                if (build_time == 100) ChatUtil.sendGlobalMessage("=== ヒント!! ===" + "\n" +
                                        "ヒントは" + ChatColor.YELLOW + now_build.hint());
                            }
                        }

                        if (build_time <= 0) {
                            if (build_time == 0) ChatUtil.sendGlobalMessage("==============" + "\n" +
                                    "時間切れです!　正解は" + now_build.name() + "でした。" + "\n" +
                                    "次のゲームまでしばらくお待ちください" + "\n" +
                                    "==============");
                            try {
                                nextGame();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else {
                            build_time--;
                        }
                    }
                }
            }.runTaskTimer(BuildBattle.getPlugin(BuildBattle.class), 0L, 20L);

            return 0; //正常開始。
        }
        else {
            return 1; //既に開始済み。
        }
    }

    public void resetGame(boolean board_reset) throws IOException {
        status = GameStatus.WAIITNG;
        for (Player online : Bukkit.getOnlinePlayers()) {
            ScoreboardManager board = ScoreboardManager.getInstance(online.getUniqueId());
            if (board_reset) board.setScoreboard();
        }
        task.cancel();
    }

    public void nextGame() throws IOException {
        if (build_player_list.isEmpty()) {
            Location lobby_loc = LocationManager.getLocation(world, ConfigPath.LOBBY);

            for (Player online : Bukkit.getOnlinePlayers()) {
                ScoreboardManager board = ScoreboardManager.getInstance(online.getUniqueId());
                board.setScoreboard();
                online.setGameMode(GameMode.ADVENTURE);
                online.teleport(lobby_loc);
            }
            ChatUtil.sendGlobalMessage("ゲーム終了!");
            resetGame(false);
        }
        else {
            build_time = config.getInt(ConfigPath.BUILD_TIME.getPath());

            //建築してた人の処理
            if (build_player != null) {
                Location lobby_loc = LocationManager.getLocation(world, ConfigPath.LOBBY);
                if (build_player.isOnline()) build_player.getPlayer().teleport(lobby_loc);
                build_end_player_list.add(build_player);
            }

            //エリアを空気にする
            Location arena_start_loc = LocationManager.getLocation(world, ConfigPath.ARENA_START);
            Location arena_start_end = LocationManager.getLocation(world, ConfigPath.ARENA_END);
            LocationManager.fill(Bukkit.createBlockData(Material.AIR), arena_start_loc, arena_start_end);

            //建築する人を選ぶ
            Collections.shuffle(build_player_list);
            build_player = build_player_list.get(0);
            build_player_list.remove(build_player);

            //建築する人をテレポート
            Location build_spawn_loc = LocationManager.getLocation(world, ConfigPath.BUILD_SPAWN);
            build_player.getPlayer().teleport(build_spawn_loc);

            //建築物を選ぶ
            Collections.shuffle(build_list);
            now_build = build_list.get(0);
            build_list.remove(now_build);


            //ルールを設定
            HashMap<RuleManager.GameRule, Object> rule_map = new HashMap<>();
            rule_map.put(RuleManager.GameRule.CAN_PLACE_BLOCK_SIZE, random.nextInt(200));
            List<Material> block_list = new ArrayList<>(Arrays.stream(Material.values()).filter(Material::isBlock).toList());
            Collections.shuffle(block_list);
            rule_map.put(RuleManager.GameRule.CAN_NOT_PLACE_BLOCK_TYPE, block_list.get(0).createBlockData());
            setRule(rule_map);

            RuleManager rule = RuleManager.getInstance();
            TranslatableComponent block = new TranslatableComponent(((BlockData) rule.getRule(RuleManager.GameRule.CAN_NOT_PLACE_BLOCK_TYPE)).getMaterial().translationKey());

            TextComponent text = new TextComponent("==============" + "\n" +
                    "あなたが建築する人に選ばれました。 次のものを建築してください。\n" +
                    "・" + now_build.name() + "\n" +
                    "また、以下のルールがあります。破ると失格です。" + "\n" +
                    "・設置可能ブロック数: " + (Integer) rule.getRule(RuleManager.GameRule.CAN_PLACE_BLOCK_SIZE) + "\n" +
                    "・設置不可能ブロック: ");
            text.addExtra(block);
            Bukkit.spigot().broadcast(text);

        }
    }

    public void setRule(Map<RuleManager.GameRule, Object> rule_map) {
        RuleManager rule_manager = RuleManager.getInstance();
        for (Map.Entry<RuleManager.GameRule, Object> entry : rule_map.entrySet()) {
            RuleManager.GameRule rule = entry.getKey();
            Object value = entry.getValue();
            rule_manager.setRule(rule, value);
        }
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        if (status != GameStatus.RUNNING) {
            this.world = world;
        }
        else {
            throw new IllegalStateException("ゲーム実行中はワールドを変更することはできません。");
        }
    }

    public int getTime() {
        return build_time;
    }

    public void setTime(int build_time) {
        this.build_time = build_time;
    }

    public BuildManager.Build getBuild() {
        return now_build;
    }

    public OfflinePlayer getBuildPlayer() {
        return build_player;
    }

    public GameStatus getStatus() {
        return status;
    }

    public List<BuildManager.Build> getBuildList() {
        return build_list;
    }

    /**
     * ゲームのプレイヤーリストを取得するもの。
     * @param end_player trueにすることで終了したプレイヤー、falseで待機中のプレイヤー。
     * @return 変更不可能なプレイヤーリストを返す。
     */
    public List<OfflinePlayer> getBuildPlayerList(boolean end_player) {
        if (end_player) {
            return Collections.unmodifiableList(build_end_player_list);
        }
        else {
            return build_player_list;
        }
    }

    public static GameManager getInstance() throws IOException {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public enum GameStatus {
        WAIITNG(0, "待機中"),
        CONNTING(1, "カウント中"),
        RUNNING(2, "実行中"),
        ENDING(3, "終了");

        private final int status;
        private final String string_status;

        GameStatus(int status, String string_status) {
            this.status = status;
            this.string_status = string_status;
        }

        public int getStatus() {
            return status;
        }

        public String getStringStatus() {
            return string_status;
        }
    }
}

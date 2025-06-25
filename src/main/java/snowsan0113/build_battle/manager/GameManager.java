package snowsan0113.build_battle.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import snowsan0113.build_battle.BuildBattle;
import snowsan0113.build_battle.util.ChatUtil;

import java.io.IOException;
import java.util.*;

public class GameManager {

    //インスタンス
    private static GameManager instance;

    //ゲーム
    private BukkitTask task; //ゲームタスク
    private GameStatus status; //ゲームの状態
    private int MAX_BUILD_TIME;
    private int build_time; //建築する残り時間
    private int count_time; //カウント時間

    //ボスバー
    private double MAX_BOSSBAR;
    private BossBar bossBar;

    //建築物
    private BuildManager.Build now_build; //現在の建築物
    private final List<BuildManager.Build> build_list; //残りの建築物

    //建築プレイヤー
    private OfflinePlayer build_player; //建築しているプレイヤー
    private List<OfflinePlayer> build_player_list; //建築予定のプレイヤー

    private GameManager() throws IOException {
        this.count_time = 10;
        this.MAX_BUILD_TIME = 60*5;
        this.build_time = 60*5;
        this.build_player_list = new ArrayList<>();
        this.build_list = new ArrayList<>(BuildManager.getInstance().getBuildList());
        this.status = GameStatus.WAIITNG;
        this.bossBar = Bukkit.createBossBar("残り時間", BarColor.BLUE, BarStyle.SEGMENTED_6);
        this.MAX_BOSSBAR = bossBar.getProgress();
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
                            nextGame();
                            status = GameStatus.RUNNING;
                        }
                        else {
                            String format = String.format("ゲーム開始まであと%d秒", count_time);
                            ChatUtil.sendGlobalMessage(format);
                            count_time--;
                        }
                    }
                    else if (status == GameStatus.RUNNING) {

                        for (Player online : Bukkit.getOnlinePlayers()) {
                            if (!bossBar.getPlayers().contains(online)) {
                                bossBar.addPlayer(online);
                            }
                        }
                        bossBar.setProgress(MAX_BOSSBAR * ((double) build_time / MAX_BUILD_TIME));
                        bossBar.setTitle("残り時間：" + build_time);

                        if (build_time == 300) {
                            ChatUtil.sendGlobalMessage("=== ヒント!! ===" + "\n" +
                                    "難しさは" + ChatColor.YELLOW + now_build.difficulty());
                        }
                        if (build_time == 200) {
                            ChatUtil.sendGlobalMessage("=== ヒント!! ===" + "\n" +
                                    "ジャンルは" + ChatColor.YELLOW + now_build.genre());
                        }
                        if (build_time == 100) {
                            ChatUtil.sendGlobalMessage("=== ヒント!! ===" + "\n" +
                                    "ヒントは" + ChatColor.YELLOW + now_build.hint());
                        }

                        if (build_time == 0) {
                            nextGame();
                        }

                        build_time--;
                    }
                }
            }.runTaskTimer(BuildBattle.getPlugin(BuildBattle.class), 0L, 20L);

            return 0; //正常開始。
        }
        else {
            return 1; //既に開始済み。
        }
    }

    public void resetGame() {

    }

    public void nextGame() {
        //建築する人を選ぶ
        Collections.shuffle(build_player_list);
        build_player = build_player_list.get(0);
        build_player_list.remove(build_player);

        //建築物を選ぶ
        Collections.shuffle(build_list);
        now_build = build_list.get(0);
        build_list.remove(now_build);

        ChatUtil.sendMessage(build_player.getPlayer(), "==============" + "\n" +
                "あなたが建築する人に選ばれました。 次のものを建築してください。\n" +
                "・" + now_build.name() + "\n" +
                "==============");

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

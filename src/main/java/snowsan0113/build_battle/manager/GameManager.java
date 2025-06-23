package snowsan0113.build_battle.manager;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import snowsan0113.build_battle.BuildBattle;
import snowsan0113.build_battle.util.ChatUtil;

import java.util.HashSet;
import java.util.Set;

public class GameManager {

    //インスタンス
    private static GameManager instance;

    //ゲーム
    private BukkitTask task; //ゲームタスク
    private GameStatus status; //ゲームの状態
    private int build_time; //建築する残り時間
    private int count_time; //カウント時間

    //建築プレイヤー
    private OfflinePlayer build_player; //建築しているプレイヤー
    private Set<OfflinePlayer> build_player_list; //建築予定のプレイヤー

    private GameManager() {
        this.build_time = 60*5;
        this.build_player_list = new HashSet<>();
        this.status = GameStatus.WAIITNG;
    }

    public int startGame() {
        if (this.task == null) {
            this.task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (status == GameStatus.WAIITNG || status == GameStatus.CONNTING) {
                        if (count_time <= 0) {
                            ChatUtil.sendGlobalMessage("ゲーム開始!");
                            status = GameStatus.RUNNING;
                        }
                        else {
                            String format = String.format("ゲーム開始まであと%d秒", count_time);
                            ChatUtil.sendGlobalMessage(format);
                            count_time--;
                        }
                    }
                    else if (status == GameStatus.RUNNING) {

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

    public int getTime() {
        return build_time;
    }

    public void setTime(int build_time) {
        this.build_time = build_time;
    }

    public static GameManager getInstance() {
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

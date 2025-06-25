package snowsan0113.build_battle.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import snowsan0113.build_battle.BuildBattle;

import java.io.IOException;
import java.util.Set;

public class ScoreboardManager {

    private static final Scoreboard new_scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    private static Objective game_obj = new_scoreboard.getObjective("BuildBattle");
    private static BukkitRunnable board_runnable;
    private static final GameManager manager;

    static {
        try {
            manager = GameManager.getInstance();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Objective getObjective() {
        if (game_obj != null) {
            createObjective();
        }

        return game_obj;
    }

    public static Scoreboard getScoreboard() {
        if (game_obj == null) {
            createObjective();
        }

        return game_obj.getScoreboard();
    }

    private static Objective createObjective() {
        if (game_obj == null) {
            game_obj = new_scoreboard.registerNewObjective("BuildBattle", "dummy");
            game_obj.setDisplayName("BuildBattle");

            Bukkit.getLogger().info("[BuildBattle-ScoreboardManager] スコアボードを作成しました。");
        }

        return game_obj;
    }

    public static void setScoreboard(GameManager.GameStatus status) {
        if (game_obj != null) {
            resetScore();
            getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);

            if (status == GameManager.GameStatus.WAIITNG) {
                //ゲーム開始前
                game_obj.getScore("===============").setScore(30);
                game_obj.getScore(" ").setScore(29);
                game_obj.getScore( ChatColor.GOLD + "ゲーム開始待機中...").setScore(28);
                game_obj.getScore("  ").setScore(27);
                // game_obj.getScore("現在の人数：" + ).setScore(26);
                game_obj.getScore("   ").setScore(25);
                game_obj.getScore("============").setScore(24);
            }
            else if (status == GameManager.GameStatus.RUNNING) {
                //ゲーム中
                game_obj.getScore("===============").setScore(30);
                game_obj.getScore(" ").setScore(29);
                // game_obj.getScore("残り時間： " + ).setScore(28);
                game_obj.getScore("  ").setScore(27);
                game_obj.getScore( ChatColor.GOLD + "建築中のプレイヤー：").setScore(26);
                //game_obj.getScore("").setScore(25);
                game_obj.getScore("   ").setScore(22);
                game_obj.getScore("============").setScore(21);
            }
            else if (status == GameManager.GameStatus.ENDING) {
                //ゲーム終了後
                game_obj.getScore("===============").setScore(30);
                game_obj.getScore(" ").setScore(29);
                game_obj.getScore( ChatColor.GOLD + "ゲーム終了!!").setScore(28);
                game_obj.getScore("   ").setScore(22);
                game_obj.getScore("============").setScore(21);
            }

            updateScoreboard();
            Bukkit.getLogger().info("[BuildBattle-ScoreboardManager] スコアボードを設定しました。（type=" + status.name() + ")");
        }
        else {
            createObjective();
            setScoreboard(status);
        }
    }

    private static void updateScoreboard() {
        if (board_runnable == null) {
            new BukkitRunnable() {
                String time = null;
                String build_player_name = null;
                String now_player_size = null;
                @Override
                public void run() {
                    if (manager.getStatus() == GameManager.GameStatus.RUNNING) {
                        Player build_player = manager.getBuildPlayer().getPlayer();

                        //残り時間
                        if (time != null) {
                            game_obj.getScoreboard().resetScores(time);
                        }
                        time = (ChatColor.GOLD + "残り時間： " + stringTime(manager.getTime()));
                        game_obj.getScore(time).setScore(28);

                        if (build_player != null) {
                            //建築中のプレイヤー
                            if (build_player_name != null) {
                                game_obj.getScoreboard().resetScores(build_player_name);
                            }
                            build_player_name = (build_player.getName());
                            game_obj.getScore(build_player_name).setScore(25);
                        }

                    }
                    else if (manager.getStatus() == GameManager.GameStatus.WAIITNG || manager.getStatus() == GameManager.GameStatus.CONNTING) {
                        //現在のプレイヤー
                        if (now_player_size != null) {
                            game_obj.getScoreboard().resetScores(now_player_size);
                        }
                        now_player_size = (ChatColor.GOLD + "現在の人数：" + Bukkit.getOnlinePlayers().size());
                        game_obj.getScore(now_player_size).setScore(26);
                    }

                    //スコアボードセット
                    Bukkit.getOnlinePlayers().forEach(player -> player.setScoreboard(ScoreboardManager.getScoreboard()));

                    board_runnable=this;
                }
            }.runTaskTimer(BuildBattle.getPlugin(BuildBattle.class), 0L, 2L);
        }
    }

    private static String stringTime(int time) {
        int hour = time / 3600;
        int min = time / 60;
        int sec = time % 60;

        return min + "分" + sec + "秒";
    }

    public static void resetScore() {
        Set<String> scores = getScoreboard().getEntries();

        if (scores != null) {
            for (String score : scores) {
                getScoreboard().resetScores(score);
            }
        }
    }

}

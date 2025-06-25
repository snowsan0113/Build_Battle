package snowsan0113.build_battle.manager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;
import snowsan0113.build_battle.BuildBattle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ScoreboardManager {

    private static final Map<UUID, ScoreboardManager> board_map = new HashMap<>();

    //スコアボード
    private GameManager manager;
    private final OfflinePlayer player;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private BukkitTask task;

    private ScoreboardManager(UUID uuid) throws IOException {
        this.manager = GameManager.getInstance();
        this.player = Bukkit.getOfflinePlayer(uuid);
        Scoreboard new_scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = new_scoreboard.registerNewObjective("BuildBattle", "dummy", "BuildBattle");
        this.scoreboard = this.objective.getScoreboard();

        board_map.put(uuid, this);
    }

    public static ScoreboardManager getInstance(UUID uuid) throws IOException {
        if (!board_map.containsKey(uuid)) {
            board_map.put(uuid, new ScoreboardManager(uuid));
        }

        return board_map.get(uuid);
    }

    public void setScoreboard() {
        Objective player_obj = getObjective();
        resetScore();
        player_obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        if (manager.getStatus() == GameManager.GameStatus.WAIITNG || manager.getStatus() == GameManager.GameStatus.CONNTING) {
            //ゲーム開始前
            player_obj.getScore("===============").setScore(30);
            player_obj.getScore(" ").setScore(29);
            player_obj.getScore( ChatColor.GOLD + "ゲーム開始待機中...").setScore(28);
            player_obj.getScore("  ").setScore(27);
            // game_obj.getScore("現在の人数：" + ).setScore(26);
            player_obj.getScore("   ").setScore(25);
            player_obj.getScore("============").setScore(24);
        }
        else if (manager.getStatus() == GameManager.GameStatus.RUNNING) {
            //ゲーム中
            player_obj.getScore("===============").setScore(30);
            player_obj.getScore(" ").setScore(29);
            // game_obj.getScore("残り時間： " + ).setScore(28);
            player_obj.getScore("  ").setScore(27);
            player_obj.getScore( ChatColor.GOLD + "建築中のプレイヤー：").setScore(26);
            //game_obj.getScore("").setScore(25);
            // player_obj.getScore("現在のポイント：").setScore(24);
            player_obj.getScore("   ").setScore(22);
            player_obj.getScore("============").setScore(21);
        }
        else if (manager.getStatus() == GameManager.GameStatus.ENDING) {
            //ゲーム終了後
            player_obj.getScore("===============").setScore(30);
            player_obj.getScore(" ").setScore(29);
            player_obj.getScore( ChatColor.GOLD + "ゲーム終了!!").setScore(28);
            player_obj.getScore("   ").setScore(22);
            player_obj.getScore("============").setScore(21);
        }

        updateScoreboard();
        Bukkit.getLogger().info("[BuildBattle-ScoreboardManager] スコアボードを設定しました。（type=" + manager.getStatus().name() + ")");
    }

    private void updateScoreboard() {
        if (task == null) {
            this.task = new BukkitRunnable() {
                String time = null;
                String build_player_name = null;
                String now_player_size = null;
                String point;
                @Override
                public void run() {
                    UUID uuid = player.getUniqueId();
                    if (manager.getStatus() == GameManager.GameStatus.RUNNING) {
                        Player build_player = manager.getBuildPlayer().getPlayer();

                        //残り時間
                        if (time != null) {
                            scoreboard.resetScores(time);
                        }
                        time = (ChatColor.GOLD + "残り時間： " + (manager.getTime() / 60) + "分" + (manager.getTime() % 60) + "秒");
                        objective.getScore(time).setScore(28);

                        //ポイント
                        if (point != null) {
                            scoreboard.resetScores(point);
                        }
                        point = (ChatColor.GOLD + "現在のポイント： " + "未実装");
                        objective.getScore(point).setScore(24);


                        if (build_player != null) {
                            //建築中のプレイヤー
                            if (build_player_name != null) {
                                scoreboard.resetScores(build_player_name);
                            }
                            build_player_name = (build_player.getName());
                            objective.getScore(build_player_name).setScore(25);
                        }
                    }
                    else if (manager.getStatus() == GameManager.GameStatus.WAIITNG || manager.getStatus() == GameManager.GameStatus.CONNTING) {
                        //現在のプレイヤー
                        if (now_player_size != null) {
                            scoreboard.resetScores(now_player_size);
                        }
                        now_player_size = (ChatColor.GOLD + "現在の人数：" + Bukkit.getOnlinePlayers().size());
                        objective.getScore(now_player_size).setScore(26);
                    }

                    //スコアボードセット
                    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                    player.getPlayer().setScoreboard(scoreboard);

                }
            }.runTaskTimer(BuildBattle.getPlugin(BuildBattle.class), 0L, 2L);
        }
    }

    public void resetScore() {
        Set<String> scores = getScoreboard().getEntries();
        for (String score : scores) {
            getScoreboard().resetScores(score);
        }
    }

    public Objective getObjective() {
        return objective;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public OfflinePlayer getOfflinePlayer() {
        return player;
    }

    public Player getPlayer() {
        return player.getPlayer();
    }

    public BukkitTask getTask() {
        return task;
    }

    public GameManager getManager() {
        return manager;
    }

}
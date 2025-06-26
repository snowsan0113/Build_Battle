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
import java.util.*;

public class ScoreboardManager {

    private static final Map<UUID, ScoreboardManager> board_map = new HashMap<>();

    //スコアボード
    private GameManager manager;
    private final OfflinePlayer player;
    private final Scoreboard scoreboard;
    private final Objective objective;
    private BukkitTask task;
    private Map<Integer, Score> score_map = new HashMap<>();

    private ScoreboardManager(UUID uuid) throws IOException {
        this.manager = GameManager.getInstance();
        this.player = Bukkit.getOfflinePlayer(uuid);
        Scoreboard new_scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = new_scoreboard.registerNewObjective("BuildBattle", "dummy", "BuildBattle");
        this.scoreboard = this.objective.getScoreboard();
        this.score_map = new HashMap<>();

        board_map.put(uuid, this);
    }

    public static ScoreboardManager getInstance(UUID uuid) throws IOException {
        if (!board_map.containsKey(uuid)) {
            board_map.put(uuid, new ScoreboardManager(uuid));
        }

        return board_map.get(uuid);
    }

    public Score getScore(int score) {
        if (!score_map.containsKey(score)) {
            score_map.put(score, new Score(getObjective(), null, score));
        }

        return score_map.get(score);
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
                @Override
                public void run() {
                    if (manager.getStatus() == GameManager.GameStatus.RUNNING) {
                        Player build_player = manager.getBuildPlayer().getPlayer();

                        getScore(28).updateScore(ChatColor.GOLD + "残り時間： " + (manager.getTime() / 60) + "分" + (manager.getTime() % 60) + "秒");
                        getScore(24).updateScore(ChatColor.GOLD + "現在のポイント： " + "未実装");
                        if (build_player != null) getScore(25).updateScore(build_player.getName());
                    }
                    else if (manager.getStatus() == GameManager.GameStatus.WAIITNG || manager.getStatus() == GameManager.GameStatus.CONNTING) {
                        getScore(26).updateScore(ChatColor.GOLD + "現在の人数：" + Bukkit.getOnlinePlayers().size());
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

    public static class Score {
        private String name = null;
        private final int score;
        private final Objective objective;

        public Score(Objective objective, String name, int score) {
            this.name = name;
            this.score = score;
            this.objective = objective;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getScore() {
            return score;
        }

        public Objective getObjective() {
            return objective;
        }

        public void updateScore(String name) {
            if (this.name != null) {
                objective.getScore(this.name).resetScore();
            }
            this.name = name;
            objective.getScore(name).setScore(score);
        }
    }

}
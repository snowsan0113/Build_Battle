package snowsan0113.build_battle;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import snowsan0113.build_battle.commands.BuildCommand;
import snowsan0113.build_battle.commands.GameStartCommand;
import snowsan0113.build_battle.manager.GameManager;
import snowsan0113.build_battle.manager.ScoreboardManager;

import java.io.IOException;

public class BuildBattle extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("buildbattle_start").setExecutor(new GameStartCommand());
        getCommand("buildbattle_build").setExecutor(new BuildCommand());

        for (Player online : Bukkit.getOnlinePlayers()) {
            try {
                ScoreboardManager.getInstance(online.getUniqueId()).setScoreboard();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        saveDefaultConfig();
        getLogger().info("プラグインが有効になりました。");
    }

    @Override
    public void onDisable() {

    }
}

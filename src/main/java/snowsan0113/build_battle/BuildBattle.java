package snowsan0113.build_battle;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import snowsan0113.build_battle.commands.BuildCommand;
import snowsan0113.build_battle.commands.GameStartCommand;
import snowsan0113.build_battle.listener.PlayerChatListener;
import snowsan0113.build_battle.listener.PlayerJoinLeaveListener;
import snowsan0113.build_battle.manager.GameManager;
import snowsan0113.build_battle.manager.ScoreboardManager;

import java.io.IOException;

public class BuildBattle extends JavaPlugin {

    @Override
    public void onEnable() {
        PluginManager plm = getServer().getPluginManager();

        getCommand("buildbattle_start").setExecutor(new GameStartCommand());
        getCommand("buildbattle_build").setExecutor(new BuildCommand());

        plm.registerEvents(new PlayerChatListener(), this);
        plm.registerEvents(new PlayerJoinLeaveListener(), this);

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

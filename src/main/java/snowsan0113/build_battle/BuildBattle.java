package snowsan0113.build_battle;

import org.bukkit.plugin.java.JavaPlugin;
import snowsan0113.build_battle.commands.BuildCommand;
import snowsan0113.build_battle.commands.GameStartCommand;

public class BuildBattle extends JavaPlugin {

    @Override
    public void onEnable() {
        getCommand("buildbattle_start").setExecutor(new GameStartCommand());
        getCommand("buildbattle_build").setExecutor(new BuildCommand());

        getLogger().info("プラグインが有効になりました。");
    }

    @Override
    public void onDisable() {

    }
}

package snowsan0113.build_battle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import snowsan0113.build_battle.BuildBattle;
import snowsan0113.build_battle.manager.GameManager;

import java.io.IOException;

public class GameStartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender send, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("buildbattle_start")) {
            try {
                GameManager.getInstance().startGame();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

}

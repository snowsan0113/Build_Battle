package snowsan0113.build_battle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class GameStartCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender send, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("buildbattle_start")) {

        }
        return false;
    }

}

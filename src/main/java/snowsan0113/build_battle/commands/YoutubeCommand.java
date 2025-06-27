package snowsan0113.build_battle.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import snowsan0113.build_battle.manager.YoutubeManager;
import snowsan0113.build_battle.util.ChatUtil;

import java.io.IOException;

public class YoutubeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender send, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {
        if (cmd.getName().equalsIgnoreCase("buildbattle_youtube")) {
            if (args[0].equalsIgnoreCase("start_comment")) {
                ChatUtil.sendMessage(send, "取得しています...");
                try {
                    YoutubeManager.getInstance().startYoutube(args[1]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }

}

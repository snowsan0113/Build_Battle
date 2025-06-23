package snowsan0113.build_battle.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {

    private static String TEXT_INFO = ChatColor.AQUA + "[BuildBattle] " + ChatColor.RESET;
    private static String TEXT_ERROR = ChatColor.RED + "[エラー] " + ChatColor.RESET;

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(TEXT_INFO + message);
    }

    public static void sendGlobalMessage(String message) {
        Bukkit.broadcastMessage(TEXT_INFO + message);
    }
}

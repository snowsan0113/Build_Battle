package snowsan0113.build_battle.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import snowsan0113.build_battle.BuildBattle;
import snowsan0113.build_battle.manager.BuildManager;
import snowsan0113.build_battle.util.ChatUtil;

import java.io.IOException;
import java.util.List;

public class BuildCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (cmd.getName().equalsIgnoreCase("buildbattle_build")) {
            if (args[0].equalsIgnoreCase("info")) {
                BuildManager buildManager = BuildManager.getInstance();

                ChatUtil.sendMessage(sender, "しばらくお待ちください。取得しています...");

                Bukkit.getScheduler().runTaskLater(BuildBattle.getPlugin(BuildBattle.class), () -> {
                    StringBuilder string_builder = new StringBuilder();
                    string_builder.append("===現在登録されている建築物=== \n");
                    try {
                        List<BuildManager.Build> build_list = buildManager.getBuildList();

                        if (build_list.isEmpty()) {
                            string_builder.append("--登録されいる建築物は見つかりませんでした。-- \n");
                        }
                        else {
                            for (BuildManager.Build build : build_list) {
                                string_builder.append("名前： " + build.name() + "\n");
                                string_builder.append("難易度： " + build.difficulty() + "\n");
                                string_builder.append("ジャンル： " + build.genre() + "\n");
                                string_builder.append("ヒント： " + build.hint() + "\n");
                            }
                        }
                    } catch (IOException e) {
                        ChatUtil.sendMessage(sender, "取得中にエラーが発生しました：" + e.getMessage());
                        throw new RuntimeException(e);
                    }
                    string_builder.append("============");

                    ChatUtil.sendMessage(sender, string_builder.toString());
                }, 20L);
            }
        }
        return false;
    }

}

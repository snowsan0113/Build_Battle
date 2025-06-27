package snowsan0113.build_battle.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;
import snowsan0113.build_battle.manager.BuildManager;
import snowsan0113.build_battle.manager.GameManager;
import snowsan0113.build_battle.util.ChatUtil;

import java.io.IOException;

public class PlayerChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) throws IOException {
        Player player = event.getPlayer();
        String message = event.getMessage();
        GameManager manager = GameManager.getInstance();
        BuildManager.Build now_build = manager.getBuild();
        if (manager.getStatus() == GameManager.GameStatus.RUNNING) {
            ChatUtil.sendGlobalMessage("==========" + "\n" +
                    player.getName() + "さん！正解です！" + "\n" +
                    "正解は" + now_build.name() + "でした！" + "\n" +
                    "次のゲームまでお待ちください。" + "\n" +
                    "==================");
            manager.nextGame();
        }
    }
}

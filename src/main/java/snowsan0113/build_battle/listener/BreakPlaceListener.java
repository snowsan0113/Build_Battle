package snowsan0113.build_battle.listener;

import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import snowsan0113.build_battle.manager.GameManager;
import snowsan0113.build_battle.manager.RuleManager;
import snowsan0113.build_battle.util.ChatUtil;

import java.io.IOException;

public class BreakPlaceListener implements Listener {

    private static int now_block_size;

    @EventHandler
    public void onPlace(BlockPlaceEvent event) throws IOException {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        GameManager game = GameManager.getInstance();
        RuleManager rule = RuleManager.getInstance();

        if (game.getStatus() == GameManager.GameStatus.RUNNING) {
            if (player.getUniqueId().equals(game.getBuildPlayer().getUniqueId())) {
                Integer place_max_size = (Integer) rule.getRule(RuleManager.GameRule.CAN_PLACE_BLOCK_SIZE);
                BlockData can_not_place_block = (BlockData) rule.getRule(RuleManager.GameRule.CAN_NOT_PLACE_BLOCK_TYPE);

                if (can_not_place_block.matches(block.getBlockData())) {
                    ChatUtil.sendGlobalMessage("=========" + "\n" +
                            "設置不可能ブロック数を設置したため終了です!" + "\n" +
                            "次のゲームまでお待ちください。 " + "\n" +
                            "===========");
                    game.nextGame();
                }

                if (now_block_size < place_max_size) {
                    now_block_size++;
                }
                else {
                    ChatUtil.sendGlobalMessage("=========" + "\n" +
                            "設置可能ブロック数を超えてしまったので終了です!" + "\n" +
                            "次のゲームまでお待ちください。 " + "\n" +
                            "===========");
                    game.nextGame();
                }
            }
        }
    }
}

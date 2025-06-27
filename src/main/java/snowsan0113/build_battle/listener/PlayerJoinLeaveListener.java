package snowsan0113.build_battle.listener;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.jetbrains.annotations.NotNull;
import snowsan0113.build_battle.manager.GameManager;
import snowsan0113.build_battle.manager.ScoreboardManager;
import snowsan0113.build_battle.util.ChatUtil;

import java.io.IOException;
import java.util.List;

public class PlayerJoinLeaveListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) throws IOException {
        Player player = event.getPlayer();
        GameManager manager = GameManager.getInstance();

        List<OfflinePlayer> wait_list = manager.getBuildPlayerList(false);
        List<OfflinePlayer> end_list = manager.getBuildPlayerList(true);

        if (!end_list.contains(player) && !wait_list.contains(player)) {
            ScoreboardManager.getInstance(player.getUniqueId()).getObjective().setDisplaySlot(DisplaySlot.SIDEBAR);
            //ゲームの状態問わず、参加したことないプレイヤーに対する、ルール通知
            ChatUtil.sendMessage(player, "====ルール====" + "\n" +
                    "----建築する側----" + "\n" +
                    "１．指定された建築物を、建築してください。" + "\n" +
                    "２．放置せず真面目に、建築してください。（※調べて放置は全然OK）" + "\n" +
                    "----建築をしているものを当てる側----" + "\n" +
                    "１．建築されているものを、当ててください。" + "\n" +
                    "２．基本、連投して当てないでください。" + "\n" +
                    "----共通ルール----" + "\n" +
                    "１．サーバーのルールに従ってください。" + "\n" +
                    "===========");
        }

        if (manager.getStatus() == GameManager.GameStatus.RUNNING) {
            if (!end_list.contains(player) && !wait_list.contains(player)) {
                //終了済み＆待機中のリストに含まれてない（＝参加したことない）
                wait_list.add(player);
            }
            else if (end_list.contains(player) && !wait_list.contains(player)) {
                //終了済みに含まれているが、待機中に含まれていない（＝途中抜けの場合）
                ChatUtil.sendMessage(player, "============" + "\n" +
                        "あなたは途中抜けしたため、参加することができません。" + "\n" +
                        "============");
            }
        }
        else {

        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event ) throws IOException {
        Player player = event.getPlayer();
        GameManager manager = GameManager.getInstance();

        List<OfflinePlayer> wait_list = manager.getBuildPlayerList(false);
        List<OfflinePlayer> end_list = manager.getBuildPlayerList(true);

        if (manager.getStatus() == GameManager.GameStatus.RUNNING) {
            if (player.getName().equals(manager.getBuildPlayer().getName())) {
                //抜けたプレイヤーが、建築しているプレイヤーだった場合
                ChatUtil.sendGlobalMessage("==========" + "\n" +
                        "プレイヤーが途中抜けしたため、終了です!" + "\n" +
                        "正解は" + manager.getBuild().name() + "でした。" + "\n" +
                        "次のゲームまでお待ちください。" + "\n" +
                        "==========");
                manager.nextGame();
            }
            else {
                if (wait_list.contains(player) && !end_list.contains(player)) {
                    //待機中のプレイヤーが抜けた場合
                    wait_list.remove(player);
                }
            }
        }
    }
}

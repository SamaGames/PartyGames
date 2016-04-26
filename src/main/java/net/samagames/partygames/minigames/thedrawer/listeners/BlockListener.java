package net.samagames.partygames.minigames.thedrawer.listeners;

import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.thedrawer.TheDrawer;
import net.samagames.partygames.minigames.thedrawer.TheDrawerRoom;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class BlockListener implements Listener {

    TheDrawer miniGame;

    public BlockListener(TheDrawer miniGame) {
        this.miniGame = miniGame;
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent e) {
        if(miniGame.getRooms().keySet().contains(miniGame.getGame().getPlayer(e.getPlayer().getUniqueId()))
                && !miniGame.getTask().isAnimationRunning()) {
            PartyGamesPlayer player = miniGame.getGame().getPlayer(e.getPlayer().getUniqueId());
            TheDrawerRoom room = miniGame.getRooms().get(player);

            if(!room.getDrawingBlocks().contains(e.getBlock())) {
                e.setCancelled(true);
                return;
            }

            if(room.blankSpaceInDrawing()) {
                Runnable runnableGiveItem = () -> {
                    ItemStack is = e.getItemInHand();
                    is.setAmount(1);
                    e.getPlayer().getInventory().addItem(is);
                };
                Bukkit.getScheduler().scheduleSyncDelayedTask(miniGame.getGame().getPlugin(), runnableGiveItem);
            } else {
                room.setActive(false);
                if(room.isDrawingIdenticalToTemplate()) {
                    e.getPlayer().sendMessage(ChatColor.GREEN + "\nVotre dessin correspond au modèle !");
                    e.getPlayer().sendMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Vous avez gagné ce mini-jeu.");

                    int pts = miniGame.getTimer().getTime() > 10 ? 100 :  miniGame.getTimer().getTime() * 10;
                    player.givePoints(pts);
                    player.getPlayerIfOnline().sendMessage(ChatColor.GOLD + "+ " + pts + " points");
                } else {
                    e.getPlayer().sendMessage(ChatColor.RED + "\nVotre dessin ne correspond pas au modèle !");
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent e) {
        if(miniGame.getRooms().keySet().contains(miniGame.getGame().getPlayer(e.getPlayer().getUniqueId()))
                && !miniGame.getTask().isAnimationRunning())
            e.setCancelled(true);
    }
}

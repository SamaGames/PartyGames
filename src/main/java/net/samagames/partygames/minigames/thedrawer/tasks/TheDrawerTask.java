package net.samagames.partygames.minigames.thedrawer.tasks;

import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.thedrawer.TheDrawer;
import net.samagames.partygames.minigames.thedrawer.TheDrawerRoom;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class TheDrawerTask extends BukkitRunnable {

    private TheDrawer miniGame;

    private boolean animationRunning = true;

    public TheDrawerTask(TheDrawer miniGame) {
        this.miniGame = miniGame;
    }

    @Override
    public void run() {
        if(!isThereActiveRooms()) {
            miniGame.endGame();
            return;
        }

        if(miniGame.getTimer() != null &&
                miniGame.getTimer().getTime() <= 0) {
            miniGame.endGame();
            return;
        }

        if(!animationRunning)
            return;

        for (PartyGamesPlayer player : miniGame.getRooms().keySet()) {
            TheDrawerRoom room = miniGame.getRooms().get(player);

            if (room.isActive()) {
                handleRoom(room, player);
            }
        }
    }

    private boolean isThereActiveRooms() {
        for(TheDrawerRoom room : miniGame.getRooms().values()) {
            if(room.isActive())
                return true;
        }
        return false;
    }

    private void handleRoom(TheDrawerRoom room, PartyGamesPlayer player) {
        List<Block> blocks = room.getTemplateBlocks();

        Block b = blocks.get(miniGame.getGame().getRandom().nextInt(blocks.size()));

        if (!b.getType().equals(Material.WOOL))
            b.setType(Material.WOOL);

        DyeColor color = DyeColor.WHITE;
        while(color.equals(DyeColor.WHITE)) {
            color = TheDrawer.getDyeColors()[miniGame.getGame().getRandom().nextInt(TheDrawer.getDyeColors().length)];
        }

        BlockState bs = b.getState();
        bs.setData(new Wool(color));
        bs.update();

        player.getPlayerIfOnline().playSound(player.getPlayerIfOnline().getLocation(), Sound.BLOCK_DISPENSER_FAIL, 1F, 1.5F);
    }

    public void setAnimationRunning(boolean animationRunning) {
        this.animationRunning = animationRunning;
    }

    public boolean isAnimationRunning() {
        return animationRunning;
    }
}

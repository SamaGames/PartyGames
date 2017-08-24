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

/*
 * This file is part of PartyGames.
 *
 * PartyGames is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PartyGames is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PartyGames.  If not, see <http://www.gnu.org/licenses/>.
 */
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

package net.samagames.partygames.minigames.blockdodger.tasks;

import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.blockdodger.BlockDodger;
import net.samagames.partygames.minigames.blockdodger.BlockDodgerRoom;
import net.samagames.tools.Area;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

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
public class BlockDodgerTask extends BukkitRunnable {

    private BlockDodger miniGame;

    public BlockDodgerTask(BlockDodger miniGame) {
        this.miniGame = miniGame;
    }

    @Override
    public void run() {
        if(!isThereActiveRooms()) {
            miniGame.endGame();
            return;
        }

        if(miniGame.getTimer().getTime() <= 0) {
            miniGame.endGame();
            return;
        }

        for(PartyGamesPlayer player : miniGame.getRooms().keySet()) {
            BlockDodgerRoom room = miniGame.getRooms().get(player);

            if(room.isActive()) {
                handleRoom(room, player);
            }
        }
    }

    private boolean isThereActiveRooms() {
        for(BlockDodgerRoom room : miniGame.getRooms().values()) {
            if(room.isActive())
                return true;
        }
        return false;
    }

    private void handleRoom(BlockDodgerRoom room, PartyGamesPlayer player) {
        for (Block oldBlock : room.getMovingBlocks().toArray(new Block[room.getMovingBlocks().size()])) {
            Block newBlock = getNewBlock(room, oldBlock);
            Area area = new Area(room.getBlockPos1(), room.getBlockPos2());
            if (area.isInArea(newBlock.getLocation())) {
                newBlock.setType(Material.STEP);
                room.getMovingBlocks().add(newBlock);
            }

            if (newBlock.getLocation().equals(player.getPlayerIfOnline().getLocation().getBlock().getLocation())) {
                int points = miniGame.getTimer().getInitialTime() - miniGame.getTimer().getTime();
                points /= 0.3; // Make points amount with 100 as maximal instead of 30

                player.givePoints(points);
                room.setActive(false);
                room.clearBlocks();

                Bukkit.broadcastMessage(ChatColor.RED + player.getPlayerData().getEffectiveName() +
                        " a été heurté par un bloc.");
                player.getPlayerIfOnline().sendMessage(ChatColor.GOLD + "+ " + points + " points");
                return;
            }

            oldBlock.setType(Material.AIR);
            room.getMovingBlocks().remove(oldBlock);
        }

        int y = room.getBlockPos1().getBlockY();
        int x;
        int z;
        if (room.getColumnAxis().equals(BlockDodgerRoom.ColumnAxis.X_AXIS)) {
            x = room.getBlockPos1().getBlockZ();
            if (room.getBlockPos1().getBlockZ() < room.getBlockPos2().getBlockZ()) {
                z = ThreadLocalRandom.current().nextInt(room.getBlockPos1().getBlockZ(),
                        room.getBlockPos2().getBlockZ() + 1);
            } else {
                z = ThreadLocalRandom.current().nextInt(room.getBlockPos2().getBlockZ(),
                        room.getBlockPos1().getBlockZ() + 1);
            }
        } else {
            z = room.getBlockPos1().getBlockZ();
            if (room.getBlockPos1().getBlockX() < room.getBlockPos2().getBlockX()) {
                x = ThreadLocalRandom.current().nextInt(room.getBlockPos1().getBlockX(),
                        room.getBlockPos2().getBlockX() + 1);
            } else {
                x = ThreadLocalRandom.current().nextInt(room.getBlockPos2().getBlockX(),
                        room.getBlockPos1().getBlockX() + 1);
            }
        }

        Block block = new Location(room.getBlockPos1().getWorld(), x, y, z).getBlock();
        block.setType(Material.STEP);
        room.getMovingBlocks().add(block);
    }

    private Block getNewBlock(BlockDodgerRoom room, Block oldBlock){
        Block newBlock;
        if (room.getColumnAxis().equals(BlockDodgerRoom.ColumnAxis.X_AXIS)) {
            if (room.getBlockPos1().getBlockX() < room.getBlockPos2().getBlockX()) {
                newBlock = oldBlock.getRelative(1, 0, 0);
            } else {
                newBlock = oldBlock.getRelative(-1, 0, 0);
            }
        } else {
            if (room.getBlockPos1().getBlockZ() < room.getBlockPos2().getBlockZ()) {
                newBlock = oldBlock.getRelative(0, 0, 1);
            } else {
                newBlock = oldBlock.getRelative(0, 0, -1);
            }
        }
        return newBlock;
    }
}

package net.samagames.partygames.minigames.blockdodger.tasks;

import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.blockdodger.BlockDodger;
import net.samagames.partygames.minigames.blockdodger.BlockDodgerRoom;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class BlockDodgerTask extends BukkitRunnable {

    private BlockDodger miniGame;

    public BlockDodgerTask(BlockDodger miniGame) {
        this.miniGame = miniGame;
    }

    @Override
    public void run() {
        if(miniGame.getTimer().getTime() <= 0) {
            cancel();
            return;
        }

        for(PartyGamesPlayer player : miniGame.getRooms().keySet()) {
            BlockDodgerRoom room = miniGame.getRooms().get(player);

            for (Block oldBlock : room.getMovingBlocks().toArray(new Block[room.getMovingBlocks().size()])) {
                Block newBlock;

                if(room.getColumnAxis().equals(BlockDodgerRoom.ColumnAxis.X_AXIS)) {
                    if(room.getBlockPos1().getBlockX() < room.getBlockPos2().getBlockX()) {
                        newBlock = oldBlock.getRelative(1, 0, 0);
                    } else {
                        newBlock = oldBlock.getRelative(-1, 0, 0);
                    }
                } else {
                    if(room.getBlockPos1().getBlockZ() < room.getBlockPos2().getBlockZ()) {
                        newBlock = oldBlock.getRelative(0, 0, 1);
                    } else {
                        newBlock = oldBlock.getRelative(0, 0, -1);
                    }
                }

                boolean out = false;
                boolean hurted = false;
                // TODO : Detect block going out and hurting player

                newBlock.setType(Material.STEP);
                room.getMovingBlocks().add(newBlock);

                oldBlock.setType(Material.AIR);
                room.getMovingBlocks().remove(oldBlock);
            }

            int y = room.getBlockPos1().getBlockY();
            int x, z;
            if(room.getColumnAxis().equals(BlockDodgerRoom.ColumnAxis.X_AXIS)) {
                x = room.getBlockPos1().getBlockZ();
                if(room.getBlockPos1().getBlockZ() < room.getBlockPos2().getBlockZ()) {
                    z = ThreadLocalRandom.current().nextInt(room.getBlockPos1().getBlockZ(),
                            room.getBlockPos2().getBlockZ());
                } else {
                    z = ThreadLocalRandom.current().nextInt(room.getBlockPos2().getBlockZ(),
                            room.getBlockPos1().getBlockZ());
                }
            } else {
                z = room.getBlockPos1().getBlockZ();
                if(room.getBlockPos1().getBlockX() < room.getBlockPos2().getBlockX()) {
                    x = ThreadLocalRandom.current().nextInt(room.getBlockPos1().getBlockX(),
                            room.getBlockPos2().getBlockX());
                } else {
                    x = ThreadLocalRandom.current().nextInt(room.getBlockPos2().getBlockX(),
                            room.getBlockPos1().getBlockX());
                }
            }

            Block block = new Location(room.getBlockPos1().getWorld(), x, y, z).getBlock();
            block.setType(Material.STEP);
            room.getMovingBlocks().add(block);
        }
    }
}

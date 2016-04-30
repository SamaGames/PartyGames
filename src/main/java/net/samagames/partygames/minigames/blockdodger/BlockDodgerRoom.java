package net.samagames.partygames.minigames.blockdodger;

import com.google.gson.JsonObject;
import net.samagames.tools.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockDodgerRoom {


    private Location spawn;
    private Location blockPos1;
    private Location blockPos2;
    private ColumnAxis columnAxis;
    private List<Block> movingBlocks;

    private boolean active;

    public BlockDodgerRoom(JsonObject json) {
        spawn = LocationUtils.str2loc(json.get("spawn").getAsString());
        blockPos1 = LocationUtils.str2loc(json.getAsJsonObject("blocks").get("pos1").getAsString());
        blockPos2 = LocationUtils.str2loc(json.getAsJsonObject("blocks").get("pos2").getAsString());

        if("X".equalsIgnoreCase(json.get("columnAxis").getAsString())) {
            columnAxis = ColumnAxis.X_AXIS;
        } else {
            columnAxis = ColumnAxis.Z_AXIS;
        }

        movingBlocks = new ArrayList<>();

        active = true;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getBlockPos1() {
        return blockPos1;
    }

    public Location getBlockPos2() {
        return blockPos2;
    }

    public ColumnAxis getColumnAxis() {
        return columnAxis;
    }

    /**
     * Clears all the moving blocks in the room
     */
    public void clearBlocks() {
        int y = blockPos1.getBlockY();

        int x = blockPos1.getBlockX();
        while(x != blockPos2.getBlockX()) {
            int z = blockPos1.getBlockZ();
            while(z != blockPos2.getBlockZ()) {
                new Location(blockPos1.getWorld(), x, y, z).getBlock().setType(Material.AIR);

                if(z < blockPos2.getBlockZ()) {
                    z++;
                } else {
                    z--;
                }
            }
            new Location(blockPos1.getWorld(), x, y, z).getBlock().setType(Material.AIR);

            if(x < blockPos2.getBlockX()) {
                x++;
            } else {
                x--;
            }
        }

        int z = blockPos1.getBlockZ();
        while(z != blockPos2.getBlockZ()) {
            new Location(blockPos1.getWorld(), x, y, z).getBlock().setType(Material.AIR);

            if(z < blockPos2.getBlockZ()) {
                z++;
            } else {
                z--;
            }
        }
        new Location(blockPos1.getWorld(), x, y, z).getBlock().setType(Material.AIR);
    }

    public List<Block> getMovingBlocks() {
        return movingBlocks;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public enum ColumnAxis {
        X_AXIS,
        Z_AXIS
    }
}

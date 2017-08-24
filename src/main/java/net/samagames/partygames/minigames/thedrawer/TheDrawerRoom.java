package net.samagames.partygames.minigames.thedrawer;

import com.google.gson.JsonObject;
import net.samagames.tools.LocationUtils;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.material.Wool;

import java.util.ArrayList;
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
public class TheDrawerRoom {

    private Location spawn;
    private Location templatePos1;
    private Location templatePos2;
    private Location drawingPos1;
    private Location drawingPos2;
    private List<Block> templateBlocks;
    private List<Block> drawingBlocks;

    private boolean active;

    public TheDrawerRoom(JsonObject json) {
        spawn = LocationUtils.str2loc(json.get("spawn").getAsString());
        templatePos1 = LocationUtils.str2loc(json.getAsJsonObject("template").get("pos1").getAsString());
        templatePos2 = LocationUtils.str2loc(json.getAsJsonObject("template").get("pos2").getAsString());
        drawingPos1 = LocationUtils.str2loc(json.getAsJsonObject("drawing").get("pos1").getAsString());
        drawingPos2 = LocationUtils.str2loc(json.getAsJsonObject("drawing").get("pos2").getAsString());

        templateBlocks = new ArrayList<>();

        int xModifier = templatePos1.getBlockX() < templatePos2.getBlockX() ? 1 : -1;
        int yModifier = templatePos1.getBlockY() < templatePos2.getBlockY() ? 1 : -1;
        int zModifier = templatePos1.getBlockZ() < templatePos2.getBlockZ() ? 1 : -1;

        for(int x = templatePos1.getBlockX();
            x * xModifier <= templatePos2.getBlockX() * xModifier;
            x += xModifier) {
            for(int y = templatePos1.getBlockY();
                y * yModifier <= templatePos2.getBlockY() * yModifier;
                y += yModifier) {
                for(int z = templatePos1.getBlockZ();
                    z * zModifier <= templatePos2.getBlockZ() * zModifier;
                    z += zModifier) {
                    templateBlocks.add(templatePos1.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        drawingBlocks = new ArrayList<>();

        xModifier = drawingPos1.getBlockX() < drawingPos2.getBlockX() ? 1 : -1;
        yModifier = drawingPos1.getBlockY() < drawingPos2.getBlockY() ? 1 : -1;
        zModifier = drawingPos1.getBlockZ() < drawingPos2.getBlockZ() ? 1 : -1;

        for(int x = drawingPos1.getBlockX();
            x * xModifier <= drawingPos2.getBlockX() * xModifier;
            x += xModifier) {
            for(int y = drawingPos1.getBlockY();
                y * yModifier <= drawingPos2.getBlockY() * yModifier;
                y += yModifier) {
                for(int z = drawingPos1.getBlockZ();
                    z * zModifier <= drawingPos2.getBlockZ() * zModifier;
                    z += zModifier) {
                    drawingBlocks.add(drawingPos1.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        active = true;
    }

    public Location getSpawn() {
        return spawn;
    }

    public Location getTemplatePos1() {
        return templatePos1;
    }

    public Location getTemplatePos2() {
        return templatePos2;
    }

    public Location getDrawingPos1() {
        return drawingPos1;
    }

    public Location getDrawingPos2() {
        return drawingPos2;
    }

    public void clearBlocks() {
        templateBlocks.forEach((Block b) -> {
            BlockState s = b.getState();
            s.setData(new Wool(DyeColor.WHITE));
            s.update();
        });

        drawingBlocks.forEach((Block b) -> b.setType(Material.AIR));
    }

    public List<Block> getTemplateBlocks() {
        return templateBlocks;
    }

    public List<Block> getDrawingBlocks() {
        return drawingBlocks;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Checks if there is black space (air blocks) in the drawing area.
     * @return True if there is at least one air block, else otherwise.
     */
    public boolean blankSpaceInDrawing() {
        for(Block b : drawingBlocks) {
            if(b.getType().equals(Material.AIR))
                return true;
        }
        return false;
    }

    /**
     * Checks if the drawing is identical to the template.
     * @return True if the drawing is identical to the template,
     * else if it isn't or if there is at least one block which is not a wool in the drawing.
     */
    public boolean isDrawingIdenticalToTemplate() {
        for(int i = 0; i < templateBlocks.size(); i++) {
            Block templateBlock = templateBlocks.get(i);
            Block drawingBlock = drawingBlocks.get(i);

            if(templateBlock.getType().equals(Material.WOOL) &&
                    drawingBlock.getType().equals(Material.WOOL)) {
                Wool templateWool = (Wool) templateBlock.getState().getData();
                Wool drawingWool = (Wool) drawingBlock.getState().getData();

                if(!templateWool.getColor().equals(drawingWool.getColor())) {
                    return false;
                }
            } else {
                return false;
            }
        }

        return true;
    }
}

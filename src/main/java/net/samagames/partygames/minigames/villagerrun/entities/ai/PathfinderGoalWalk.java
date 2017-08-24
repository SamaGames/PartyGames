package net.samagames.partygames.minigames.villagerrun.entities.ai;

import net.minecraft.server.v1_9_R1.PathfinderGoal;
import net.samagames.partygames.minigames.villagerrun.entities.NPC;
import org.bukkit.Location;

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
public class PathfinderGoalWalk extends PathfinderGoal {
    /**
     * NPC's destination
     */
    private Location objective;
    /**
     * Le famous NPC
     */
    private NPC npc;

    public PathfinderGoalWalk(NPC npc, Location objective){
        this.npc = npc;
        this.objective = objective;
    }

    /**
     * Method shouldExecute()
     * @return Always true so the NPC will always go to its destination
     */
    @Override
    public boolean a() {
        return true;
    }

    /**
     * Method startExecuting(), we make the NPC go to his destination
     */
    @Override
    public void c(){
        this.npc.getNavigation().a(objective.getBlockX(), objective.getBlockY(), objective.getBlockZ(), 0.6);
    }

    /**
     * Nothing should stop this NPC
     * @return false
     */
    @Override
    public boolean b() {
        return false;
    }

    /**
     * When we update his path, we lock his Y and Z motion so he can follow only an one-axis path
     */
    @Override
    public void e(){
        npc.addLife(1);
        npc.motZ = 0;
        npc.motY = 0;
    }


}

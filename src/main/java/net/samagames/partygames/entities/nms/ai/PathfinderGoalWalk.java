package net.samagames.partygames.entities.nms.ai;

import net.samagames.partygames.entities.nms.NPC;
import net.minecraft.server.v1_9_R1.PathfinderGoal;
import org.bukkit.Location;


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
        this.npc.getNavigation().a(objective.getBlockX()+0.5, objective.getBlockY(), objective.getBlockZ()+0.5, 0.5f );
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

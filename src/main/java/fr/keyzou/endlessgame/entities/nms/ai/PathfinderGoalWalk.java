package fr.keyzou.endlessgame.entities.nms.ai;

import fr.keyzou.endlessgame.entities.nms.PNJ;
import net.minecraft.server.v1_9_R1.PathfinderGoal;
import org.bukkit.Location;


public class PathfinderGoalWalk extends PathfinderGoal {
    /**
     * PNJ's destination
     */
    private Location objective;
    /**
     * Le famous PNJ
     */
    private PNJ pnj;

    public PathfinderGoalWalk(PNJ pnj, Location objective){
        this.pnj = pnj;
        this.objective = objective;
    }

    /**
     * Method shouldExecute()
     * @return Always true so the PNJ will always go to its destination
     */
    @Override
    public boolean a() {
        return true;
    }

    /**
     * Method startExecuting(), we make the PNJ go to his destination
     */
    @Override
    public void c(){
        this.pnj.getNavigation().a(objective.getBlockX()+0.5, objective.getBlockY(), objective.getBlockZ()+0.5, 0.5f );
    }

    /**
     * Nothing should stop this PNJ
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
        pnj.addLife(1);
        pnj.motZ = 0;
        pnj.motY = 0;
    }


}

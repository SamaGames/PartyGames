package net.samagames.partygames.minigames.villagerrun.tasks;

import net.samagames.partygames.minigames.villagerrun.VillagerRun;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;


public class SpawnTask extends BukkitRunnable {

    private VillagerRun game;

    private Random random;

    public SpawnTask(VillagerRun game){
        this.game = game;
        random = new Random();
    }

    @Override
    public void run() {
        if(game.mustEnd()){
            return;
        }

        int spawnID = random.nextInt(game.getRoomManager().getVillagerSpawnCount());
        game.getRoomManager().spawnNPC(spawnID, random.nextBoolean());
        SpawnTask nextTask = new SpawnTask(game);
        nextTask.runTaskLater(game.getGame().getPlugin(), game.getSpawnFrequency());
    }
}

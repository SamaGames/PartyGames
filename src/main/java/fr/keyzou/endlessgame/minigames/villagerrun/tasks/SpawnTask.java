package fr.keyzou.endlessgame.minigames.villagerrun.tasks;

import fr.keyzou.endlessgame.minigames.villagerrun.VillagerRun;
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
            this.cancel();
            return;
        }

        int spawnID = random.nextInt(game.getRoomManager().getVillagerSpawnCount());
        game.getRoomManager().spawnNPC(spawnID, random.nextBoolean());
        SpawnTask nextTask = new SpawnTask(game);
        nextTask.runTaskLater(game.getPlugin(), game.getSpawnFrequency());
    }
}

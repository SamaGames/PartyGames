package net.samagames.partygames.minigames.villagerrun.tasks;

import net.samagames.partygames.minigames.villagerrun.VillagerRun;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

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

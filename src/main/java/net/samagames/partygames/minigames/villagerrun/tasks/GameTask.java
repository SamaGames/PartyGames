package net.samagames.partygames.minigames.villagerrun.tasks;

import net.samagames.partygames.minigames.villagerrun.VillagerRun;
import net.md_5.bungee.api.ChatColor;
import net.samagames.tools.GameUtils;
import org.bukkit.scheduler.BukkitRunnable;

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
public class GameTask extends BukkitRunnable {

    private VillagerRun game;

    public GameTask(VillagerRun game){
        this.game = game;
    }

    @Override
    public void run() {
        if(game.getRoomManager().getRoomsPlayingCount() <= 1 && !game.mustEnd()){
            game.setWinner(game.getRoomManager().getRoomPlayer(0));
            game.endGame();
            return;
        }

        game.incrementSecondsElapsed();
        if(game.getSecondsElapsed() % 30 == 0 && game.getSpawnFrequency() > 20){
            game.reduceSpawnFrequency();
            GameUtils.broadcastMessage(ChatColor.GOLD+"On accélère la cadence !");
        }

        game.getRoomManager().updateRooms();
        game.getRoomManager().checkErrors();
        game.getRoomManager().cleanRooms();

    }
}

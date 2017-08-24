package net.samagames.partygames.minigames.skyfall.tasks;

import net.samagames.partygames.minigames.skyfall.Skyfall;
import net.samagames.partygames.tasks.Timer;
import net.samagames.tools.Titles;
import org.bukkit.ChatColor;

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
public class FloorBreakTimer extends Timer {

    private Skyfall game;
    public FloorBreakTimer(Skyfall game, int time) {
        super(time);
        this.game = game;
    }

    @Override
    public void run(){
        if(game.getPlayers().size() <= 1|| game.getRound() > 6) { // If there is only one player or that we have an ex-aequo
            game.endGame();
            this.cancel();
        }
        if(time == 0) {
            int currentRound = game.getRound();
            game.increaseRound();
            game.generateRoom();
            game.destroyLayer(currentRound);
            if(!game.getPlayers().isEmpty()){
                time = 10;
            }
        } else if(time <= 10) {
            game.getPlayers().forEach(partyGamesPlayer -> Titles.sendTitle(partyGamesPlayer.getPlayerIfOnline(), 0, 22, 0, ChatColor.RED + "" + ChatColor.BOLD + this.time, "secondes avant l'écroulement du sol !"));
        }
        super.run();
    }
}

package net.samagames.partygames.minigames;

import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.game.PartyGames;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

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
public abstract class MiniGame {

    private String name;
    private String description;

    protected PartyGames game;

    protected boolean shouldEnd;

    protected PartyGamesPlayer winner;

    protected List<PartyGamesPlayer> players;

    public MiniGame(String name, String description, PartyGames game){
        this.name = name;
        this.description = description;
        this.game = game;
        players = new ArrayList<>();
    }

    public abstract void initGame();
    public abstract void startGame();
    public abstract void endGame();

    public void addPoints(PartyGamesPlayer player, int points){
        player.givePoints(points);
    }

    public PartyGames getGame(){
        return game;
    }

    public boolean mustEnd() {
        return shouldEnd;
    }

    public void setWinner(PartyGamesPlayer player) {
        winner = player;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<PartyGamesPlayer> getPlayers(){
        return players;
    }

    public void removePlayer(PartyGamesPlayer player){
        players.remove(player);
    }

    public void handlePlayerDeath(PlayerDeathEvent e){}

    public void handleDamage(EntityDamageEvent e){}

}

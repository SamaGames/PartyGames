package net.samagames.partygames.game;

import net.samagames.api.games.GamePlayer;
import org.bukkit.entity.Player;

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
public class PartyGamesPlayer extends GamePlayer {

    private int roomId;
    private int points;

    public PartyGamesPlayer(Player player) {
        super(player);
    }

    public void givePoints(int points){
        this.points += points;
    }

    public int getPoints() {
        return points;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }
}

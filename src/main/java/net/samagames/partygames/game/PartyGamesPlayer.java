package net.samagames.partygames.game;

import net.samagames.api.games.GamePlayer;
import org.bukkit.entity.Player;

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

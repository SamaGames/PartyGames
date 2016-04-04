package fr.keyzou.endlessgame.entities;

import net.samagames.api.games.GamePlayer;
import org.bukkit.entity.Player;

public class EndlessPlayer extends GamePlayer {

    private int points;

    public EndlessPlayer(Player player) {
        super(player);
    }

    public void addPoints(int points){
        this.points += points;
    }
}

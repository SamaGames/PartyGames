package fr.keyzou.endlessgame.minigames;

import fr.keyzou.endlessgame.Main;
import fr.keyzou.endlessgame.entities.EndlessPlayer;
import fr.keyzou.endlessgame.game.EndlessGame;

import java.util.ArrayList;
import java.util.List;

public abstract class MiniGame {

    String name;
    String description;

    protected EndlessGame currentGame;

    protected boolean shouldEnd;

    protected List<EndlessPlayer> playerList = new ArrayList<>();

    protected EndlessPlayer winner;

    protected Main plugin;

    public MiniGame(String gameName, String description, EndlessGame currentGame){
        this.name = gameName;
        this.description = description;
        this.currentGame = currentGame;
    }

    public abstract void initGame();
    public abstract void startGame(List<EndlessPlayer> players);
    public abstract void endGame();

    public void addPoints(EndlessPlayer player, int points){
        player.addPoints(points);
    }

    public EndlessGame getGame(){
        return currentGame;
    }

    public boolean mustEnd() {
        return shouldEnd;
    }

    public void setWinner(EndlessPlayer player) {
        winner = player;
    }


}

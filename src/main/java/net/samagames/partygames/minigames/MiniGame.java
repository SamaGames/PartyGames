package net.samagames.partygames.minigames;

import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.game.PartyGames;

public abstract class MiniGame {

    private String name;
    private String description;

    protected PartyGames game;

    protected boolean shouldEnd;

    protected PartyGamesPlayer winner;

    public MiniGame(String name, String description, PartyGames game){
        this.name = name;
        this.description = description;
        this.game = game;
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
}

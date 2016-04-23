package net.samagames.partygames.minigames.skyfall.tasks;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.minigames.skyfall.Skyfall;
import net.samagames.partygames.tasks.Timer;
import org.bukkit.ChatColor;

public class FloorBreakTimer extends Timer {

    private Skyfall game;
    public FloorBreakTimer(Skyfall game, int time) {
        super(time);
        this.game = game;
    }

    @Override
    public void run(){
        if(game.getPlayers().size() <= 1 || game.getRound() > 9) {
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
        } else if(time % 10 == 0 || time <= 5) {
            SamaGamesAPI.get().getGameManager().getCoherenceMachine().getMessageManager()
                    .writeCustomMessage(ChatColor.YELLOW + "Le sol va se détruire dans "+ChatColor.RED+time+" secondes"+ChatColor.YELLOW+".", true);
        }
        super.run();
    }
}

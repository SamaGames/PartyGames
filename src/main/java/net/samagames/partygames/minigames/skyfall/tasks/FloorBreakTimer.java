package net.samagames.partygames.minigames.skyfall.tasks;

import net.samagames.partygames.minigames.skyfall.Skyfall;
import net.samagames.partygames.tasks.Timer;
import net.samagames.tools.Titles;
import org.bukkit.ChatColor;

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

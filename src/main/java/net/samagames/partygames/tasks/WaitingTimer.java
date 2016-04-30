package net.samagames.partygames.tasks;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.minigames.MiniGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

/**
 * Created by deanc on 29/04/2016.
 */
public class WaitingTimer extends Timer {

    private MiniGame miniGame;
    private PartyGames game;

    public WaitingTimer(MiniGame miniGame, PartyGames game) {
        super(game.getMgManager().getTeleportTimer());
        this.miniGame = miniGame;
        this.game = game;

        Bukkit.broadcastMessage(ChatColor.BLUE + "Le prochain jeu est " + ChatColor.BOLD + ChatColor.GREEN + miniGame.getName());
        Bukkit.broadcastMessage(ChatColor.BLUE + "Les règles sont les suivantes :");
        Bukkit.broadcastMessage(ChatColor.GRAY+miniGame.getDescription());
    }
    @Override
    public void run() {
        if(time == 0) {
            miniGame.initGame();
            new MiniGameStartTimer(miniGame, game).runTaskTimer(game.getPlugin(), 0L, 20L);
        } else if(time % 10 == 0 || time <= 5) {
            SamaGamesAPI.get().getGameManager().getCoherenceMachine().getMessageManager()
                    .writeCustomMessage(String.format(ChatColor.YELLOW + "Vous allez être téléporté dans " +
                                    ChatColor.RED + "%s secondes" + ChatColor.YELLOW + ".",
                            time), true);
        }

        super.run();
    }
}

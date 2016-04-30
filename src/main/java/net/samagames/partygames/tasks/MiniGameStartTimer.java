package net.samagames.partygames.tasks;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.minigames.MiniGame;
import org.bukkit.ChatColor;

public class MiniGameStartTimer extends Timer {

    private MiniGame miniGame;

    MiniGameStartTimer(MiniGame miniGame, PartyGames game) {
        super(game.getMgManager().getMinigameStartTimer());

        this.miniGame = miniGame;
    }

    @Override
    public void run() {
        if(time == 0) {
            SamaGamesAPI.get().getGameManager().getCoherenceMachine().getMessageManager()
                    .writeCustomMessage("Le mini-jeu commence !", true);
            miniGame.startGame();
        } else if(time % 10 == 0 || time <= 5) {
            SamaGamesAPI.get().getGameManager().getCoherenceMachine().getMessageManager()
                    .writeCustomMessage(String.format(ChatColor.YELLOW + "Début du mini-jeu dans " +
                            ChatColor.RED + "%s secondes" + ChatColor.YELLOW + ".",
                            time), true);
        }

        super.run();
    }
}

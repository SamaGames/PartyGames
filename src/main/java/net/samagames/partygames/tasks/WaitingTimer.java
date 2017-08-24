package net.samagames.partygames.tasks;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.minigames.MiniGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

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

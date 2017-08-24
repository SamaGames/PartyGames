package net.samagames.partygames.minigames;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.tasks.WaitingTimer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

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
public class MGManager {

    private List<MiniGame> miniGameList = new ArrayList<>();

    private int currentGameID;

    private PartyGames game;

    private int minigameStartTimer;
    private int teleportTimer;

    private BukkitTask updateTask;

    public MGManager(PartyGames game)
    {
        this.game = game;
    }

    public void addMiniGame(MiniGame game){
        if(!miniGameList.contains(game))
            miniGameList.add(game);
    }

    public void startGame(){
        // Assign one room ID per user
        int i = 0;
        for(PartyGamesPlayer p : game.getRegisteredGamePlayers().values()) {
            p.setRoomId(i);
            i++;
        }


        minigameStartTimer = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get("minigame-start-timer").getAsInt();
        teleportTimer = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get("teleport-timer").getAsInt();

        currentGameID = game.getRandom().nextInt(miniGameList.size());
        switchMiniGame(miniGameList.get(currentGameID));

        updateTask = Bukkit.getScheduler().runTaskTimer(game.getPlugin(), () -> {
            if(miniGameList.get(currentGameID).mustEnd()){
                miniGameList.remove(miniGameList.get(currentGameID));
                if(!miniGameList.isEmpty()) {
                    currentGameID = game.getRandom().nextInt(miniGameList.size());
                    switchMiniGame(miniGameList.get(currentGameID));
                } else {
                    updateTask.cancel();
                    game.handleGameEnd();
                }
            }
        }, 0L, 20L);
    }

    private void switchMiniGame(MiniGame miniGame) {
        game.getRegisteredGamePlayers().values().forEach(partyGamesPlayer -> {
            partyGamesPlayer.getPlayerIfOnline().setGameMode(GameMode.ADVENTURE);
            partyGamesPlayer.getPlayerIfOnline().teleport(game.getWaitingRoom());
            partyGamesPlayer.getPlayerIfOnline().setMaxHealth(20);
            partyGamesPlayer.getPlayerIfOnline().setHealth(20);
        });

        SamaGamesAPI.get().getGameManager().getCoherenceMachine().setNameShortcut(miniGame.getName());
        new WaitingTimer(miniGame, game).runTaskTimer(game.getPlugin(), 0L, 20L);
    }

    public void sendEvent(Event e){
        if(e instanceof PlayerDeathEvent){
            getCurrentMinigame().handlePlayerDeath((PlayerDeathEvent) e);
        }

        if(e instanceof EntityDamageEvent){
            getCurrentMinigame().handleDamage((EntityDamageEvent) e);
        }
    }

    public MiniGame getCurrentMinigame(){
        return miniGameList.get(currentGameID);
    }

    public int getMinigameStartTimer() {
        return minigameStartTimer;
    }

    public int getTeleportTimer() {
        return teleportTimer;
    }
}

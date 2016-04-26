package net.samagames.partygames.minigames;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.tasks.MiniGameStartTimer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class MGManager {

    private List<MiniGame> miniGameList = new ArrayList<>();

    private int currentGameID;

    private PartyGames game;

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

        currentGameID = 0;
        switchMiniGame(miniGameList.get(0));

<<<<<<< Updated upstream
<<<<<<< Updated upstream
        // Must NOT be Asynchronous because some methods it may call use the Bukkit API
        updateTask = Bukkit.getScheduler().runTaskTimer(game.getPlugin(), this::update, 0L, 20L);
=======
=======
>>>>>>> Stashed changes
        updateTask = Bukkit.getScheduler().runTaskTimer(game.getPlugin(), () -> {
            if(miniGameList.get(currentGameID).mustEnd()){
                if(miniGameList.size() > currentGameID + 1) {
                    currentGameID++;

                    switchMiniGame(miniGameList.get(currentGameID));
                } else {
                    updateTask.cancel();
                    game.handleGameEnd();
                }
            }
        }, 0L, 20L);
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
    }

    private void switchMiniGame(MiniGame miniGame) {
        game.getRegisteredGamePlayers().values().forEach(partyGamesPlayer -> {
            partyGamesPlayer.getPlayerIfOnline().setGameMode(GameMode.ADVENTURE);
            partyGamesPlayer.getPlayerIfOnline().teleport(game.getWaitingRoom());
        });

        SamaGamesAPI.get().getGameManager().getCoherenceMachine().setNameShortcut(miniGame.getName());
        miniGame.initGame();

        Bukkit.broadcastMessage(ChatColor.BLUE + "Le prochain jeu est " + ChatColor.BOLD + ChatColor.GREEN + miniGame.getName());
        Bukkit.broadcastMessage("Les règles sont les suivantes :");
        Bukkit.broadcastMessage(ChatColor.GRAY+miniGame.getDescription());

        new MiniGameStartTimer(miniGame).runTaskTimer(game.getPlugin(), 0L, 20L);
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
}

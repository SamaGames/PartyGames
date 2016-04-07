package net.samagames.partygames.minigames;

import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.Status;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.tasks.MiniGameStartTimer;
import net.samagames.tools.Titles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class MGManager {

    private List<MiniGame> miniGameList = new ArrayList<>();

    private int currentGameID;

    private boolean finished;

    private PartyGames game;

    private BukkitTask updateTask;

    private BukkitTask timerTask;
    private int timer = 5;

    public MGManager(PartyGames game)
    {
        this.game = game;
    }

    public void addMiniGame(MiniGame game){
        if(!miniGameList.contains(game))
            miniGameList.add(game);
    }

    private void nextMiniGame(){
        currentGameID++;
        if (currentGameID >= miniGameList.size()) {
            finished = true;
            return;
        }

        miniGameList.get(currentGameID).startGame();
    }

    private void update(){
        if(miniGameList.get(currentGameID).mustEnd()){
            if(miniGameList.size() > currentGameID + 1) {
                currentGameID++;

                switchMiniGame(miniGameList.get(currentGameID));
            } else {
                game.handleGameEnd();
            }
        }
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

        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(game.getPlugin(), this::update, 0L, 20L);
    }

    private void switchMiniGame(MiniGame miniGame) {
        SamaGamesAPI.get().getGameManager().getCoherenceMachine().setNameShortcut(miniGame.getName());
        miniGame.initGame();

        Bukkit.broadcastMessage(ChatColor.BLUE + "Le prochain jeu est " + ChatColor.BOLD + ChatColor.GREEN + miniGame.getName());
        Bukkit.broadcastMessage("Les règles sont les suivantes :");
        Bukkit.broadcastMessage(miniGame.getDescription());

        new MiniGameStartTimer(miniGame).runTaskTimer(game.getPlugin(), 0L, 20L);
    }
}
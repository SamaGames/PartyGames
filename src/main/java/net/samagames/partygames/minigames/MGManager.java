package net.samagames.partygames.minigames;

import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.game.PartyGames;
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

    public void initMiniGames(){
        miniGameList.forEach(MiniGame::initGame);
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
        if(finished){
            updateTask.cancel();
            endGame();
            return;
        }

        if(miniGameList.get(currentGameID).shouldEnd){
            Bukkit.broadcastMessage(ChatColor.BLUE+"Le prochain jeu est "+ChatColor.BOLD + ChatColor.GREEN + miniGameList.get(currentGameID+1).name);
            Bukkit.broadcastMessage("Les règles sont les suivantes:");
            Bukkit.broadcastMessage(miniGameList.get(currentGameID+1).getDescription());
            timerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(game.getPlugin(), () -> {
                if(timer > 0) {
                    timer--;
                    Bukkit.broadcastMessage("La partie commence dans "+timer+" secondes !");
                }
                else{
                    nextMiniGame();
                    timerTask.cancel();
                }
            }, 20L, 20L);
        }
    }

    private void endGame() {
        //game.getRegisteredGamePlayers().values().forEach(player -> Titles.sendTitle(player.getPlayerIfOnline(), 0, 22, 0, "Testouille", "Et le grand gagnant est: "));
    }

    public void startGame(){

        // Assign one room ID per user
        int i = 0;
        for(PartyGamesPlayer p : game.getRegisteredGamePlayers().values()) {
            p.setRoomId(i);
            i++;
        }

        currentGameID = 0;
        miniGameList.get(currentGameID).startGame();

        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(game.getPlugin(), this::update, 0L, 20L);
    }
}

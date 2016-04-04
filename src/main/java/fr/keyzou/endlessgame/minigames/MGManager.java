package fr.keyzou.endlessgame.minigames;

import fr.keyzou.endlessgame.entities.EndlessPlayer;
import fr.keyzou.endlessgame.game.EndlessGame;
import net.samagames.api.SamaGamesAPI;
import net.samagames.api.games.BeginTimer;
import net.samagames.tools.Titles;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class MGManager {

    private List<MiniGame> miniGameList = new ArrayList<>();
    private List<EndlessPlayer> playerList = new ArrayList<>();

    private int currentGameID;

    private boolean finished;

    private EndlessGame pluginGame;

    private BukkitTask updateTask;

    private BukkitTask timerTask;
    private int timer = 5;

    public MGManager(EndlessGame game)
    {
        this.pluginGame = game;
    }

    public void addMiniGame(MiniGame game){
        if(!miniGameList.contains(game))
            miniGameList.add(game);
    }

    public void initGames(){
        miniGameList.forEach(MiniGame::initGame);
    }

    private void nextGame(){
            currentGameID++;
            if (currentGameID >= miniGameList.size()) {
                finished = true;
                return;
            }
            miniGameList.get(currentGameID).startGame(playerList);
    }

    private void update(){
        if(finished){
            updateTask.cancel();
            endAndRewards();
            return;
        }
        if(miniGameList.get(currentGameID).shouldEnd){
            Bukkit.broadcastMessage(ChatColor.BLUE+"Le prochain jeu est "+ChatColor.BOLD + ChatColor.GREEN + miniGameList.get(currentGameID+1).name);
            Bukkit.broadcastMessage("Les règles sont les suivantes:");
            Bukkit.broadcastMessage(miniGameList.get(currentGameID+1).description);
            timerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(pluginGame.getPlugin(), () -> {
                if(timer > 0) {
                    timer--;
                    Bukkit.broadcastMessage("La partie commence dans "+timer+" secondes !");
                }
                else{
                    nextGame();
                    timerTask.cancel();
                }
            }, 20L, 20L);
        }
    }

    private void endAndRewards() {
        Bukkit.getOnlinePlayers().forEach(player -> Titles.sendTitle(player, 0, 22, 0, "Testouille", "Et le grand gagnant est: "));
    }

    public void start(){
        Bukkit.getOnlinePlayers().forEach(o -> playerList.add(pluginGame.getPlayer(o.getUniqueId())));
        miniGameList.get(currentGameID).startGame(playerList);
        updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(pluginGame.getPlugin(), this::update, 0L, 20L);
    }
}

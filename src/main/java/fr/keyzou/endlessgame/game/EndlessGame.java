package fr.keyzou.endlessgame.game;

import fr.keyzou.endlessgame.Main;
import fr.keyzou.endlessgame.entities.EndlessPlayer;
import fr.keyzou.endlessgame.minigames.MGManager;
import fr.keyzou.endlessgame.minigames.villagerrun.VillagerRun;
import net.samagames.api.games.Game;

public class EndlessGame extends Game<EndlessPlayer> {

    private Main plugin;
    private MGManager mgManager;

    public EndlessGame(String gameCodeName, String gameName, String gameDescription, Class<EndlessPlayer> gamePlayerClass, Main plugin) {
        super(gameCodeName, gameName, gameDescription, gamePlayerClass);
        this.plugin = plugin;
    }

    @Override
    public void startGame(){
        super.startGame();
        mgManager.start();
    }


    @Override
    public void handlePostRegistration(){
        super.handlePostRegistration();
        mgManager = new MGManager(this);
        mgManager.addMiniGame(new VillagerRun("Villager Run!", "Sauvez un maximum de villageois !", this));
        mgManager.initGames();
    }
    public Main getPlugin(){
        return plugin;
    }

}

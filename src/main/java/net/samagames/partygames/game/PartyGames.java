package net.samagames.partygames.game;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.Main;
import net.samagames.partygames.minigames.MGManager;
import net.samagames.partygames.minigames.blockdodger.BlockDodger;
import net.samagames.api.games.Game;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.scheduler.BukkitRunnable;

public class PartyGames extends Game<PartyGamesPlayer> {

    public static final String CODE_NAME = "PG";
    public static final String NAME = "PartyGames";
    public static final String DESCRIPTION = "Survivez à la série de mini-jeux !";

    private Main plugin;
    private MGManager mgManager;

    public PartyGames(Main plugin) {
        super(CODE_NAME, NAME, DESCRIPTION, PartyGamesPlayer.class);

        this.plugin = plugin;
    }

    @Override
    public void startGame(){
        super.startGame();

        mgManager.startGame();
    }

    @Override
    public void handlePostRegistration(){
        super.handlePostRegistration();

        mgManager = new MGManager(this);
        mgManager.addMiniGame(new BlockDodger(this));
    }

    public Main getPlugin(){
        return plugin;
    }

    public MGManager getMgManager() {
        return mgManager;
    }
}

package net.samagames.partygames.game;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.Main;
import net.samagames.partygames.minigames.MGManager;
import net.samagames.partygames.minigames.blockdodger.BlockDodger;
import net.samagames.api.games.Game;
import net.samagames.partygames.minigames.villagerrun.VillagerRun;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

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
        mgManager.addMiniGame(new VillagerRun(this));
        mgManager.addMiniGame(new BlockDodger(this));
    }

    @Override
    public void handleGameEnd() {
        super.handleGameEnd();

        Comparator<PartyGamesPlayer> comparator = (PartyGamesPlayer p1, PartyGamesPlayer p2)
                -> p2.getPoints() - p1.getPoints();
        SortedSet<PartyGamesPlayer> players = new TreeSet<>(comparator);
        players.addAll(getInGamePlayers().values());

        List<PartyGamesPlayer> winners = new ArrayList<>();

        Iterator<PartyGamesPlayer> it = players.iterator();
        int i = 0;
        while(it.hasNext()) {
            PartyGamesPlayer player = it.next();

            if(i <= 2) {
                player.addStars(3 - i, "Classé n°" + (i + 1));

                winners.add(player);
            }

            player.addCoins(player.getPoints() / 5, player.getPoints() + " points");

            i++;
        }

        // For making tests with less than three players
        if(winners.size() < 2)
            winners.add(winners.get(0));
        if(winners.size() < 3)
            winners.add(winners.get(1));

        SamaGamesAPI.get().getGameManager().getCoherenceMachine().getTemplateManager()
                .getPlayerLeaderboardWinTemplate()
                .execute(winners.get(0).getPlayerIfOnline(),
                        winners.get(1).getPlayerIfOnline(),
                        winners.get(2).getPlayerIfOnline(),
                        "Ça sert à quoi ça lol ?",
                        winners.get(0).getPoints(),
                        winners.get(1).getPoints(),
                        winners.get(2).getPoints());
    }

    public Main getPlugin(){
        return plugin;
    }

    public MGManager getMgManager() {
        return mgManager;
    }
}

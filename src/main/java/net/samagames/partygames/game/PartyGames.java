package net.samagames.partygames.game;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.Main;
import net.samagames.partygames.minigames.MGManager;
import net.samagames.partygames.minigames.blockdodger.BlockDodger;
import net.samagames.api.games.Game;
import net.samagames.partygames.minigames.skyfall.Skyfall;
import net.samagames.partygames.minigames.villagerrun.VillagerRun;
import net.samagames.partygames.minigames.thedrawer.TheDrawer;
import net.samagames.tools.LocationUtils;
import org.bukkit.Location;

import java.util.*;

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
public class PartyGames extends Game<PartyGamesPlayer> {

    public static final String CODE_NAME = "PG";
    public static final String NAME = "PartyGames";
    public static final String DESCRIPTION = "Survivez à la série de mini-jeux !";

    private Random random;

    private Main plugin;
    private MGManager mgManager;
    private Location waitingRoom;

    public PartyGames(Main plugin) {
        super(CODE_NAME, NAME, DESCRIPTION, PartyGamesPlayer.class);

        random = new Random(System.currentTimeMillis());

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

        waitingRoom = LocationUtils.str2loc(SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().get("waiting-room").getAsString());
        waitingRoom.getWorld().setPVP(false);
        mgManager = new MGManager(this);
        mgManager.addMiniGame(new Skyfall(this));
        mgManager.addMiniGame(new VillagerRun(this));
        mgManager.addMiniGame(new BlockDodger(this));
        mgManager.addMiniGame(new TheDrawer(this));
    }

    @Override
    public void handleGameEnd() {
        super.handleGameEnd();

        // Ordering players by points descending
        Comparator<PartyGamesPlayer> comparator = (PartyGamesPlayer p1, PartyGamesPlayer p2)
                -> p2.getPoints() - p1.getPoints();
        SortedSet<PartyGamesPlayer> players = new TreeSet<>(comparator);
        players.addAll(getInGamePlayers().values());

        List<PartyGamesPlayer> winners = new ArrayList<>();

        // Then showing them
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

        SamaGamesAPI.get().getGameManager().getCoherenceMachine().getTemplateManager()
                .getPlayerLeaderboardWinTemplate()
                .execute(winners.get(0).getPlayerIfOnline(),
                        winners.get(1).getPlayerIfOnline(),
                        winners.get(2).getPlayerIfOnline(),
                        "",
                        winners.get(0).getPoints(),
                        winners.get(1).getPoints(),
                        winners.get(2).getPoints());
    }

    public Location getWaitingRoom(){
        return waitingRoom;
    }

    public Main getPlugin(){
        return plugin;
    }

    public MGManager getMgManager() {
        return mgManager;
    }

    public Random getRandom() {
        return random;
    }
}

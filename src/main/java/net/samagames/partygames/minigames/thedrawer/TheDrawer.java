package net.samagames.partygames.minigames.thedrawer;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.MiniGame;
import net.samagames.partygames.minigames.thedrawer.listeners.BlockListener;
import net.samagames.partygames.minigames.thedrawer.tasks.TheDrawerTask;
import net.samagames.partygames.tasks.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.event.HandlerList;
import org.bukkit.material.Wool;

import java.util.HashMap;
import java.util.Map;

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
public class TheDrawer extends MiniGame{

    private static final String NAME = "The Drawer";
    private static final String DESCRIPTION = "Vous allez devoir reproduire un modèle consitué de blocs de laine de différentes couleurs " +
            "généré aléatoirement. Vous n'avez que 20 secondes pour ce faire, alors faites vite !";

    private static final DyeColor[] DYE_COLORS = {
            DyeColor.WHITE,
            DyeColor.ORANGE,
            DyeColor.LIGHT_BLUE,
            DyeColor.LIME,
            DyeColor.GRAY,
            DyeColor.CYAN,
            DyeColor.GREEN,
            DyeColor.RED,
            DyeColor.BLACK
    };

    private HashMap<PartyGamesPlayer, TheDrawerRoom> rooms;
    private Timer timer;
    private TheDrawerTask task;
    private BlockListener blockListener;

    public TheDrawer(PartyGames game) {
        super(NAME, DESCRIPTION, game);
    }

    @Override
    public void initGame() {
        rooms = new HashMap<>();

        game.getRegisteredGamePlayers().values().forEach(player -> {
            TheDrawerRoom room = new TheDrawerRoom(SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs()
                    .getAsJsonObject("games").getAsJsonObject("thedrawer").getAsJsonArray("rooms")
                    .get(player.getRoomId()).getAsJsonObject());
            rooms.put(player, room);

            player.getPlayerIfOnline().teleport(room.getSpawn());
            player.getPlayerIfOnline().setGameMode(GameMode.SURVIVAL);
            room.clearBlocks();
        });

        task = new TheDrawerTask(this);
        task.runTaskTimer(game.getPlugin(), 0L, 5L);

        blockListener = new BlockListener(this);
        Bukkit.getPluginManager().registerEvents(blockListener, game.getPlugin());
    }

    @Override
    public void startGame() {
        game.getPlugin().getLogger().fine("The Drawer game starting.");

        Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "\nLe mini-jeu commence ! Reproduisez le modèle en blocs de laine en vingt secondes maximum !");

        timer = new Timer(20);
        timer.runTaskTimer(game.getPlugin(), 0L, 20L);

        task.setAnimationRunning(false);

        rooms.keySet().forEach((PartyGamesPlayer p) -> {
            p.getPlayerIfOnline().getInventory().clear();

            for(DyeColor color : DYE_COLORS)
                p.getPlayerIfOnline().getInventory().addItem(new Wool(color).toItemStack(1));
        });
    }

    @Override
    public void endGame() {
        HandlerList.unregisterAll(blockListener);

        timer.cancel();
        task.cancel();

        Bukkit.broadcastMessage(ChatColor.RED + "Le temps est écoulé.");
        rooms.forEach((partyGamesPlayer, room) -> {

            if(room.isActive()) {
                room.setActive(false);
                room.clearBlocks();

                partyGamesPlayer.getPlayerIfOnline().sendMessage(ChatColor.RED + "+ 0 points");
            }
        });
        shouldEnd = true;
    }

    public Timer getTimer() {
        return timer;
    }

    public Map<PartyGamesPlayer, TheDrawerRoom> getRooms() {
        return rooms;
    }

    public TheDrawerTask getTask() {
        return task;
    }
    public static DyeColor[] getDyeColors() {
        return DYE_COLORS;
    }

}

package net.samagames.partygames.minigames.thedrawer;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.MiniGame;
import net.samagames.partygames.minigames.blockdodger.tasks.BlockDodgerTask;
import net.samagames.partygames.minigames.thedrawer.listeners.BlockListener;
import net.samagames.partygames.minigames.thedrawer.tasks.TheDrawerTask;
import net.samagames.partygames.tasks.Timer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.event.HandlerList;
import org.bukkit.material.Wool;

import java.util.HashMap;

public class TheDrawer extends MiniGame{

    public static final String NAME = "The Drawer";
    public static final String DESCRIPTION = "Vous allez devoir reproduire un modèle consitué de blocs de laine de différentes couleurs " +
            "généré aléatoirement. Vous n'avez que 20 secondes pour ce faire, alors faites vite !";

    public static final DyeColor[] DYE_COLORS = {
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

        for(PartyGamesPlayer player : rooms.keySet()) {
            TheDrawerRoom room = rooms.get(player);

            if(room.isActive()) {
                room.setActive(false);
                room.clearBlocks();

                Bukkit.broadcastMessage(ChatColor.RED + "Le temps est écoulé.");
            }
        }

        shouldEnd = true;
    }

    public Timer getTimer() {
        return timer;
    }

    public HashMap<PartyGamesPlayer, TheDrawerRoom> getRooms() {
        return rooms;
    }

    public TheDrawerTask getTask() {
        return task;
    }
}

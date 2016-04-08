package net.samagames.partygames.minigames.blockdodger;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.MiniGame;
import net.samagames.partygames.minigames.blockdodger.tasks.BlockDodgerTask;
import net.samagames.partygames.tasks.Timer;
import net.samagames.tools.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;

import java.util.HashMap;

public class BlockDodger extends MiniGame{

    public static final String NAME = "Block Dodger";
    public static final String DESCRIPTION = "Évitez les blocs d'or qui arrivent vers vous en vous décalant sur la gauche ou la droite. " +
            "Vous devez tenir pendant 30 secondes.";

    private HashMap<PartyGamesPlayer, BlockDodgerRoom> rooms;
    private Timer timer;
    private BlockDodgerTask task;

    public BlockDodger(PartyGames game) {
        super(NAME, DESCRIPTION, game);
    }

    @Override
    public void initGame() {
        rooms = new HashMap<>();

        game.getRegisteredGamePlayers().values().forEach(player -> {
            BlockDodgerRoom room = new BlockDodgerRoom(SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs()
                    .getAsJsonObject("games").getAsJsonObject("blockdodger").getAsJsonArray("rooms")
                    .get(player.getRoomId()).getAsJsonObject());
            rooms.put(player, room);

            player.getPlayerIfOnline().teleport(room.getSpawn());
            room.clearBlocks();
        });
    }

    @Override
    public void startGame() {
        game.getPlugin().getLogger().info("BlockDodger game starting.");

        Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "\nLe mini-jeu commence ! Évitez les blocs qui arrivent vers vous.");

        timer = new Timer(30);
        timer.runTaskTimer(game.getPlugin(), 0L, 20L);

        task = new BlockDodgerTask(this);
        task.runTaskTimer(game.getPlugin(), 0L, 5L);
    }

    @Override
    public void endGame() {
        timer.cancel();
        task.cancel();

        for(PartyGamesPlayer player : rooms.keySet()) {
            BlockDodgerRoom room = rooms.get(player);

            if(room.isActive()) {
                room.setActive(false);
                room.clearBlocks();

                player.givePoints((int) (timer.getInitialTime() / 0.3));

                Bukkit.broadcastMessage(ChatColor.YELLOW + "Le temps est écoulé.");
                player.getPlayerIfOnline().sendMessage(ChatColor.GOLD + "+ " + timer.getInitialTime() + " points");
            }
        }

        shouldEnd = true;
    }

    public Timer getTimer() {
        return timer;
    }

    public HashMap<PartyGamesPlayer, BlockDodgerRoom> getRooms() {
        return rooms;
    }
}

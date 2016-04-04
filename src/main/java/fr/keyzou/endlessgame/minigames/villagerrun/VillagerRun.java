package fr.keyzou.endlessgame.minigames.villagerrun;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import fr.keyzou.endlessgame.Main;
import fr.keyzou.endlessgame.entities.EndlessPlayer;
import fr.keyzou.endlessgame.game.EndlessGame;
import fr.keyzou.endlessgame.minigames.MiniGame;
import fr.keyzou.endlessgame.minigames.villagerrun.rooms.RoomManager;
import fr.keyzou.endlessgame.minigames.villagerrun.tasks.GameTask;
import fr.keyzou.endlessgame.minigames.villagerrun.tasks.SpawnTask;
import net.samagames.api.SamaGamesAPI;
import net.samagames.tools.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class VillagerRun extends MiniGame {

    /**
     * The only instance of RoomManager
     */
    private RoomManager roomManager;

    /**
     * Gap between two rooms (from one player spawn to another)
     */
    private int gap;

    /**
     * Frequency at which villagers are spawning (ticks)
     */
    private long spawnFrequency = 40L;

    /**
     * Minimum frequency at which villagers spawn
     */
    private long minSpawnFrequency;

    /**
     * Task's ID that checks every villager pos
     */
    private int verifTaskID;
    /**
     * Seconds elapsed since the game started
     */
    private int secondsElapsed;


    public VillagerRun(String gameName, String description, EndlessGame currentGame) {
        super(gameName, description, currentGame);
        this.plugin = currentGame.getPlugin();
    }

    public void initGame(){
        roomManager = new RoomManager();
        gap = SamaGamesAPI.get().getGameManager().getGameProperties().getConfig("gap", new JsonPrimitive(10)).getAsInt();
        minSpawnFrequency = SamaGamesAPI.get().getGameManager().getGameProperties().getConfig("min-spawnFrequency", new JsonPrimitive(10)).getAsLong();
        spawnFrequency =  SamaGamesAPI.get().getGameManager().getGameProperties().getConfig("max-spawnFrequency", new JsonPrimitive(10)).getAsLong();
        JsonObject firstRoom = SamaGamesAPI.get().getGameManager().getGameProperties().getConfig("firstRoom", new JsonObject()).getAsJsonObject();
        Location playerSpawn = LocationUtils.str2loc(firstRoom.get("playerSpawn").getAsString());
        JsonArray paths = firstRoom.get("paths").getAsJsonArray();
        List<Location> originList = new ArrayList<>();
        List<Location> destinationList = new ArrayList<>();

        paths.forEach(jsonElement -> {
            JsonObject entry = jsonElement.getAsJsonObject();
            originList.add(LocationUtils.str2loc(entry.get("origin").getAsString()));
            destinationList.add(LocationUtils.str2loc(entry.get("destination").getAsString()));
        });
        roomManager.createFirstRoom(playerSpawn, originList, destinationList);
        this.getPlugin().getServer().getWorld("world").setPVP(false);
    }

    /**
     * Called when the game starts
     */
    public void startGame(List<EndlessPlayer> players) {
        this.playerList.addAll(players);
        for (int i = 0; i < playerList.size(); i++) {
            roomManager.duplicateRoom((gap) * (i + 1)); // We duplicate the first room and moves the new room
            roomManager.dispatchPlayer(i, playerList.get(i)); // Attach every player to a room
        }

        roomManager.startGame();

        // FIXME: Async tasks
        GameTask gameTask = new GameTask(this);
        gameTask.runTaskTimer(this.plugin, 0L, 20L);
        // Task vérif
        SpawnTask spawnTask = new SpawnTask(this);
        spawnTask.runTaskLater(this.plugin, spawnFrequency);
        // Task spawn variable
        verifTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> roomManager.checkRoomsPNJ(), 0L, 3L);

        this.getPlugin().getServer().getWorld("world").setPVP(true);

    }

    /**
     * Called when the game can end
     */
    public void endGame() {
        this.shouldEnd = true;
        this.roomManager.clearRooms();
        SamaGamesAPI.get().getGameManager().getCoherenceMachine().getTemplateManager().getPlayerWinTemplate().execute(winner.getPlayerIfOnline());
    }

    public long getSpawnFrequency() {
        return spawnFrequency;
    }

    public RoomManager getRoomManager() {
        return roomManager;
    }

    public Main getPlugin() {
        return plugin;
    }

    public int getVerifTaskID() {
        return verifTaskID;
    }

    public int getSecondsElapsed() {
        return secondsElapsed;
    }

    public void incrementSecondsElapsed() {
        this.secondsElapsed++;
    }

    /**
     * Reduces the spawn frequency, at first by 10 ticks then by 5, capped at 20 ticks
     */
    public void reduceSpawnFrequency() {
        spawnFrequency -= spawnFrequency <= 30 ? 5 : 10;
        if (spawnFrequency < minSpawnFrequency)
            spawnFrequency = minSpawnFrequency;
    }
}

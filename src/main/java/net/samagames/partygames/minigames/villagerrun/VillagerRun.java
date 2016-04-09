package net.samagames.partygames.minigames.villagerrun;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.minigames.MiniGame;
import net.samagames.partygames.minigames.villagerrun.rooms.RoomManager;
import net.samagames.partygames.minigames.villagerrun.tasks.GameTask;
import net.samagames.partygames.minigames.villagerrun.tasks.SpawnTask;
import net.samagames.tools.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class VillagerRun extends MiniGame {

    public static final String NAME = "Villager Run!";
    public static final String DESCRIPTION = "Sauvez les bons villageois, mais ne laissez pas les mauvais s'infiltrer ! "
            +"Que le dernier survivant gagne !";

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


    public VillagerRun(PartyGames game) {
        super(NAME, DESCRIPTION, game);
    }

    @Override
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
        game.getPlugin().getServer().getWorld("world").setPVP(false);
    }

    /**
     * Called when the game starts
     */
    @Override
    public void startGame() {
        game.getPlugin().getLogger().info("VillagerRun game starting");

        roomManager.startGame();

        GameTask gameTask = new GameTask(this);
        gameTask.runTaskTimer(game.getPlugin(), 0L, 20L);
        // Task vérif
        SpawnTask spawnTask = new SpawnTask(this);
        spawnTask.runTaskLater(game.getPlugin(), spawnFrequency);
        // Task spawn variable
        verifTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.getPlugin(), () -> roomManager.checkRoomsNPC(), 0L, 3L);

        game.getPlugin().getServer().getWorld("world").setPVP(true);

    }

    /**
     * Called when the game can end
     */
    @Override
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

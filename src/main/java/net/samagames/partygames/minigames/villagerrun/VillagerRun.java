package net.samagames.partygames.minigames.villagerrun;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.minigames.MiniGame;
import net.samagames.partygames.minigames.villagerrun.rooms.Room;
import net.samagames.partygames.minigames.villagerrun.rooms.RoomManager;
import net.samagames.partygames.minigames.villagerrun.tasks.GameTask;
import net.samagames.partygames.minigames.villagerrun.tasks.SpawnTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;


public class VillagerRun extends MiniGame {

    public static final String NAME = "Villager Run!";
    public static final String DESCRIPTION = "Sauvez les bons villageois, mais ne laissez pas les mauvais s'infiltrer ! "
            +"Que le dernier survivant gagne !";

    /**
     * The only instance of RoomManager
     */
    private RoomManager roomManager;

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
    private BukkitTask verifTask;
    /**
     * Seconds elapsed since the game started
     */
    private int secondsElapsed;

    private GameTask gameTask;

    private SpawnTask spawnTask;


    public VillagerRun(PartyGames game) {
        super(NAME, DESCRIPTION, game);
    }

    @Override
    public void initGame(){
        roomManager = new RoomManager();
        minSpawnFrequency = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().getAsJsonObject("games").getAsJsonObject("villagerrun").get("min-spawnFrequency").getAsLong();
        spawnFrequency =  SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().getAsJsonObject("games").getAsJsonObject("villagerrun").get("max-spawnFrequency").getAsLong();
        game.getRegisteredGamePlayers().values().forEach(player -> {
            Room room = new Room(SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs()
                    .getAsJsonObject("games").getAsJsonObject("villagerrun").getAsJsonArray("rooms")
                    .get(player.getRoomId()).getAsJsonObject());
            room.attachPlayer(player);
            roomManager.addRoom(room);

            player.getPlayerIfOnline().teleport(room.getSpawnPoint());
        });
        game.getPlugin().getServer().getWorld("world").setPVP(false);
    }

    /**
     * Called when the game starts
     */
    @Override
    public void startGame() {
        game.getPlugin().getLogger().info("VillagerRun game starting");

        roomManager.startGame();

        gameTask = new GameTask(this);
        gameTask.runTaskTimerAsynchronously(game.getPlugin(), 0L, 20L);
        // Task vérif
        spawnTask = new SpawnTask(this);
        spawnTask.runTaskLater(game.getPlugin(), spawnFrequency);
        // Task spawn variable
        verifTask = Bukkit.getScheduler().runTaskTimerAsynchronously(game.getPlugin(), () -> roomManager.checkRoomsNPC(), 0L, 3L);

        game.getPlugin().getServer().getWorld("world").setPVP(true);

    }

    /**
     * Called when the game can end
     */
    @Override
    public void endGame() {
        gameTask.cancel();
        spawnTask.cancel();
        verifTask.cancel();
        roomManager.clearRooms();
        //winner.givePoints(10);
        shouldEnd = true;
    }

    public long getSpawnFrequency() {
        return spawnFrequency;
    }

    public RoomManager getRoomManager() {
        return roomManager;
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

package net.samagames.partygames.minigames.villagerrun;

import com.google.gson.JsonObject;
import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.MiniGame;
import net.samagames.partygames.minigames.villagerrun.rooms.Room;
import net.samagames.partygames.minigames.villagerrun.rooms.RoomManager;
import net.samagames.partygames.minigames.villagerrun.tasks.GameTask;
import net.samagames.partygames.minigames.villagerrun.tasks.SpawnTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;


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

    private Map<Integer, PartyGamesPlayer> winners = new HashMap<>();

    public VillagerRun(PartyGames game) {
        super(NAME, DESCRIPTION, game);
    }

    @Override
    public void initGame(){
        roomManager = new RoomManager(this);
        JsonObject villagerRun = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().getAsJsonObject("games").getAsJsonObject("villagerrun");
        minSpawnFrequency = villagerRun.get("min-spawnFrequency").getAsLong();
        spawnFrequency =  villagerRun.get("max-spawnFrequency").getAsLong();
        game.getRegisteredGamePlayers().values().forEach(player -> {
            Room room = new Room(villagerRun.getAsJsonArray("rooms")
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
        winners.put(1, winner);
        winners.forEach((i, partyGamesPlayer) -> {
            String pos = "";
            int points = 0;
            if (i == 1) {
                pos = "1er";
                points = 100;
            }
            if (i == 2) {
                pos = "2ème";
                points = 50;
            }
            if (i == 3){
                pos = "3ème";
                points = 25;
            }
            partyGamesPlayer.getPlayerIfOnline().setMaxHealth(20);
            partyGamesPlayer.getPlayerIfOnline().setHealth(20);
            partyGamesPlayer.givePoints(points);
            partyGamesPlayer.getPlayerIfOnline().sendMessage(ChatColor.BLUE + "Vous êtes "+ChatColor.GOLD+pos+ChatColor.BLUE+" !");
            partyGamesPlayer.getPlayerIfOnline().sendMessage(ChatColor.GOLD + "+ "+points+" points");
        });
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

    public void addWinner(int pos, PartyGamesPlayer player){
        this.winners.put(pos, player);
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

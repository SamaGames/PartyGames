package net.samagames.partygames.minigames.skyfall;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.MiniGame;
import net.samagames.partygames.minigames.skyfall.tasks.FloorBreakTimer;
import net.samagames.tools.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Dean on 20/04/2016.
 */
public class Skyfall extends MiniGame {

    public static final String NAME = "Skyfall";
    public static final String DESCRIPTION = "Le sol se détruit sous vos pieds ! Faites ce que vous pouvez pour ne pas mourir !";
    private int roomSize = 0;
    private Location playerSpawn;

    private int floorBreakTime = 0;

    private int round = 0;

    private int fallDistance = 23;

    private static final Material[] GOOD_BLOCKS = { Material.SLIME_BLOCK, Material.WATER };
    private static final Material[] BAD_BLOCKS = { Material.WOOD, Material.WOOL, Material.STONE, Material.OBSIDIAN, Material.GLASS};
    private Random random;


    public Skyfall(PartyGames game) {
        super(NAME, DESCRIPTION, game);
    }

    @Override
    public void initGame() {
        roomSize =  SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().getAsJsonObject("games").getAsJsonObject("skyfall").get("room-size").getAsInt();
        floorBreakTime = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().getAsJsonObject("games").getAsJsonObject("skyfall").get("floor-break-time").getAsInt();
        playerSpawn = LocationUtils.str2loc(SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().getAsJsonObject("games").getAsJsonObject("skyfall").get("spawn").getAsString());
        random = new Random();
        players.addAll(game.getInGamePlayers().values());
        generateRoom();
        players.forEach(partyGamesPlayer -> {
            partyGamesPlayer.getPlayerIfOnline().setMaxHealth(2);
            partyGamesPlayer.getPlayerIfOnline().setHealth(2);
            partyGamesPlayer.getPlayerIfOnline().teleport(playerSpawn);
        });
    }

    public void generateRoom(){
        double xPos = playerSpawn.getBlockX() - roomSize / 2;
        double zPos = playerSpawn.getBlockZ() - roomSize / 2;
        for (int x = 0; x < roomSize; x++) {
            for (int z = 0; z < roomSize; z++) {
                Material block = BAD_BLOCKS[random.nextInt(BAD_BLOCKS.length)];
                playerSpawn.getWorld().getBlockAt((int) (xPos + x), playerSpawn.getBlockY() - 1 - round * fallDistance, (int) zPos + z).setType(block, true);
                playerSpawn.getWorld().getBlockAt((int) (xPos + x), playerSpawn.getBlockY() - 2 - round * fallDistance, (int) zPos + z).setType(Material.WOOL, true);
            }
        }
        for(int i = 0; i < players.size(); i++){
            Material block = GOOD_BLOCKS[random.nextInt(GOOD_BLOCKS.length)];
            playerSpawn.getWorld().getBlockAt(ThreadLocalRandom.current().nextInt((int)xPos, (int)xPos+roomSize), playerSpawn.getBlockY()-1-round*fallDistance, ThreadLocalRandom.current().nextInt((int)zPos, (int)zPos+roomSize)).setType(block, true);
        }

    }

    public void destroyLayer(int layer){
        double xPos = playerSpawn.getBlockX() - roomSize / 2;
        double zPos = playerSpawn.getBlockZ() - roomSize / 2;
        for(int x = 0; x < roomSize; x++){
            for(int z = 0; z < roomSize; z++){
                playerSpawn.getWorld().getBlockAt((int) (xPos + x), playerSpawn.getBlockY()-1-layer*fallDistance, (int) zPos + z).setType(Material.AIR, true);
                playerSpawn.getWorld().getBlockAt((int) (xPos + x), playerSpawn.getBlockY()-2-layer*fallDistance, (int) zPos + z).setType(Material.AIR, true);
            }
        }
    }

    @Override
    public void startGame() {
        FloorBreakTimer timer = new FloorBreakTimer(this, floorBreakTime);
        timer.runTaskTimer(game.getPlugin(), 0L, 20L);
    }

    @Override
    public void endGame() {
        players.forEach(partyGamesPlayer -> {
            partyGamesPlayer.givePoints(10);
            partyGamesPlayer.getPlayerIfOnline().sendMessage(ChatColor.GOLD + "+ 10 points");
        });
    }

    public void handlePlayerDeath(PlayerDeathEvent e){
        Bukkit.broadcastMessage(ChatColor.RED + e.getEntity().getDisplayName() +" est mort !");
        players.remove(game.getPlayer(e.getEntity().getUniqueId()));
    }

    public int getRound(){
        return round;
    }


    public void increaseRound(){
        round++;
    }
}

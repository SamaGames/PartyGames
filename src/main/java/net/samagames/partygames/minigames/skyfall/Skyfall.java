package net.samagames.partygames.minigames.skyfall;

import com.google.gson.JsonObject;
import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.minigames.MiniGame;
import net.samagames.partygames.minigames.skyfall.tasks.FloorBreakTimer;
import net.samagames.tools.LocationUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Skyfall extends MiniGame {

    private static final String NAME = "Skyfall";
    private static final String DESCRIPTION = "Le sol se détruit sous vos pieds ! Faites ce que vous pouvez pour ne pas mourir !";
    private int roomSize = 0;
    private Location roomLocation;

    private int floorBreakTime = 0;

    private int round = 0;

    private int fallDistance = 40;

    private static final Material[] GOOD_BLOCKS = { Material.SLIME_BLOCK, Material.WATER };
    private static final Material[] BAD_BLOCKS = { Material.WOOD, Material.WOOL, Material.STONE, Material.OBSIDIAN, Material.GLASS};
    private Random random;


    public Skyfall(PartyGames game) {
        super(NAME, DESCRIPTION, game);
    }

    @Override
    public void initGame() {
        JsonObject skyfall = SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().getAsJsonObject("games").getAsJsonObject("skyfall");
        roomSize =  skyfall.get("room-size").getAsInt();
        floorBreakTime = skyfall.get("floor-break-time").getAsInt();
        roomLocation = LocationUtils.str2loc(skyfall.get("spawn").getAsString());
        random = new Random();
        players.addAll(game.getInGamePlayers().values());
        generateRoom();
        players.forEach(partyGamesPlayer -> {
            partyGamesPlayer.getPlayerIfOnline().setMaxHealth(2);
            partyGamesPlayer.getPlayerIfOnline().setHealth(2);
            int x = roomLocation.getBlockX();
            int z = roomLocation.getBlockZ();
            Location playerLoc = new Location(roomLocation.getWorld(), ThreadLocalRandom.current().nextInt(x+1, x+roomSize-1), roomLocation.getBlockY(), ThreadLocalRandom.current().nextInt(z+1, z+roomSize-1));
            partyGamesPlayer.getPlayerIfOnline().teleport(playerLoc);
        });
    }

    public void generateRoom(){
        for(int z = 0; z < roomSize; z++){
            for(int x = 0; x < roomSize; x++){
                roomLocation.getWorld().getBlockAt(roomLocation.getBlockX() + x, roomLocation.getBlockY()- 1 - round*fallDistance, roomLocation.getBlockZ() + z).setType(BAD_BLOCKS[random.nextInt(BAD_BLOCKS.length)]);
                roomLocation.getWorld().getBlockAt(roomLocation.getBlockX() + x, roomLocation.getBlockY()- 2 - round*fallDistance, roomLocation.getBlockZ() + z).setType(Material.WOOL);
            }
        }
        for(int i = 0; i < (10-round)*2; i++){
            roomLocation.getWorld().getBlockAt(ThreadLocalRandom.current().nextInt(roomLocation.getBlockX(), roomLocation.getBlockX() + roomSize), roomLocation.getBlockY()- 1 - round*fallDistance, ThreadLocalRandom.current().nextInt(roomLocation.getBlockZ(), roomLocation.getBlockZ() + roomSize)).setType(GOOD_BLOCKS[random.nextInt(GOOD_BLOCKS.length)]);
        }
    }

    public void destroyLayer(int layer){
        for(int z = 0; z < roomSize; z++){
            for(int x = 0; x < roomSize; x++){
                roomLocation.getWorld().getBlockAt(roomLocation.getBlockX() + x, roomLocation.getBlockY()- 1 - layer*fallDistance, roomLocation.getBlockZ() + z).setType(Material.AIR);
                roomLocation.getWorld().getBlockAt(roomLocation.getBlockX() + x, roomLocation.getBlockY()- 2 - layer*fallDistance, roomLocation.getBlockZ() + z).setType(Material.AIR);
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
            partyGamesPlayer.getPlayerIfOnline().setMaxHealth(20);
            partyGamesPlayer.getPlayerIfOnline().setHealth(20);
            partyGamesPlayer.givePoints(10);
            partyGamesPlayer.getPlayerIfOnline().sendMessage(ChatColor.GOLD + "+ 10 points");
        });
        shouldEnd = true;
    }

    @Override
    public void handleDamage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player) {
            Player entity = (Player) e.getEntity();
            if(entity.getHealth() - e.getDamage() <= 0) {
                Bukkit.broadcastMessage(ChatColor.RED + entity.getDisplayName() +" est mort !");
                players.remove(game.getPlayer(e.getEntity().getUniqueId()));
                entity.setMaxHealth(20);
                entity.setHealth(20);
                entity.setGameMode(GameMode.SPECTATOR);
                e.setCancelled(true);
            }
        }
    }

    public int getRound(){
        return round;
    }


    public void increaseRound(){
        round++;
    }
}

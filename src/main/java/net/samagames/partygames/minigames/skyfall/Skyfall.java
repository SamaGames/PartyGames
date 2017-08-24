package net.samagames.partygames.minigames.skyfall;

import com.google.gson.JsonObject;
import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.MiniGame;
import net.samagames.partygames.minigames.skyfall.tasks.FloorBreakTimer;
import net.samagames.tools.LocationUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
public class Skyfall extends MiniGame {

    private static final String NAME = "Skyfall";
    private static final String DESCRIPTION = "Le sol se détruit sous vos pieds ! Faites ce que vous pouvez pour ne pas mourir !";
    private int roomSize = 0;
    private Location roomLocation;

    private int floorBreakTime = 0;

    private int round = 0;

    private int fallDistance = 40;

    private Map<Integer, PartyGamesPlayer> winners = new HashMap<>();

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

    /**
     * Generating two layers of random blocks which will be destroyed in the future
     */
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

    /**
     * Destroys the layer of random blocks.
     * @param layer
     */
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
        players.forEach(partyGamesPlayer -> winners.put(1, partyGamesPlayer));

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
            partyGamesPlayer.givePoints(points);
            partyGamesPlayer.getPlayerIfOnline().sendMessage(ChatColor.BLUE + "Vous êtes "+ChatColor.GOLD+pos+ChatColor.BLUE+" !");
            partyGamesPlayer.getPlayerIfOnline().sendMessage(ChatColor.GOLD + "+ "+points+" points");
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
                if(players.size() == 2)
                    winners.put(3, game.getPlayer(e.getEntity().getUniqueId()));
                else if(players.size() == 1)
                    winners.put(2, game.getPlayer(e.getEntity().getUniqueId()));
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

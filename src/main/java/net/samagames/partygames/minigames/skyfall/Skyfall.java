package net.samagames.partygames.minigames.skyfall;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.minigames.MiniGame;
import net.samagames.tools.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.Random;

/**
 * Created by Dean on 20/04/2016.
 */
public class Skyfall extends MiniGame {

    public static final String NAME = "Skyfall";
    public static final String DESCRIPTION = "Le sol se détruit sous vos pieds ! Faites ce que vous pouvez pour ne pas mourir !";
    private int roomSize = 0;
    private Location playerSpawn;

    private Material[] GOOD_BLOCKS = { Material.SLIME_BLOCK, Material.WATER };
    private Material[] BAD_BLOCKS = { Material.WOOD, Material.WOOL, Material.STONE, Material.OBSIDIAN, Material.GLASS};
    private Random random;


    public Skyfall(PartyGames game) {
        super(NAME, DESCRIPTION, game);
    }

    @Override
    public void initGame() {
        roomSize =  SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().getAsJsonObject("games").getAsJsonObject("skyfall").get("room-size").getAsInt();
        playerSpawn = LocationUtils.str2loc(SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs().getAsJsonObject("games").getAsJsonObject("skyfall").get("spawn").getAsString());
        random = new Random();
        generateRoom();
        getGame().getRegisteredGamePlayers().values().forEach(partyGamesPlayer -> partyGamesPlayer.getPlayerIfOnline().teleport(playerSpawn));
    }

    private void generateRoom(){
        double xPos = playerSpawn.getBlockX() - roomSize / 2;
        double zPos = playerSpawn.getBlockZ() - roomSize / 2;
        for(int x = 0; x < roomSize; x++){
            for(int z = 0; z < roomSize; z++){
                Material block = BAD_BLOCKS[random.nextInt(BAD_BLOCKS.length)];
                playerSpawn.getWorld().getBlockAt((int) (xPos + x), playerSpawn.getBlockY()-1, (int) zPos + z).setType(block, true);
            }
        }
    }

    @Override
    public void startGame() {

    }

    @Override
    public void endGame() {

    }
}

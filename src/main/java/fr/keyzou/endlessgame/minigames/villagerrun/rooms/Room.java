package fr.keyzou.endlessgame.minigames.villagerrun.rooms;

import fr.keyzou.endlessgame.entities.EndlessPlayer;
import fr.keyzou.endlessgame.entities.nms.PNJ;
import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.data.DataException;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import net.samagames.api.games.GamePlayer;
import net.samagames.tools.scoreboards.ObjectiveSign;
import net.samagames.tools.scoreboards.VObjective;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;


class Room {

    private EndlessPlayer attachedPlayer;
    /**
     * Player spawn point
     */
    private Location spawnPoint;

    /**
     * Where villagers spawn
     */
    List<Location> villagerSpawnPoints = new ArrayList<>();
    /**
     * Where villagers must go
     */
    List<Location> fencesLocations = new ArrayList<>();

    List<PNJ> pnjList = new ArrayList<>();
    /**
     * All the {@link PNJ}s which will be removed next tick
     */
    List<PNJ> pnjToRemove = new ArrayList<>();

    int score = 0;
    int errors = 0;

    private ObjectiveSign scoreBoard;

    Room(Location loc, List<Location> villagerSpawnPoints, List<Location> destinationPoints){
        spawnPoint = loc;
        this.villagerSpawnPoints = villagerSpawnPoints;
        this.fencesLocations = destinationPoints;
    }

    /**
     * Generates the room and the scoreboard
     */
    void startGame(){
        generateRoom();
        scoreBoard = new ObjectiveSign("villagerRun", ChatColor.AQUA+""+ChatColor.BOLD+"     Villager Run     ");
        scoreBoard.addReceiver(attachedPlayer.getPlayerIfOnline());
        scoreBoard.setLocation(VObjective.ObjectiveLocation.SIDEBAR);
        scoreBoard.setLine(1, ChatColor.GOLD+""+ChatColor.BOLD+"Score:");
        scoreBoard.setLine(2, "0");
        scoreBoard.setLine(4, ChatColor.GOLD+""+ChatColor.BOLD+"Erreurs:");
        attachedPlayer.getPlayerIfOnline().teleport(spawnPoint);
    }

    /**
     * Loads and paste the {@link WorldEdit} schematic at the player spawn location
     */
    private void generateRoom(){
        org.bukkit.World world = Bukkit.getWorld("world");
        EditSessionFactory esf = WorldEdit.getInstance().getEditSessionFactory();
        EditSession es = esf.getEditSession(new BukkitWorld(world), -1);
        File file = new File("room.schematic");
        Vector loc = new Vector(spawnPoint.getBlockX(), spawnPoint.getBlockY(), spawnPoint.getBlockZ());
        try {
            MCEditSchematicFormat.getFormat(file).load(file).paste(es, loc, false);
        } catch (MaxChangedBlocksException | IOException | DataException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Erreur [VR]", e);
        }
    }

    /**
     * Called when a player loses, clears the room and set the player as spectator
     */
    void lose(){
        // clear pnj list
        pnjList.forEach(this::removePNJ);
        // Add to Waiting List
        attachedPlayer.setSpectator();
        attachedPlayer = null;
        // Reset scores
        errors = 0;
        score = 0;
    }

    void addPNJ(PNJ pnj){
        pnjList.add(pnj);
    }
    void removePNJ(PNJ pnj){
        pnjToRemove.add(pnj);
        pnj.die();
    }

    /**
     * Updates the scoreboard
     */
    void updateRoom(){
        scoreBoard.setLine(2, String.valueOf(score));
        StringBuilder errorsSB = new StringBuilder();
        for(int i = 0; i < errors; i++){
            errorsSB.append("\u2716");
        }
        StringBuilder errorsLeftSB = new StringBuilder();
        for(int i = errors; i < 3; i++){
            errorsLeftSB.append("\u25EF");
        }
        scoreBoard.setLine(5, ChatColor.RED+errorsSB.toString()+ChatColor.BOLD+ChatColor.GREEN+errorsLeftSB.toString());
        scoreBoard.updateLines();
    }

    EndlessPlayer getRoomPlayer(){
        return attachedPlayer;
    }
    void attachPlayer(EndlessPlayer player){
        attachedPlayer = player;
    }

    /**
     * Create a new room and moves it by zOffset blocks
     * @param zOffset gap between the current room and the new one
     * @return the new {@link Room}
     */
    Room duplicate(int zOffset){
        List<Location> newOriginList = new ArrayList<>();
        List<Location> newDestinationList = new ArrayList<>();
        villagerSpawnPoints.forEach(location -> newOriginList.add(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()+zOffset)));
        fencesLocations.forEach(location -> newDestinationList.add(new Location(location.getWorld(), location.getX(), location.getY(), location.getZ()+zOffset)));
        Location newSpawn = new Location(spawnPoint.getWorld(), spawnPoint.getX(), spawnPoint.getY(), spawnPoint.getZ()+zOffset);
        return new Room(newSpawn, newOriginList, newDestinationList);
    }

}

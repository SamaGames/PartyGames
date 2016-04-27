package net.samagames.partygames.minigames.villagerrun.rooms;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.villagerrun.entities.NPC;
import net.samagames.tools.LocationUtils;
import net.samagames.tools.scoreboards.ObjectiveSign;
import net.samagames.tools.scoreboards.VObjective;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;


public class Room {

    private PartyGamesPlayer attachedPlayer;
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

    List<NPC> npcList = new ArrayList<>();
    /**
     * All the {@link NPC}s which will be removed next tick
     */
    List<NPC> npcToRemove = new ArrayList<>();

    int score = 0;
    int errors = 0;

    private ObjectiveSign scoreBoard;

    public Room(JsonObject json){
        spawnPoint = LocationUtils.str2loc(json.get("playerSpawn").getAsString());
        JsonArray paths = json.get("paths").getAsJsonArray();
        List<Location> originList = new ArrayList<>();
        List<Location> destinationList = new ArrayList<>();

        paths.forEach(jsonElement -> {
            JsonElement entry = jsonElement.getAsJsonObject().get("origin");
            Location origin = LocationUtils.str2loc(entry.getAsString());
            origin.setPitch(Float.parseFloat(entry.getAsString().split(", ")[5]));
            origin.setYaw(Float.parseFloat(entry.getAsString().split(", ")[4]));
            originList.add(origin);
            destinationList.add(LocationUtils.str2loc(jsonElement.getAsJsonObject().get("destination").getAsString()));
        });

        villagerSpawnPoints.addAll(originList);
        fencesLocations.addAll(destinationList);
    }

    /**
     * Generates the room and the scoreboard
     */
    void startGame(){
        scoreBoard = new ObjectiveSign("villagerRun", ChatColor.AQUA+""+ChatColor.BOLD+"     Villager Run     ");
        scoreBoard.addReceiver(attachedPlayer.getPlayerIfOnline());
        scoreBoard.setLocation(VObjective.ObjectiveLocation.SIDEBAR);
        scoreBoard.setLine(1, ChatColor.GOLD+""+ChatColor.BOLD+"Score:");
        scoreBoard.setLine(2, "0");
        scoreBoard.setLine(4, ChatColor.GOLD+""+ChatColor.BOLD+"Erreurs:");
    }


    /**
     * Called when a player loses, clears the room and set the player as spectator
     */
    void lose(){
        npcList.forEach(this::removeNPC);
        scoreBoard.clearScores();
        scoreBoard.removeReceiver(attachedPlayer.getPlayerIfOnline());
        attachedPlayer = null;
        errors = 0;
        score = 0;
    }

    void addNPC(NPC npc){
        npcList.add(npc);
    }
    void removeNPC(NPC npc){
        npcToRemove.add(npc);
        npc.die();
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

    PartyGamesPlayer getRoomPlayer(){
        return attachedPlayer;
    }
    public void attachPlayer(PartyGamesPlayer player){
        attachedPlayer = player;
    }


    public Location getSpawnPoint(){
        return spawnPoint;
    }

    ObjectiveSign getScoreboard(){
        return scoreBoard;
    }
}

package fr.keyzou.endlessgame.minigames.villagerrun.rooms;

import fr.keyzou.endlessgame.entities.EndlessPlayer;
import fr.keyzou.endlessgame.entities.nms.PNJ;
import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.World;
import net.samagames.api.games.GamePlayer;
import net.samagames.tools.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {

    /**
     * Array that contains every {@link Room} (playing and not playing)
     */
    private List<Room> rooms = new ArrayList<>();
    /**
     * Array that contains every playing {@link Room}
     */
    private List<Room> roomsPlaying = new ArrayList<>();
    /**
     * Array that contains every {@link Room} which will lose next tick
     */
    private List<Room> roomsRemove = new ArrayList<>();

    /**
     * First {@link Room}, serves as template for the next ones
     */
    private Room firstRoom;

    public void startGame(){
        roomsPlaying.addAll(rooms);
        roomsPlaying.forEach(Room::startGame);
    }

    /**
     * Spawns an either good or bad {@link PNJ} at a specified spawn
     * @param spawnerID Location where it'll be spawned
     * @param isGood
     */
    public void spawnNPC(int spawnerID, boolean isGood){
        roomsPlaying.forEach(room -> {
            Location loc = room.villagerSpawnPoints.get(spawnerID);
            World mcWorld = ((CraftWorld) loc.getWorld()).getHandle();
            PNJ pnj = new PNJ(mcWorld, room.fencesLocations.get(spawnerID), isGood);
            pnj.setLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), 0, 0);
            mcWorld.addEntity(pnj);
            room.addPNJ(pnj);
        });
    }

    public void checkErrors(){
        roomsPlaying.forEach(room -> {if(room.errors > 2) roomsRemove.add(room);});
    }

    /**
     * Called every ticks, removes the {@link PNJ} that must be removed, and every room that must lose, loses.
     */
    public void cleanRooms(){
        roomsPlaying.forEach(roomPlaying -> roomPlaying.pnjList.removeAll(roomPlaying.pnjToRemove));
        roomsRemove.forEach(room -> {
            Bukkit.getServer().broadcastMessage(ChatColor.RED + room.getRoomPlayer().getPlayerIfOnline().getDisplayName() +" a perdu !");
            Player p = room.getRoomPlayer().getPlayerIfOnline();
            p.sendMessage(ChatColor.RED+"Vous avez perdu !");
            room.lose();
        });
        roomsPlaying.removeAll(roomsRemove);
        roomsRemove.clear();
    }

    public int getRoomsPlayingCount(){
        return roomsPlaying.size();
    }


    /**
     * Check every PNJ and check its position if it's not moving anymore (stopped in front of the door / on the objective)
     */
    public void checkRoomsPNJ() {
        roomsPlaying.forEach(room -> room.pnjList.stream().filter(pnj -> !room.pnjToRemove.contains(pnj)).forEach(pnj->{
                if ((pnj.motX == 0) && (pnj.motZ == 0) && (pnj.getLife() > 10)) {
                    Location pnjLoc = new Location(Bukkit.getServer().getWorld("world"), pnj.locX, pnj.locY, pnj.locZ);
                    BlockPosition pnjPos = new BlockPosition(pnj.locX, pnj.locY, pnj.locZ);
                    BlockPosition objPos = new BlockPosition(pnj.getObjective().getBlockX() + 0.5, pnj.getObjective().getBlockY(), pnj.getObjective().getBlockZ() + 0.5);
                    if(compare(pnjPos, objPos, room, pnj) == 1) {
                        ParticleEffect.HEART.display(0.3f, 1, 0.3f, 1, 5, pnjLoc, 5);
                        room.getRoomPlayer().getPlayerIfOnline().playSound(pnjLoc, Sound.BLOCK_NOTE_HARP, 1, 1.5f);
                        room.getRoomPlayer().getPlayerIfOnline().playSound(pnjLoc, Sound.BLOCK_NOTE_HARP, 1, 1.7f);
                    }else if(compare(pnjPos, objPos, room, pnj) == 0) {
                        ParticleEffect.CLOUD.display(0.3f, 0.3f, 0.3f, 0.2f, 10, new Location(Bukkit.getServer().getWorld("world"), pnj.locX, pnj.locY, pnj.locZ), 5);
                        room.getRoomPlayer().getPlayerIfOnline().playSound(pnjLoc, Sound.BLOCK_NOTE_BASS, 1, 0.7f);
                        room.getRoomPlayer().getPlayerIfOnline().playSound(pnjLoc, Sound.BLOCK_NOTE_BASS, 1, 0.2f);
                    } else{
                        ParticleEffect.FLAME.display(0.3f, 1, 0.3f, 1, 5, pnjLoc, 5);
                        room.getRoomPlayer().getPlayerIfOnline().playSound(pnjLoc, Sound.BLOCK_NOTE_SNARE, 1, 0.7f);
                        room.getRoomPlayer().getPlayerIfOnline().playSound(pnjLoc, Sound.BLOCK_NOTE_SNARE, 1, 0.2f);
                    }
                    room.removePNJ(pnj);
                }
        }));
    }

    /**
     * Compares the pnj pos and the objective pos
     * @param pnjPos
     * @param objPos
     * @param room
     * @param pnj
     * @return true if the player did good, else false
     */
    private int compare(BlockPosition pnjPos, BlockPosition objPos, Room room, PNJ pnj){
        if (pnjPos.equals(objPos)) { // If it reached its destination
            if (pnj.isGood()) { // If he's white then we score a point
                room.score++;
                return 1;
            }
            else {
                room.errors++; // Else we score an error
                return -1;
            }
        } else { // If it didnt reach its destination
            if (pnj.isGood()) { // but he's white we score an error
                room.errors++;
                return 0;
            }else
                return 0;
        }
    }

    public EndlessPlayer getRoomPlayer(int roomID){
        return roomsPlaying.get(roomID).getRoomPlayer();
    }

    /**
     * Clears all the rooms (kills ALL the PNJs)
     */
    public void clearRooms(){
        roomsPlaying.forEach(room -> {
            room.pnjList.forEach(Entity::die);
            room.pnjToRemove.forEach(Entity::die);
            room.pnjList.clear();
            room.pnjToRemove.clear();
        });
    }

    public void updateRooms() {
        roomsPlaying.forEach(Room::updateRoom);
    }

    /**
     * Create the first room that serves as template to every next room
     * @param playerSpawn
     * @param originList
     * @param destinationList
     */
    public void createFirstRoom(Location playerSpawn, List<Location> originList, List<Location> destinationList) {
        firstRoom = new Room(playerSpawn, originList, destinationList);
        rooms.add(firstRoom);
    }

    /**
     * Creates a new room and move it by "zOffset" blocks
     * @param zOffset
     */
    public void duplicateRoom(int zOffset){
        rooms.add(firstRoom.duplicate(zOffset));
    }

    /**
     * Attach a player to a room
     * @param roomID
     * @param player
     */
    public void dispatchPlayer(int roomID, EndlessPlayer player){
        rooms.get(roomID).attachPlayer(player);
    }

    public int getVillagerSpawnCount(){
        return firstRoom.villagerSpawnPoints.size();
    }
}

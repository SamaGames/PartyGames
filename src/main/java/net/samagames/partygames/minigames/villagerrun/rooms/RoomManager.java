package net.samagames.partygames.minigames.villagerrun.rooms;

import net.minecraft.server.v1_9_R1.BlockPosition;
import net.minecraft.server.v1_9_R1.Entity;
import net.minecraft.server.v1_9_R1.World;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.villagerrun.VillagerRun;
import net.samagames.partygames.minigames.villagerrun.entities.NPC;
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
     * VillagerRun instance
     */
    private VillagerRun game;

    public RoomManager(VillagerRun game){
        this.game = game;
    }

    public void startGame(){
        roomsPlaying.addAll(rooms);
        roomsPlaying.forEach(Room::startGame);
    }

    public void addRoom(Room r){
        rooms.add(r);
    }

    /**
     * Spawns an either good or bad {@link NPC} at a specified spawn
     * @param spawnerID Location where it'll be spawned
     * @param isGood
     */
    public void spawnNPC(int spawnerID, boolean isGood){
        roomsPlaying.forEach(room -> {
            Location loc = room.villagerSpawnPoints.get(spawnerID);
            World mcWorld = ((CraftWorld) loc.getWorld()).getHandle();
            NPC npc = new NPC(mcWorld, room.fencesLocations.get(spawnerID), isGood);
            npc.setPositionRotation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            mcWorld.addEntity(npc);
            room.addNPC(npc);
        });
    }

    public void checkErrors(){
        roomsPlaying.forEach(room -> {if(room.errors > 2) roomsRemove.add(room);});
    }

    /**
     * Called every ticks, removes the {@link NPC} that must be removed, and every room that must lose, loses.
     */
    public void cleanRooms(){
        roomsPlaying.forEach(roomPlaying -> roomPlaying.npcList.removeAll(roomPlaying.npcToRemove));
        roomsRemove.forEach(room -> {
            Bukkit.getServer().broadcastMessage(ChatColor.RED + room.getRoomPlayer().getPlayerIfOnline().getDisplayName() +" a perdu !");
            Player p = room.getRoomPlayer().getPlayerIfOnline();
            p.sendMessage(ChatColor.RED+"Vous avez perdu !");
            room.lose();
        });
        roomsPlaying.removeAll(roomsRemove);
        if(roomsPlaying.size() == 2){
            game.addWinner(3, roomsRemove.get(0).getRoomPlayer());
        }
        else if(roomsPlaying.size() == 1){
            game.addWinner(2, roomsRemove.get(0).getRoomPlayer());
        }
        roomsRemove.clear();
    }

    public int getRoomsPlayingCount(){
        return roomsPlaying.size();
    }


    /**
     * Check every NPC and check its position if it's not moving anymore (stopped in front of the door / on the objective)
     */
    public void checkRoomsNPC() {
        roomsPlaying.forEach(room -> room.npcList.forEach(npc -> {
            if(room.npcToRemove.contains(npc))
                return;
            handleNPC(room, npc);
        }));
    }

    /**
     * Checking the NPC position and plays an animation depending on the result
     * @param room
     * @param npc
     */
    private void handleNPC(Room room, NPC npc){
        if(room.npcToRemove.contains(npc) || !npc.isAlive())
            return;
        if (Math.abs(npc.motX) < 0.001f && Math.abs(npc.motZ) < 0.001f) {
            Location npcLoc = new Location(Bukkit.getServer().getWorld("VillagerRun"), npc.locX, npc.locY, npc.locZ);
            BlockPosition npcPos = new BlockPosition(npc.locX, npc.locY, npc.locZ);
            BlockPosition objPos = new BlockPosition(npc.getObjective().getX(), npc.getObjective().getY(), npc.getObjective().getZ());
            if(objPos.getZ() - npcPos.getZ() > 2) {
                return;
            }
            int compare = compare(npcPos, objPos, room, npc);
            if(compare == 1) {
                ParticleEffect.HEART.display(0.3f, 1, 0.3f, 1, 5, npcLoc, 5);
                room.getRoomPlayer().getPlayerIfOnline().playSound(npcLoc, Sound.BLOCK_NOTE_HARP, 1, 1.5f);
                room.getRoomPlayer().getPlayerIfOnline().playSound(npcLoc, Sound.BLOCK_NOTE_HARP, 1, 1.7f);
            }else if(compare == 0) {
                ParticleEffect.CLOUD.display(0.3f, 0.3f, 0.3f, 0.2f, 10, npcLoc, 5);
                room.getRoomPlayer().getPlayerIfOnline().playSound(npcLoc, Sound.BLOCK_NOTE_BASS, 1, 0.7f);
                room.getRoomPlayer().getPlayerIfOnline().playSound(npcLoc, Sound.BLOCK_NOTE_BASS, 1, 0.2f);
            } else{
                ParticleEffect.FLAME.display(0.3f, 1, 0.3f, 1, 5, npcLoc, 5);
                room.getRoomPlayer().getPlayerIfOnline().playSound(npcLoc, Sound.BLOCK_NOTE_SNARE, 1, 0.7f);
                room.getRoomPlayer().getPlayerIfOnline().playSound(npcLoc, Sound.BLOCK_NOTE_SNARE, 1, 0.2f);
            }
            room.removeNPC(npc);
        }
    }

    /**
     * Compares the npc pos and the objective pos
     * @param npcPos
     * @param objPos
     * @param room
     * @param npc
     * @return true if the player did good, else false
     */
    private int compare(BlockPosition npcPos, BlockPosition objPos, Room room, NPC npc){
        if (npcPos.equals(objPos)) { // If it reached its destination
            if (npc.isGood()) { // If he's white then we score a point
                room.score++;
                return 1;
            }
            else {
                room.errors++; // Else we score an error
                return -1;
            }
        } else { // If it didnt reach its destination
            if (npc.isGood()) { // but he's white we score an error
                room.errors++;
                return 0;
            }else
                return 0;
        }
    }

    public PartyGamesPlayer getRoomPlayer(int roomID){
        return roomsPlaying.get(roomID).getRoomPlayer();
    }

    /**
     * Clears all the rooms (kills ALL the NPCs)
     */
    public void clearRooms(){
        roomsPlaying.forEach(room -> {
            room.getScoreboard().removeReceiver(room.getRoomPlayer().getPlayerIfOnline());
            room.npcList.forEach(Entity::die);
            room.npcToRemove.forEach(Entity::die);
            room.npcList.clear();
            room.npcToRemove.clear();
        });
    }

    public void updateRooms() {
        roomsPlaying.forEach(Room::updateRoom);
    }

    public int getVillagerSpawnCount(){
        return rooms.get(0).villagerSpawnPoints.size();
    }
}

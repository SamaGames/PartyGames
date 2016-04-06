package net.samagames.partygames;

import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntityTypes;
import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.entities.nms.NPC;
import net.samagames.partygames.game.PartyGames;
import net.samagames.tools.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Main extends JavaPlugin{

    @Override
    public void onEnable(){
        PartyGames game = new PartyGames(this);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            SamaGamesAPI.get().getGameManager().getGameProperties().getOptions()
                    .getAsJsonArray("worlds").forEach(world ->
                    Bukkit.createWorld(new WorldCreator(world.getAsString())));

            SamaGamesAPI.get().getGameManager().registerGame(game);
        });

        registerEntity("CustomVillager", 120, NPC.class);
    }

    /**
     * Using reflection to add a custom entity to Minecraft's entity list
     * @param name Name of the Entity
     * @param id id of the entity (120 is EntityVillager)
     * @param customClass {@link NPC} class
     */
    private void registerEntity(String name, int id, Class<? extends EntityInsentient> customClass){
        try {
            List<Map<?, ?>> dataMap = new ArrayList<>();
            for (Field f : EntityTypes.class.getDeclaredFields()){
                if (Map.class.isAssignableFrom(f.getType())){
                    f.setAccessible(true);
                    dataMap.add((Map<?, ?>) f.get(null));
                }
            }

            if (dataMap.get(2).containsKey(id)){
                dataMap.get(0).remove(name);
                dataMap.get(2).remove(id);
            }

            Method method = EntityTypes.class.getDeclaredMethod("a", Class.class, String.class, int.class);
            method.setAccessible(true);
            method.invoke(null, customClass, name, id);
        } catch (Exception e){
            Bukkit.getLogger().log(Level.SEVERE, "Erreur !", e);
        }


    }

}

package fr.keyzou.endlessgame;

import fr.keyzou.endlessgame.entities.EndlessPlayer;
import fr.keyzou.endlessgame.game.EndlessGame;
import fr.keyzou.endlessgame.entities.nms.PNJ;
import net.minecraft.server.v1_9_R1.EntityInsentient;
import net.minecraft.server.v1_9_R1.EntityTypes;
import net.samagames.api.SamaGamesAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by Dean on 28/03/2016.
 */
public class Main extends JavaPlugin{

    @Override
    public void onEnable(){
        EndlessGame game = new EndlessGame("id", "Endless Game", "Survivez à la série de mini-jeux !", EndlessPlayer.class, this);
        SamaGamesAPI.get().getGameManager().registerGame(game);
        registerEntity("CustomVillager", 120, PNJ.class);
    }

    /**
     * Using reflection to add a custom entity to Minecraft's entity list
     * @param name Name of the Entity
     * @param id id of the entity (120 is EntityVillager)
     * @param customClass {@link PNJ} class
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

package net.samagames.partygames;

import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

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
    }

}

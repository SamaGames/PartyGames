package net.samagames.partygames.minigames.blockdodger;

import com.google.gson.JsonObject;
import net.samagames.api.SamaGamesAPI;
import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.game.PartyGamesPlayer;
import net.samagames.partygames.minigames.MiniGame;

import java.util.HashMap;

public class BlockDodger extends MiniGame{

    public static final String NAME = "Block Dodger";
    public static final String DESCRIPTION = "Évitez les blocs d'or qui arrivent vers vous en vous décalant sur la gauche ou la droite. " +
            "Vous devez tenir pendant 30 secondes.";

    private HashMap<PartyGamesPlayer, JsonObject> rooms;

    public BlockDodger(PartyGames game) {
        super(NAME, DESCRIPTION, game);
    }

    @Override
    public void initGame() {
        rooms = new HashMap<>();

        game.getRegisteredGamePlayers().values().forEach(player ->
            rooms.put(player, SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs()
                    .getAsJsonArray("games.blockdodger.rooms")
                    .get(player.getRoomId()).getAsJsonObject())
        );
    }

    @Override
    public void startGame() {
        game.getRegisteredGamePlayers().values().forEach(player ->
            player.getPlayerIfOnline()
                    .teleport(net.samagames.tools.LocationUtils.str2loc(rooms.get(player)
                    .get("spawn").getAsString()))
        );
    }

    @Override
    public void endGame() {

    }

}

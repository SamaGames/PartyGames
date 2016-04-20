package net.samagames.partygames.minigames.thedrawer;

import net.samagames.partygames.game.PartyGames;
import net.samagames.partygames.minigames.MiniGame;

public class TheDrawer extends MiniGame{

    public static final String NAME = "The Drawer";
    public static final String DESCRIPTION = "Vous allez devoir reproduire un modèle consitué de blocs de laine de différentes couleurs " +
            "généré aléatoirement. Vous n'avez que 20 secondes pour ce faire, alors faites vite !";

    //private HashMap<PartyGamesPlayer, BlockDodgerRoom> rooms;
    //private Timer timer;
    //private BlockDodgerTask task;

    public TheDrawer(PartyGames game) {
        super(NAME, DESCRIPTION, game);
    }

    @Override
    public void initGame() {
        //rooms = new HashMap<>();

        /*game.getRegisteredGamePlayers().values().forEach(player -> {
            BlockDodgerRoom room = new BlockDodgerRoom(SamaGamesAPI.get().getGameManager().getGameProperties().getConfigs()
                    .getAsJsonObject("games").getAsJsonObject("blockdodger").getAsJsonArray("rooms")
                    .get(player.getRoomId()).getAsJsonObject());
            rooms.put(player, room);

            player.getPlayerIfOnline().teleport(room.getSpawn());
            room.clearBlocks();
        });*/
    }

    @Override
    public void startGame() {
        game.getPlugin().getLogger().fine("The Drawer game starting.");

        /*Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "\nLe mini-jeu commence ! Évitez les blocs qui arrivent vers vous.");

        timer = new Timer(30);
        timer.runTaskTimer(game.getPlugin(), 0L, 20L);

        task = new BlockDodgerTask(this);
        task.runTaskTimer(game.getPlugin(), 0L, 5L);*/
    }

    @Override
    public void endGame() {
        /*timer.cancel();
        task.cancel();

        for(PartyGamesPlayer player : rooms.keySet()) {
            BlockDodgerRoom room = rooms.get(player);

            if(room.isActive()) {
                room.setActive(false);
                room.clearBlocks();

                player.givePoints((int) (timer.getInitialTime() / 0.3));

                Bukkit.broadcastMessage(ChatColor.YELLOW + "Le temps est écoulé.");
                player.getPlayerIfOnline().sendMessage(ChatColor.GOLD + "+ " + timer.getInitialTime() + " points");
            }
        }*/

        shouldEnd = true;
    }

    /*public Timer getTimer() {
        return timer;
    }

    public HashMap<PartyGamesPlayer, BlockDodgerRoom> getRooms() {
        return rooms;
    }*/
}

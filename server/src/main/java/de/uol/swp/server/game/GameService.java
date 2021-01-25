package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.game.message.CreateGameMessage;
import de.uol.swp.common.game.message.NextPlayerMessage;
import de.uol.swp.common.game.message.UpdateInventoryMessage;
import de.uol.swp.common.game.request.EndTurnRequest;
import de.uol.swp.common.game.request.UpdateInventoryRequest;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.lobby.LobbyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapping EventBus calls to GameManagement calls
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @see AbstractService
 * @since 2021-01-15
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class GameService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(GameService.class);

    private final GameManagement gameManagement;

    private final LobbyService lobbyService;




    /**
     * Constructor
     *
     * @param bus            The EventBus used throughout the entire server (injected)
     * @param gameManagement The ChatManagement to use (injected)
     * @param lobbyService   The LobbyService to use (injected)
     * @since 2021-01-15
     */
    @Inject
    public GameService(EventBus bus, GameManagement gameManagement, LobbyService lobbyService) {
        super(bus);
        this.gameManagement = gameManagement;
        this.lobbyService = lobbyService;
    }

    /**
     * Handles a EndTurnRequest found on the EventBus
     * <p>
     * If a EndTurnRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to change to current active player.
     *
     * @param msg The EndTurnRequest found on the EventBus
     * @see de.uol.swp.common.game.request.EndTurnRequest
     * @see de.uol.swp.common.game.message.NextPlayerMessage
     * @since 2021-01-15
     */
    @Subscribe
    private void onEndTurnRequest(EndTurnRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(msg.getOriginLobby() + ": User " + msg.getUser().getUsername() + " wants to end his turn.");
        }
        try {
            Game game = gameManagement.getGame(msg.getOriginLobby());
            ServerMessage returnMessage = new NextPlayerMessage(msg.getOriginLobby(), game.nextPlayer());
            lobbyService.sendToAllInLobby(msg.getOriginLobby(), returnMessage);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * Handles a CreateGameMessage found on the EventBus
     * <p>
     * If a CreateGameMessage is detected on the Eventbus, this method is called.
     * It then requests the GameManagement to create a game.
     *
     * @param message The CreateGameMessage
     * @since 2021-01-24
     */
    @Subscribe
    private void onCreateGameMessage(CreateGameMessage message) {
        gameManagement.createGame(message.getLobby(), message.getFirst());
    }

    /**
     * Handles a UpdateInventoryRequest found on the EventBus
     * <p>
     * It searches the inventories in the current game for the one that belongs
     * to the player sending the request. It then gets the latest ressource
     * variables out of said inventory.
     *
     * @author Sven Ahrens
     * @author Finn Haase
     * @param msg The UpdateInventoryRequest
     * @since 2021-01-25
     */
    @Subscribe
    private void onUpdateInventoryRequest(UpdateInventoryRequest msg) {
        Game game = gameManagement.getGame(msg.getOriginLobby());
        Inventory[] inventories = game.getInventories();
        Inventory inventory = null;
        for (int i=0; i<= game.getInventories().length;i++) {
            if( inventories[i].getPlayer() == msg.getUser()) {
                inventory = inventories[i];
                break;
            }

        }
        int brick = inventory.getBrick();
        int grain = inventory.getGrain();
        int lumber = inventory.getLumber();
        int ore = inventory.getOre();
        int wool = inventory.getWool();

        int victoryPointCards = inventory.getVictoryPointCards();

        boolean longestRoad = inventory.isLongestRoad();
        boolean largestArmy = inventory.isLargestArmy();

        List ressourceList = new ArrayList();
        ressourceList.add(brick);
        ressourceList.add(grain);
        ressourceList.add(lumber);
        ressourceList.add(ore);
        ressourceList.add(wool);

        ressourceList.add(victoryPointCards);

        ressourceList.add(longestRoad);
        ressourceList.add(largestArmy);

        ServerMessage returnMassage = new UpdateInventoryMessage(game);

    }
}

package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.game.message.CreateGameMessage;
import de.uol.swp.common.game.message.NextPlayerMessage;
import de.uol.swp.common.game.request.EndTurnRequest;
import de.uol.swp.common.game.request.UpdateInventoryRequest;
import de.uol.swp.common.lobby.response.UpdateInventoryResponse;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.lobby.LobbyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
     * to the player sending the request. It then posts a UpdateInventoryResponse
     * that contains all the user's items, saved in a resourceMap for
     * counted items (bricks, grain, etc.) and a armyAndRoadMap which
     * contains the boolean attributes longestRoad and largestArmy.
     *
     * @param msg The UpdateInventoryRequest found on the EventBus
     * @author Sven Ahrens
     * @author Finn Haase
     * @since 2021-01-25
     */
    @Subscribe
    private void onUpdateInventoryRequest(UpdateInventoryRequest msg) {
        Game game = gameManagement.getGame(msg.getOriginLobby());
        Inventory[] inventories = game.getInventories();
        Inventory inventory = null;
        for (int i = 0; i < inventories.length; i++) {
            if (inventories[i].getPlayer().equals(msg.getUser())) {
                inventory = inventories[i];
                System.out.println(inventory.getPlayer());
                break;
            }
        }
        if (inventory != null) {
            Map<String, Integer> resourceMap = new HashMap<>();
            resourceMap.put("Brick", inventory.getBrick());
            resourceMap.put("Grain", inventory.getGrain());
            resourceMap.put("Lumber", inventory.getLumber());
            resourceMap.put("Ore", inventory.getOre());
            resourceMap.put("Wool", inventory.getWool());
            resourceMap.put("Victory Point Cards", inventory.getVictoryPointCards());
            resourceMap.put("Knight Cards", inventory.getKnightCards());
            resourceMap.put("Road Building Cards", inventory.getRoadBuildingCards());
            resourceMap.put("Year of Plenty Cards", inventory.getYearOfPlentyCards());
            resourceMap.put("Monopoly Cards", inventory.getMonopolyCards());

            Map<String, Boolean> armyAndRoadMap = new HashMap<>();
            armyAndRoadMap.put("Largest Army", inventory.isLargestArmy());
            armyAndRoadMap.put("Longest Road", inventory.isLongestRoad());

            AbstractResponseMessage returnMessage = new UpdateInventoryResponse(msg.getUser(), msg.getOriginLobby(), Collections.unmodifiableMap(resourceMap), Collections.unmodifiableMap(armyAndRoadMap));
            if (msg.getMessageContext().isPresent()) {
                returnMessage.setMessageContext(msg.getMessageContext().get());
            }
            post(returnMessage);
        }
    }
}

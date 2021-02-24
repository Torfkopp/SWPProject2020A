package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.Inventory;
import de.uol.swp.common.game.message.CreateGameMessage;
import de.uol.swp.common.game.message.NextPlayerMessage;
import de.uol.swp.common.game.message.TradeWithUserOfferMessage;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.InventoryForTradeWithUserResponse;
import de.uol.swp.common.lobby.request.TradeWithUserRequest;
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
 * @see de.uol.swp.server.AbstractService
 * @since 2021-01-15
 */
@SuppressWarnings("UnstableApiUsage")
public class GameService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(GameService.class);

    private final IGameManagement gameManagement;
    private final LobbyService lobbyService;

    /**
     * Constructor
     *
     * @param bus            The EventBus used throughout the entire server (injected)
     * @param gameManagement The ChatManagement to use (injected)
     * @param lobbyService   The LobbyService to use (injected)
     *
     * @since 2021-01-15
     */
    @Inject
    public GameService(EventBus bus, IGameManagement gameManagement, LobbyService lobbyService) {
        super(bus);
        LOG.debug("GameService started");
        this.gameManagement = gameManagement;
        this.lobbyService = lobbyService;
    }

    /**
     * Helper function
     * <p>
     * Checks if there are enough resources in the needed Inventory.
     * It compares the needed inventory with the actual inventory.
     *
     * @param inventoryMap       Saved inventory in game
     * @param neededInventoryMap Trading inventory
     *
     * @return if there are enough resources in the neededInventoryMap
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @since 2021-02-24
     */
    private boolean checkEnoughResourcesInInventory(Map<String, Integer> inventoryMap,
                                                    Map<String, Integer> neededInventoryMap) {
        if (inventoryMap.get("grain") <= neededInventoryMap.get("grain")) return false;
        else if (inventoryMap.get("ore") <= neededInventoryMap.get("ore")) return false;
        else if (inventoryMap.get("wool") <= neededInventoryMap.get("wool")) return false;
        else if (inventoryMap.get("brick") <= neededInventoryMap.get("brick")) return false;
        else if (inventoryMap.get("lumber") <= neededInventoryMap.get("grain")) return false;
        else return true;
    }

    /**
     * Handles a AcceptUserTradeRequest found on the EventBus
     * <p>
     * If there is a AcceptUserTradeRequest on the EventBus, this method
     * checks if there are enough resources available in the inventorys
     * to make a trade between the 2 users.
     * //todo doku
     *
     * @param req AcceptUserTradeRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.AcceptUserTradeRequest
     * @since 2021-02-24
     */
    @Subscribe
    private void onAcceptUserTradeRequest(AcceptUserTradeRequest req) {
        System.out.println("Du tauscht mit dem Spieler namens" + req.getOfferingUser());
        if (LOG.isDebugEnabled()) LOG.debug("Received TradeWithUserRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory[] inventories = game.getInventories();
        Inventory offeringInventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().getUsername().equals(req.getOfferingUser())) {
                offeringInventory = value;
                break;
            }
        }
        Inventory respondingInventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().getUsername().equals(req.getRespondingUser())) {
                respondingInventory = value;
                break;
            }
        }
        Map<String, Integer> offeringInventoryMap = new HashMap<>();
        offeringInventoryMap.put("brick", offeringInventory.getBrick());
        offeringInventoryMap.put("ore", offeringInventory.getOre());
        offeringInventoryMap.put("lumber", offeringInventory.getLumber());
        offeringInventoryMap.put("wool", offeringInventory.getWool());
        offeringInventoryMap.put("grain", offeringInventory.getGrain());
        Map<String, Integer> responseInventoryMap = new HashMap<>();
        responseInventoryMap.put("brick", respondingInventory.getBrick());
        responseInventoryMap.put("ore", respondingInventory.getOre());
        responseInventoryMap.put("lumber", respondingInventory.getLumber());
        responseInventoryMap.put("wool", respondingInventory.getWool());
        responseInventoryMap.put("grain", respondingInventory.getGrain());
        if (checkEnoughResourcesInInventory(offeringInventoryMap,
                                            req.getOfferingResourceMap()) && checkEnoughResourcesInInventory(
                responseInventoryMap, req.getRespondingResourceMap())) {
            System.out.println("Hast genug Resourcen");
            //todo resourcen Tausch
            //todo response
        } else System.out.println("Hast nicht genug Resourcen");
    }

    /**
     * Handles a CreateGameMessage found on the EventBus
     * <p>
     * If a CreateGameMessage is detected on the Eventbus, this method is called.
     * It then requests the GameManagement to create a game.
     *
     * @param msg The CreateGameMessage
     *
     * @since 2021-01-24
     */
    @Subscribe
    private void onCreateGameMessage(CreateGameMessage msg) {
        if (LOG.isDebugEnabled()) LOG.debug("Received CreateGameMessage for Lobby " + msg.getLobbyName());
        gameManagement.createGame(msg.getLobby(), msg.getFirst());
    }

    /**
     * Handles a EndTurnRequest found on the EventBus
     * <p>
     * If a EndTurnRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to change to current active player.
     *
     * @param req The EndTurnRequest found on the EventBus
     *
     * @see de.uol.swp.common.game.request.EndTurnRequest
     * @see de.uol.swp.common.game.message.NextPlayerMessage
     * @since 2021-01-15
     */
    @Subscribe
    private void onEndTurnRequest(EndTurnRequest req) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received EndTurnRequest for Lobby " + req.getOriginLobby());
            LOG.debug("---- " + "User " + req.getUser().getUsername() + " wants to end his turn.");
        }
        try {
            Game game = gameManagement.getGame(req.getOriginLobby());
            ServerMessage returnMessage = new NextPlayerMessage(req.getOriginLobby(), game.nextPlayer());
            lobbyService.sendToAllInLobby(req.getOriginLobby(), returnMessage);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * Handles a OfferingTradeWithUserRequest found on the EventBus
     * <p>
     * If there is a OfferingTradeWithUserRequest found on the EventBus,
     * a TradeWithUserOfferMessage is posted onto the EventBus containing
     * both users, the lobby, the resourceMap of the respondingUser
     * and the two maps containing information of the trade.
     *
     * @param req OfferingTradeWithUserRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.OfferingTradeWithUserRequest
     * @see de.uol.swp.common.game.message.TradeWithUserOfferMessage
     * @since 2021-02-24
     */
    @Subscribe
    private void onOfferingTradeWithUserRequest(OfferingTradeWithUserRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received OfferingTradeWithUserRequest for Lobby " + req.getOriginLobby());
        System.out.println(req.getRespondingUser());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory[] inventories = game.getInventories();
        Inventory respondingInventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().getUsername().equals(req.getRespondingUser())) {
                respondingInventory = value;
            }
        }
        Map<String, Integer> resourceMap = new HashMap<>();
        resourceMap.put("brick", respondingInventory.getBrick());
        resourceMap.put("grain", respondingInventory.getGrain());
        resourceMap.put("lumber", respondingInventory.getLumber());
        resourceMap.put("ore", respondingInventory.getOre());
        resourceMap.put("wool", respondingInventory.getWool());

        LOG.debug("Sending a TradeWithUserOfferMessage to lobby" + req.getOriginLobby());
        ServerMessage returnMessage = new TradeWithUserOfferMessage(req.getOfferingUser(), req.getRespondingUser(),
                                                                    req.getOriginLobby(), resourceMap,
                                                                    req.getOfferingResourceMap(),
                                                                    req.getRespondingResourceMap());
        post(returnMessage);
    }

    /**
     * Handles a TradeWithUserRequest found on the EventBus
     * <p>
     * It searches the inventories in the current game for the one that belongs
     * to the player sending the request and gets the amount of resource cards
     * the trading user has in the inventory. It then posts a InventoryForTradeWithUserResponse
     * that contains all the user's resources, saved in a resourceMap for
     * counted items (bricks, grain, etc.) .
     *
     * @param req The TradeWithBankRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.response.InventoryForTradeWithUserResponse
     * @since 2021-02-23
     */
    @Subscribe
    private void onTradeWithUserRequest(TradeWithUserRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received TradeWithUserRequest for Lobby " + req.getName());
        Game game = gameManagement.getGame(req.getName());
        Inventory[] inventories = game.getInventories();
        Inventory inventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().equals(req.getUser())) {
                inventory = value;
                break;
            }
        }
        Inventory traderInventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().getUsername().equals(req.getTradingUser())) {
                traderInventory = value;
                break;
            }
        }
        int traderInventorySize = traderInventory.getResourceAmount();
        if (inventory != null) {
            Map<String, Integer> resourceMap = new HashMap<>();
            resourceMap.put("brick", inventory.getBrick());
            resourceMap.put("grain", inventory.getGrain());
            resourceMap.put("lumber", inventory.getLumber());
            resourceMap.put("ore", inventory.getOre());
            resourceMap.put("wool", inventory.getWool());
            AbstractResponseMessage returnMessage = new InventoryForTradeWithUserResponse(req.getUser(), req.getName(),
                                                                                          Collections.unmodifiableMap(
                                                                                                  resourceMap),
                                                                                          traderInventorySize,
                                                                                          req.getTradingUser());
            LOG.debug("Sent a InventoryForTradeWithUserResponse for Lobby " + req.getName());
            returnMessage.initWithMessage(req);
            post(returnMessage);
        }
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
     * @param req The UpdateInventoryRequest found on the EventBus
     *
     * @author Sven Ahrens
     * @author Finn Haase
     * @since 2021-01-25
     */
    @Subscribe
    private void onUpdateInventoryRequest(UpdateInventoryRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received UpdateInventoryRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory[] inventories = game.getInventories();
        Inventory inventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().equals(req.getUser())) {
                inventory = value;
                break;
            }
        }
        if (inventory != null) {
            Map<String, Integer> resourceMap = new HashMap<>();
            resourceMap.put("brick", inventory.getBrick());
            resourceMap.put("grain", inventory.getGrain());
            resourceMap.put("lumber", inventory.getLumber());
            resourceMap.put("ore", inventory.getOre());
            resourceMap.put("wool", inventory.getWool());
            resourceMap.put("cards.victorypoints", inventory.getVictoryPointCards());
            resourceMap.put("cards.knights", inventory.getKnightCards());
            resourceMap.put("cards.roadbuilding", inventory.getRoadBuildingCards());
            resourceMap.put("cards.yearofplenty", inventory.getYearOfPlentyCards());
            resourceMap.put("cards.monopoly", inventory.getMonopolyCards());

            Map<String, Boolean> armyAndRoadMap = new HashMap<>();
            armyAndRoadMap.put("cards.unique.largestarmy", inventory.isLargestArmy());
            armyAndRoadMap.put("cards.unique.longestroad", inventory.isLongestRoad());

            AbstractResponseMessage returnMessage = new UpdateInventoryResponse(req.getUser(), req.getOriginLobby(),
                                                                                Collections
                                                                                        .unmodifiableMap(resourceMap),
                                                                                Collections.unmodifiableMap(
                                                                                        armyAndRoadMap));
            if (req.getMessageContext().isPresent()) {
                returnMessage.setMessageContext(req.getMessageContext().get());
            }
            post(returnMessage);
        }
    }
}

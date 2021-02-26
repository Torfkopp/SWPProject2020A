package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.Inventory;
import de.uol.swp.common.game.message.CreateGameMessage;
import de.uol.swp.common.game.message.NextPlayerMessage;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.*;
import de.uol.swp.common.lobby.request.TradeWithUserRequest;
import de.uol.swp.common.lobby.response.UpdateInventoryResponse;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.message.ResponseMessage;
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
        if (inventoryMap.get("grain") < neededInventoryMap.get("grain")) return false;
        else if (inventoryMap.get("ore") < neededInventoryMap.get("ore")) return false;
        else if (inventoryMap.get("wool") < neededInventoryMap.get("wool")) return false;
        else if (inventoryMap.get("brick") < neededInventoryMap.get("brick")) return false;
        else return inventoryMap.get("lumber") >= neededInventoryMap.get("lumber");
    }

    /**
     * Helper method to make a resourceMap from a provided inventory
     *
     * @param inventory The inventory to make a resourceMap from
     *
     * @return The Map of resources
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @since 2021-02-25
     */
    private Map<String, Integer> getResourceMapFromInventory(Inventory inventory) {
        Map<String, Integer> offeringInventoryMap = new HashMap<>();
        offeringInventoryMap.put("brick", inventory.getBrick());
        offeringInventoryMap.put("ore", inventory.getOre());
        offeringInventoryMap.put("lumber", inventory.getLumber());
        offeringInventoryMap.put("wool", inventory.getWool());
        offeringInventoryMap.put("grain", inventory.getGrain());
        return offeringInventoryMap;
    }

    /**
     * Handles a AcceptUserTradeRequest found on the EventBus
     * <p>
     * If there is a AcceptUserTradeRequest on the EventBus, this method
     * checks if there are enough resources available in the inventories
     * to make a trade between the 2 users.
     * If there are enough resources this method creates a
     * TradeOfUsersAcceptedResponse and sends it with a GetUserSessionEvent
     * to direct the Response to the right client.
     * Otherwise a InvalidTradeOfUsersResponse is posted onto the EventBus.
     *
     * @param req The AcceptUserTradeRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.AcceptUserTradeRequest
     * @see de.uol.swp.common.game.response.TradeOfUsersAcceptedResponse
     * @see de.uol.swp.common.game.response.InvalidTradeOfUsersResponse
     * @see de.uol.swp.server.game.GetUserSessionEvent
     * @since 2021-02-24
     */
    @Subscribe
    private void onAcceptUserTradeRequest(AcceptUserTradeRequest req) {
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
        if (offeringInventory == null || respondingInventory == null) return;
        Map<String, Integer> offeringInventoryMap = getResourceMapFromInventory(offeringInventory);
        Map<String, Integer> responseInventoryMap = getResourceMapFromInventory(respondingInventory);
        if (checkEnoughResourcesInInventory(offeringInventoryMap,
                                            req.getOfferingResourceMap()) && checkEnoughResourcesInInventory(
                responseInventoryMap, req.getRespondingResourceMap())) {
            //changes the inventories according to the offer
            if (req.getOfferingResourceMap().get("grain") > 0) {
                offeringInventory.setGrain(offeringInventory.getGrain() - req.getOfferingResourceMap().get("grain"));
                respondingInventory
                        .setGrain(respondingInventory.getGrain() + req.getOfferingResourceMap().get("grain"));
            }
            if (req.getOfferingResourceMap().get("ore") > 0) {
                offeringInventory.setOre(offeringInventory.getOre() - req.getOfferingResourceMap().get("ore"));
                respondingInventory.setOre(respondingInventory.getOre() + req.getOfferingResourceMap().get("ore"));
            }
            if (req.getOfferingResourceMap().get("lumber") > 0) {
                offeringInventory.setLumber(offeringInventory.getLumber() - req.getOfferingResourceMap().get("lumber"));
                respondingInventory
                        .setLumber(respondingInventory.getLumber() + req.getOfferingResourceMap().get("lumber"));
            }
            if (req.getOfferingResourceMap().get("wool") > 0) {
                offeringInventory.setWool(offeringInventory.getWool() - req.getOfferingResourceMap().get("wool"));
                respondingInventory.setWool(respondingInventory.getWool() + req.getOfferingResourceMap().get("wool"));
            }
            if (req.getOfferingResourceMap().get("brick") > 0) {
                offeringInventory.setBrick(offeringInventory.getBrick() - req.getOfferingResourceMap().get("brick"));
                respondingInventory
                        .setBrick(respondingInventory.getBrick() + req.getOfferingResourceMap().get("brick"));
            }

            //changes the inventories according to the wanted resources
            if (req.getRespondingResourceMap().get("grain") > 0) {
                offeringInventory.setGrain(offeringInventory.getGrain() + req.getRespondingResourceMap().get("grain"));
                respondingInventory
                        .setGrain(respondingInventory.getGrain() - req.getRespondingResourceMap().get("grain"));
            }
            if (req.getRespondingResourceMap().get("ore") > 0) {
                offeringInventory.setOre(offeringInventory.getOre() + req.getRespondingResourceMap().get("ore"));
                respondingInventory.setOre(respondingInventory.getOre() - req.getRespondingResourceMap().get("ore"));
            }
            if (req.getRespondingResourceMap().get("lumber") > 0) {
                offeringInventory
                        .setLumber(offeringInventory.getLumber() + req.getRespondingResourceMap().get("lumber"));
                respondingInventory
                        .setLumber(respondingInventory.getLumber() - req.getRespondingResourceMap().get("lumber"));
            }
            if (req.getRespondingResourceMap().get("wool") > 0) {
                offeringInventory.setWool(offeringInventory.getWool() + req.getRespondingResourceMap().get("wool"));
                respondingInventory.setWool(respondingInventory.getWool() - req.getRespondingResourceMap().get("wool"));
            }
            if (req.getRespondingResourceMap().get("brick") > 0) {
                offeringInventory.setBrick(offeringInventory.getBrick() + req.getRespondingResourceMap().get("brick"));
                respondingInventory
                        .setBrick(respondingInventory.getBrick() - req.getRespondingResourceMap().get("brick"));
            }
            ResponseMessage returnMessage = new TradeOfUsersAcceptedResponse(req.getOriginLobby());
            LOG.debug("Preparing a TradeOfUsersAcceptedResponse for Lobby " + req.getOriginLobby());
            post(new GetUserSessionEvent(offeringInventory.getPlayer(), returnMessage));
            returnMessage.initWithMessage(req);
            post(returnMessage);
        } else {
            ResponseMessage returnMessage = new InvalidTradeOfUsersResponse(req.getOriginLobby(),
                                                                            offeringInventory.getPlayer());
            LOG.debug("Sent a InvalidTradeOfUsersResponse for Lobby " + req.getOriginLobby());
            returnMessage.initWithMessage(req);
            post(returnMessage);
        }
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
     * a new GetUserSessionEvent is posted onto the EventBus containing
     * the respondingUser and a new TradeWithUserOfferResponse wich contains
     * both users, the lobby, the resourceMap of the respondingUser
     * and the two maps containing information of the trade.
     *
     * @param req The OfferingTradeWithUserRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.OfferingTradeWithUserRequest
     * @see de.uol.swp.common.game.response.TradeWithUserOfferResponse
     * @see de.uol.swp.server.game.GetUserSessionEvent
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
        if (respondingInventory == null) return;
        Map<String, Integer> resourceMap = getResourceMapFromInventory(respondingInventory);

        LOG.debug("Sending a TradeWithUserOfferMessage to lobby" + req.getOriginLobby());
        ResponseMessage offerResponse = new TradeWithUserOfferResponse(req.getOfferingUser(), req.getOriginLobby(),
                                                                       resourceMap, req.getOfferingResourceMap(),
                                                                       req.getRespondingResourceMap(),
                                                                       respondingInventory.getPlayer());
        post(new GetUserSessionEvent(respondingInventory.getPlayer(), offerResponse));
    }

    /**
     * Handles a ResetOfferTradeButtonRequest found on the EventBus
     * If a ResetOfferTradeButtonRequest is found on the EventBus,
     * a new GetUserSessionEvent is posted onto the EventBus containing
     * the user and a new ResetOfferTradeButtonResponse wich contains
     * the lobby name.
     *
     * @param req The ResetOfferTradeButtonRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @since 2021-02-25
     */
    @Subscribe
    private void onResetOfferTradeButtonRequest(ResetOfferTradeButtonRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received ResetOfferTradeButtonRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory[] inventories = game.getInventories();
        Inventory offeringInventory = null;
        for (Inventory value : inventories) {
            if (value.getPlayer().getUsername().equals(req.getOfferingUserName())) {
                offeringInventory = value;
                break;
            }
        }
        if (offeringInventory == null) return;
        ResponseMessage returnMessage = new ResetOfferTradeButtonResponse(req.getOriginLobby());
        post(new GetUserSessionEvent(offeringInventory.getPlayer(), returnMessage));
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
     * @see de.uol.swp.common.lobby.request.TradeWithUserRequest
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
            if (value.getPlayer().getUsername().equals(req.getRespondingUser())) {
                traderInventory = value;
                break;
            }
        }
        if (inventory == null || traderInventory == null) return;
        int traderInventorySize = traderInventory.getResourceAmount();
        Map<String, Integer> offeringResourceMap = getResourceMapFromInventory(inventory);
        ResponseMessage returnMessage = new InventoryForTradeWithUserResponse(req.getUser(), req.getName(), Collections
                .unmodifiableMap(offeringResourceMap), traderInventorySize, req.getRespondingUser());
        LOG.debug("Sent a InventoryForTradeWithUserResponse for Lobby " + req.getName());
        returnMessage.initWithMessage(req);
        post(returnMessage);
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
        if (inventory == null) return;
        Map<String, Integer> resourceMap = getResourceMapFromInventory(inventory);
        resourceMap.put("cards.victorypoints", inventory.getVictoryPointCards());
        resourceMap.put("cards.knights", inventory.getKnightCards());
        resourceMap.put("cards.roadbuilding", inventory.getRoadBuildingCards());
        resourceMap.put("cards.yearofplenty", inventory.getYearOfPlentyCards());
        resourceMap.put("cards.monopoly", inventory.getMonopolyCards());

        Map<String, Boolean> armyAndRoadMap = new HashMap<>();
        armyAndRoadMap.put("cards.unique.largestarmy", inventory.isLargestArmy());
        armyAndRoadMap.put("cards.unique.longestroad", inventory.isLongestRoad());

        AbstractResponseMessage returnMessage = new UpdateInventoryResponse(req.getUser(), req.getOriginLobby(),
                                                                            Collections.unmodifiableMap(resourceMap),
                                                                            Collections
                                                                                    .unmodifiableMap(armyAndRoadMap));
        returnMessage.initWithMessage(req);
        post(returnMessage);
    }
}

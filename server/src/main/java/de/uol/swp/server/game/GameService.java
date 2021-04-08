package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.message.SystemMessageForPlayingCardsMessage;
import de.uol.swp.common.chat.message.SystemMessageForTradeMessage;
import de.uol.swp.common.chat.message.SystemMessageForTradeWithBankMessage;
import de.uol.swp.common.chat.response.SystemMessageForTradeWithBankResponse;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.Inventory;
import de.uol.swp.common.game.map.*;
import de.uol.swp.common.game.map.configuration.IConfiguration;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.game.response.*;
import de.uol.swp.common.lobby.message.LobbyDeletedMessage;
import de.uol.swp.common.lobby.message.LobbyExceptionMessage;
import de.uol.swp.common.lobby.message.StartSessionMessage;
import de.uol.swp.common.lobby.request.KickUserRequest;
import de.uol.swp.common.message.ExceptionMessage;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.event.CreateGameInternalRequest;
import de.uol.swp.server.game.event.ForwardToUserInternalRequest;
import de.uol.swp.server.game.event.KickUserEvent;
import de.uol.swp.server.lobby.LobbyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

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
    //TODO: Set to false while trading
    private boolean buildingCurrentlyAllowed;

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
     * @return true if there are enough resources in the neededInventoryMap, false if not
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
     * Handles an AcceptUserTradeRequest found on the EventBus
     * <p>
     * If an AcceptUserTradeRequest is found on the EventBus, this method
     * checks if there are enough resources available in the inventories
     * to make a trade between the 2 users.
     * If there are enough resources this method creates a
     * TradeOfUsersAcceptedResponse and sends it with a ForwardToUserInternalRequest
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
     * @see de.uol.swp.server.game.event.ForwardToUserInternalRequest
     * @since 2021-02-24
     */
    @Subscribe
    private void onAcceptUserTradeRequest(AcceptUserTradeRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received TradeWithUserRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory offeringInventory = game.getInventory(req.getOfferingUser());
        Inventory respondingInventory = game.getInventory(req.getRespondingUser());
        if (offeringInventory == null || respondingInventory == null) return;
        Map<String, Integer> offeringInventoryMap = getResourceMapFromInventory(offeringInventory);
        Map<String, Integer> responseInventoryMap = getResourceMapFromInventory(respondingInventory);
        if (checkEnoughResourcesInInventory(offeringInventoryMap,
                                            req.getOfferingResourceMap()) && checkEnoughResourcesInInventory(
                responseInventoryMap, req.getRespondingResourceMap())) {
            //changes the inventories according to the offer
            Map<I18nWrapper, Integer> offeredResourcesWrapperMap = new HashMap<>();
            Map<I18nWrapper, Integer> respondingResourcesWrapperMap = new HashMap<>();
            if (req.getOfferingResourceMap().get("grain") > 0) {
                offeredResourcesWrapperMap
                        .put(new I18nWrapper("game.resources.grain"), req.getOfferingResourceMap().get("grain"));
                offeringInventory.increaseGrain(-req.getOfferingResourceMap().get("grain"));
                respondingInventory.increaseGrain(req.getOfferingResourceMap().get("grain"));
            }
            if (req.getOfferingResourceMap().get("ore") > 0) {
                offeredResourcesWrapperMap
                        .put(new I18nWrapper("game.resources.ore"), req.getOfferingResourceMap().get("ore"));
                offeringInventory.increaseOre(-req.getOfferingResourceMap().get("ore"));
                respondingInventory.increaseOre(req.getOfferingResourceMap().get("ore"));
            }
            if (req.getOfferingResourceMap().get("lumber") > 0) {
                offeredResourcesWrapperMap
                        .put(new I18nWrapper("game.resources.lumber"), req.getOfferingResourceMap().get("lumber"));
                offeringInventory.increaseLumber(-req.getOfferingResourceMap().get("lumber"));
                respondingInventory.increaseLumber(req.getOfferingResourceMap().get("lumber"));
            }
            if (req.getOfferingResourceMap().get("wool") > 0) {
                offeredResourcesWrapperMap
                        .put(new I18nWrapper("game.resources.wool"), req.getOfferingResourceMap().get("wool"));
                offeringInventory.increaseWool(-req.getOfferingResourceMap().get("wool"));
                respondingInventory.increaseWool(req.getOfferingResourceMap().get("wool"));
            }
            if (req.getOfferingResourceMap().get("brick") > 0) {
                offeredResourcesWrapperMap
                        .put(new I18nWrapper("game.resources.brick"), req.getOfferingResourceMap().get("brick"));
                offeringInventory.increaseBrick(-req.getOfferingResourceMap().get("brick"));
                respondingInventory.increaseBrick(req.getOfferingResourceMap().get("brick"));
            }
            if (offeredResourcesWrapperMap.isEmpty())
                offeredResourcesWrapperMap.put(new I18nWrapper("game.trade.offer.nothing"), 0);

            //changes the inventories according to the wanted resources
            if (req.getRespondingResourceMap().get("grain") > 0) {
                respondingResourcesWrapperMap
                        .put(new I18nWrapper("game.resources.grain"), req.getRespondingResourceMap().get("grain"));
                offeringInventory.increaseGrain(req.getRespondingResourceMap().get("grain"));
                respondingInventory.increaseGrain(-req.getRespondingResourceMap().get("grain"));
            }
            if (req.getRespondingResourceMap().get("ore") > 0) {
                respondingResourcesWrapperMap
                        .put(new I18nWrapper("game.resources.ore"), req.getRespondingResourceMap().get("ore"));
                offeringInventory.increaseOre(req.getRespondingResourceMap().get("ore"));
                respondingInventory.increaseOre(-req.getRespondingResourceMap().get("ore"));
            }
            if (req.getRespondingResourceMap().get("lumber") > 0) {
                respondingResourcesWrapperMap
                        .put(new I18nWrapper("game.resources.lumber"), req.getRespondingResourceMap().get("lumber"));
                offeringInventory.increaseLumber(req.getRespondingResourceMap().get("lumber"));
                respondingInventory.increaseLumber(-req.getRespondingResourceMap().get("lumber"));
            }
            if (req.getRespondingResourceMap().get("wool") > 0) {
                respondingResourcesWrapperMap
                        .put(new I18nWrapper("game.resources.wool"), req.getRespondingResourceMap().get("wool"));
                offeringInventory.increaseWool(req.getRespondingResourceMap().get("wool"));
                respondingInventory.increaseWool(-req.getRespondingResourceMap().get("wool"));
            }
            if (req.getRespondingResourceMap().get("brick") > 0) {
                respondingResourcesWrapperMap
                        .put(new I18nWrapper("game.resources.brick"), req.getRespondingResourceMap().get("brick"));
                offeringInventory.increaseBrick(req.getRespondingResourceMap().get("brick"));
                respondingInventory.increaseBrick(-req.getRespondingResourceMap().get("brick"));
            }
            if (respondingResourcesWrapperMap.isEmpty())
                respondingResourcesWrapperMap.put(new I18nWrapper("game.trade.offer.nothing"), 0);

            ServerMessage returnSystemMessage = new SystemMessageForTradeMessage(req.getOriginLobby(),
                                                                                 req.getOfferingUser(),
                                                                                 req.getRespondingUser().getUsername(),
                                                                                 offeredResourcesWrapperMap,
                                                                                 respondingResourcesWrapperMap);
            LOG.debug("Sending SystemMessageForTradeMessage for Lobby " + req.getOriginLobby());
            lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);
            ResponseMessage returnMessage = new TradeOfUsersAcceptedResponse(req.getOriginLobby());
            LOG.debug("Preparing a TradeOfUsersAcceptedResponse for Lobby " + req.getOriginLobby());
            post(new ForwardToUserInternalRequest(req.getOfferingUser(), returnMessage));
            returnMessage.initWithMessage(req);
            LOG.debug("Sending a TradeOfUsersAcceptedResponse for Lobby " + req.getOriginLobby());
            post(returnMessage);
            ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getOfferingUser(),
                                                             game.getCardAmounts());
            LOG.debug("Sending RefreshCardAmountMessage for Lobby " + req.getOriginLobby());
            lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
        } else {
            ResponseMessage returnMessage = new InvalidTradeOfUsersResponse(req.getOriginLobby(),
                                                                            req.getRespondingUser());
            LOG.debug("Sending an InvalidTradeOfUsersResponse for Lobby " + req.getOriginLobby());
            returnMessage.initWithMessage(req);
            post(returnMessage);
        }
    }

    /**
     * Handles a BuildRequest found on the bus
     * <p>
     * If a BuildRequest is found on the bus this method tries to build something
     * at the specified MapPoint
     *
     * @param req The Build Request
     *
     * @author Aldin Dervisi
     * @author Temmo Junkhoff
     * @since 2021-04-07
     */
    @Subscribe
    private void onBuildRequest(BuildRequest req) {
        LOG.debug("Received BuildRequest for Lobby " + req.getOriginLobby());
        if (!buildingCurrentlyAllowed) return;
        Game game = gameManagement.getGame(req.getOriginLobby());
        IGameMap gameMap = game.getMap();
        MapPoint mapPoint = req.getMapPoint();
        UserOrDummy user = req.getUser();
        Player player = game.getPlayer(user);
        Inventory inv = game.getInventory(user);
        switch (mapPoint.getType()) {
            case INTERSECTION:
                if (gameMap.settlementPlaceable(player, mapPoint)) {
                    if (inv.getBrick() >= 1 && inv.getLumber() >= 1 && inv.getWool() >= 1 && inv.getGrain() >= 1) {
                        inv.increaseBrick(-1);
                        inv.increaseLumber(-1);
                        inv.increaseWool(-1);
                        inv.increaseGrain(-1);
                        gameMap.placeSettlement(player, mapPoint);
                        LOG.debug("Sending BuildingSuccessfulMessage");
                        lobbyService.sendToAllInLobby(req.getOriginLobby(),
                                                      new BuildingSuccessfulMessage(req.getOriginLobby(), user,
                                                                                    mapPoint,
                                                                                    BuildingSuccessfulMessage.Type.SETTLEMENT));
                    } else {
                        LOG.debug("Sending BuildingFailedResponse");
                        BuildingFailedResponse msg = new BuildingFailedResponse(req.getOriginLobby(),
                                                                                BuildingFailedResponse.Reason.NOT_ENOUGH_RESOURCES);
                        msg.initWithMessage(req);
                        post(msg);
                    }
                } else if (gameMap.settlementUpgradeable(player, mapPoint)) {
                    if (inv.getOre() >= 3 && inv.getGrain() >= 2) {
                        inv.increaseOre(-3);
                        inv.increaseGrain(-2);
                        gameMap.upgradeSettlement(player, mapPoint);
                        LOG.debug("Sending BuildingSuccessfulMessage");
                        lobbyService.sendToAllInLobby(req.getOriginLobby(),
                                                      new BuildingSuccessfulMessage(req.getOriginLobby(), user,
                                                                                    mapPoint,
                                                                                    BuildingSuccessfulMessage.Type.CITY));
                    } else {
                        LOG.debug("Sending BuildingFailedResponse");
                        BuildingFailedResponse msg = new BuildingFailedResponse(req.getOriginLobby(),
                                                                                BuildingFailedResponse.Reason.NOT_ENOUGH_RESOURCES);
                        msg.initWithMessage(req);
                        post(msg);
                    }
                } else {
                    LOG.debug("Sending BuildingFailedResponse");
                    BuildingFailedResponse msg = new BuildingFailedResponse(req.getOriginLobby(),
                                                                            BuildingFailedResponse.Reason.CANT_BUILD_HERE);
                    msg.initWithMessage(req);
                    post(msg);
                }
                break;
            case EDGE:
                if (gameMap.roadPlaceable(player, mapPoint)) {
                    if (inv.getBrick() >= 1 && inv.getLumber() >= 1) {
                        inv.increaseBrick(-1);
                        inv.increaseLumber(-1);
                        gameMap.placeRoad(player, mapPoint);
                        LOG.debug("Sending BuildingSuccessfulMessage");
                        lobbyService.sendToAllInLobby(req.getOriginLobby(),
                                                      new BuildingSuccessfulMessage(req.getOriginLobby(), user,
                                                                                    mapPoint,
                                                                                    BuildingSuccessfulMessage.Type.ROAD));
                    } else {
                        LOG.debug("Sending BuildingFailedResponse");
                        BuildingFailedResponse msg = new BuildingFailedResponse(req.getOriginLobby(),
                                                                                BuildingFailedResponse.Reason.NOT_ENOUGH_RESOURCES);
                        msg.initWithMessage(req);
                        post(msg);
                    }
                } else {
                    LOG.debug("Sending BuildingFailedResponse");
                    BuildingFailedResponse msg = new BuildingFailedResponse(req.getOriginLobby(),
                                                                            BuildingFailedResponse.Reason.CANT_BUILD_HERE);
                    msg.initWithMessage(req);
                    post(msg);
                }
                break;
            case HEX:
            case INVALID:
                LOG.debug("Sending BuildingFailedResponse");
                BuildingFailedResponse msg = new BuildingFailedResponse(req.getOriginLobby(),
                                                                        BuildingFailedResponse.Reason.CANT_BUILD_HERE);
                msg.initWithMessage(req);
                post(msg);
                break;
        }
    }

    /**
     * Handles a BuyDevelopmentCard found on the event bus
     * <p>
     * If a BuyDevelopmentCard is found on the event bus, this method checks
     * if there are development cards to sell available in the bankInventory.
     * If there is at least one card, a random card gets chosen and if the
     * user has enough resources, he gets the new card(happens in helper method).
     * Afterwards a new BuyDevelopmentCardResponse is posted onto the event bus.
     *
     * @param req The request found on the event bus
     *
     * @author Maximilian Lindner
     * @author Alwin Bossert
     * @see de.uol.swp.common.game.request.BuyDevelopmentCardRequest
     * @see de.uol.swp.common.game.response.BuyDevelopmentCardResponse
     * @since 2021-02-22
     */
    @Subscribe
    private void onBuyDevelopmentCardRequest(BuyDevelopmentCardRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received BuyDevelopmentCardRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        List<String> bankInventory = game.getBankInventory();
        if (bankInventory != null && bankInventory.size() > 0) {
            Random random = new Random(); // new Random object, named random
            int randInt = random.nextInt(bankInventory.size());
            String developmentCard = bankInventory.get(randInt);
            if (updatePlayersInventoryWithDevelopmentCard(developmentCard, req.getUser(), req.getOriginLobby())) {
                bankInventory.remove(randInt);
                ResponseMessage returnMessage = new BuyDevelopmentCardResponse(req.getUser(), req.getOriginLobby(),
                                                                               developmentCard);
                returnMessage.initWithMessage(req);
                LOG.debug("Sending a BuyDevelopmentCardResponse for Lobby " + req.getOriginLobby());
                post(returnMessage);
                ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(),
                                                                 game.getCardAmounts());
                LOG.debug("Sending RefreshCardAmountMessage for Lobby " + req.getOriginLobby());
                lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
            } else LOG.debug("In the lobby " + req.getOriginLobby() + " the User " + req.getUser()
                                                                                        .getUsername() + "couldn't buy a development Card");
        }
    }

    /**
     * Handles a CheckVictoryPointsRequest found on the EventBus
     * If a CheckVictoryPointsRequest is found on the EventBus, this method
     * checks if the player has 10 or more victory points. If he has 10 ore more
     * victory points, a new PlayerWonGameMessage is sent to all lobby members
     * and the GameManagement drops the game.
     *
     * @param req The CheckVictoryPointsRequest found on the EventBus
     *
     * @author Steven Luong
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.CheckVictoryPointsRequest
     * @see de.uol.swp.common.game.message.PlayerWonGameMessage
     * @since 2021-03-22
     */
    @Subscribe
    private void onCheckVictoryPointsRequest(CheckVictoryPointsRequest req) {
        Game game = gameManagement.getGame(req.getOriginLobby());
        int vicPoints = game.calculateVictoryPoints(game.getPlayer(req.getUser()));
        if (vicPoints >= 10) {
            ServerMessage message = new PlayerWonGameMessage(req.getOriginLobby(), req.getUser());
            lobbyService.sendToAllInLobby(req.getOriginLobby(), message);
            gameManagement.dropGame(req.getOriginLobby());
        }
    }

    /**
     * Handles a CreateGameInternalRequest found on the EventBus
     * <p>
     * If a CreateGameInternalRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to create a game. Afterwards, it sets up
     * the map of the game according to the settings of the lobby.
     *
     * @param msg The CreateGameInternalRequest found on the EventBus
     *
     * @see de.uol.swp.server.game.event.CreateGameInternalRequest
     * @since 2021-01-24
     */
    @Subscribe
    private void onCreateGameInternalRequest(CreateGameInternalRequest msg) {
        String lobbyName = msg.getLobby().getName();
        if (LOG.isDebugEnabled()) LOG.debug("Received CreateGameInternalRequest for Lobby " + lobbyName);
        try {
            IGameMap gameMap = new GameMap();
            IConfiguration configuration;
            if (msg.getLobby().randomPlayfieldEnabled()) {
                configuration = gameMap.getRandomisedConfiguration();
            } else {
                configuration = gameMap.getBeginnerConfiguration();
            }
            gameMap = gameMap.createMapFromConfiguration(configuration);
            if (!msg.getLobby().startUpPhaseEnabled()) {
                gameMap.makeBeginnerSettlementsAndRoads(msg.getLobby().getUserOrDummies().size());
            } // TODO: handle founder phase
            gameManagement.createGame(msg.getLobby(), msg.getFirst(), gameMap);
            LOG.debug("Sending GameCreatedMessage");
            post(new GameCreatedMessage(msg.getLobby().getName(), msg.getFirst()));
            LOG.debug("Sending StartSessionMessage for Lobby " + lobbyName);
            StartSessionMessage message = new StartSessionMessage(lobbyName, msg.getFirst(), configuration,
                                                                  msg.getLobby().startUpPhaseEnabled());
            lobbyService.sendToAllInLobby(lobbyName, message);
        } catch (IllegalArgumentException e) {
            ExceptionMessage exceptionMessage = new ExceptionMessage(e.getMessage());
            exceptionMessage.initWithMessage(msg);
            LOG.debug("Sending ExceptionMessage");
            post(exceptionMessage);
        }
    }

    /**
     * Handles an EditInventoryRequest found on the EventBus
     * <p>
     * This method changes the amount of the requested resource by the amount
     * specified in the request or inverts the ownership status of unique
     * cards like "Largest Army" or "Longest Road".
     *
     * @param req The EditInventoryRequest found on the EventBus
     *
     * @implNote The amount is ignored for "Largest Army" and "Longest Road",
     * as those are only saved as boolean. For them, the ownership is inverted
     * instead. So if "Largest Army" is requested and the player doesn't own
     * the card, they will own it after this method is done (and vice versa).
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.common.game.request.EditInventoryRequest
     * @since 2021-03-07
     */
    @Subscribe
    private void onEditInventoryRequest(EditInventoryRequest req) {
        LOG.debug("Received EditInventoryRequest");
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory inventory = game.getInventory(req.getUser());
        switch (req.getResource().toLowerCase()) {
            case "bricks":
            case "brick":
                inventory.increaseBrick(req.getAmount());
                break;
            case "grains":
            case "grain":
                inventory.increaseGrain(req.getAmount());
                break;
            case "ore":
                inventory.increaseOre(req.getAmount());
                break;
            case "lumber":
                inventory.increaseLumber(req.getAmount());
                break;
            case "wool":
                inventory.increaseWool(req.getAmount());
                break;
            case "knightcard":
            case "kc":
                inventory.increaseKnightCards(req.getAmount());
                break;
            case "knight":
            case "knights":
                inventory.increaseKnights(req.getAmount());
                break;
            case "monopolycard":
            case "mc":
                inventory.increaseMonopolyCards(req.getAmount());
                break;
            case "roadbuildingcard":
            case "rbc":
                inventory.increaseRoadBuildingCards(req.getAmount());
                break;
            case "victorypointcard":
            case "vpc":
                inventory.increaseVictoryPointCards(req.getAmount());
                break;
            case "victorypoints":
            case "vp":
                inventory.setVictoryPoints(inventory.getVictoryPoints() + req.getAmount());
                break;
            case "yearofplentycard":
            case "yearofplenty":
            case "yopc":
                inventory.increaseYearOfPlentyCards(req.getAmount());
                break;
            case "largestarmy":
            case "la":
                inventory.setLargestArmy(!inventory.isLargestArmy());
                break;
            case "longestroad":
            case "lr":
                inventory.setLongestRoad(!inventory.isLongestRoad());
                break;
        }
        inventory = game.getInventory(req.getUser());
        Map<String, Integer> resourceMap = getResourceMapFromInventory(inventory);
        resourceMap.put("cards.victorypoints", inventory.getVictoryPointCards());
        resourceMap.put("cards.knight", inventory.getKnightCards());
        resourceMap.put("cards.roadbuilding", inventory.getRoadBuildingCards());
        resourceMap.put("cards.yearofplenty", inventory.getYearOfPlentyCards());
        resourceMap.put("cards.monopoly", inventory.getMonopolyCards());

        Map<String, Boolean> armyAndRoadMap = new HashMap<>();
        armyAndRoadMap.put("cards.unique.largestarmy", inventory.isLargestArmy());
        armyAndRoadMap.put("cards.unique.longestroad", inventory.isLongestRoad());

        ResponseMessage returnMessage = new UpdateInventoryResponse(req.getUser(), req.getOriginLobby(),
                                                                    Collections.unmodifiableMap(resourceMap),
                                                                    Collections.unmodifiableMap(armyAndRoadMap));
        LOG.debug("Sending ForwardToUserInternalRequest containing UpdateInventoryResponse");
        post(new ForwardToUserInternalRequest(req.getUser(), returnMessage));
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
    }

    /**
     * Handles a EndTurnRequest found on the EventBus
     * <p>
     * If a EndTurnRequest is detected on the EventBus, this method is called.
     * It then sends a NextPlayerMessage to all members in the lobby.
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
        Game game = gameManagement.getGame(req.getOriginLobby());
        UserOrDummy nextPlayer = game.nextPlayer();
        buildingCurrentlyAllowed = false;
        ServerMessage returnMessage = new NextPlayerMessage(req.getOriginLobby(), nextPlayer);
        LOG.debug("Sending NextPlayerMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnMessage);

        if (nextPlayer instanceof User) {
        } else { //Dummy
            onRollDiceRequest(new RollDiceRequest(nextPlayer, req.getOriginLobby()));
            onEndTurnRequest(new EndTurnRequest(nextPlayer, req.getOriginLobby()));
        }
    }

    /**
     * Handles a KickUserRequest found on the EventBus
     * <p>
     * If a KickUserRequest is detected on the EventBus this method
     * checks if a game has already started in this lobby.
     * If not, a KickUserEvent is posted onto the EventBus.
     * Otherwise a LobbyExceptionMessage ist posted onto the EventBus.
     *
     * @param req KickUserRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Sven Ahrens
     * @see de.uol.swp.common.lobby.request.KickUserRequest
     * @see de.uol.swp.server.game.event.KickUserEvent
     * @since 2021-03-02
     */
    @Subscribe
    private void onKickUserRequest(KickUserRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received KickUserRequest for Lobby " + req.getName());
        if (gameManagement.getGames().containsKey(req.getName())) {
            ExceptionMessage exceptionMessage = new LobbyExceptionMessage("Can not kick while a game is ongoing");
            exceptionMessage.initWithMessage(req);
            LOG.debug("Sending ExceptionMessage");
            LOG.debug(exceptionMessage.getException());
            post(exceptionMessage);
        } else {
            LOG.debug("Sending KickUserEvent");
            post(new KickUserEvent(req));
        }
    }

    /**
     * Handles a LobbyDeletedMessage found on the EventBus
     * <p>
     * If a LobbyDeletedMessage is found on the EventBus this method drops the
     * game associated with the Lobby, if one existed.
     *
     * @param msg The LobbyDeletedMessage found on the EventBus
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @author Phillip-André-Suhr
     * @since 2021-03-01
     */
    @Subscribe
    private void onLobbyDeletedMessage(LobbyDeletedMessage msg) {
        Game game = gameManagement.getGame(msg.getName());
        if (game == null) return;
        try {
            gameManagement.dropGame(msg.getName());
        } catch (IllegalArgumentException e) {
            ExceptionMessage exceptionMessage = new ExceptionMessage(e.getMessage());
            exceptionMessage.initWithMessage(msg);
            LOG.debug("Sending ExceptionMessage");
            post(exceptionMessage);
        }
    }

    /**
     * Handles an OfferingTradeWithUserRequest found on the EventBus
     * <p>
     * If an OfferingTradeWithUserRequest is found on the EventBus,
     * a new ForwardToUserInternalRequest is posted onto the EventBus containing
     * the respondingUser and a new TradeWithUserOfferResponse which in turn
     * contains both users, the lobby, the resourceMap of the respondingUser,
     * and the two maps containing information of the trade.
     *
     * @param req The OfferingTradeWithUserRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.OfferingTradeWithUserRequest
     * @see de.uol.swp.common.game.response.TradeWithUserOfferResponse
     * @see de.uol.swp.server.game.event.ForwardToUserInternalRequest
     * @since 2021-02-24
     */
    @Subscribe
    private void onOfferingTradeWithUserRequest(OfferingTradeWithUserRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received OfferingTradeWithUserRequest for Lobby " + req.getOriginLobby());
        if (!(req.getRespondingUser() instanceof User)) {
            post(new ResetOfferTradeButtonRequest(req.getOriginLobby(), req.getOfferingUser()));
            return;
        }
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory respondingInventory = game.getInventory(game.getPlayer(req.getRespondingUser()));
        if (respondingInventory == null) return;
        Map<String, Integer> resourceMap = getResourceMapFromInventory(respondingInventory);

        LOG.debug("Sending a TradeWithUserOfferMessage to lobby" + req.getOriginLobby());
        ResponseMessage offerResponse = new TradeWithUserOfferResponse(req.getOfferingUser(), req.getRespondingUser(),
                                                                       resourceMap, req.getOfferingResourceMap(),
                                                                       req.getRespondingResourceMap(),
                                                                       req.getOriginLobby());
        post(new ForwardToUserInternalRequest(req.getRespondingUser(), offerResponse));
    }

    /**
     * Handles a PlayKnightCardRequest found on the EventBus
     * <p>
     * If a PlayKnightCardRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param req The PlayKnightCardRequest found on the EventBus
     *
     * @see de.uol.swp.common.game.request.PlayCardRequest.PlayKnightCardRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onPlayKnightCardRequest(PlayKnightCardRequest req) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received KnightCardPlayedMessage for Lobby " + req.getOriginLobby());
            LOG.debug("---- " + req.getUser().getUsername() + " wants to improve the army");
        }

        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory inv = game.getInventory(req.getUser());

        if (inv.getKnightCards() == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending a PlayCardFailureResponse");
            LOG.debug("---- Not enough Knight cards");
            return;
        }
        inv.setKnights(inv.getKnights() + 1);
        inv.increaseKnightCards(-1);

        I18nWrapper knightCard = new I18nWrapper("game.resources.cards.knight");
        ServerMessage returnSystemMessage = new SystemMessageForPlayingCardsMessage(req.getOriginLobby(), req.getUser(),
                                                                                    knightCard);
        LOG.debug("Sending SystemMessageForPlayingCardsMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);

        ResponseMessage returnMessage = new PlayCardSuccessResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        LOG.debug("Sending a PlayCardSuccessResponse");
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
    }

    /**
     * Handles a PlayMonopolyCardRequest found on the EventBus
     * <p>
     * If a PlayMonopolyCardRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param req The PlayMonopolyCardRequest found on the EventBus
     *
     * @see de.uol.swp.common.game.request.PlayCardRequest.PlayMonopolyCardRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onPlayMonopolyCardRequest(PlayMonopolyCardRequest req) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received MonopolyCardPlayedMessage for Lobby " + req.getOriginLobby());
            LOG.debug("---- " + req.getUser().getUsername() + " wants to monopolise " + req.getResource());
        }

        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory invMono = game.getInventory(req.getUser());

        if (invMono.getMonopolyCards() == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending a PlayCardFailureResponse");
            LOG.debug("---- Not enough Monopoly cards");
            return;
        }
        Inventory[] inventories = game.getAllInventories();

        switch (req.getResource()) {
            case ORE:
                for (Inventory inv : inventories)
                    if (inv.getOre() > 0) {
                        inv.increaseOre(-1);
                        invMono.increaseOre(1);
                    }
                break;
            case WOOL:
                for (Inventory inv : inventories)
                    if (inv.getWool() > 0) {
                        inv.increaseWool(-1);
                        invMono.increaseWool(1);
                    }
                break;
            case BRICK:
                for (Inventory inv : inventories)
                    if (inv.getBrick() > 0) {
                        inv.increaseBrick(-1);
                        invMono.increaseBrick(1);
                    }
                break;
            case GRAIN:
                for (Inventory inv : inventories)
                    if (inv.getGrain() > 0) {
                        inv.increaseGrain(-1);
                        invMono.increaseGrain(1);
                    }
                break;
            case LUMBER:
                for (Inventory inv : inventories)
                    if (inv.getLumber() > 0) {
                        inv.increaseLumber(-1);
                        invMono.increaseLumber(1);
                    }
                break;
        }

        invMono.increaseMonopolyCards(-1);

        I18nWrapper monopolyCard = new I18nWrapper("game.resources.cards.monopoly");
        ServerMessage returnSystemMessage = new SystemMessageForPlayingCardsMessage(req.getOriginLobby(), req.getUser(),
                                                                                    monopolyCard);
        LOG.debug("Sending SystemMessageForPlayingCardsMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);
        ResponseMessage returnMessage = new PlayCardSuccessResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        LOG.debug("Sending a PlayCardSuccessResponse");
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
    }

    /**
     * Handles a PlayRoadBuildingCardRequest found on the EventBus
     * <p>
     * If a PlayRoadBuildingCardRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param req The PlayRoadBuildingCardRequest found on the EventBus
     *
     * @see de.uol.swp.common.game.request.PlayCardRequest.PlayRoadBuildingCardRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onPlayRoadBuildingCardRequest(PlayRoadBuildingCardRequest req) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received RoadBuildingCardPlayedMessage for Lobby " + req.getOriginLobby());
            LOG.debug("---- " + req.getUser().getUsername() + " wants to build a road");
        }

        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory inv = game.getInventory(req.getUser());

        if (inv.getRoadBuildingCards() == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending a PlayCardFailureResponse");
            LOG.debug("---- Not enough RoadBuilding cards");
            return;
        }
        //TODO: Implementierung

        inv.increaseRoadBuildingCards(-1);

        I18nWrapper roadBuildingCard = new I18nWrapper("game.resources.cards.roadbuilding");
        ServerMessage returnSystemMessage = new SystemMessageForPlayingCardsMessage(req.getOriginLobby(), req.getUser(),
                                                                                    roadBuildingCard);
        LOG.debug("Sending SystemMessageForPlayingCardsMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);

        ResponseMessage returnMessage = new PlayCardSuccessResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        LOG.debug("Sending a PlayCardSuccessResponse");
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
    }

    /**
     * Handles a PlayYearOfPlentyCardRequest found on the EventBus
     * <p>
     * If a PlayYearOfPlentyCardRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param req The PlayYearOfPlentyCardRequest found on the EventBus
     *
     * @see de.uol.swp.common.game.request.PlayCardRequest.PlayYearOfPlentyCardRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onPlayYearOfPlentyCardRequest(PlayYearOfPlentyCardRequest req) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received YearOfPlentyCardPlayedMessage for Lobby " + req.getOriginLobby());
            LOG.debug("---- " + req.getUser().getUsername() + " wants " + req.getResource1() + " and " + req
                    .getResource2());
        }

        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory inv = game.getInventory(req.getUser());

        if (inv.getYearOfPlentyCards() == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending a PlayCardFailureResponse");
            LOG.debug("---- Not enough YearOfPlenty cards");
            return;
        }

        switch (req.getResource1()) {
            case ORE:
                inv.increaseOre(1);
                break;
            case WOOL:
                inv.increaseWool(1);
                break;
            case BRICK:
                inv.increaseBrick(1);
                break;
            case GRAIN:
                inv.increaseGrain(1);
                break;
            case LUMBER:
                inv.increaseLumber(1);
                break;
        }

        switch (req.getResource2()) {
            case ORE:
                inv.increaseOre(1);
                break;
            case WOOL:
                inv.increaseWool(1);
                break;
            case BRICK:
                inv.increaseBrick(1);
                break;
            case GRAIN:
                inv.increaseGrain(1);
                break;
            case LUMBER:
                inv.increaseLumber(1);
                break;
        }
        inv.increaseYearOfPlentyCards(-1);

        I18nWrapper yearOfPlentyCard = new I18nWrapper("game.resources.cards.yearofplenty");
        ServerMessage returnSystemMessage = new SystemMessageForPlayingCardsMessage(req.getOriginLobby(), req.getUser(),
                                                                                    yearOfPlentyCard);
        LOG.debug("Sending SystemMessageForPlayingCardsMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);

        ResponseMessage returnMessage = new PlayCardSuccessResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        LOG.debug("Sending a PlayCardSuccessResponse");
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
    }

    /**
     * Handles a ResetOfferTradeButtonRequest found on the EventBus
     * <p>
     * If a ResetOfferTradeButtonRequest is found on the EventBus,
     * a new ForwardToUserInternalRequest is posted onto the EventBus containing
     * the user and a new ResetOfferTradeButtonResponse which contains
     * the lobby name.
     *
     * @param req The ResetOfferTradeButtonRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.ResetOfferTradeButtonRequest
     * @see de.uol.swp.common.game.response.ResetOfferTradeButtonResponse
     * @see de.uol.swp.server.game.event.ForwardToUserInternalRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onResetOfferTradeButtonRequest(ResetOfferTradeButtonRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received ResetOfferTradeButtonRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory offeringInventory = game.getInventory(req.getOfferingUser());
        if (offeringInventory == null) return;
        ResponseMessage returnMessage = new ResetOfferTradeButtonResponse(req.getOriginLobby());
        LOG.debug("Sending ResetOfferTradeButtonResponse for Lobby " + req.getOriginLobby());
        post(new ForwardToUserInternalRequest(req.getOfferingUser(), returnMessage));
    }

    /**
     * Handles a RollDiceRequest found on the EventBus
     * If a RollDiceRequest is detected on the EventBus, this method is called.
     * It then sends a DiceCastMessage to all members in the lobby.
     *
     * @param req The RollDiceRequest found on the EventBus
     *
     * @see de.uol.swp.common.game.request.RollDiceRequest
     * @see de.uol.swp.common.game.message.DiceCastMessage
     * @since 2021-02-22
     */
    @Subscribe
    private void onRollDiceRequest(RollDiceRequest req) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Received RollDiceRequest for Lobby " + req.getOriginLobby());
            LOG.debug("---- " + "User " + req.getUser().getUsername() + " wants to roll the dices.");
        }
        Game game = gameManagement.getGame(req.getOriginLobby());
        buildingCurrentlyAllowed = true;
        int[] result = Game.rollDice();
        int numberOfPips = result[0] + result[1];
        if (numberOfPips == 7) {
            //Robber things
            LOG.debug("---- Robber things");
        } else {
            LOG.debug("---- Distributing the resources for token " + numberOfPips);
            game.distributeResources(numberOfPips);
        }
        ServerMessage returnMessage = new DiceCastMessage(req.getOriginLobby(), req.getUser(), result[0], result[1]);
        LOG.debug("Sending DiceCastMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
    }

    /**
     * Handles a TradeWithBankRequest found on the EventBus
     * <p>
     * It searches the inventories in the current game for the one that belongs
     * to the player sending the request. It then posts an InventoryForTradeResponse
     * that contains all the user's resources, saved in a resourceMap for
     * counted items (bricks, grain, etc.).
     *
     * @param req The TradeWithBankRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Alwin Bossert
     * @see de.uol.swp.common.game.request.TradeWithBankRequest
     * @see de.uol.swp.common.game.response.InventoryForTradeResponse
     * @since 2021-02-21
     */
    @Subscribe
    private void onTradeWithBankRequest(TradeWithBankRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received TradeWithBankRequest for Lobby " + req.getName());
        Inventory inventory = gameManagement.getGame(req.getName()).getInventory(req.getUser());

        if (inventory == null) return;
        Map<String, Integer> resourceMap = getResourceMapFromInventory(inventory);

        ResponseMessage returnMessage = new InventoryForTradeResponse(req.getUser(), req.getName(),
                                                                      Collections.unmodifiableMap(resourceMap));
        returnMessage.initWithMessage(req);
        LOG.debug("Sending InventoryForTradeResponse for Lobby " + req.getName());
        post(returnMessage);
    }

    /**
     * Handles a TradeWithUserCancelRequest found on the EventBus
     * <p>
     * If a TradeWithUserCancelRequest is detected on the EventBus, this
     * method creates a TradeWithUserCancelResponse to close the responding
     * trade window of the responding user of the request.
     * Therefore a ForwardToUserInternalRequest is needed.
     *
     * @param req TradeWithUserCancelRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.TradeWithUserCancelRequest
     * @see de.uol.swp.common.game.response.TradeWithUserCancelResponse
     * @see de.uol.swp.server.game.event.ForwardToUserInternalRequest
     * @since 2021-02-28
     */
    @Subscribe
    private void onTradeWithUserCancelRequest(TradeWithUserCancelRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received TradeWithUserCancelRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory respondingInventory = game.getInventory(req.getRespondingUser());

        if (respondingInventory == null) return;
        ResponseMessage returnMessageForOfferingUser = new TradeWithUserCancelResponse(req.getOriginLobby(),
                                                                                       game.getActivePlayer());
        returnMessageForOfferingUser.initWithMessage(req);
        LOG.debug("Sending a TradeWithUserCancelResponse for lobby" + req.getOriginLobby());
        post(returnMessageForOfferingUser);
        ResponseMessage returnMessageForRespondingUser = new TradeWithUserCancelResponse(req.getOriginLobby(),
                                                                                         game.getActivePlayer());
        LOG.debug("Sending a TradeWithUserCancelResponse for Lobby " + req.getOriginLobby());
        post(new ForwardToUserInternalRequest(req.getRespondingUser(), returnMessageForRespondingUser));
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
     * @see de.uol.swp.common.game.request.TradeWithUserRequest
     * @since 2021-02-23
     */
    @Subscribe
    private void onTradeWithUserRequest(TradeWithUserRequest req) {
        LOG.debug("Received TradeWithUserRequest for Lobby " + req.getName());
        Game game = gameManagement.getGame(req.getName());
        Inventory inventory = game.getInventory(req.getUser());
        Inventory traderInventory = game.getInventory(req.getRespondingUser());
        if (inventory == null || traderInventory == null) return;
        int traderInventorySize = traderInventory.getResourceAmount();
        Map<String, Integer> offeringResourceMap = getResourceMapFromInventory(inventory);
        ResponseMessage returnMessage = new InventoryForTradeWithUserResponse(req.getUser(), req.getName(), Collections
                .unmodifiableMap(offeringResourceMap), traderInventorySize, req.getRespondingUser());
        LOG.debug("Sending a InventoryForTradeWithUserResponse for Lobby " + req.getName());
        returnMessage.initWithMessage(req);
        post(returnMessage);
    }

    /**
     * Handles an UpdateGameMapRequest found on the bus
     * <p>
     * If an UpdateGameMapRequest is found on the bus this method responds with an UpdateGameMapResponse
     *
     * @param req The UpdateGameMapRequest
     *
     * @author Aldin Dervisi
     * @author Temmo Junkhoff
     * @since 2021-04-07
     */
    @Subscribe
    private void onUpdateGameMapRequest(UpdateGameMapRequest req) {
        LOG.debug("Received UpdateGameMapRequest");
        Game game = gameManagement.getGame(req.getOriginLobby());
        if (game == null) return;
        LOG.debug("Sending UpdateGameMapresponse");
        UpdateGameMapResponse rsp = new UpdateGameMapResponse(req.getOriginLobby(), game.getMap().getGameMapDTO());
        rsp.initWithMessage(req);
        post(rsp);
    }

    /**
     * Handles a UpdateInventoryAfterTradeWithBankRequest found on the EventBus
     * <p>
     * If a UpdateInventoryAfterTradeWithBankRequest is found on the EventBus this method updates the inventory
     * of the player who traded with the bank. If the User has enough resources, the resource he wants to trade gets -4
     * and the resource he wants gets +1. It then posts a TradeWithBankAcceptedResponse onto the EventBus.
     *
     * @param req The UpdateInventoryAfterTradeWithBankRequest found on the EventBus
     *
     * @author Alwin Bossert
     * @author Maximilian Lindner
     * @see de.uol.swp.common.game.request.UpdateInventoryAfterTradeWithBankRequest
     * @see de.uol.swp.common.game.response.TradeWithBankAcceptedResponse
     * @since 2021-02-21
     */
    @Subscribe
    private void onUpdateInventoryAfterTradeWithBankRequest(UpdateInventoryAfterTradeWithBankRequest req) {
        if (LOG.isDebugEnabled())
            LOG.debug("Received UpdateInventoryAfterTradeWithBankRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory inventory = game.getInventory(req.getUser());
        if (inventory == null) return;
        Map<I18nWrapper, Integer> offeredResourcesWrapperMap = new HashMap<>();
        Map<I18nWrapper, Integer> respondingResourcesWrapperMap = new HashMap<>();
        if (req.getGiveResource().equals("ore") && (inventory.getOre() < 4)) return;
        if (req.getGiveResource().equals("brick") && (inventory.getBrick() < 4)) return;
        if (req.getGiveResource().equals("grain") && (inventory.getGrain() < 4)) return;
        if (req.getGiveResource().equals("lumber") && (inventory.getLumber() < 4)) return;
        if (req.getGiveResource().equals("wool") && (inventory.getWool() < 4)) return;

        if (req.getGetResource().equals("ore")) {
            inventory.setOre(inventory.getOre() + 1);
            respondingResourcesWrapperMap.put(new I18nWrapper("game.resources.ore"), 1);
        }
        if (req.getGetResource().equals("brick")) {
            inventory.setBrick(inventory.getBrick() + 1);
            respondingResourcesWrapperMap.put(new I18nWrapper("game.resources.brick"), 1);
        }
        if (req.getGetResource().equals("grain")) {
            inventory.setGrain(inventory.getGrain() + 1);
            respondingResourcesWrapperMap.put(new I18nWrapper("game.resources.grain"), 1);
        }
        if (req.getGetResource().equals("lumber")) {
            inventory.setLumber(inventory.getLumber() + 1);
            respondingResourcesWrapperMap.put(new I18nWrapper("game.resources.lumber"), 1);
        }
        if (req.getGetResource().equals("wool")) {
            inventory.setWool(inventory.getWool() + 1);
            respondingResourcesWrapperMap.put(new I18nWrapper("game.resources.wool"), 1);
        }

        if (req.getGiveResource().equals("ore")) {
            inventory.setOre(inventory.getOre() - 4);
            offeredResourcesWrapperMap.put(new I18nWrapper("game.resources.ore"), 4);
        }
        if (req.getGiveResource().equals("brick")) {
            inventory.setBrick(inventory.getBrick() - 4);
            offeredResourcesWrapperMap.put(new I18nWrapper("game.resources.brick"), 4);
        }
        if (req.getGiveResource().equals("grain")) {
            inventory.setGrain(inventory.getGrain() - 4);
            offeredResourcesWrapperMap.put(new I18nWrapper("game.resources.grain"), 4);
        }
        if (req.getGiveResource().equals("lumber")) {
            inventory.setLumber(inventory.getLumber() - 4);
            offeredResourcesWrapperMap.put(new I18nWrapper("game.resources.lumber"), 4);
        }
        if (req.getGiveResource().equals("wool")) {
            inventory.setWool(inventory.getWool() - 4);
            offeredResourcesWrapperMap.put(new I18nWrapper("game.resources.wool"), 4);
        }

        ResponseMessage returnMessage = new TradeWithBankAcceptedResponse(req.getUser(), req.getOriginLobby());
        returnMessage.initWithMessage(req);
        post(returnMessage);
        LOG.debug("Received a SystemMessageForTradeMessage");
        ServerMessage serverMessage = new SystemMessageForTradeMessage(req.getOriginLobby(), req.getUser(), "Bank",
                                                                       offeredResourcesWrapperMap,
                                                                       respondingResourcesWrapperMap);
        LOG.debug("Sending a TradeWithBankAcceptedResponse to lobby" + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), serverMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
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
     * @see de.uol.swp.common.game.request.UpdateInventoryRequest
     * @see de.uol.swp.common.game.response.UpdateInventoryResponse
     * @since 2021-01-25
     */
    @Subscribe
    private void onUpdateInventoryRequest(UpdateInventoryRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received UpdateInventoryRequest for Lobby " + req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory inventory = game.getInventory(req.getUser());
        if (inventory == null) return;
        Map<String, Integer> resourceMap = getResourceMapFromInventory(inventory);
        resourceMap.put("cards.victorypoints", inventory.getVictoryPointCards());
        resourceMap.put("cards.knight", inventory.getKnightCards());
        resourceMap.put("cards.roadbuilding", inventory.getRoadBuildingCards());
        resourceMap.put("cards.yearofplenty", inventory.getYearOfPlentyCards());
        resourceMap.put("cards.monopoly", inventory.getMonopolyCards());

        Map<String, Boolean> armyAndRoadMap = new HashMap<>();
        armyAndRoadMap.put("cards.unique.largestarmy", inventory.isLargestArmy());
        armyAndRoadMap.put("cards.unique.longestroad", inventory.isLongestRoad());

        ResponseMessage returnMessage = new UpdateInventoryResponse(req.getUser(), req.getOriginLobby(),
                                                                    Collections.unmodifiableMap(resourceMap),
                                                                    Collections.unmodifiableMap(armyAndRoadMap));
        returnMessage.initWithMessage(req);
        LOG.debug("Sending UpdateInventoryResponse for Lobby " + req.getOriginLobby());
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby " + req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
    }

    /**
     * Helper method
     * <p>
     * Adds the random chosen development card and deletes the resources
     * he had to pay from his inventory.
     *
     * @param developmentCard Name of the random chosen development Card
     * @param user            User who wants to buy the development Card
     * @param lobbyName       Name of the lobby where the trade is happening
     *
     * @return a boolean if the trade worked out
     *
     * @author Maximilian Lindner
     * @author Alwin Bossert
     * @since 2021-02-22
     */
    private boolean updatePlayersInventoryWithDevelopmentCard(String developmentCard, UserOrDummy user,
                                                              String lobbyName) {
        Inventory inventory = gameManagement.getGame(lobbyName).getInventory(user);
        if (inventory == null) return false;
        if (inventory.getOre() >= 1 && inventory.getGrain() >= 1 && inventory.getWool() >= 1) {
            inventory.setOre(inventory.getOre() - 1);
            inventory.setGrain(inventory.getGrain() - 1);
            inventory.setWool(inventory.getWool() - 1);
            switch (developmentCard) {
                case "game.resources.cards.knight":
                    inventory.increaseKnightCards(1);
                    break;
                case "game.resources.cards.roadbuilding":
                    inventory.increaseRoadBuildingCards(1);
                    break;
                case "game.resources.cards.yearofplenty":
                    inventory.increaseYearOfPlentyCards(1);
                    break;
                case "game.resources.cards.monopoly":
                    inventory.increaseMonopolyCards(1);
                    break;
                case "game.resources.cards.victorypoints":
                    inventory.increaseVictoryPointCards(1);
                    break;
            }
            ResponseMessage serverMessage = new SystemMessageForTradeWithBankResponse(lobbyName, developmentCard);
            LOG.debug("Sending SystemMessageForTradeWithBankResponse for Lobby " + lobbyName);
            post(new ForwardToUserInternalRequest(user, serverMessage));
            LOG.debug("Sending SystemMessageForTradeWithBankMessage for Lobby " + lobbyName);
            lobbyService.sendToAllInLobby(lobbyName, new SystemMessageForTradeWithBankMessage(lobbyName, user));
        }
        return true;
    }
}

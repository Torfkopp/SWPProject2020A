package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.message.*;
import de.uol.swp.common.chat.response.SystemMessageForTradeWithBankResponse;
import de.uol.swp.common.exception.ExceptionMessage;
import de.uol.swp.common.exception.LobbyExceptionMessage;
import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.Game.StartUpPhase;
import de.uol.swp.common.game.Inventory;
import de.uol.swp.common.game.StartUpPhaseBuiltStructures;
import de.uol.swp.common.game.map.Hexes.IHarborHex;
import de.uol.swp.common.game.map.Hexes.ResourceHex;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.map.configuration.IConfiguration;
import de.uol.swp.common.game.map.management.*;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.game.response.*;
import de.uol.swp.common.game.robber.*;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.LobbyDeletedMessage;
import de.uol.swp.common.lobby.message.StartSessionMessage;
import de.uol.swp.common.lobby.request.KickUserRequest;
import de.uol.swp.common.message.MessageContext;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.Dummy;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.event.*;
import de.uol.swp.server.lobby.ILobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static de.uol.swp.common.game.Game.StartUpPhase.*;
import static de.uol.swp.common.game.RoadBuildingCardPhase.*;
import static de.uol.swp.common.game.StartUpPhaseBuiltStructures.*;
import static de.uol.swp.common.game.message.BuildingSuccessfulMessage.Type.*;
import static de.uol.swp.common.game.response.BuildingFailedResponse.Reason.*;

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

    private final ILobbyManagement lobbyManagement;
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
    public GameService(EventBus bus, IGameManagement gameManagement, ILobbyManagement lobbyManagement,
                       LobbyService lobbyService) {
        super(bus);
        this.gameManagement = gameManagement;
        this.lobbyManagement = lobbyManagement;
        this.lobbyService = lobbyService;
        LOG.debug("GameService started");
    }

    /**
     * Helper function
     * <p>
     * Checks if there are enough resources in the needed Inventory.
     * It compares the needed inventory with the actual inventory.
     *
     * @param inventoryList      Saved inventory in game
     * @param neededResourceList Trading inventory
     *
     * @return true if there are enough resources in the neededInventoryMap, false if not
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @since 2021-02-24
     */
    private boolean checkEnoughResourcesInInventory(List<Map<String, Object>> inventoryList,
                                                    List<Map<String, Object>> neededResourceList) {
        for (Map<String, Object> resourceMap : inventoryList) {
            for (Map<String, Object> neededResourceMap : neededResourceList) {
                //@formatter:off
                if (resourceMap.get("enumType").equals(neededResourceMap.get("enumType"))
                        && ((int) resourceMap.get("amount")) < (int) neededResourceMap.get("amount"))
                    return false;
                //@formatter:on
            }
        }
        return true;
    }

    /**
     * Handles the allocation of the largest Army
     *
     * @param lobbyName The lobbyname
     * @param user      The user for whom the largest army should be checked
     *
     * @author Eric Vuong
     * @author Temmo Junkhoff
     * @since 2021-04-10
     */
    private void checkLargestArmy(String lobbyName, UserOrDummy user) {
        Game game = gameManagement.getGame(lobbyName);
        Inventory[] invs = game.getAllInventories();
        Inventory largest = game.getInventory(game.getPlayerWithLargestArmy());
        Inventory currentlyLargest = null;
        if (largest == null) {
            largest = new Inventory();
            largest.setKnights(2);
        }

        if (game.getInventory(user).getKnights() > largest.getKnights())
            game.setPlayerWithLargestArmy(game.getPlayer(user));

        lobbyService
                .sendToAllInLobby(lobbyName, new UpdateUniqueCardsListMessage(lobbyName, game.getUniqueCardsList()));
    }

    /**
     * Handles the allocation of the longest Road
     *
     * @param lobbyName The lobbyname
     * @param mapPoint  The map point pointing to the edge based on which the longest road should be checked
     *
     * @author Eric Vuong
     * @auhtor Temmo Junkhoff
     * @since 2021-04-10
     */
    private void checkLongestRoad(String lobbyName, MapPoint mapPoint) {
        Game game = gameManagement.getGame(lobbyName);
        int length = game.getMap().longestRoadWith(mapPoint);
        if (length > 4 && length > game.getLongestRoadLength()) {
            game.setLongestRoadLength(length);
            game.setPlayerWithLongestRoad(game.getMap().getEdge(mapPoint).getOwner());
        }
        lobbyService
                .sendToAllInLobby(lobbyName, new UpdateUniqueCardsListMessage(lobbyName, game.getUniqueCardsList()));
    }

    /**
     * Helper method to handle ending the game if the last change to the
     * inventory pushed the player over the edge in terms of Victory Points
     *
     * @param game        The game in which the player might have won
     * @param originLobby The lobby in which the game is taking place
     * @param user        The user who might have won
     *
     * @author Phillip-André Suhr
     * @author Steven Luong
     * @see de.uol.swp.common.game.message.PlayerWonGameMessage
     * @since 2021-04-07
     */
    private void endGameIfPlayerWon(Game game, String originLobby, UserOrDummy user) {
        int vicPoints = game.calculateVictoryPoints(game.getPlayer(user));
        if (vicPoints >= 10) {
            ServerMessage message = new PlayerWonGameMessage(originLobby, user);
            lobbyService.sendToAllInLobby(originLobby, message);
            game.setBuildingAllowed(false);
        }
    }

    /**
     * Helper method to end a dummy's turn
     * AFTER every player has chosen the resources
     * to give up on.
     *
     * @param game The game
     *
     * @author Mario Fokken
     * @since 2021-04-09
     */
    private void endTurnDummy(Game game) {
        UserOrDummy activePlayer = game.getActivePlayer();
        if (activePlayer instanceof User) {
        } else {
            if (game.getTaxPayers().isEmpty())
                onEndTurnRequest(new EndTurnRequest(activePlayer, game.getLobby().getName()));
        }
    }

    /**
     * Helper method to make a Map of Development Cards as required by
     * MapValueFactories from a provided inventory.
     *
     * @param inventory The inventory to make a Map from
     *
     * @return List of Development Cards as Maps with keys "amount" and "card"
     *
     * @author Phillip-André Suhr
     * @since 2021-04-18
     */
    private List<Map<String, Object>> getDevelopmentCardListFromInventory(Inventory inventory) {
        HashMap<String, Object> vpMap = new HashMap<>();
        vpMap.put("amount", inventory.getVictoryPointCards());
        vpMap.put("card", new I18nWrapper("game.resources.cards.victorypoints"));
        vpMap.put("type", "game.resources.cards.victorypoints");
        HashMap<String, Object> kMap = new HashMap<>();
        kMap.put("amount", inventory.getKnightCards());
        kMap.put("card", new I18nWrapper("game.resources.cards.knight"));
        kMap.put("type", "game.resources.cards.knight");
        HashMap<String, Object> rbMap = new HashMap<>();
        rbMap.put("amount", inventory.getRoadBuildingCards());
        rbMap.put("card", new I18nWrapper("game.resources.cards.roadbuilding"));
        rbMap.put("type", "game.resources.cards.roadbuilding");
        HashMap<String, Object> yopMap = new HashMap<>();
        yopMap.put("amount", inventory.getYearOfPlentyCards());
        yopMap.put("card", new I18nWrapper("game.resources.cards.yearofplenty"));
        yopMap.put("type", "game.resources.cards.yearofplenty");
        HashMap<String, Object> mMap = new HashMap<>();
        mMap.put("amount", inventory.getMonopolyCards());
        mMap.put("card", new I18nWrapper("game.resources.cards.monopoly"));
        mMap.put("type", "game.resources.cards.monopoly");
        List<Map<String, Object>> cardList = new ArrayList<>();
        cardList.add(vpMap);
        cardList.add(kMap);
        cardList.add(rbMap);
        cardList.add(yopMap);
        cardList.add(mMap);
        return cardList;
    }

    /**
     * Helper method to make a Map of Resources as required by
     * MapValueFactories from a provided inventory.
     *
     * @param inventory The inventory to make a Map from
     *
     * @return List of Resources Maps with keys "amount" and "resource"
     *
     * @author Phillip-André Suhr
     * @since 2021-04-18
     */
    private List<Map<String, Object>> getResourceListFromInventory(Inventory inventory) {
        HashMap<String, Object> brickMap = new HashMap<>();
        brickMap.put("amount", inventory.getBrick());
        brickMap.put("enumType", Resources.BRICK);
        brickMap.put("resource", new I18nWrapper("game.resources.brick"));
        Map<String, Object> grainMap = new HashMap<>();
        grainMap.put("amount", inventory.getGrain());
        grainMap.put("enumType", Resources.GRAIN);
        grainMap.put("resource", new I18nWrapper("game.resources.grain"));
        Map<String, Object> lumberMap = new HashMap<>();
        lumberMap.put("amount", inventory.getLumber());
        lumberMap.put("enumType", Resources.LUMBER);
        lumberMap.put("resource", new I18nWrapper("game.resources.lumber"));
        Map<String, Object> oreMap = new HashMap<>();
        oreMap.put("amount", inventory.getOre());
        oreMap.put("enumType", Resources.ORE);
        oreMap.put("resource", new I18nWrapper("game.resources.ore"));
        Map<String, Object> woolMap = new HashMap<>();
        woolMap.put("amount", inventory.getWool());
        woolMap.put("enumType", Resources.WOOL);
        woolMap.put("resource", new I18nWrapper("game.resources.wool"));
        List<Map<String, Object>> resourceList = new ArrayList<>();
        resourceList.add(brickMap);
        resourceList.add(grainMap);
        resourceList.add(lumberMap);
        resourceList.add(oreMap);
        resourceList.add(woolMap);
        return resourceList;
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
        LOG.debug("Received AcceptUserTradeRequest for Lobby {}", req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getOfferingUser()) || !game.isDiceRolledAlready()) return;
        game.setBuildingAllowed(false);
        Inventory offeringInventory = game.getInventory(req.getOfferingUser());
        Inventory respondingInventory = game.getInventory(req.getRespondingUser());
        if (offeringInventory == null || respondingInventory == null) return;
        List<Map<String, Object>> offeringInventoryMap = getResourceListFromInventory(offeringInventory);
        List<Map<String, Object>> responseInventoryMap = getResourceListFromInventory(respondingInventory);
        boolean enoughToOffer = checkEnoughResourcesInInventory(offeringInventoryMap, req.getOfferedResources());
        boolean enoughToDemand = checkEnoughResourcesInInventory(responseInventoryMap, req.getDemandedResources());
        if (enoughToOffer && enoughToDemand) {
            //changes the inventories according to the offer
            Map<I18nWrapper, Integer> offeredResourcesWrapperMap = new HashMap<>();
            Map<I18nWrapper, Integer> demandedResourcesWrapperMap = new HashMap<>();
            for (Map<String, Object> resourceMap : req.getOfferedResources()) {
                int amount = (int) resourceMap.get("amount");
                if (amount <= 0) continue;
                Resources resource = (Resources) resourceMap.get("enumType");
                offeredResourcesWrapperMap.put((I18nWrapper) resourceMap.get("resource"), amount);
                switch (resource) {
                    case BRICK:
                        offeringInventory.increaseBrick(-amount);
                        respondingInventory.increaseBrick(amount);
                        break;
                    case GRAIN:
                        offeringInventory.increaseGrain(-amount);
                        respondingInventory.increaseGrain(amount);
                        break;
                    case LUMBER:
                        offeringInventory.increaseLumber(-amount);
                        respondingInventory.increaseLumber(amount);
                        break;
                    case ORE:
                        offeringInventory.increaseOre(-amount);
                        respondingInventory.increaseOre(amount);
                        break;
                    case WOOL:
                        offeringInventory.increaseWool(-amount);
                        respondingInventory.increaseWool(amount);
                        break;
                }
            }
            if (offeredResourcesWrapperMap.isEmpty())
                offeredResourcesWrapperMap.put(new I18nWrapper("game.trade.offer.nothing"), 0);

            for (Map<String, Object> resourceMap : req.getDemandedResources()) {
                int amount = (int) resourceMap.get("amount");
                if (amount <= 0) continue;
                Resources resource = (Resources) resourceMap.get("enumType");
                demandedResourcesWrapperMap.put((I18nWrapper) resourceMap.get("resource"), amount);
                switch (resource) {
                    case BRICK:
                        offeringInventory.increaseBrick(amount);
                        respondingInventory.increaseBrick(-amount);
                        break;
                    case GRAIN:
                        offeringInventory.increaseGrain(amount);
                        respondingInventory.increaseGrain(-amount);
                        break;
                    case LUMBER:
                        offeringInventory.increaseLumber(amount);
                        respondingInventory.increaseLumber(-amount);
                        break;
                    case ORE:
                        offeringInventory.increaseOre(amount);
                        respondingInventory.increaseOre(-amount);
                        break;
                    case WOOL:
                        offeringInventory.increaseWool(amount);
                        respondingInventory.increaseWool(-amount);
                        break;
                }
            }
            if (demandedResourcesWrapperMap.isEmpty())
                demandedResourcesWrapperMap.put(new I18nWrapper("game.trade.offer.nothing"), 0);

            ServerMessage returnSystemMessage = new SystemMessageForTradeMessage(req.getOriginLobby(),
                                                                                 req.getOfferingUser(),
                                                                                 req.getRespondingUser().getUsername(),
                                                                                 offeredResourcesWrapperMap,
                                                                                 demandedResourcesWrapperMap);
            LOG.debug("Sending SystemMessageForTradeMessage for Lobby {}", req.getOriginLobby());
            lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);
            ResponseMessage returnMessage = new TradeOfUsersAcceptedResponse(req.getOriginLobby());
            LOG.debug("Preparing a TradeOfUsersAcceptedResponse for Lobby {}", req.getOriginLobby());
            post(new ForwardToUserInternalRequest(req.getOfferingUser(), returnMessage));
            returnMessage.initWithMessage(req);
            LOG.debug("Sending TradeOfUsersAcceptedResponse for Lobby {}", req.getOriginLobby());
            post(returnMessage);
            ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getOfferingUser(),
                                                             game.getCardAmounts());
            LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
            lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
        } else {
            ResponseMessage returnMessage = new InvalidTradeOfUsersResponse(req.getOriginLobby(),
                                                                            req.getRespondingUser());
            LOG.debug("Sending InvalidTradeOfUsersResponse for Lobby {}", req.getOriginLobby());
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
        LOG.debug("Received BuildRequest for Lobby {}", req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        StartUpPhase currentPhase = game.getStartUpPhase();
        Deque<UserOrDummy> startUpPlayerOrder = game.getStartUpPlayerOrder();
        if (currentPhase == NOT_IN_STARTUP_PHASE) {
            if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready()) {
                return;
            }
        } else if (startUpPlayerOrder.peekFirst() == null || !startUpPlayerOrder.peekFirst().equals(req.getUser())) {
            return;
        }
        Consumer<BuildingFailedResponse.Reason> sendFailResponse = reason -> {
            LOG.debug("Sending BuildingFailedResponse with reason {}", reason);
            BuildingFailedResponse msg = new BuildingFailedResponse(req.getOriginLobby(), reason);
            msg.initWithMessage(req);
            post(msg);
        };

        BiConsumer<String, BuildingSuccessfulMessage> sendSuccess = (lobbyName, message) -> {
            LOG.debug("Sending BuildingSuccessfulMessage");
            lobbyService.sendToAllInLobby(lobbyName, message);
        };

        if (!game.isBuildingAllowed() && currentPhase == NOT_IN_STARTUP_PHASE) {
            sendFailResponse.accept(NOT_THE_RIGHT_TIME);
            return;
        }
        IGameMapManagement gameMap = game.getMap();
        MapPoint mapPoint = req.getMapPoint();
        UserOrDummy user = req.getUser();
        Player player = game.getPlayer(user);
        Inventory inv = game.getInventory(user);

        switch (mapPoint.getType()) {
            case INTERSECTION: {
                if (gameMap.getIntersection(mapPoint).getState() == IIntersection.IntersectionState.CITY) {
                    sendFailResponse.accept(ALREADY_BUILT_HERE);
                } else if (gameMap.settlementPlaceable(player, mapPoint)) {
                    if (inv.getBrick() >= 1 && inv.getLumber() >= 1 && inv.getWool() >= 1 && inv.getGrain() >= 1) {
                        inv.increaseBrick(-1);
                        inv.increaseLumber(-1);
                        inv.increaseWool(-1);
                        inv.increaseGrain(-1);
                        try {
                            gameMap.placeSettlement(player, mapPoint);
                        } catch (GameMapManagement.SettlementMightInterfereWithLongestRoadException e) {
                            GameMapManagement.PlayerWithLengthOfLongestRoad a = gameMap.findLongestRoad();
                            if (a.getLength() >= 5) {
                                game.setPlayerWithLongestRoad(a.getPlayer());
                                game.setLongestRoadLength(a.getLength());
                            } else {
                                game.setPlayerWithLongestRoad(null);
                                game.setLongestRoadLength(0);
                            }
                            lobbyService.sendToAllInLobby(req.getOriginLobby(),
                                                          new UpdateUniqueCardsListMessage(req.getOriginLobby(),
                                                                                           game.getUniqueCardsList()));
                        }
                        sendSuccess.accept(req.getOriginLobby(),
                                           new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint,
                                                                         SETTLEMENT));
                    } else {
                        sendFailResponse.accept(NOT_ENOUGH_RESOURCES);
                    }
                } else if (currentPhase != NOT_IN_STARTUP_PHASE) {
                    Map<UserOrDummy, StartUpPhaseBuiltStructures> startUpBuiltMap = game.getPlayersStartUpBuiltMap();
                    StartUpPhaseBuiltStructures built = startUpBuiltMap.get(user);
                    if (built == NONE_BUILT && currentPhase == PHASE_1) {
                        boolean success = gameMap.placeFoundingSettlement(player, mapPoint);
                        if (!success) sendFailResponse.accept(CANT_BUILD_HERE);
                        else {
                            startUpBuiltMap.put(user, FIRST_SETTLEMENT_BUILT);
                            sendSuccess.accept(req.getOriginLobby(),
                                               new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint,
                                                                             SETTLEMENT));
                        }
                    } else if (built == FIRST_BOTH_BUILT && currentPhase == PHASE_2) {
                        boolean success = gameMap.placeFoundingSettlement(player, mapPoint);
                        if (!success) sendFailResponse.accept(CANT_BUILD_HERE);
                        else {
                            startUpBuiltMap.put(user, SECOND_SETTLEMENT_BUILT);
                            for (MapPoint hexPoint : gameMap.getResourceHexesFromIntersection(mapPoint)) {
                                ResourceHex hex = (ResourceHex) gameMap.getHex(hexPoint);
                                switch (hex.getResource()) {
                                    case HILLS:
                                        inv.increaseBrick(1);
                                        break;
                                    case FOREST:
                                        inv.increaseLumber(1);
                                        break;
                                    case MOUNTAINS:
                                        inv.increaseOre(1);
                                        break;
                                    case FIELDS:
                                        inv.increaseGrain(1);
                                        break;
                                    case PASTURE:
                                        inv.increaseWool(1);
                                        break;
                                }
                            }
                            List<Map<String, Object>> resources = getResourceListFromInventory(inv);
                            List<Map<String, Object>> devCards = getDevelopmentCardListFromInventory(inv);
                            ResponseMessage rsp = new UpdateInventoryResponse(user, req.getOriginLobby(), devCards,
                                                                              resources);
                            rsp.initWithMessage(req);
                            LOG.debug("Sending UpdateInventoryResponse of Start Up Phase");
                            post(rsp);
                            sendSuccess.accept(req.getOriginLobby(),
                                               new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint,
                                                                             SETTLEMENT));
                        }
                    } else sendFailResponse.accept(NOT_THE_RIGHT_TIME);
                } else if (gameMap.settlementUpgradeable(player, mapPoint)) {
                    if (inv.getOre() >= 3 && inv.getGrain() >= 2) {
                        inv.increaseOre(-3);
                        inv.increaseGrain(-2);
                        gameMap.upgradeSettlement(player, mapPoint);
                        sendSuccess.accept(req.getOriginLobby(),
                                           new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint, CITY));
                    } else {
                        sendFailResponse.accept(NOT_ENOUGH_RESOURCES);
                    }
                } else {
                    sendFailResponse.accept(CANT_BUILD_HERE);
                }
                break;
            }
            case EDGE: {
                if (gameMap.getEdge(mapPoint) == null) {
                    sendFailResponse.accept(NOT_A_REAL_ROAD);
                } else if (gameMap.getEdge(mapPoint).getOwner() != null) {
                    sendFailResponse.accept(ALREADY_BUILT_HERE);
                } else if (gameMap.roadPlaceable(player, mapPoint)) {
                    if (game.getRoadBuildingCardPhase() != NO_ROAD_BUILDING_CARD_PLAYED) {
                        if (game.getRoadBuildingCardPhase() == WAITING_FOR_FIRST_ROAD)
                            game.setRoadBuildingCardPhase(WAITING_FOR_SECOND_ROAD);
                        else if (game.getRoadBuildingCardPhase() == WAITING_FOR_SECOND_ROAD) {
                            LOG.debug("---- RoadBuildingCardPhase phase ends");
                            game.setRoadBuildingCardPhase(NO_ROAD_BUILDING_CARD_PLAYED);
                        }
                        gameMap.placeRoad(player, mapPoint);
                        sendSuccess.accept(req.getOriginLobby(),
                                           new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint, ROAD));
                    } else if (currentPhase != NOT_IN_STARTUP_PHASE) {
                        Map<UserOrDummy, StartUpPhaseBuiltStructures> startUpBuiltMap = game
                                .getPlayersStartUpBuiltMap();
                        StartUpPhaseBuiltStructures built = startUpBuiltMap.get(user);
                        if (built == FIRST_SETTLEMENT_BUILT && currentPhase == PHASE_1) {
                            boolean success = gameMap.placeRoad(player, mapPoint);
                            if (!success) sendFailResponse.accept(CANT_BUILD_HERE);
                            else {
                                startUpBuiltMap.put(user, FIRST_BOTH_BUILT);
                                sendSuccess.accept(req.getOriginLobby(),
                                                   new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint,
                                                                                 ROAD));
                            }
                        } else if (built == SECOND_SETTLEMENT_BUILT && currentPhase == PHASE_2) {
                            boolean success = gameMap.placeRoad(player, mapPoint);
                            if (!success) sendFailResponse.accept(CANT_BUILD_HERE);
                            else {
                                startUpBuiltMap.put(user, ALL_BUILT);
                                sendSuccess.accept(req.getOriginLobby(),
                                                   new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint,
                                                                                 ROAD));
                            }
                        } else sendFailResponse.accept(NOT_THE_RIGHT_TIME);
                    } else if (inv.getBrick() >= 1 && inv.getLumber() >= 1) {
                        inv.increaseBrick(-1);
                        inv.increaseLumber(-1);
                        gameMap.placeRoad(player, mapPoint);
                        checkLongestRoad(req.getOriginLobby(), mapPoint);
                        sendSuccess.accept(req.getOriginLobby(),
                                           new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint, ROAD));
                    } else {
                        sendFailResponse.accept(NOT_ENOUGH_RESOURCES);
                    }
                } else {
                    sendFailResponse.accept(CANT_BUILD_HERE);
                }
                break;
            }
            case HEX: {
                sendFailResponse.accept(BAD_GROUND);
            }
            case INVALID: {
                sendFailResponse.accept(NOTHING_HERE);
            }
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
        LOG.debug("Received BuyDevelopmentCardRequest for Lobby {}", req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready()) return;
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
                LOG.debug("Sending BuyDevelopmentCardResponse for Lobby {}", req.getOriginLobby());
                post(returnMessage);
                ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(),
                                                                 game.getCardAmounts());
                LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
                lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
                endGameIfPlayerWon(game, req.getOriginLobby(), req.getUser());
            } else LOG.debug("In the Lobby {} the User {} couldn't buy a Development Card", req.getOriginLobby(),
                             req.getUser().getUsername());
        }
    }

    /**
     * Handles a ChangeAutoRollStateRequest found on the EventBus
     * <p>
     * If a ChangeAutoRollStateRequest is found on the EventBus,
     * the requesting Users autoRoll status gets changed in the game
     * according to the value in the request.
     *
     * @param req The ChangeAutoRollStateRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @since 2021-04-26
     */
    @Subscribe
    private void onChangeAutoRollStateRequest(ChangeAutoRollStateRequest req) {
        LOG.debug("Received a ChangeAutoRollStateRequest");
        Game game = gameManagement.getGame(req.getOriginLobby());
        game.setAutoRollEnabled(req.getUser(), req.isAutoRollEnabled());
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
        LOG.debug("Received CreateGameInternalRequest for Lobby {}", lobbyName);
        try {
            IGameMapManagement gameMap = new GameMapManagement();
            IConfiguration configuration;
            if (msg.getLobby().randomPlayfieldEnabled()) {
                configuration = gameMap.getRandomisedConfiguration();
            } else {
                configuration = gameMap.getBeginnerConfiguration();
            }
            msg.getLobby().setConfiguration(configuration);
            gameMap = gameMap.createMapFromConfiguration(configuration);
            if (!msg.getLobby().startUpPhaseEnabled()) {
                gameMap.makeBeginnerSettlementsAndRoads(msg.getLobby().getUserOrDummies().size());
            }
            gameManagement.createGame(msg.getLobby(), msg.getFirst(), gameMap, msg.getMoveTime());
            LOG.debug("Sending GameCreatedMessage");
            post(new GameCreatedMessage(msg.getLobby().getName(), msg.getFirst()));
            LOG.debug("Sending StartSessionMessage for Lobby {}", lobbyName);
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
                checkLargestArmy(req.getOriginLobby(), req.getUser());
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
        }
        inventory = game.getInventory(req.getUser());
        List<Map<String, Object>> developmentCardList = getDevelopmentCardListFromInventory(inventory);
        List<Map<String, Object>> resourceList = getResourceListFromInventory(inventory);
        ResponseMessage returnMessage = new UpdateInventoryResponse(req.getUser(), req.getOriginLobby(),
                                                                    developmentCardList, resourceList);
        LOG.debug("Sending ForwardToUserInternalRequest containing UpdateInventoryResponse");
        post(new ForwardToUserInternalRequest(req.getUser(), returnMessage));
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
        endGameIfPlayerWon(game, req.getOriginLobby(), req.getUser());
    }

    /**
     * Handles a EndTurnRequest found on the EventBus
     * <p>
     * If a EndTurnRequest is detected on the EventBus, this method is called.
     * It then sends a NextPlayerMessage to all members in the lobby.
     *
     * @param req The EndTurnRequest found on the EventBus
     *
     * @author Mario Fokken
     * @see de.uol.swp.common.game.request.EndTurnRequest
     * @see de.uol.swp.common.game.message.NextPlayerMessage
     * @since 2021-01-15
     */
    @Subscribe
    private void onEndTurnRequest(EndTurnRequest req) {
        LOG.debug("Received EndTurnRequest for Lobby {}", req.getOriginLobby());
        LOG.debug("---- User {} wants to end their turn.", req.getUser().getUsername());
        Game game = gameManagement.getGame(req.getOriginLobby());
        StartUpPhase currentPhase = game.getStartUpPhase();
        Deque<UserOrDummy> startUpPlayerOrder = game.getStartUpPlayerOrder();
        if (currentPhase == NOT_IN_STARTUP_PHASE) {
            if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready()) {
                return;
            }
        } else if (startUpPlayerOrder.peekFirst() == null || !startUpPlayerOrder.peekFirst().equals(req.getUser())) {
            return;
        }
        game.setBuildingAllowed(false);
        ServerMessage returnMessage = new NextPlayerMessage(req.getOriginLobby(), game.nextPlayer(), game.getRound());
        UserOrDummy nextPlayer;
        UserOrDummy user;
        Optional<Lobby> optionalLobby = lobbyManagement.getLobby(req.getOriginLobby());
        if (optionalLobby.isEmpty()) return;
        if (optionalLobby.get().isStartUpPhaseEnabled()) {
            if (currentPhase.equals(PHASE_1)) {
                // in phase 1, continue with Deque order
                startUpPlayerOrder.addLast(startUpPlayerOrder.pollFirst());
                user = startUpPlayerOrder.peekFirst();
                if (user == null) return;
                if (user.equals(game.getFirst())) {
                    // first again at the beginning, signals reversal as per rules (2nd phase)
                    game.setStartUpPhase(PHASE_2);
                    nextPlayer = startUpPlayerOrder.pollLast();
                    startUpPlayerOrder.addFirst(nextPlayer);
                } else {
                    nextPlayer = user;
                }
            } else if (currentPhase.equals(PHASE_2)) {
                if (game.getPlayersStartUpBuiltMap().get(game.getFirst()) == ALL_BUILT) {
                    nextPlayer = game.getFirst();
                    game.setStartUpPhase(NOT_IN_STARTUP_PHASE);
                } else {
                    startUpPlayerOrder.addFirst(startUpPlayerOrder.pollLast());
                    user = startUpPlayerOrder.peekFirst();
                    if (user == null) return;
                    nextPlayer = user;
                }
            } else {
                nextPlayer = game.nextPlayer();
            }
        } else {
            nextPlayer = game.nextPlayer();
        }
        returnMessage = new NextPlayerMessage(req.getOriginLobby(), nextPlayer, game.getRound());

        LOG.debug("Sending NextPlayerMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnMessage);

        game.setDiceRolledAlready(false);
        if (nextPlayer instanceof Dummy) {
            onRollDiceRequest(new RollDiceRequest(nextPlayer, req.getOriginLobby()));
            endTurnDummy(game);
        }
    }

    /**
     * Handles a ExecuteTradeWithBankRequest found on the EventBus
     * <p>
     * If a ExecuteTradeWithBankRequest is found on the EventBus this method updates the inventory
     * of the player who traded with the bank. If the User has enough resources, the resource he wants to trade gets -4
     * and the resource he wants gets +1. It then posts a TradeWithBankAcceptedResponse onto the EventBus.
     *
     * @param req The ExecuteTradeWithBankRequest found on the EventBus
     *
     * @author Alwin Bossert
     * @author Maximilian Lindner
     * @see de.uol.swp.common.game.request.ExecuteTradeWithBankRequest
     * @see de.uol.swp.common.game.response.TradeWithBankAcceptedResponse
     * @since 2021-02-21
     */
    @Subscribe
    private void onExecuteTradeWithBankRequest(ExecuteTradeWithBankRequest req) {
        LOG.debug("Received ExecuteTradeWithBankRequest for Lobby {}", req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready()) return;
        Inventory inventory = game.getInventory(req.getUser());
        if (inventory == null) return;
        Map<I18nWrapper, Integer> offeredResourcesWrapperMap = new HashMap<>();
        Map<I18nWrapper, Integer> respondingResourcesWrapperMap = new HashMap<>();
        //getting the tradingRatios with the bank according to the harbors
        IGameMapManagement gameMap = game.getMap();
        Map<Player, List<MapPoint>> settlementsAndCities = gameMap.getPlayerSettlementsAndCities();
        Player player = game.getPlayer(req.getUser());
        List<IHarborHex.HarborResource> harborTradingList = new ArrayList<>();
        if (settlementsAndCities.containsKey(player)) {
            List<MapPoint> ownSettlementsAndCities = settlementsAndCities.get(player);
            for (MapPoint ownSettlementsAndCity : ownSettlementsAndCities) {
                IHarborHex.HarborResource resource = gameMap.getHarborResource(ownSettlementsAndCity);
                harborTradingList.add(resource);
            }
        }
        //preparing a map with the tradingRatios according to the harbors
        Map<IHarborHex.HarborResource, Integer> tradingRatio = new HashMap<>();
        int prepareTradingRatio = 4;
        if (harborTradingList.contains(IHarborHex.HarborResource.ANY)) prepareTradingRatio = 3;
        tradingRatio.put(IHarborHex.HarborResource.BRICK, prepareTradingRatio);
        tradingRatio.put(IHarborHex.HarborResource.ORE, prepareTradingRatio);
        tradingRatio.put(IHarborHex.HarborResource.GRAIN, prepareTradingRatio);
        tradingRatio.put(IHarborHex.HarborResource.WOOL, prepareTradingRatio);
        tradingRatio.put(IHarborHex.HarborResource.LUMBER, prepareTradingRatio);
        if (harborTradingList.contains(IHarborHex.HarborResource.BRICK))
            tradingRatio.replace(IHarborHex.HarborResource.BRICK, 2);
        if (harborTradingList.contains(IHarborHex.HarborResource.ORE))
            tradingRatio.replace(IHarborHex.HarborResource.ORE, 2);
        if (harborTradingList.contains(IHarborHex.HarborResource.GRAIN))
            tradingRatio.replace(IHarborHex.HarborResource.GRAIN, 2);
        if (harborTradingList.contains(IHarborHex.HarborResource.WOOL))
            tradingRatio.replace(IHarborHex.HarborResource.WOOL, 2);
        if (harborTradingList.contains(IHarborHex.HarborResource.LUMBER))
            tradingRatio.replace(IHarborHex.HarborResource.LUMBER, 2);
        //check if user has enough resources
        if (req.getGiveResource().equals(Resources.ORE) && (inventory.getOre() < tradingRatio
                .get(IHarborHex.HarborResource.ORE))) return;
        if (req.getGiveResource().equals(Resources.BRICK) && (inventory.getBrick() < tradingRatio
                .get(IHarborHex.HarborResource.BRICK))) return;
        if (req.getGiveResource().equals(Resources.GRAIN) && (inventory.getGrain() < tradingRatio
                .get(IHarborHex.HarborResource.GRAIN))) return;
        if (req.getGiveResource().equals(Resources.LUMBER) && (inventory.getLumber() < tradingRatio
                .get(IHarborHex.HarborResource.LUMBER))) return;
        if (req.getGiveResource().equals(Resources.WOOL) && (inventory.getWool() < tradingRatio
                .get(IHarborHex.HarborResource.WOOL))) return;
        //user gets the resource he demands
        if (req.getGetResource().equals(Resources.ORE)) {
            inventory.increaseOre(1);
            respondingResourcesWrapperMap.put(new I18nWrapper("game.resources.ore"), 1);
        }
        if (req.getGetResource().equals(Resources.BRICK)) {
            inventory.increaseBrick(1);
            respondingResourcesWrapperMap.put(new I18nWrapper("game.resources.brick"), 1);
        }
        if (req.getGetResource().equals(Resources.GRAIN)) {
            inventory.increaseGrain(1);
            respondingResourcesWrapperMap.put(new I18nWrapper("game.resources.grain"), 1);
        }
        if (req.getGetResource().equals(Resources.LUMBER)) {
            inventory.increaseLumber(1);
            respondingResourcesWrapperMap.put(new I18nWrapper("game.resources.lumber"), 1);
        }
        if (req.getGetResource().equals(Resources.WOOL)) {
            inventory.increaseWool(1);
            respondingResourcesWrapperMap.put(new I18nWrapper("game.resources.wool"), 1);
        }
        //user gives the resource he offers according to the harbors
        if (req.getGiveResource().equals(Resources.ORE)) {
            inventory.setOre(inventory.getOre() - tradingRatio.get(IHarborHex.HarborResource.ORE));
            offeredResourcesWrapperMap
                    .put(new I18nWrapper("game.resources.ore"), tradingRatio.get(IHarborHex.HarborResource.ORE));
        }
        if (req.getGiveResource().equals(Resources.BRICK)) {
            inventory.setBrick(inventory.getBrick() - tradingRatio.get(IHarborHex.HarborResource.BRICK));
            offeredResourcesWrapperMap
                    .put(new I18nWrapper("game.resources.brick"), tradingRatio.get(IHarborHex.HarborResource.BRICK));
        }
        if (req.getGiveResource().equals(Resources.GRAIN)) {
            inventory.setGrain(inventory.getGrain() - tradingRatio.get(IHarborHex.HarborResource.GRAIN));
            offeredResourcesWrapperMap
                    .put(new I18nWrapper("game.resources.grain"), tradingRatio.get(IHarborHex.HarborResource.GRAIN));
        }
        if (req.getGiveResource().equals(Resources.LUMBER)) {
            inventory.setLumber(inventory.getLumber() - tradingRatio.get(IHarborHex.HarborResource.LUMBER));
            offeredResourcesWrapperMap
                    .put(new I18nWrapper("game.resources.lumber"), tradingRatio.get(IHarborHex.HarborResource.LUMBER));
        }
        if (req.getGiveResource().equals(Resources.WOOL)) {
            inventory.setWool(inventory.getWool() - tradingRatio.get(IHarborHex.HarborResource.WOOL));
            offeredResourcesWrapperMap
                    .put(new I18nWrapper("game.resources.wool"), tradingRatio.get(IHarborHex.HarborResource.WOOL));
        }

        ResponseMessage returnMessage = new TradeWithBankAcceptedResponse(req.getUser(), req.getOriginLobby());
        returnMessage.initWithMessage(req);
        post(returnMessage);
        LOG.debug("Received SystemMessageForTradeMessage");
        ServerMessage serverMessage = new SystemMessageForTradeMessage(req.getOriginLobby(), req.getUser(), "Bank",
                                                                       offeredResourcesWrapperMap,
                                                                       respondingResourcesWrapperMap);
        LOG.debug("Sending TradeWithBankAcceptedResponse to Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), serverMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
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
        LOG.debug("Received KickUserRequest for Lobby {}", req.getName());
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
        LOG.debug("Received OfferingTradeWithUserRequest for Lobby {}", req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!(req.getRespondingUser() instanceof User && game.getActivePlayer().equals(req.getOfferingUser()) && game
                .isDiceRolledAlready())) {
            post(new ResetOfferTradeButtonRequest(req.getOriginLobby(), req.getOfferingUser()));
            return;
        }
        game.setBuildingAllowed(false);
        Inventory respondingInventory = game.getInventory(game.getPlayer(req.getRespondingUser()));
        if (respondingInventory == null) return;
        List<Map<String, Object>> resourceMap = getResourceListFromInventory(respondingInventory);

        LOG.debug("Sending TradeWithUserOfferMessage to Lobby {}", req.getOriginLobby());
        ResponseMessage offerResponse = new TradeWithUserOfferResponse(req.getOfferingUser(), resourceMap,
                                                                       req.getOfferedResources(),
                                                                       req.getDemandedResources(),
                                                                       req.getOriginLobby());
        post(new ForwardToUserInternalRequest(req.getRespondingUser(), offerResponse));
    }

    /**
     * Handles a PauseTimerRequest found on the EventBus
     * <p>
     * If a PauseTimerRequest is found on the EventBus,
     * the game gets paused.
     * It also posts a new PauseTimerMessage to all the players in the lobby.
     *
     * @param req The PauseTimerRequest found on the EventBus
     *
     * @author Alwin Bossert
     * @see de.uol.swp.common.game.request.PauseTimerRequest
     * @see de.uol.swp.common.game.message.PauseTimerMessage
     * @since 2021-05-02
     */
    @Subscribe
    private void onPauseTimerRequest(PauseTimerRequest req) {
        String lobbyName = req.getOriginLobby();
        LOG.debug("Received PauseTimerRequest for Lobby {}", lobbyName);
        Game game = gameManagement.getGame(req.getOriginLobby());
        game.setPaused(true);
        ServerMessage msg = new PauseTimerMessage(req.getOriginLobby(), req.getUser());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
    }

    /**
     * Handles a PlayKnightCardRequest found on the EventBus
     * <p>
     * If a PlayKnightCardRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param req The PlayKnightCardRequest found on the EventBus
     *
     * @author Mario Fokken
     * @see de.uol.swp.common.game.request.PlayCardRequest.PlayKnightCardRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onPlayKnightCardRequest(PlayKnightCardRequest req) {
        LOG.debug("Received KnightCardPlayedMessage for Lobby {}", req.getOriginLobby());
        LOG.debug("---- User {} wants to improve their army", req.getUser().getUsername());

        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready()) return;
        Inventory inv = game.getInventory(req.getUser());

        if (inv.getKnightCards() == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending PlayCardFailureResponse");
            LOG.debug("---- Not enough Knight cards");
            return;
        }
        inv.setKnights(inv.getKnights() + 1);
        checkLargestArmy(req.getOriginLobby(), req.getUser());
        inv.increaseKnightCards(-1);

        robberMovementPlayer(req, req.getUser());

        I18nWrapper knightCard = new I18nWrapper("game.resources.cards.knight");
        ServerMessage returnSystemMessage = new SystemMessageForPlayingCardsMessage(req.getOriginLobby(), req.getUser(),
                                                                                    knightCard);
        LOG.debug("Sending SystemMessageForPlayingCardsMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);

        ResponseMessage returnMessage = new PlayCardSuccessResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        LOG.debug("Sending PlayCardSuccessResponse");
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
        endGameIfPlayerWon(game, req.getOriginLobby(), req.getUser());
    }

    /**
     * Handles a PlayMonopolyCardRequest found on the EventBus
     * <p>
     * If a PlayMonopolyCardRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param req The PlayMonopolyCardRequest found on the EventBus
     *
     * @author Mario Fokken
     * @see de.uol.swp.common.game.request.PlayCardRequest.PlayMonopolyCardRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onPlayMonopolyCardRequest(PlayMonopolyCardRequest req) {
        LOG.debug("Received MonopolyCardPlayedMessage for Lobby {}", req.getOriginLobby());
        LOG.debug("---- User {} wants to monopolise {}", req.getUser().getUsername(), req.getResource().name());

        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready()) return;
        Inventory invMono = game.getInventory(req.getUser());

        if (invMono.getMonopolyCards() == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending PlayCardFailureResponse");
            LOG.debug("---- Not enough Monopoly cards");
            return;
        }
        Inventory[] inventories = game.getAllInventories();

        switch (req.getResource()) {
            case ORE:
                for (Inventory inv : inventories)
                    if (inv.getOre() > 0) {
                        invMono.increaseOre(inv.getOre());
                        inv.increaseOre(-inv.getOre());
                    }
                break;
            case WOOL:
                for (Inventory inv : inventories)
                    if (inv.getWool() > 0) {
                        invMono.increaseWool(inv.getWool());
                        inv.increaseWool(-inv.getWool());
                    }
                break;
            case BRICK:
                for (Inventory inv : inventories)
                    if (inv.getBrick() > 0) {
                        invMono.increaseBrick(inv.getBrick());
                        inv.increaseBrick(-inv.getBrick());
                    }
                break;
            case GRAIN:
                for (Inventory inv : inventories)
                    if (inv.getGrain() > 0) {
                        invMono.increaseGrain(inv.getGrain());
                        inv.increaseGrain(-inv.getGrain());
                    }
                break;
            case LUMBER:
                for (Inventory inv : inventories)
                    if (inv.getLumber() > 0) {
                        invMono.increaseLumber(inv.getLumber());
                        inv.increaseLumber(-inv.getLumber());
                    }
                break;
        }

        invMono.increaseMonopolyCards(-1);

        I18nWrapper monopolyCard = new I18nWrapper("game.resources.cards.monopoly");
        ServerMessage returnSystemMessage = new SystemMessageForPlayingCardsMessage(req.getOriginLobby(), req.getUser(),
                                                                                    monopolyCard);
        LOG.debug("Sending SystemMessageForPlayingCardsMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);
        ResponseMessage returnMessage = new PlayCardSuccessResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        LOG.debug("Sending PlayCardSuccessResponse");
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);

        for (UserOrDummy user : game.getPlayers()) {
            if (!(user instanceof Dummy)) {
                Inventory inventory = game.getInventory(user);
                List<Map<String, Object>> developmentCardList = Collections
                        .unmodifiableList(getDevelopmentCardListFromInventory(inventory));
                List<Map<String, Object>> resourceList = Collections
                        .unmodifiableList(getResourceListFromInventory(inventory));
                ResponseMessage responseMessage = new UpdateInventoryResponse(user, req.getOriginLobby(),
                                                                              developmentCardList, resourceList);
                LOG.debug("Sending ForwardToUserInternalRequest with UpdateInventoryResponse to User {} in Lobby {}",
                          user, req.getOriginLobby());
                post(new ForwardToUserInternalRequest(user, responseMessage));
            }
        }
    }

    /**
     * Handles a PlayRoadBuildingCardRequest found on the EventBus
     * <p>
     * If a PlayRoadBuildingCardRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param req The PlayRoadBuildingCardRequest found on the EventBus
     *
     * @author Mario Fokken
     * @see de.uol.swp.common.game.request.PlayCardRequest.PlayRoadBuildingCardRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onPlayRoadBuildingCardRequest(PlayRoadBuildingCardRequest req) {
        LOG.debug("Received RoadBuildingCardPlayedMessage for Lobby {}", req.getOriginLobby());
        LOG.debug("---- User {} wants to build a road", req.getUser().getUsername());

        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready() || !game.isBuildingAllowed())
            return;
        Inventory inv = game.getInventory(req.getUser());

        if (inv.getRoadBuildingCards() == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending PlayCardFailureResponse");
            LOG.debug("---- Not enough RoadBuildingCardPhase cards");
            return;
        }

        LOG.debug("---- RoadBuildingCardPhase phase starts");
        game.setRoadBuildingCardPhase(WAITING_FOR_FIRST_ROAD);

        inv.increaseRoadBuildingCards(-1);

        I18nWrapper roadBuildingCard = new I18nWrapper("game.resources.cards.roadbuilding");
        ServerMessage returnSystemMessage = new SystemMessageForPlayingCardsMessage(req.getOriginLobby(), req.getUser(),
                                                                                    roadBuildingCard);
        LOG.debug("Sending SystemMessageForPlayingCardsMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);

        ResponseMessage returnMessage = new PlayCardSuccessResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        LOG.debug("Sending PlayCardSuccessResponse");
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
        endGameIfPlayerWon(game, req.getOriginLobby(), req.getUser());
    }

    /**
     * Handles a PlayYearOfPlentyCardRequest found on the EventBus
     * <p>
     * If a PlayYearOfPlentyCardRequest is detected on the EventBus, this method is called.
     * It then requests the GameManagement to handle the card.
     *
     * @param req The PlayYearOfPlentyCardRequest found on the EventBus
     *
     * @author Mario Fokken
     * @see de.uol.swp.common.game.request.PlayCardRequest.PlayYearOfPlentyCardRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onPlayYearOfPlentyCardRequest(PlayYearOfPlentyCardRequest req) {
        LOG.debug("Received YearOfPlentyCardPlayedMessage for Lobby {}", req.getOriginLobby());
        LOG.debug("---- User {} wants {} and {}", req.getUser().getUsername(), req.getResource1().name(),
                  req.getResource2().name());

        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready()) return;
        Inventory inv = game.getInventory(req.getUser());

        if (inv.getYearOfPlentyCards() == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending PlayCardFailureResponse");
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
        LOG.debug("Sending SystemMessageForPlayingCardsMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);

        ResponseMessage returnMessage = new PlayCardSuccessResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        LOG.debug("Sending PlayCardSuccessResponse");
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
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
        LOG.debug("Received ResetOfferTradeButtonRequest for Lobby {}", req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getOfferingUser()) || !game.isDiceRolledAlready()) return;
        game.setBuildingAllowed(true);
        Inventory offeringInventory = game.getInventory(req.getOfferingUser());
        if (offeringInventory == null) return;
        ResponseMessage returnMessage = new ResetOfferTradeButtonResponse(req.getOriginLobby());
        LOG.debug("Sending ResetOfferTradeButtonResponse for Lobby {}", req.getOriginLobby());
        post(new ForwardToUserInternalRequest(req.getOfferingUser(), returnMessage));
    }

    /**
     * Handles a ReturnToPreGameLobbyMessage found on the EventBus
     * <p>
     * If a ReturnToPreGameLobbyMessage is found on the EventBus this method drops the
     * game associated with the Lobby, if one existed.
     *
     * @param msg The ReturnToPreGameLobbyMessage found on the EventBus
     *
     * @author Steven Luong
     * @since 2021-04-30
     */
    @Subscribe
    private void onReturnToPreGameLobbyMessage(ReturnToPreGameLobbyMessage msg) {
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
     * Handles a RobberChosenVictimRequest found on the EventBus.
     * If a RobberChosenVictimRequest is detected on the EventBus, this method is called.
     * It then decreases the resources in the player's inventory
     *
     * @param msg The RobberChosenVictimRequest found on the EventBus
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @see de.uol.swp.common.game.robber.RobberChosenVictimRequest
     * @since 2021-04-05
     */
    @Subscribe
    private void onRobberChosenVictimRequest(RobberChosenVictimRequest msg) {
        LOG.debug("Received RobberChosenVictimRequest for Lobby {}", msg.getLobby());
        robRandomResource(msg.getLobby(), msg.getPlayer(), msg.getVictim());
    }

    /**
     * Handles a RobberNewPositionChosenRequest found on the EventBus.
     * If a RobberNewPositionChosenRequest is detected on the EventBus, this method is called.
     * It then decreases the resources in the player's inventory
     *
     * @param msg The RobberNewPositionChosenRequest found on the EventBus
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @see de.uol.swp.common.game.robber.RobberNewPositionChosenRequest
     * @since 2021-04-05
     */
    @Subscribe
    private void onRobberNewPositionChosenRequest(RobberNewPositionChosenRequest msg) {
        LOG.debug("Received RobberNewPositionChosenRequest for Lobby {}", msg.getLobby());
        IGameMapManagement map = gameManagement.getGame(msg.getLobby()).getMap();
        map.moveRobber(msg.getPosition());
        LOG.debug("Sending RobberPositionMessage for Lobby {}", msg.getLobby());
        AbstractGameMessage rpm = new RobberPositionMessage(msg.getLobby(), msg.getPlayer(), msg.getPosition());
        lobbyService.sendToAllInLobby(msg.getLobby(), rpm);
        Set<Player> players = map.getPlayersAroundHex(msg.getPosition());
        Set<UserOrDummy> victims = new HashSet<>();
        for (Player p : players) victims.add(gameManagement.getGame(msg.getLobby()).getUserFromPlayer(p));
        if (players.size() > 1) {
            LOG.debug("Sending RobberChooseVictimResponse for Lobby {}", msg.getLobby());
            ResponseMessage rcvm = new RobberChooseVictimResponse(msg.getPlayer(), victims);
            rcvm.initWithMessage(msg);
            post(rcvm);
        } else if (players.size() == 1) {
            robRandomResource(msg.getLobby(), msg.getPlayer(), new ArrayList<>(victims).get(0));
        }
    }

    /**
     * Handles a RobberTaxChosenRequest found on the EventBus.
     * If a RobberTaxChosenRequest is detected on the EventBus, this method is called.
     * It then decreases the resources in the player's inventory
     *
     * @param req The RobberTaxChosenRequest found on the EventBus
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @see de.uol.swp.common.game.robber.RobberTaxChosenRequest
     * @since 2021-04-05
     */
    @Subscribe
    private void onRobberTaxChosenRequest(RobberTaxChosenRequest req) {
        LOG.debug("Received RobberTaxChosenRequest for Lobby {}", req.getLobby());
        Inventory i = gameManagement.getGame(req.getLobby()).getInventory(req.getPlayer());
        for (Resources r : req.getResources().keySet()) {
            switch (r) {
                case ORE:
                    i.increaseOre(req.getResources().get(r) * -1);
                    break;
                case WOOL:
                    i.increaseWool(req.getResources().get(r) * -1);
                    break;
                case BRICK:
                    i.increaseBrick(req.getResources().get(r) * -1);
                    break;
                case GRAIN:
                    i.increaseGrain(req.getResources().get(r) * -1);
                    break;
                case LUMBER:
                    i.increaseLumber(req.getResources().get(r) * -1);
                    break;
            }
        }
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getLobby());
        ServerMessage msg = new RefreshCardAmountMessage(req.getLobby(), req.getPlayer(),
                                                         gameManagement.getGame(req.getLobby()).getCardAmounts());
        lobbyService.sendToAllInLobby(req.getLobby(), msg);

        Game game = gameManagement.getGame(req.getLobby());
        game.removeTaxPayer(req.getPlayer());
        if (game.getTaxPayers().isEmpty()) lobbyService
                .sendToAllInLobby(req.getLobby(), new RobberAllTaxPayedMessage(req.getLobby(), game.getActivePlayer()));
        endTurnDummy(game);
    }

    /**
     * Handles a RollDiceRequest found on the EventBus
     * If a RollDiceRequest is detected on the EventBus, this method is called.
     * It then sends a DiceCastMessage to all members in the lobby.
     *
     * @param req The RollDiceRequest found on the EventBus
     *
     * @author Mario Fokken
     * @see de.uol.swp.common.game.request.RollDiceRequest
     * @see de.uol.swp.common.game.message.DiceCastMessage
     * @see de.uol.swp.common.game.robber.RobberTaxMessage
     * @since 2021-02-22
     */
    @Subscribe
    private void onRollDiceRequest(RollDiceRequest req) {
        LOG.debug("Received RollDiceRequest for Lobby {}", req.getOriginLobby());
        LOG.debug("---- User {} wants to roll the dices.", req.getUser().getUsername());

        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getUser()) || game.isDiceRolledAlready()) return;
        int[] result = Game.rollDice();
        int numberOfPips = result[0] + result[1];
        if (numberOfPips == 7) {
            //Robber things
            LOG.debug("---- Robber things");
            Map<User, Integer> players = new HashMap<>();
            Game g = gameManagement.getGame(req.getOriginLobby());
            for (UserOrDummy p : g.getPlayers()) {
                if (g.getInventory(p).getResourceAmount() > 7) {
                    //Takes a dummy's resources away
                    if (p instanceof Dummy) {
                        Inventory inv = g.getInventory(p);
                        int i = inv.getResourceAmount() / 2;
                        LOG.debug("{} has to give up {} of their {} cards", p, i, inv.getResourceAmount());
                        while (i > 0) {
                            if (inv.getBrick() > 0) {
                                inv.increaseBrick(-1);
                                i--;
                                if (i == 0) break;
                            }
                            if (inv.getGrain() > 0) {
                                inv.increaseGrain(-1);
                                i--;
                                if (i == 0) break;
                            }
                            if (inv.getLumber() > 0) {
                                inv.increaseLumber(-1);
                                i--;
                                if (i == 0) break;
                            }
                            if (inv.getOre() > 0) {
                                inv.increaseOre(-1);
                                i--;
                                if (i == 0) break;
                            }
                            if (inv.getWool() > 0) {
                                inv.increaseWool(-1);
                                i--;
                                if (i == 0) break;
                            }
                        }
                    } else {
                        players.put((User) p, g.getInventory(p).getResourceAmount() / 2);
                    }
                }
            }
            Map<User, Map<Resources, Integer>> inventory = new HashMap<>();
            for (User user : players.keySet()) {
                Map<Resources, Integer> resourceMap = new LinkedHashMap<>();
                Inventory inv = game.getInventory(user);
                resourceMap.put(Resources.BRICK, inv.getBrick());
                resourceMap.put(Resources.GRAIN, inv.getGrain());
                resourceMap.put(Resources.LUMBER, inv.getLumber());
                resourceMap.put(Resources.ORE, inv.getOre());
                resourceMap.put(Resources.WOOL, inv.getWool());
                inventory.put(user, resourceMap);

                game.addTaxPayer(user);
            }
            RobberTaxMessage rtm = new RobberTaxMessage(req.getOriginLobby(), req.getUser(), players, inventory);
            LOG.debug("Sending RobberTaxMessage for Lobby {}", req.getOriginLobby());
            lobbyService.sendToAllInLobby(req.getOriginLobby(), rtm);
            if (req.getUser() instanceof Dummy) {
                robberMovementDummy((Dummy) req.getUser(), req.getOriginLobby());
            } else {
                robberMovementPlayer(req, (User) req.getUser());
            }
        } else {
            LOG.debug("---- Distributing the resources for token {}", numberOfPips);
            game.distributeResources(numberOfPips);
        }
        game.setBuildingAllowed(true);
        game.setDiceRolledAlready(true);
        ServerMessage returnMessage = new DiceCastMessage(req.getOriginLobby(), req.getUser(), result[0], result[1]);
        LOG.debug("Sending DiceCastMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
    }

    /**
     * Handles a TradeWithBankRequest found on the EventBus
     * <p>
     * It searches the inventories in the current game for the one that belongs
     * to the player sending the request. It then posts an InventoryForTradeResponse
     * that contains all the user's resources, saved in a resourceMap for
     * counted items (bricks, grain, etc.) and all of the harbors of the user.
     *
     * @param req The TradeWithBankRequest found on the EventBus
     *
     * @author Steven Luong
     * @author Maximilian Lindner
     * @author Alwin Bossert
     * @see de.uol.swp.common.game.request.TradeWithBankRequest
     * @see de.uol.swp.common.game.response.InventoryForTradeResponse
     * @since 2021-04-07
     */
    @Subscribe
    private void onTradeWithBankRequest(TradeWithBankRequest req) {
        LOG.debug("Received TradeWithBankRequest for Lobby {}", req.getName());
        Game game = gameManagement.getGame(req.getName());
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready()) return;
        Inventory inventory = game.getInventory(req.getUser());
        if (inventory == null) return;
        List<Map<String, Object>> resourceList = getResourceListFromInventory(inventory);

        IGameMapManagement gameMap = game.getMap();
        Map<Player, List<MapPoint>> settlementsAndCities = gameMap.getPlayerSettlementsAndCities();
        Player player = game.getPlayer(req.getUser());
        List<IHarborHex.HarborResource> harborTradingList = new ArrayList<>();
        if (settlementsAndCities.containsKey(player)) {
            List<MapPoint> ownSettlementsAndCities = settlementsAndCities.get(player);
            for (MapPoint ownSettlementsAndCity : ownSettlementsAndCities) {
                IHarborHex.HarborResource resource = gameMap.getHarborResource(ownSettlementsAndCity);
                harborTradingList.add(resource);
            }
        }

        ResponseMessage returnMessage = new InventoryForTradeResponse(req.getUser(), req.getName(),
                                                                      Collections.unmodifiableList(resourceList),
                                                                      harborTradingList);
        returnMessage.initWithMessage(req);
        LOG.debug("Sending InventoryForTradeResponse for Lobby {}", req.getName());
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
        LOG.debug("Received TradeWithUserCancelRequest for Lobby {}", req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        if (req.getSession().isEmpty()) return;
        if (!game.getActivePlayer().equals(req.getSession().get().getUser()) || !game.isDiceRolledAlready()) return;
        game.setBuildingAllowed(true);
        Inventory respondingInventory = game.getInventory(req.getRespondingUser());

        if (respondingInventory == null) return;
        ResponseMessage returnMessageForOfferingUser = new TradeWithUserCancelResponse(req.getOriginLobby(),
                                                                                       game.getActivePlayer());
        returnMessageForOfferingUser.initWithMessage(req);
        LOG.debug("Sending TradeWithUserCancelResponse for Lobby {}", req.getOriginLobby());
        post(returnMessageForOfferingUser);
        ResponseMessage returnMessageForRespondingUser = new TradeWithUserCancelResponse(req.getOriginLobby(),
                                                                                         game.getActivePlayer());
        LOG.debug("Sending TradeWithUserCancelResponse for Lobby {}", req.getOriginLobby());
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
        LOG.debug("Received TradeWithUserRequest for Lobby {}", req.getName());
        Game game = gameManagement.getGame(req.getName());
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready()) return;
        game.setBuildingAllowed(false);
        Inventory inventory = game.getInventory(req.getUser());
        Inventory traderInventory = game.getInventory(req.getRespondingUser());
        if (inventory == null || traderInventory == null) return;
        int traderInventorySize = traderInventory.getResourceAmount();
        List<Map<String, Object>> offeringInventory = getResourceListFromInventory(inventory);
        ResponseMessage returnMessage;
        returnMessage = new InventoryForTradeWithUserResponse(req.getUser(), req.getName(),
                                                              Collections.unmodifiableList(offeringInventory),
                                                              traderInventorySize, req.getRespondingUser());
        LOG.debug("Sending InventoryForTradeWithUserResponse for Lobby {}", req.getName());
        returnMessage.initWithMessage(req);
        post(returnMessage);
    }

    /**
     * Handles an TransferLobbyStateEvent found on the EventBus
     * <p>
     * If an TransferLobbyStateEvent is found on the EventBus, this method
     * is called. It crafts a StartSessionResponse with the lobby
     * provided by the event, its active player, the game configuration
     * and the previously rolled dice and posts it on the EventBus.
     *
     * @param event The TransferLobbyStateEvent found on the EventBus
     *
     * @author Marvin Drees
     * @author Maximilian Lindner
     * @since 2021-04-09
     */
    @Subscribe
    private void onTransferLobbyStateEvent(TransferLobbyStateEvent event) {
        Lobby lobby = event.getLobby();
        Game game = gameManagement.getGame(lobby.getName());
        Map<Player, UserOrDummy> playerUserOrDummyMap = game.getPlayerUserMapping();
        Optional<MessageContext> ctx = event.getMessageContext();
        if (ctx.isPresent()) {
            ResponseMessage returnMessage = new StartSessionResponse(lobby, game.getActivePlayer(),
                                                                     lobby.getConfiguration(),
                                                                     game.getMap().getGameMapDTO(playerUserOrDummyMap),
                                                                     game.getDices(), game.isDiceRolledAlready(),
                                                                     game.getAutoRollEnabled(event.getUser()),
                                                                     game.getLobby().getMoveTime(),
                                                                     game.getPlayersStartUpBuiltMap()
                                                                         .get(event.getUser()));
            returnMessage.setMessageContext(ctx.get());
            LOG.debug("Sending StartSessionResponse");
            post(returnMessage);
        }
    }

    /**
     * Handles an UnpauseTimerRequest found on the EventBus
     * <p>
     * If an UnpauseTimerRequest is found on the EventBus,
     * the game gets unpaused.
     * It also posts a new UnpauseTimerMessage to all the players in the lobby.
     *
     * @param req The UnpauseTimerRequest found on the EventBus
     *
     * @author Alwin Bossert
     * @see de.uol.swp.common.game.request.UnpauseTimerRequest
     * @see de.uol.swp.common.game.message.UnpauseTimerMessage
     * @since 2021-05-02
     */
    @Subscribe
    private void onUnpauseTimerRequest(UnpauseTimerRequest req) {
        String lobbyName = req.getOriginLobby();
        LOG.debug("Received UnpauseTimerRequest for Lobby {}", lobbyName);
        Game game = gameManagement.getGame(req.getOriginLobby());
        game.setPaused(false);
        ServerMessage msg = new UnpauseTimerMessage(req.getOriginLobby(), req.getUser());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
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
        Map<Player, UserOrDummy> playerUserOrDummyMap = game.getPlayerUserMapping();
        LOG.debug("Sending UpdateGameMapResponse");
        UpdateGameMapResponse rsp = new UpdateGameMapResponse(req.getOriginLobby(),
                                                              game.getMap().getGameMapDTO(playerUserOrDummyMap));
        rsp.initWithMessage(req);
        post(rsp);
    }

    /**
     * Handles a UpdateInventoryRequest found on the EventBus
     * <p>
     * It searches the inventories in the current game for the one that belongs
     * to the player sending the request. It then posts a UpdateInventoryResponse
     * that contains all the user's items, saved in a resourceMap for
     * counted items (bricks, grain, etc.).
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
        LOG.debug("Received UpdateInventoryRequest for Lobby {}", req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory inventory = game.getInventory(req.getUser());
        if (inventory == null) return;
        List<Map<String, Object>> developmentCardList = getDevelopmentCardListFromInventory(inventory);
        List<Map<String, Object>> resourceList = getResourceListFromInventory(inventory);
        ResponseMessage returnMessage = new UpdateInventoryResponse(req.getUser(), req.getOriginLobby(),
                                                                    developmentCardList, resourceList);
        returnMessage.initWithMessage(req);
        LOG.debug("Sending UpdateInventoryResponse for Lobby {}", req.getOriginLobby());
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
        endGameIfPlayerWon(game, req.getOriginLobby(), req.getUser());
    }

    /**
     * Helper method to rob a player of a random resource card
     *
     * @param receiver Player to receive the card
     * @param victim   Player to lose a card
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-06
     */
    private void robRandomResource(String lobby, UserOrDummy receiver, UserOrDummy victim) {
        LOG.debug("{} wants to rob from {} in Lobby {}", receiver, victim, lobby);
        Inventory receiverInventory = gameManagement.getGame(lobby).getInventory(receiver);
        Inventory victimInventory = gameManagement.getGame(lobby).getInventory(victim);
        List<Resources> victimsResources = new ArrayList<>();
        if (victimInventory.getResourceAmount() == 0) {
            ServerMessage returnSystemMessage = new SystemMessageForRobbingMessage(lobby, receiver, null);
            LOG.debug("Sending SystemMessageForRobbingMessage for Lobby {}", lobby);
            LOG.debug("---- Victim has no cards to rob");
            lobbyService.sendToAllInLobby(lobby, returnSystemMessage);
            return;
        }
        if (victimInventory.getBrick() > 0) victimsResources.add(Resources.BRICK);
        if (victimInventory.getGrain() > 0) victimsResources.add(Resources.GRAIN);
        if (victimInventory.getLumber() > 0) victimsResources.add(Resources.LUMBER);
        if (victimInventory.getOre() > 0) victimsResources.add(Resources.ORE);
        if (victimInventory.getWool() > 0) victimsResources.add(Resources.WOOL);

        switch (victimsResources.get((int) (Math.random() * victimsResources.size()))) {
            case BRICK:
                victimInventory.increaseBrick(-1);
                receiverInventory.increaseBrick(1);
                break;
            case GRAIN:
                victimInventory.increaseGrain(-1);
                receiverInventory.increaseGrain(1);
                break;
            case LUMBER:
                victimInventory.increaseLumber(-1);
                receiverInventory.increaseLumber(1);
                break;
            case ORE:
                victimInventory.increaseOre(-1);
                receiverInventory.increaseOre(1);
                break;
            case WOOL:
                victimInventory.increaseWool(-1);
                receiverInventory.increaseWool(1);
                break;
        }
        ServerMessage returnSystemMessage = new SystemMessageForRobbingMessage(lobby, receiver, victim);
        ServerMessage msg = new RefreshCardAmountMessage(lobby, receiver,
                                                         gameManagement.getGame(lobby).getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", lobby);
        lobbyService.sendToAllInLobby(lobby, msg);
        LOG.debug("Sending SystemMessageForRobbingMessage for Lobby {}", lobby);
        lobbyService.sendToAllInLobby(lobby, returnSystemMessage);
    }

    /**
     * Helper method to move the robber when
     * a dummy gets a seven.
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-06
     */
    private void robberMovementDummy(Dummy dummy, String lobby) {
        IGameMapManagement map = gameManagement.getGame(lobby).getMap();
        int y = (int) (Math.random() * 4 + 1);
        int x = (y == 1 || y == 5) ? ((int) (Math.random() * 3 + 1)) :
                ((y == 2 || y == 4) ? ((int) (Math.random() * 4 + 1)) : ((int) (Math.random() * 5 + 1)));
        MapPoint mapPoint = MapPoint.HexMapPoint(y, x);
        map.moveRobber(mapPoint);
        LOG.debug("Sending RobberPositionMessage for Lobby {}", lobby);
        AbstractGameMessage msg = new RobberPositionMessage(lobby, dummy, mapPoint);
        lobbyService.sendToAllInLobby(lobby, msg);
        LOG.debug("{} moves the robber to position: {}|{}", dummy, y, x);
        Set<Player> players = map.getPlayersAroundHex(mapPoint);
        Set<UserOrDummy> players2 = new HashSet<>();
        for (Player p : players) {
            players2.add(gameManagement.getGame(lobby).getUserFromPlayer(p));
        }
        if (players.size() > 0) {
            int i = (int) (Math.random() * players.size());
            UserOrDummy victim = (UserOrDummy) players2.toArray()[i];
            robRandomResource(lobby, dummy, victim);
        }
    }

    /**
     * Helper method to move the robber when
     * a player gets a seven.
     * Sends a RobberNewPositionResponse to the lobby
     * after an onRollDiceRequest or an onPlayKnightCardRequest
     *
     * @param req AbstractGameRequest
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-05
     */
    private void robberMovementPlayer(AbstractGameRequest req, User player) {
        LOG.debug("Sending RobberNewPositionResponse for Lobby {}", req.getOriginLobby());
        RobberNewPositionResponse msg = new RobberNewPositionResponse(player);
        msg.initWithMessage(req);
        post(msg);
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
            LOG.debug("Sending SystemMessageForTradeWithBankResponse for Lobby {}", lobbyName);
            post(new ForwardToUserInternalRequest(user, serverMessage));
            LOG.debug("Sending SystemMessageForTradeWithBankMessage for Lobby {}", lobbyName);
            lobbyService.sendToAllInLobby(lobbyName, new SystemMessageForTradeWithBankMessage(lobbyName, user));
        }
        return true;
    }
}

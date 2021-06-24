package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.dto.InGameSystemMessageDTO;
import de.uol.swp.common.chat.message.SystemMessageMessage;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.chat.response.SystemMessageResponse;
import de.uol.swp.common.exception.ExceptionMessage;
import de.uol.swp.common.exception.LobbyExceptionMessage;
import de.uol.swp.common.game.StartUpPhaseBuiltStructures;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.configuration.IConfiguration;
import de.uol.swp.common.game.map.hexes.IHarbourHex;
import de.uol.swp.common.game.map.hexes.IHarbourHex.HarbourResource;
import de.uol.swp.common.game.map.hexes.ResourceHex;
import de.uol.swp.common.game.map.management.IEdge;
import de.uol.swp.common.game.map.management.IIntersection;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.message.*;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.BankInventory;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.Inventory;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.IDevelopmentCardList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.*;
import de.uol.swp.common.game.response.*;
import de.uol.swp.common.game.robber.*;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.message.LobbyDeletedMessage;
import de.uol.swp.common.lobby.message.StartSessionMessage;
import de.uol.swp.common.lobby.request.KickUserRequest;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.specialisedUtil.*;
import de.uol.swp.common.user.*;
import de.uol.swp.common.util.Util;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.event.CreateGameInternalRequest;
import de.uol.swp.server.game.event.ForwardToUserInternalRequest;
import de.uol.swp.server.game.event.KickUserEvent;
import de.uol.swp.server.game.map.IGameMapManagement;
import de.uol.swp.server.lobby.ILobby;
import de.uol.swp.server.lobby.ILobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.specialisedUtil.ActorStartUpBuiltMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static de.uol.swp.common.game.RoadBuildingCardPhase.*;
import static de.uol.swp.common.game.StartUpPhaseBuiltStructures.*;
import static de.uol.swp.common.game.message.BuildingSuccessfulMessage.Type.*;
import static de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType.*;
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

    protected static final Logger LOG = LogManager.getLogger(GameService.class);

    private final ILobbyManagement lobbyManagement;
    private final IGameManagement gameManagement;
    private final LobbyService lobbyService;
    private final GameAI gameAI;

    /**
     * Constructor
     *
     * @param bus            The EventBus used throughout the entire server (injected)
     * @param gameManagement The GameManagement to use (injected)
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
        this.gameAI = new GameAI(this, gameManagement, lobbyService);
        LOG.debug("GameService started");
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
    void checkLargestArmy(LobbyName lobbyName, Actor user) {
        Game game = gameManagement.getGame(lobbyName);
        Inventory largest = game.getInventory(game.getPlayerWithLargestArmy());
        if ((largest == null || game.getInventory(user).getKnights() > largest.getKnights()) && game.getInventory(user)
                                                                                                    .getKnights() > 2) {
            game.setPlayerWithLargestArmy(game.getPlayer(user));
        }
        lobbyService
                .sendToAllInLobby(lobbyName, new UpdateUniqueCardsListMessage(lobbyName, game.getUniqueCardsList()));
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
    void onBuyDevelopmentCardRequest(BuyDevelopmentCardRequest req) {
        LOG.debug("Received BuyDevelopmentCardRequest for Lobby {}", req.getOriginLobby());
        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getActor()) || !game.isDiceRolledAlready() || game.isPausedByVoting())
            return;
        BankInventory bankInventory = game.getBankInventory();
        if (bankInventory.getDevelopmentCards() != null && !bankInventory.getDevelopmentCards().isEmpty()) {
            DevelopmentCardType developmentCard = bankInventory.getRandomDevelopmentCard();
            if (updatePlayersInventoryWithDevelopmentCard(developmentCard, req.getActor(), req.getOriginLobby())) {
                bankInventory.decrease(developmentCard);
                ResponseMessage returnMessage = new BuyDevelopmentCardResponse(req.getActor(), req.getOriginLobby(),
                                                                               developmentCard);
                returnMessage.initWithMessage(req);
                LOG.debug("Sending BuyDevelopmentCardResponse for Lobby {}", req.getOriginLobby());
                post(returnMessage);
                ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getActor(),
                                                                 game.getCardAmounts());
                LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
                lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
                updateVictoryPoints(req.getOriginLobby());
                endGameIfPlayerWon(game, req.getOriginLobby(), req.getActor());
            } else LOG.debug("In the Lobby {} the User {} couldn't buy a Development Card", req.getOriginLobby(),
                             req.getActor().getUsername());
        } else LOG.debug("No Development Cards left in Inventory for Lobby {}", req.getOriginLobby());
    }

    /**
     * Method to post a message for the AI
     *
     * @param ai        The AI to send the message
     * @param content   The content of said message
     * @param lobbyName The lobby to send the message in
     *
     * @author Mario Fokken
     * @since 2021-05-17
     */
    void postAI(AI ai, String content, LobbyName lobbyName) {
        Message msg = new NewChatMessageRequest(ai, content, lobbyName);
        post(msg);
    }

    /**
     * Helper method to rob a random resource
     * <p>
     * It sets the game's robResourceReceiverVictimPair and then tries
     * to rob a random resource
     *
     * @param game     The game the resource robbing takes place
     * @param receiver The Actor to receive the resource
     * @param victim   The Actor to lose a resource
     *
     * @author Mario Fokken
     * @since 2021-06-11
     */
    void robRandomResource(Game game, Actor receiver, Actor victim) {
        game.setRobResourceReceiverVictimPair(new ActorPair(receiver, victim));
        robRandomResource(game);
    }

    /**
     * Helper method to end an AI's turn
     *
     * @param game The game the AI is in
     * @param ai   The AI to make its turn
     *
     * @author Mario Fokken
     * @since 2021-05-13
     */
    void turnEndAI(Game game, AI ai) {
        if (game.getTaxPayers().isEmpty()) onEndTurnRequest(new EndTurnRequest(ai, game.getLobby().getName()));
    }

    /**
     * Helper method to handle the calculation of Victory Points of each User in the Lobby
     *
     * @param originLobby The lobby in which the game is taking place
     *
     * @author Steven Luong
     * @see de.uol.swp.common.game.message.UpdateVictoryPointsMessage
     * @since 2021-05-21
     */
    void updateVictoryPoints(LobbyName originLobby) {
        Game game = gameManagement.getGame(originLobby);
        Actor[] players = game.getPlayers();
        ActorIntegerMap victoryPointsMap = new ActorIntegerMap();
        for (Actor player : players) {
            if (player instanceof User) {
                victoryPointsMap.put(player, game.calculateVictoryPoints(game.getPlayer(player)));
            }
            VictoryPointOverTimeMap victoryPointsOverTimeMap = game.getVictoryPointsOverTimeMap();
            Map<Integer, Integer> integerIntegerMap = victoryPointsOverTimeMap.get(player);
            int round = game.getRound();
            Player player1 = game.getPlayer(player);
            int victoryPoints = game.calculateVictoryPoints(player1);
            integerIntegerMap.put(round, victoryPoints);
        }
        ServerMessage msg = new UpdateVictoryPointsMessage(originLobby, victoryPointsMap);
        lobbyService.sendToAllInLobby(originLobby, msg);
    }

    /**
     * Helper method to transform a resource list into the corresponding string.
     *
     * @param resourceMap The resource list containing the traded resources
     *
     * @return The string containing the traded resources
     *
     * @author Marvin Drees
     * @since 2021-05-11
     */
    private String buildTradeString(IResourceList resourceMap) {
        StringBuilder tradeString = new StringBuilder();
        for (IResource entry : resourceMap) {
            if (entry.getAmount() > 0) {
                tradeString.append(", ");
                tradeString.append(entry.getAmount()).append(" ");
                tradeString.append(entry.getType().toString());
            }
        }
        return tradeString.toString().replaceFirst("^, ", "");
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
    private boolean checkEnoughResourcesInInventory(Inventory inventoryList, ResourceList neededResourceList) {
        for (IResource resource : neededResourceList) {
            if (resource.getAmount() > inventoryList.get(resource.getType())) return false;
        }
        return true;
    }

    /**
     * Handles the allocation of the longest Road
     *
     * @param lobbyName The lobbyname
     * @param mapPoint  The map point pointing to the edge based on which the longest road should be checked
     *
     * @author Eric Vuong
     * @author Temmo Junkhoff
     * @since 2021-04-10
     */
    private void checkLongestRoad(LobbyName lobbyName, MapPoint mapPoint) {
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
     * Helper method to realise a dummies turn in the founder phase
     * Checks whether the next player in the founding phase player queue is a dummy
     * and builds the founding settlement and road accordingly.
     *
     * @param game     The game, the dummy is in
     * @param computer The player whose turn the next one is
     *
     * @author Sven Ahrens
     * @since 2021-05-22
     */
    private void dummyTurnInFoundingPhase(Game game, Computer computer) {
        boolean roadPlaced = false;

        IGameMapManagement map = game.getMap();
        MapPoint randomCoordinates = map.getRandomFreeIntersection(game, game.getPlayer(computer));
        onBuildRequest(new BuildRequest(game.getLobby().getName(), computer, randomCoordinates));

        Set<IEdge> incidentEdges = map.getEdgesAroundIntersection(randomCoordinates);
        while (!roadPlaced) {
            {
                for (IEdge edge : incidentEdges) {
                    if (map.roadPlaceable(game.getPlayer(computer), edge)) {
                        onBuildRequest(
                                new BuildRequest(game.getLobby().getName(), computer, map.getEdgeMapPoint(edge)));
                        roadPlaced = true;
                        break;
                    }
                }
            }
        }
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
    private void endGameIfPlayerWon(Game game, LobbyName originLobby, Actor user) {
        int vicPoints = game.calculateVictoryPoints(game.getPlayer(user));
        if (vicPoints >= 10) {
            ServerMessage message = new PlayerWonGameMessage(originLobby, user, game.getVictoryPointsOverTimeMap());
            lobbyService.sendToAllInLobby(originLobby, message);
            game.setBuildingAllowed(false);
            for (Actor ai : game.getPlayers())
                if (ai instanceof AI) gameAI.writeChatMessageAI((AI) ai, originLobby,
                                                                user.getUsername().equals(ai.getUsername()) ?
                                                                AI.WriteType.GAME_WIN : AI.WriteType.GAME_LOSE);
        }
    }

    /**
     * Helper method to create a singular I18nWrapper from the resource maps
     *
     * @param offeringUser          The name of the offering user
     * @param respondingUser        The name of the responding user
     * @param offeringResourceMap   The Map of resources that were offered as a
     *                              Map of I18nWrappers to amount
     * @param respondingResourceMap The Map of resources that were demanded as
     *                              a Map of I18nWrappers to amount
     *
     * @return An I18nWrapper that contains all the details provided and will
     * be displayed in the client's chosen language
     */
    private I18nWrapper makeSingularI18nWrapper(Actor offeringUser, String respondingUser,
                                                IResourceList offeringResourceMap,
                                                IResourceList respondingResourceMap) {
        String offerString = buildTradeString(offeringResourceMap);
        String demandString = buildTradeString(respondingResourceMap);
        return new I18nWrapper("lobby.trade.resources.systemmessage", offeringUser.getUsername(), respondingUser,
                               offerString, demandString);
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
        if (!game.isDiceRolledAlready() || game.isPausedByVoting()) return;
        if (!(game.getActivePlayer().equals(req.getOfferingUser()) || game.getActivePlayer()
                                                                          .equals(req.getRespondingUser()))) return;
        game.setBuildingAllowed(false);
        Inventory offeringInventory = game.getInventory(req.getOfferingUser());
        Inventory respondingInventory = game.getInventory(req.getRespondingUser());
        if (offeringInventory == null || respondingInventory == null) return;
        if (req.getOfferedResources() == null || req.getDemandedResources() == null) return;
        boolean enoughToOffer = checkEnoughResourcesInInventory(offeringInventory, req.getOfferedResources());
        boolean enoughToDemand = checkEnoughResourcesInInventory(respondingInventory, req.getDemandedResources());
        if (enoughToOffer && enoughToDemand) {
            for (IResource resource : req.getOfferedResources()) {
                offeringInventory.decrease(resource.getType(), resource.getAmount());
                respondingInventory.increase(resource.getType(), resource.getAmount());
            }
            for (IResource resource : req.getDemandedResources()) {
                offeringInventory.increase(resource.getType(), resource.getAmount());
                respondingInventory.decrease(resource.getType(), resource.getAmount());
            }

            ServerMessage returnSystemMessage = new SystemMessageMessage(req.getOriginLobby(),
                                                                         new InGameSystemMessageDTO(
                                                                                 makeSingularI18nWrapper(
                                                                                         req.getOfferingUser(),
                                                                                         req.getRespondingUser() == null ?
                                                                                         "bank" :
                                                                                         req.getRespondingUser()
                                                                                            .getUsername(),
                                                                                         req.getOfferedResources(),
                                                                                         req.getDemandedResources())));
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
        Game.StartUpPhase currentPhase = game.getStartUpPhase();
        Deque<Actor> startUpPlayerOrder = game.getStartUpPlayerOrder();
        if (currentPhase == Game.StartUpPhase.NOT_IN_STARTUP_PHASE) {
            if (!game.getActivePlayer().equals(req.getActor()) || !game.isDiceRolledAlready() || game
                    .isPausedByVoting()) {
                return;
            }
        } else if (startUpPlayerOrder.peekFirst() == null || !startUpPlayerOrder.peekFirst().equals(req.getActor())) {
            return;
        }
        Consumer<BuildingFailedResponse.Reason> sendFailResponse = reason -> {
            LOG.debug("Sending BuildingFailedResponse with reason {}", reason);
            BuildingFailedResponse msg = new BuildingFailedResponse(req.getOriginLobby(), reason);
            msg.initWithMessage(req);
            post(msg);
        };

        BiConsumer<LobbyName, BuildingSuccessfulMessage> sendSuccess = (lobbyName, message) -> {
            LOG.debug("Sending BuildingSuccessfulMessage");
            lobbyService.sendToAllInLobby(lobbyName, message);
            updateVictoryPoints(req.getOriginLobby());
            endGameIfPlayerWon(game, lobbyName, req.getActor());
        };

        if (!game.isBuildingAllowed() && currentPhase == Game.StartUpPhase.NOT_IN_STARTUP_PHASE) {
            sendFailResponse.accept(NOT_THE_RIGHT_TIME);
            return;
        }
        IGameMapManagement gameMap = game.getMap();
        MapPoint mapPoint = req.getMapPoint();
        Actor user = req.getActor();
        Player player = game.getPlayer(user);
        Inventory inv = game.getInventory(user);

        switch (mapPoint.getType()) {
            case INTERSECTION: {
                if (gameMap.getIntersection(mapPoint).getState() == IIntersection.IntersectionState.CITY) {
                    sendFailResponse.accept(ALREADY_BUILT_HERE);
                } else if (gameMap.settlementPlaceable(player, mapPoint)) {
                    if (inv.get(BRICK) >= 1 && inv.get(LUMBER) >= 1 && inv.get(WOOL) >= 1 && inv.get(GRAIN) >= 1) {
                        inv.increase(BRICK, -1);
                        inv.increase(LUMBER, -1);
                        inv.increase(WOOL, -1);
                        inv.increase(GRAIN, -1);
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
                } else if (currentPhase != Game.StartUpPhase.NOT_IN_STARTUP_PHASE) {
                    ActorStartUpBuiltMap startUpBuiltMap = game.getPlayersStartUpBuiltMap();
                    StartUpPhaseBuiltStructures built = startUpBuiltMap.get(user);
                    if (built == NONE_BUILT && currentPhase == Game.StartUpPhase.PHASE_1) {
                        boolean success = gameMap.placeFoundingSettlement(player, mapPoint);
                        if (!success) sendFailResponse.accept(CANT_BUILD_HERE);
                        else {
                            startUpBuiltMap.put(user, FIRST_SETTLEMENT_BUILT);
                            sendSuccess.accept(req.getOriginLobby(),
                                               new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint,
                                                                             SETTLEMENT));
                        }
                    } else if (built == FIRST_BOTH_BUILT && currentPhase == Game.StartUpPhase.PHASE_2) {
                        boolean success = gameMap.placeFoundingSettlement(player, mapPoint);
                        if (!success) sendFailResponse.accept(CANT_BUILD_HERE);
                        else {
                            startUpBuiltMap.put(user, SECOND_SETTLEMENT_BUILT);
                            for (MapPoint hexPoint : gameMap.getResourceHexesFromIntersection(mapPoint)) {
                                ResourceHex hex = (ResourceHex) gameMap.getHex(hexPoint);
                                inv.increase(hex.getResource());
                            }
                            IResourceList resources = inv.getResources();
                            IDevelopmentCardList devCards = inv.getDevelopmentCards();
                            ResponseMessage rsp = new UpdateInventoryResponse(user, req.getOriginLobby(), resources,
                                                                              devCards);
                            rsp.initWithMessage(req);
                            LOG.debug("Sending UpdateInventoryResponse of Start Up Phase");
                            post(rsp);
                            sendSuccess.accept(req.getOriginLobby(),
                                               new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint,
                                                                             SETTLEMENT));
                        }
                    } else sendFailResponse.accept(NOT_THE_RIGHT_TIME);
                } else if (gameMap.settlementUpgradeable(player, mapPoint)) {
                    if (inv.get(ORE) >= 3 && inv.get(GRAIN) >= 2) {
                        inv.increase(ORE, -3);
                        inv.increase(GRAIN, -2);
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
                    } else if (currentPhase != Game.StartUpPhase.NOT_IN_STARTUP_PHASE) {
                        ActorStartUpBuiltMap startUpBuiltMap = game.getPlayersStartUpBuiltMap();
                        StartUpPhaseBuiltStructures built = startUpBuiltMap.get(user);
                        if (built == FIRST_SETTLEMENT_BUILT && currentPhase == Game.StartUpPhase.PHASE_1) {
                            boolean success = gameMap.placeRoad(player, mapPoint);
                            if (!success) sendFailResponse.accept(CANT_BUILD_HERE);
                            else {
                                startUpBuiltMap.put(user, FIRST_BOTH_BUILT);
                                sendSuccess.accept(req.getOriginLobby(),
                                                   new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint,
                                                                                 ROAD));
                            }
                        } else if (built == SECOND_SETTLEMENT_BUILT && currentPhase == Game.StartUpPhase.PHASE_2) {
                            boolean success = gameMap.placeRoad(player, mapPoint);
                            if (!success) sendFailResponse.accept(CANT_BUILD_HERE);
                            else {
                                startUpBuiltMap.put(user, ALL_BUILT);
                                sendSuccess.accept(req.getOriginLobby(),
                                                   new BuildingSuccessfulMessage(req.getOriginLobby(), user, mapPoint,
                                                                                 ROAD));
                            }
                        } else sendFailResponse.accept(NOT_THE_RIGHT_TIME);
                    } else if (inv.get(BRICK) >= 1 && inv.get(LUMBER) >= 1) {
                        inv.increase(BRICK, -1);
                        inv.increase(LUMBER, -1);
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
     * the map of the game according to the settings of the lobby. It also randomly
     * selects the first player.
     *
     * @param msg The CreateGameInternalRequest found on the EventBus
     *
     * @see de.uol.swp.server.game.event.CreateGameInternalRequest
     * @since 2021-01-24
     */
    @Subscribe
    private void onCreateGameInternalRequest(CreateGameInternalRequest msg) {
        LobbyName lobbyName = msg.getLobby().getName();
        LOG.debug("Received CreateGameInternalRequest for Lobby {}", lobbyName);
        try {
            IGameMapManagement gameMap = new GameMapManagement();
            IConfiguration configuration;
            if (msg.getLobby().isRandomPlayFieldEnabled()) {
                configuration = gameMap.getRandomisedConfiguration();
            } else {
                configuration = gameMap.getBeginnerConfiguration();
            }
            gameMap = gameMap.createMapFromConfiguration(configuration);
            if (!msg.getLobby().isStartUpPhaseEnabled()) {
                gameMap.makeBeginnerSettlementsAndRoads(msg.getLobby().getActors().size());
            }
            ActorSet users = msg.getLobby().getActors();
            int randomNbr = Util.randomInt(users.size());
            Actor[] playerArray = users.toArray(new Actor[0]);
            Actor firstPlayer = playerArray[randomNbr];
            gameManagement.createGame(msg.getLobby(), firstPlayer, gameMap, msg.getMoveTime());
            LOG.debug("Sending GameCreatedMessage");
            Game game = gameManagement.getGame(msg.getLobby().getName());
            post(new GameCreatedMessage(msg.getLobby().getName(), firstPlayer));
            LOG.debug("Sending StartSessionMessage for Lobby {}", lobbyName);
            StartSessionMessage message = new StartSessionMessage(lobbyName, firstPlayer, configuration,
                                                                  msg.getLobby().isStartUpPhaseEnabled(),
                                                                  game.getUserToPlayerMap(), game.getUserColoursMap(),
                                                                  game.getPlayerList());
            lobbyService.sendToAllInLobby(lobbyName, message);
        } catch (IllegalArgumentException e) {
            ExceptionMessage exceptionMessage = new ExceptionMessage(e.getMessage());
            exceptionMessage.initWithMessage(msg);
            LOG.debug("Sending ExceptionMessage");
            post(exceptionMessage);
        }
        for (Actor ai : msg.getLobby().getActors())
            if (ai instanceof AI) gameAI.writeChatMessageAI((AI) ai, lobbyName, AI.WriteType.START);
        Game game = gameManagement.getGame(lobbyName);
        Actor first = game.getFirst();
        if (first instanceof Computer) turnNPC(game, (Computer) first);
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
        if (req.getActor() == null) return;
        Game game = gameManagement.getGame(req.getOriginLobby());
        Inventory inventory = game.getInventory(req.getActor());
        if (req.getResource() != null) inventory.increase(req.getResource(), req.getAmount());
        else if (req.getDevelopmentCard() != null) inventory.increase(req.getDevelopmentCard(), req.getAmount());
        else if (req.isGiveAllCards()) inventory.increaseAll(req.getAmount());

        ResponseMessage returnMessage = new UpdateInventoryResponse(req.getActor(), req.getOriginLobby(),
                                                                    inventory.getResources(),
                                                                    inventory.getDevelopmentCards());
        LOG.debug("Sending ForwardToUserInternalRequest containing UpdateInventoryResponse");
        post(new ForwardToUserInternalRequest(req.getActor(), returnMessage));
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getActor(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
        updateVictoryPoints(req.getOriginLobby());
        endGameIfPlayerWon(game, req.getOriginLobby(), req.getActor());
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
     * @author Sven Ahrens
     * @see de.uol.swp.common.game.request.EndTurnRequest
     * @see de.uol.swp.common.game.message.NextPlayerMessage
     * @since 2021-01-15
     */
    @Subscribe
    private void onEndTurnRequest(EndTurnRequest req) {
        LOG.debug("Received EndTurnRequest for Lobby {}", req.getOriginLobby());
        LOG.debug("---- User {} wants to end their turn.", req.getActor().getUsername());
        Game game = gameManagement.getGame(req.getOriginLobby());
        Game.StartUpPhase currentPhase = game.getStartUpPhase();
        Deque<Actor> startUpPlayerOrder = game.getStartUpPlayerOrder();
        if (currentPhase == Game.StartUpPhase.NOT_IN_STARTUP_PHASE) {
            if (!game.getActivePlayer().equals(req.getActor()) || !game.isDiceRolledAlready() || game
                    .isPausedByVoting()) {
                LOG.debug("Can't end the turn now");
                return;
            }
        } else if (startUpPlayerOrder.peekFirst() == null || !startUpPlayerOrder.peekFirst().equals(req.getActor())) {
            return;
        }
        game.setBuildingAllowed(false);
        Actor nextPlayer;
        Actor user;
        Optional<ILobby> optionalLobby = lobbyManagement.getLobby(req.getOriginLobby());
        if (optionalLobby.isEmpty()) return;
        if (optionalLobby.get().isStartUpPhaseEnabled()) {
            if (currentPhase.equals(Game.StartUpPhase.PHASE_1)) {
                // in phase 1, continue with Deque order
                startUpPlayerOrder.addLast(startUpPlayerOrder.pollFirst());
                user = startUpPlayerOrder.peekFirst();
                if (user == null) return;
                if (user.equals(game.getFirst())) {
                    // first again at the beginning, signals reversal as per rules (2nd phase)
                    game.setStartUpPhase(Game.StartUpPhase.PHASE_2);
                    nextPlayer = startUpPlayerOrder.pollLast();
                    startUpPlayerOrder.addFirst(nextPlayer);
                } else {
                    nextPlayer = user;
                }
            } else if (currentPhase.equals(Game.StartUpPhase.PHASE_2)) {
                if (game.getPlayersStartUpBuiltMap().finished(game.getFirst())) {
                    nextPlayer = game.getFirst();
                    game.setStartUpPhase(Game.StartUpPhase.NOT_IN_STARTUP_PHASE);
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
        ServerMessage returnMessage = new NextPlayerMessage(req.getOriginLobby(), nextPlayer, game.getRound());

        LOG.debug("Sending NextPlayerMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnMessage);

        game.setDiceRolledAlready(false);
        if (nextPlayer instanceof Computer) turnNPC(game, (Computer) nextPlayer);
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
        if (!game.getActivePlayer().equals(req.getActor()) || !game.isDiceRolledAlready() || game.isPausedByVoting())
            return;
        Inventory inventory = game.getInventory(req.getActor());
        if (inventory == null) return;
        ResourceList offeredResourcesWrapperMap = new ResourceList();
        ResourceList respondingResourcesWrapperMap = new ResourceList();
        //getting the tradingRatios with the bank according to the harbours
        IGameMapManagement gameMap = game.getMap();
        Map<Player, List<MapPoint>> settlementsAndCities = gameMap.getPlayerSettlementsAndCities();
        Player player = game.getPlayer(req.getActor());
        List<IHarbourHex.HarbourResource> harbourTradingList = new ArrayList<>();
        if (settlementsAndCities.containsKey(player)) {
            List<MapPoint> ownSettlementsAndCities = settlementsAndCities.get(player);
            for (MapPoint ownSettlementsAndCity : ownSettlementsAndCities) {
                HarbourResource resource = gameMap.getHarbourResource(ownSettlementsAndCity);
                harbourTradingList.add(resource);
            }
        }
        //preparing a map with the tradingRatios according to the harbours
        Map<HarbourResource, Integer> tradingRatio = new HashMap<>();
        int prepareTradingRatio = 4;
        if (harbourTradingList.contains(HarbourResource.ANY)) prepareTradingRatio = 3;
        tradingRatio.put(HarbourResource.BRICK, prepareTradingRatio);
        tradingRatio.put(HarbourResource.ORE, prepareTradingRatio);
        tradingRatio.put(HarbourResource.GRAIN, prepareTradingRatio);
        tradingRatio.put(HarbourResource.WOOL, prepareTradingRatio);
        tradingRatio.put(HarbourResource.LUMBER, prepareTradingRatio);
        for (HarbourResource resource : harbourTradingList)
            tradingRatio.replace(resource, 2);
        //check if user has enough resources
        if (inventory.get(req.getGiveResource()) >= tradingRatio
                .get(IHarbourHex.getHarbourResource(req.getGiveResource()))) {
            //user gets the resource he demands
            inventory.increase(req.getGetResource());
            //user gives the resource he offers according to the harbours
            inventory.decrease(req.getGiveResource(),
                               tradingRatio.get(IHarbourHex.getHarbourResource(req.getGiveResource())));
        }
        respondingResourcesWrapperMap.set(req.getGetResource(), 1);
        offeredResourcesWrapperMap
                .set(req.getGiveResource(), tradingRatio.get(IHarbourHex.getHarbourResource(req.getGiveResource())));

        ResponseMessage returnMessage = new TradeWithBankAcceptedResponse(req.getActor(), req.getOriginLobby());
        returnMessage.initWithMessage(req);
        post(returnMessage);
        LOG.debug("Received SystemMessageForTradeMessage");
        ServerMessage serverMessage = new SystemMessageMessage(req.getOriginLobby(), new InGameSystemMessageDTO(
                makeSingularI18nWrapper(req.getActor(), null, offeredResourcesWrapperMap,
                                        respondingResourcesWrapperMap)));
        LOG.debug("Sending TradeWithBankAcceptedResponse to Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), serverMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getActor(), game.getCardAmounts());
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
        if (game.isPausedByVoting()) return;
        //only false if respondingUser is no Dummy, the dice is rolled already,
        //and the offeringUser is the active user/ it is a counteroffer
        if (req.getRespondingUser() instanceof Dummy || !game.isDiceRolledAlready()) return;
        if (!(game.getActivePlayer().equals(req.getOfferingUser()) || req.isCounterOffer())) {
            post(new ResetOfferTradeButtonRequest(req.getOriginLobby(), req.getOfferingUser()));
            return;
        }
        Inventory respondingInventory = game.getInventory(game.getPlayer(req.getRespondingUser()));
        if (respondingInventory == null) return;

        if (req.getRespondingUser() instanceof AI) {
            for (ResourceType r : ResourceType.values())
                if (respondingInventory.get(r) - req.getDemandedResources().getAmount(r) < 0) {
                    onResetOfferTradeButtonRequest(
                            new ResetOfferTradeButtonRequest(req.getOriginLobby(), req.getOfferingUser()));
                    return;
                }
            boolean accepted = gameAI
                    .tradeAcceptationAI(((AI) req.getRespondingUser()), req.getOriginLobby(), req.getOfferedResources(),
                                        req.getDemandedResources());
            if (accepted) {
                gameAI.writeChatMessageAI((AI) req.getRespondingUser(), req.getOriginLobby(),
                                          AI.WriteType.TRADE_ACCEPT);
                onAcceptUserTradeRequest(
                        new AcceptUserTradeRequest(req.getRespondingUser(), req.getOfferingUser(), req.getOriginLobby(),
                                                   req.getDemandedResources(), req.getOfferedResources()));
            } else {
                gameAI.writeChatMessageAI((AI) req.getRespondingUser(), req.getOriginLobby(),
                                          AI.WriteType.TRADE_DECLINE);
                onResetOfferTradeButtonRequest(
                        new ResetOfferTradeButtonRequest(req.getOriginLobby(), req.getOfferingUser()));
            }
            return;
        }
        game.setBuildingAllowed(false);

        ResourceList resourceMap = respondingInventory.getResources();

        LOG.debug("Sending TradeWithUserOfferMessage to Lobby {}", req.getOriginLobby());
        ResponseMessage offerResponse = new TradeWithUserOfferResponse(req.getOfferingUser(), resourceMap,
                                                                       req.getOfferedResources(),
                                                                       req.getDemandedResources(),
                                                                       req.getOriginLobby());
        post(new ForwardToUserInternalRequest(req.getRespondingUser(), offerResponse));
    }

    /**
     * Handles a PauseGameRequest found on the eventBus
     * <p>
     * If a PauseGameRequest is found on the eventBus, the
     * pauseStatus of the game is changed if everyone of the lobby
     * wants it and a PauseGameMessage is posted to everyone in the
     * lobby to update everyone of the lobby.
     *
     * @param req The request found on the eventBus
     *
     * @author Maximilian Lindner
     * @see de.uol.swp.common.game.request.PauseGameRequest
     * @see de.uol.swp.common.game.message.UpdatePauseStatusMessage
     * @since 2021-05-21
     */
    @Subscribe
    private void onPauseGameRequest(PauseGameRequest req) {
        Game game = gameManagement.getGame(req.getOriginLobby());
        game.changePauseStatus(req.getActor());
        int pausingPlayers = game.getPausedMembers();
        game.updatePauseByVotingStatus();
        ServerMessage msg = new UpdatePauseStatusMessage(req.getOriginLobby(), game.isPausedByVoting(), pausingPlayers,
                                                         game.getActivePlayer());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
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
        LobbyName lobbyName = req.getOriginLobby();
        LOG.debug("Received PauseTimerRequest for Lobby {}", lobbyName);
        Game game = gameManagement.getGame(req.getOriginLobby());
        game.setPausedByTrade(true);
        ServerMessage msg = new PauseTimerMessage(req.getOriginLobby(), req.getActor());
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
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready() || game.isPausedByVoting())
            return;
        Inventory inv = game.getInventory(req.getUser());

        if (inv.get(DevelopmentCardType.KNIGHT_CARD) == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending PlayCardFailureResponse");
            LOG.debug("---- Not enough Knight cards");
            return;
        }
        inv.increaseKnights();
        checkLargestArmy(req.getOriginLobby(), req.getUser());
        inv.decrease(DevelopmentCardType.KNIGHT_CARD);

        robberMovementPlayer(req, req.getUser());

        ServerMessage returnSystemMessage = new SystemMessageMessage(req.getOriginLobby(), new InGameSystemMessageDTO(
                new I18nWrapper("game.play.card.knightcard", req.getUser())));
        LOG.debug("Sending SystemMessageForPlayingCardsMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);

        ResponseMessage returnMessage = new PlayCardSuccessResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        LOG.debug("Sending PlayCardSuccessResponse");
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
        updateVictoryPoints(req.getOriginLobby());
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
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready() || game.isPausedByVoting())
            return;
        Inventory invMono = game.getInventory(req.getUser());

        if (invMono.get(DevelopmentCardType.MONOPOLY_CARD) == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending PlayCardFailureResponse");
            LOG.debug("---- Not enough Monopoly cards");
            return;
        }
        Inventory[] inventories = game.getAllInventories();

        for (Inventory inv : inventories)
            if (inv.get(req.getResource()) > 0) {
                invMono.increase(req.getResource(), inv.get(req.getResource()));
                inv.decrease(req.getResource(), inv.get(req.getResource()));
            }

        invMono.decrease(DevelopmentCardType.MONOPOLY_CARD);

        ServerMessage returnSystemMessage = new SystemMessageMessage(req.getOriginLobby(), new InGameSystemMessageDTO(
                new I18nWrapper("game.card.play.monopoly", req.getUser())));
        LOG.debug("Sending SystemMessageForPlayingCardsMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);
        ResponseMessage returnMessage = new PlayCardSuccessResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        LOG.debug("Sending PlayCardSuccessResponse");
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);

        for (Actor user : game.getPlayers()) {
            if (user instanceof User) {
                Inventory inventory = game.getInventory(user);
                DevelopmentCardList developmentCardList = inventory.getDevelopmentCards();
                ResourceList resourceList = inventory.getResources();
                ResponseMessage responseMessage = new UpdateInventoryResponse(user, req.getOriginLobby(), resourceList,
                                                                              developmentCardList);
                LOG.debug("Sending ForwardToUserInternalRequest with UpdateInventoryResponse to User {} in Lobby {}",
                          user, req.getOriginLobby());
                post(new ForwardToUserInternalRequest(user, responseMessage));
            }
        }
    }

    /**
     * Handles a PlayRoadBuildingCardAllowedRequest found on the eventBus
     * <p>
     * If a PlayRoadBuildingCardAllowedRequest is found on the event bus,
     * this method checks if the user has more than zero RoadBuildingCards.
     * If there is atleast one card, this method posts a
     * new PlayRoadBuildingCardAllowedResponse onto the eventBus.
     *
     * @param req The request found on the event bus
     *
     * @author Alwin Bossert
     * @see de.uol.swp.common.game.request.PlayCardRequest.PlayRoadBuildingCardAllowedRequest
     * @since 2021-05-16
     */
    @Subscribe
    private void onPlayRoadBuildingCardAllowedRequest(PlayRoadBuildingCardAllowedRequest req) {
        LOG.debug("Received PlayRoadBuildingCardRequest for Lobby {}", req.getOriginLobby());

        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready() || !game.isBuildingAllowed())
            return;
        Inventory inv = game.getInventory(req.getUser());

        if (inv.get(DevelopmentCardType.ROAD_BUILDING_CARD) == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending PlayCardFailureResponse");
            LOG.debug("---- Not enough RoadBuildingCardPhase cards");
            return;
        }
        ResponseMessage returnMessage = new PlayRoadBuildingCardAllowedResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        post(returnMessage);
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
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready() || !game
                .isBuildingAllowed() || game.isPausedByVoting()) return;
        Inventory inv = game.getInventory(req.getUser());

        LOG.debug("---- RoadBuildingCardPhase phase starts");
        game.setRoadBuildingCardPhase(WAITING_FOR_FIRST_ROAD);
        inv.decrease(DevelopmentCardType.ROAD_BUILDING_CARD);
        ServerMessage returnSystemMessage = new SystemMessageMessage(req.getOriginLobby(), new InGameSystemMessageDTO(
                new I18nWrapper("game.card.play.roadbuilding", req.getUser())));
        LOG.debug("Sending SystemMessageForPlayingCardsMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnSystemMessage);

        ResponseMessage returnMessage = new PlayCardSuccessResponse(req.getOriginLobby(), req.getUser());
        returnMessage.initWithMessage(req);
        LOG.debug("Sending PlayCardSuccessResponse");
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getUser(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
        updateVictoryPoints(req.getOriginLobby());
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
        LOG.debug("---- User {} wants {} and {}", req.getUser().getUsername(), req.getFirstResource().name(),
                  req.getSecondResource().name());

        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getUser()) || !game.isDiceRolledAlready() || game.isPausedByVoting())
            return;
        Inventory inv = game.getInventory(req.getUser());

        if (inv.get(DevelopmentCardType.YEAR_OF_PLENTY_CARD) == 0) {
            ResponseMessage returnMessage = new PlayCardFailureResponse(req.getOriginLobby(), req.getUser(),
                                                                        PlayCardFailureResponse.Reasons.NO_CARDS);
            returnMessage.initWithMessage(req);
            post(returnMessage);
            LOG.debug("Sending PlayCardFailureResponse");
            LOG.debug("---- Not enough YearOfPlenty cards");
            return;
        }

        inv.increase(req.getFirstResource());
        inv.increase(req.getSecondResource());

        inv.decrease(DevelopmentCardType.YEAR_OF_PLENTY_CARD);

        ServerMessage returnSystemMessage = new SystemMessageMessage(req.getOriginLobby(), new InGameSystemMessageDTO(
                new I18nWrapper("game.card.play.yearofplenty", req.getUser())));
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
        if (!game.isDiceRolledAlready()) return;
        game.setBuildingAllowed(true);
        Inventory offeringInventory = game.getInventory(req.getOfferingUser());
        if (offeringInventory == null) return;
        boolean secondOfferOfRespondingUser = game.getActivePlayer().equals(req.getSession().get().getUser());
        ResponseMessage returnMessage = new ResetOfferTradeButtonResponse(req.getOriginLobby(),
                                                                          secondOfferOfRespondingUser);
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
        robRandomResource(gameManagement.getGame(msg.getLobby()), msg.getPlayer(), msg.getVictim());
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
        Set<Player> players = new HashSet<>(map.getPlayersAroundHex(msg.getPosition()));
        ActorSet victims = new ActorSet();
        for (Player p : players) victims.add(gameManagement.getGame(msg.getLobby()).getActorFromPlayer(p));
        if (players.size() > 1) {
            LOG.debug("Sending RobberChooseVictimResponse for Lobby {}", msg.getLobby());
            ResponseMessage rcvm = new RobberChooseVictimResponse(msg.getPlayer(), victims);
            rcvm.initWithMessage(msg);
            post(rcvm);
        } else if (players.size() == 1) {
            robRandomResource(gameManagement.getGame(msg.getLobby()), msg.getPlayer(), victims.get(0));
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
        for (IResource r : req.getResources()) {
            i.decrease(r.getType(), r.getAmount());
        }
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getLobby());
        ServerMessage msg = new RefreshCardAmountMessage(req.getLobby(), req.getPlayer(),
                                                         gameManagement.getGame(req.getLobby()).getCardAmounts());
        lobbyService.sendToAllInLobby(req.getLobby(), msg);

        Game game = gameManagement.getGame(req.getLobby());
        game.removeTaxPayer(req.getPlayer());
        if (game.getTaxPayers().isEmpty()) lobbyService
                .sendToAllInLobby(req.getLobby(), new RobberAllTaxPaidMessage(req.getLobby(), game.getActivePlayer()));
        Actor activePlayer = game.getActivePlayer();
        robRandomResource(game);
        if (activePlayer instanceof Dummy) turnEndDummy(game, (Dummy) activePlayer);
        else if (activePlayer instanceof AI) turnEndAI(game, (AI) activePlayer);
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
        LOG.debug("---- User {} wants to roll the dices.", req.getActor().getUsername());
        Game game = gameManagement.getGame(req.getOriginLobby());
        if (!game.getActivePlayer().equals(req.getActor()) || game.isDiceRolledAlready() || game.isPausedByVoting())
            return;
        int[] result = Game.rollDice();
        int numberOfPips = result[0] + result[1];
        if (numberOfPips == 7) {
            //Robber things
            LOG.debug("---- Robber things");
            ActorIntegerMap players = new ActorIntegerMap();
            Game g = gameManagement.getGame(req.getOriginLobby());
            for (Actor p : g.getPlayers()) {
                if (g.getInventory(p).getResourceAmount() > 7) {
                    if (p instanceof Dummy) {
                        taxPayDummy(g, (Dummy) p);
                    } else if (p instanceof AI) {
                        gameAI.taxPayAI(g, (AI) p);
                    } else {
                        players.put(p, g.getInventory(p).getResourceAmount() / 2);
                    }
                }
            }
            ActorResourceListMap inventories = new ActorResourceListMap();
            for (Actor user : players.keySet()) {
                ResourceList resourceMap = new ResourceList();
                Inventory inv = game.getInventory(user);
                for (ResourceType resource : ResourceType.values())
                    resourceMap.set(resource, inv.get(resource));
                inventories.put(user, resourceMap);

                game.addTaxPayer((User) user);
            }
            RobberTaxMessage rtm = new RobberTaxMessage(req.getOriginLobby(), req.getActor(), players, inventories);
            LOG.debug("Sending RobberTaxMessage for Lobby {}", req.getOriginLobby());
            lobbyService.sendToAllInLobby(req.getOriginLobby(), rtm);
            if (req.getActor() instanceof Dummy) {
                robberMovementDummy((Dummy) req.getActor(), req.getOriginLobby());
            } else if (req.getActor() instanceof AI) {
                gameAI.robberMovementAI((AI) req.getActor(), req.getOriginLobby());
            } else {
                robberMovementPlayer(req, (User) req.getActor());
            }
        } else {
            LOG.debug("---- Distributing the resources for token {}", numberOfPips);
            game.distributeResources(numberOfPips);
        }
        game.setBuildingAllowed(true);
        game.setDiceRolledAlready(true);
        ServerMessage returnMessage = new DiceCastMessage(req.getOriginLobby(), req.getActor(), result[0], result[1]);
        LOG.debug("Sending DiceCastMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getActor(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
    }

    /**
     * Handles a TradeWithBankRequest found on the EventBus
     * <p>
     * It searches the inventories in the current game for the one that belongs
     * to the player sending the request. It then posts an InventoryForTradeResponse
     * that contains all the user's resources, saved in a resourceMap for
     * counted items (bricks, grain, etc.) and all of the harbours of the user.
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
        if (!game.getActivePlayer().equals(req.getActor()) || !game.isDiceRolledAlready() || game.isPausedByVoting())
            return;
        Inventory inventory = game.getInventory(req.getActor());
        if (inventory == null) return;
        ResourceList resourceMap = inventory.getResources();

        IGameMapManagement gameMap = game.getMap();
        Map<Player, List<MapPoint>> settlementsAndCities = gameMap.getPlayerSettlementsAndCities();
        Player player = game.getPlayer(req.getActor());
        List<HarbourResource> harbourTradingList = new ArrayList<>();
        if (settlementsAndCities.containsKey(player)) {
            List<MapPoint> ownSettlementsAndCities = settlementsAndCities.get(player);
            for (MapPoint ownSettlementsAndCity : ownSettlementsAndCities) {
                IHarbourHex.HarbourResource resource = gameMap.getHarbourResource(ownSettlementsAndCity);
                harbourTradingList.add(resource);
            }
        }

        ResponseMessage returnMessage = new InventoryForTradeResponse(req.getActor(), req.getName(),
                                                                      resourceMap.create(), harbourTradingList);
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
        if (req.getSession().isEmpty() || game == null) return;
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
        if (!game.isDiceRolledAlready()) return;
        if (game.getActivePlayer().equals(req.getActor()) || req.isCounterOffer() || game.isPausedByVoting()) {
            game.setBuildingAllowed(false);
            Inventory inventory = game.getInventory(req.getActor());
            Inventory traderInventory = game.getInventory(req.getRespondingUser());
            if (inventory == null || traderInventory == null) return;
            int traderInventorySize = traderInventory.getResourceAmount();
            ResourceList offeringInventory = inventory.getResources();
            ResponseMessage returnMessage;
            returnMessage = new InventoryForTradeWithUserResponse(req.getActor(), req.getName(),
                                                                  offeringInventory.create(), traderInventorySize,
                                                                  req.getRespondingUser(), req.isCounterOffer(),
                                                                  game.getMaxTradeDiff());
            LOG.debug("Sending InventoryForTradeWithUserResponse for Lobby {}", req.getName());
            returnMessage.initWithMessage(req);
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
        LobbyName lobbyName = req.getOriginLobby();
        LOG.debug("Received UnpauseTimerRequest for Lobby {}", lobbyName);
        Game game = gameManagement.getGame(req.getOriginLobby());
        game.setPausedByTrade(false);
        ServerMessage msg = new UnpauseTimerMessage(req.getOriginLobby(), req.getActor());
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
        Map<Player, Actor> playerActorMap = game.getPlayerUserMapping();
        LOG.debug("Sending UpdateGameMapResponse");
        UpdateGameMapResponse rsp = new UpdateGameMapResponse(req.getOriginLobby(),
                                                              game.getMap().getGameMapDTO(playerActorMap));
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
        if (game == null) return;
        Inventory inventory = game.getInventory(req.getActor());
        if (inventory == null) return;
        DevelopmentCardList developmentCardList = inventory.getDevelopmentCards();
        ResourceList resourceList = inventory.getResources();
        ResponseMessage returnMessage = new UpdateInventoryResponse(req.getActor(), req.getOriginLobby(),
                                                                    resourceList.create(), developmentCardList);
        returnMessage.initWithMessage(req);
        LOG.debug("Sending UpdateInventoryResponse for Lobby {}", req.getOriginLobby());
        post(returnMessage);
        ServerMessage msg = new RefreshCardAmountMessage(req.getOriginLobby(), req.getActor(), game.getCardAmounts());
        LOG.debug("Sending RefreshCardAmountMessage for Lobby {}", req.getOriginLobby());
        lobbyService.sendToAllInLobby(req.getOriginLobby(), msg);
        updateVictoryPoints(req.getOriginLobby());
        endGameIfPlayerWon(game, req.getOriginLobby(), req.getActor());
    }

    /**
     * Helper method to rob a random resource
     * <p>
     * It tries to rob a random resource. If someone has yet to pay his taxes, or
     * if the game's robResourceReceiverVictimPair is not set, it returns.
     *
     * @param game The game the resource robbing takes place
     *
     * @author Mario Fokken
     * @since 2021-06-11
     */
    private void robRandomResource(Game game) {
        ActorPair pair = game.getRobResourceReceiverVictimPair();
        if (pair == null || !game.getTaxPayers().isEmpty()) return;
        robRandomResourceExecutive(game.getLobby().getName(), pair.getActor1(), pair.getActor2());
        game.setRobResourceReceiverVictimPair(null);
    }

    /**
     * Helper method to rob a player of a random resource card
     * <p>
     * It chooses a random resource from the victim and gives it to the receiver.
     *
     * @param receiver Player to receive the card
     * @param victim   Player to lose a card
     *
     * @author Mario Fokken
     * @since 2021-04-06
     */
    private void robRandomResourceExecutive(LobbyName lobby, Actor receiver, Actor victim) {
        LOG.debug("{} wants to rob from {} in Lobby {}", receiver, victim, lobby);
        Inventory receiverInventory = gameManagement.getGame(lobby).getInventory(receiver);
        Inventory victimInventory = gameManagement.getGame(lobby).getInventory(victim);
        List<ResourceType> victimsResource = new ArrayList<>();
        if (victimInventory.getResourceAmount() == 0) {
            ServerMessage returnSystemMessage = new RobbingMessage(lobby, receiver, null);
            LOG.debug("Sending SystemMessageForRobbingMessage for Lobby {}", lobby);
            LOG.debug("---- Victim has no cards to rob");
            lobbyService.sendToAllInLobby(lobby, returnSystemMessage);
            return;
        }
        if (victimInventory.get(BRICK) > 0) victimsResource.add(BRICK);
        if (victimInventory.get(GRAIN) > 0) victimsResource.add(GRAIN);
        if (victimInventory.get(LUMBER) > 0) victimsResource.add(LUMBER);
        if (victimInventory.get(ORE) > 0) victimsResource.add(ORE);
        if (victimInventory.get(WOOL) > 0) victimsResource.add(WOOL);
        ResourceType stolenResource = victimsResource.get(Util.randomInt(victimsResource.size()));
        victimInventory.decrease(stolenResource);
        receiverInventory.increase(stolenResource);

        ServerMessage returnSystemMessage = new RobbingMessage(lobby, receiver, victim);
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
    private void robberMovementDummy(Dummy dummy, LobbyName lobby) {
        Game game = gameManagement.getGame(lobby);
        IGameMapManagement map = game.getMap();
        MapPoint mapPoint = MapPoint.HexMapPoint(3, 3);
        map.moveRobber(mapPoint);
        LOG.debug("Sending RobberPositionMessage for Lobby {}", lobby);
        AbstractGameMessage msg = new RobberPositionMessage(lobby, dummy, mapPoint);
        lobbyService.sendToAllInLobby(lobby, msg);
        LOG.debug("{} moves the robber to position: {}|{}", dummy, 3, 3);
        List<Player> players = map.getPlayersAroundHex(mapPoint);
        if (players.size() > 0) robRandomResource(game, dummy, game.getActorFromPlayer((Player) players.toArray()[0]));
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
     * Helper method to pay the tax for a dummy
     *
     * @param game  The game the dummy is in
     * @param dummy The Dummy to pay the tax
     *
     * @author Mario Fokken
     * @since 2021-05-11
     */
    private void taxPayDummy(Game game, Dummy dummy) {
        Inventory inv = game.getInventory(dummy);
        int i = inv.getResourceAmount() / 2;
        LOG.debug("{} has to give up {} of their {} cards", dummy, i, inv.getResourceAmount());
        for (ResourceType resourceType : ResourceType.values()) {
            //Not 100% accurate, but it's a dummy, they don't care
            inv.set(resourceType, inv.get(resourceType) / 2);
        }
    }

    /**
     * Helper method to end a dummy's turn
     * AFTER every player has chosen the resources
     * to give up on.
     *
     * @param game  The game
     * @param dummy The dummy to end it
     *
     * @author Mario Fokken
     * @since 2021-04-09
     */
    private void turnEndDummy(Game game, Dummy dummy) {
        if (game.getTaxPayers().isEmpty()) onEndTurnRequest(new EndTurnRequest(dummy, game.getLobby().getName()));
    }

    /**
     * Method to end an Computer's turn
     *
     * @param game     The game the Computer is in
     * @param computer The computer to make the turn
     *
     * @author Mario Fokken
     * @since 2021-06-07
     */
    private void turnNPC(Game game, Computer computer) {
        if (computer instanceof AI) {
            if (game.getStartUpPhase() == Game.StartUpPhase.NOT_IN_STARTUP_PHASE) {
                onRollDiceRequest(new RollDiceRequest(computer, game.getLobby().getName()));
                gameAI.turnAI(game, (AI) computer);
            } else gameAI.turnAISetUp(game, (AI) computer);
        } else if (computer instanceof Dummy) {
            if (game.getStartUpPhase() == Game.StartUpPhase.NOT_IN_STARTUP_PHASE)
                onRollDiceRequest(new RollDiceRequest(computer, game.getLobby().getName()));
            else {
                ActorStartUpBuiltMap startUpBuiltMap = game.getPlayersStartUpBuiltMap();
                dummyTurnInFoundingPhase(game, computer);
                startUpBuiltMap.nextPhase(computer);
            }
            turnEndDummy(game, (Dummy) computer);
        }
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
    private boolean updatePlayersInventoryWithDevelopmentCard(DevelopmentCardType developmentCard, Actor user,
                                                              LobbyName lobbyName) {
        Inventory inventory = gameManagement.getGame(lobbyName).getInventory(user);
        if (inventory == null || developmentCard == null) return false;
        if (inventory.get(ORE) >= 1 && inventory.get(GRAIN) >= 1 && inventory.get(WOOL) >= 1) {
            inventory.decrease(ORE);
            inventory.decrease(GRAIN);
            inventory.decrease(WOOL);
            inventory.increase(developmentCard);
            ResponseMessage serverMessage = new SystemMessageResponse(lobbyName, new InGameSystemMessageDTO(
                    new I18nWrapper("lobby.trade.withbank.systemresponse", developmentCard)));
            LOG.debug("Sending SystemMessageForTradeWithBankResponse for Lobby {}", lobbyName);
            post(new ForwardToUserInternalRequest(user, serverMessage));
            LOG.debug("Sending SystemMessageForTradeWithBankMessage for Lobby {}", lobbyName);
            lobbyService.sendToAllInLobby(lobbyName, new SystemMessageMessage(lobbyName, new InGameSystemMessageDTO(
                    new I18nWrapper("lobby.trade.withbank.systemmessage", user))));
        }
        return true;
    }
}

package de.uol.swp.client.game;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.lobby.request.StartSessionRequest;
import de.uol.swp.common.message.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The GameService is responsible for posting requests and events regarding
 * the state of a Game, like playing Development Cards or rolling dice.
 *
 * @author Maximilian Lindner
 * @author Phillip-André Suhr
 * @since 2021-04-07
 */
@SuppressWarnings("UnstableApiUsage")
public class GameService implements IGameService {

    private static final Logger LOG = LogManager.getLogger(LobbyService.class);
    private final EventBus eventBus;
    private final IUserService userService;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2021-04-07
     */
    @Inject
    public GameService(EventBus eventBus, IUserService userService) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.userService = userService;
        LOG.debug("GameService started");
    }

    @Override
    public void buildRequest(String lobbyName, User user, MapPoint mapPoint) {
        LOG.debug("Sending BuildRequest");
        Message request = new BuildRequest(lobbyName, user, mapPoint);
        eventBus.post(request);
    }

    @Override
    public void endTurn(String lobbyName) {
        LOG.debug("Sending EndTurnRequest");
        Message request = new EndTurnRequest(userService.getLoggedInUser(), lobbyName);
        eventBus.post(request);
    }

    @Override
    public void playKnightCard(String lobbyName) {
        LOG.debug("Sending PlayKnightCardRequest");
        Message request = new PlayKnightCardRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(request);
    }

    @Override
    public void playMonopolyCard(String lobbyName, Resources resource) {
        LOG.debug("Sending PlayMonopolyCardRequest");
        Message request = new PlayMonopolyCardRequest(lobbyName, userService.getLoggedInUser(), resource);
        eventBus.post(request);
    }

    @Override
    public void playRoadBuildingCard(String lobbyName) {
        LOG.debug("Sending PlayRoadBuildingCardRequest");
        Message request = new PlayRoadBuildingCardRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(request);
    }

    @Override
    public void playYearOfPlentyCard(String lobbyName, Resources resource1, Resources resource2) {
        LOG.debug("Sending PlayYearOfPlentyCardRequest");
        Message request = new PlayYearOfPlentyCardRequest(lobbyName, userService.getLoggedInUser(), resource1,
                                                          resource2);
        eventBus.post(request);
    }

    @Override
    public void rollDice(String lobbyName) {
        LOG.debug("Sending RollDiceRequest");
        Message request = new RollDiceRequest(userService.getLoggedInUser(), lobbyName);
        eventBus.post(request);
    }

    @Override
    public void startSession(String lobbyName) {
        LOG.debug("Sending StartSessionRequest");
        Message request = new StartSessionRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(request);
    }

    @Override
    public void updateGameMap(String lobbyName) {
        LOG.debug("Sending UpdateGameMapRequest");
        Message request = new UpdateGameMapRequest(lobbyName);
        eventBus.post(request);
    }

    @Override
    public void updateInventory(String lobbyName) {
        LOG.debug("Sending UpdateInventoryRequest");
        Message request = new UpdateInventoryRequest(userService.getLoggedInUser(), lobbyName);
        eventBus.post(request);
    }
}

package de.uol.swp.client.game;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.lobby.event.CloseRobberTaxViewEvent;
import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.game.robber.RobberChosenVictimRequest;
import de.uol.swp.common.game.robber.RobberNewPositionChosenRequest;
import de.uol.swp.common.game.robber.RobberTaxChosenRequest;
import de.uol.swp.common.lobby.request.StartSessionRequest;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

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

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2021-04-07
     */
    @Inject
    public GameService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
        LOG.debug("GameService started");
    }

    @Override
    public void buildRequest(String lobbyName, User user, MapPoint mapPoint) {
        LOG.debug("Sending BuildRequest");
        Message request = new BuildRequest(lobbyName, user, mapPoint);
        eventBus.post(request);
    }

    @Override
    public void endTurn(String lobbyName, User user) {
        LOG.debug("Sending EndTurnRequest");
        Message request = new EndTurnRequest(user, lobbyName);
        eventBus.post(request);
    }

    @Override
    public void playKnightCard(String lobbyName, User user) {
        LOG.debug("Sending PlayKnightCardRequest");
        Message request = new PlayKnightCardRequest(lobbyName, user);
        eventBus.post(request);
    }

    @Override
    public void playMonopolyCard(String lobbyName, User user, Resources resource) {
        LOG.debug("Sending PlayMonopolyCardRequest");
        Message request = new PlayMonopolyCardRequest(lobbyName, user, resource);
        eventBus.post(request);
    }

    @Override
    public void playRoadBuildingCard(String lobbyName, User user) {
        LOG.debug("Sending PlayRoadBuildingCardRequest");
        Message request = new PlayRoadBuildingCardRequest(lobbyName, user);
        eventBus.post(request);
    }

    @Override
    public void playYearOfPlentyCard(String lobbyName, User user, Resources resource1, Resources resource2) {
        LOG.debug("Sending PlayYearOfPlentyCardRequest");
        Message request = new PlayYearOfPlentyCardRequest(lobbyName, user, resource1, resource2);
        eventBus.post(request);
    }

    @Override
    public void rollDice(String lobbyName, User user) {
        LOG.debug("Sending RollDiceRequest");
        Message request = new RollDiceRequest(user, lobbyName);
        eventBus.post(request);
    }

    @Override
    public void startSession(String lobbyName, User user) {
        LOG.debug("Sending StartSessionRequest");
        Message request = new StartSessionRequest(lobbyName, user);
        eventBus.post(request);
    }

    @Override
    public void updateGameMap(String lobbyName) {
        LOG.debug("Sending UpdateGameMapRequest");
        Message request = new UpdateGameMapRequest(lobbyName);
        eventBus.post(request);
    }

    @Override
    public void robberChooseVictim(String lobbyName, User user, UserOrDummy victim) {
        Message msg = new RobberChosenVictimRequest(lobbyName, user, victim);
        eventBus.post(msg);
    }

    @Override
    public void robberNewPosition(String lobbyName, User user, MapPoint mapPoint) {
        Message msg = new RobberNewPositionChosenRequest(lobbyName, user, mapPoint);
        eventBus.post(msg);
    }

    @Override
    public void taxPayed(String lobbyName, User user, Map<Resources, Integer> selectedResources) {
        eventBus.post(new RobberTaxChosenRequest(selectedResources, user, lobbyName));
        eventBus.post(new CloseRobberTaxViewEvent(lobbyName, user));
    }

    @Override
    public void updateInventory(String lobbyName, User user) {
        LOG.debug("Sending UpdateInventoryRequest");
        Message request = new UpdateInventoryRequest(user, lobbyName);
        eventBus.post(request);
    }
}

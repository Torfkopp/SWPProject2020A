package de.uol.swp.client.game;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.request.EndTurnRequest;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.game.request.RollDiceRequest;
import de.uol.swp.common.game.request.UpdateInventoryRequest;
import de.uol.swp.common.lobby.request.StartSessionRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("UnstableApiUsage")
class GameServiceTest {

    private static final User defaultUser = new UserDTO(42, "test", "test123", "test@test.test");
    private static final String defaultLobbyName = "Test lobby";
    private static final Resources defaultResource = Resources.BRICK;
    private static final Resources secondResource = Resources.GRAIN;

    private final EventBus eventBus = new EventBus();
    private final CountDownLatch lock = new CountDownLatch(1);

    private IGameService gameService;
    private Object event;

    @BeforeEach
    protected void setUp() {
        gameService = new GameService(eventBus);
        eventBus.register(this);
    }

    @AfterEach
    protected void tearDown() {
        event = null;
        gameService = null;
        eventBus.unregister(this);
    }

    @Test
    void endTurn() throws InterruptedException {
        gameService.endTurn(defaultLobbyName, defaultUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof EndTurnRequest);

        EndTurnRequest request = (EndTurnRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
    }

    @Test
    void playKnightCard() throws InterruptedException {
        gameService.playKnightCard(defaultLobbyName, defaultUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof PlayKnightCardRequest);

        PlayKnightCardRequest request = (PlayKnightCardRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
    }

    @Test
    void playMonopolyCard() throws InterruptedException {
        gameService.playMonopolyCard(defaultLobbyName, defaultUser, defaultResource);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof PlayMonopolyCardRequest);

        PlayMonopolyCardRequest request = (PlayMonopolyCardRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
        assertEquals(defaultResource, request.getResource());
    }

    @Test
    void playRoadBuildingCard() throws InterruptedException {
        gameService.playRoadBuildingCard(defaultLobbyName, defaultUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof PlayRoadBuildingCardRequest);

        PlayRoadBuildingCardRequest request = (PlayRoadBuildingCardRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
    }

    @Test
    void playYearOfPlentyCard() throws InterruptedException {
        gameService.playYearOfPlentyCard(defaultLobbyName, defaultUser, defaultResource, secondResource);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof PlayYearOfPlentyCardRequest);

        PlayYearOfPlentyCardRequest request = (PlayYearOfPlentyCardRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
        assertEquals(defaultResource, request.getResource1());
        assertEquals(secondResource, request.getResource2());
    }

    @Test
    void rollDice() throws InterruptedException {
        gameService.rollDice(defaultLobbyName, defaultUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RollDiceRequest);

        RollDiceRequest request = (RollDiceRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
    }

    @Test
    void startSession() throws InterruptedException {
        gameService.startSession(defaultLobbyName, defaultUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof StartSessionRequest);

        StartSessionRequest request = (StartSessionRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
    }

    @Test
    void updateInventory() throws InterruptedException {
        gameService.updateInventory(defaultLobbyName, defaultUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UpdateInventoryRequest);

        UpdateInventoryRequest request = (UpdateInventoryRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
    }

    @Subscribe
    private void onDeadEvent(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }
}
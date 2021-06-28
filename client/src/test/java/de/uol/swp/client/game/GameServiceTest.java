package de.uol.swp.client.game;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.game.robber.RobberChosenVictimRequest;
import de.uol.swp.common.game.robber.RobberNewPositionChosenRequest;
import de.uol.swp.common.game.robber.RobberTaxChosenRequest;
import de.uol.swp.common.lobby.LobbyName;
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

@SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
class GameServiceTest {

    private static final User defaultUser = new UserDTO(42, "test", "test123", "test@test.test");
    private static final User secondUser = new UserDTO(69, "Har Ald", "aldhar96", "har.ald@ald.har");
    private static final LobbyName defaultLobbyName = new LobbyName("Test lobby");
    private static final ResourceType defaultResource = ResourceType.BRICK;
    private static final ResourceType secondResource = ResourceType.GRAIN;

    private final EventBus eventBus = new EventBus();
    private final CountDownLatch lock = new CountDownLatch(1);

    private IGameService gameService;
    private IUserService userService;
    private Object event;

    @BeforeEach
    protected void setUp() {
        userService = new UserService(eventBus);
        userService.setLoggedInUser(defaultUser);
        gameService = new GameService(eventBus, userService);
        eventBus.register(this);
    }

    @AfterEach
    protected void tearDown() {
        event = null;
        gameService = null;
        userService = null;
        eventBus.unregister(this);
    }

    @Test
    void buildRequest() throws InterruptedException {
        MapPoint hexMapPointL = MapPoint.HexMapPoint(1, 1);
        MapPoint hexMapPointR = MapPoint.HexMapPoint(1, 2);
        MapPoint edgeMapPoint = MapPoint.EdgeMapPoint(hexMapPointL, hexMapPointR);
        gameService.buildRequest(defaultLobbyName, edgeMapPoint);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof BuildRequest);

        BuildRequest request = (BuildRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
        assertEquals(edgeMapPoint, request.getMapPoint());
        assertEquals(MapPoint.Type.EDGE, request.getMapPoint().getType());
        assertEquals(hexMapPointL, request.getMapPoint().getL());
        assertEquals(hexMapPointR, request.getMapPoint().getR());
    }

    @Test
    void changeAutoRollState() throws InterruptedException {
        gameService.changeAutoRollState(defaultLobbyName, true);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof ChangeAutoRollStateRequest);

        ChangeAutoRollStateRequest request = (ChangeAutoRollStateRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
        assertTrue(request.isAutoRollEnabled());
    }

    @Test
    void endTurn() throws InterruptedException {
        gameService.endTurn(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof EndTurnRequest);

        EndTurnRequest request = (EndTurnRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
    }

    @Test
    void pauseGame() throws InterruptedException {
        gameService.pauseGame(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof PauseGameRequest);

        PauseGameRequest request = (PauseGameRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getActor());
    }

    @Test
    void playKnightCard() throws InterruptedException {
        gameService.playKnightCard(defaultLobbyName);

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
        gameService.playMonopolyCard(defaultLobbyName, defaultResource);

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
        gameService.playRoadBuildingCard(defaultLobbyName);

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
        gameService.playYearOfPlentyCard(defaultLobbyName, defaultResource, secondResource);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof PlayYearOfPlentyCardRequest);

        PlayYearOfPlentyCardRequest request = (PlayYearOfPlentyCardRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
        assertEquals(defaultResource, request.getFirstResource());
        assertEquals(secondResource, request.getSecondResource());
    }

    @Test
    void robberChooseVictim() throws InterruptedException {
        gameService.robberChooseVictim(defaultLobbyName, secondUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RobberChosenVictimRequest);

        RobberChosenVictimRequest request = (RobberChosenVictimRequest) event;

        assertEquals(defaultLobbyName, request.getLobby());
        assertEquals(defaultUser, request.getPlayer());
        assertEquals(defaultUser.getID(), request.getPlayer().getID());
        assertEquals(defaultUser.getUsername(), request.getPlayer().getUsername());
        assertEquals(secondUser, request.getVictim());
        assertEquals(secondUser.getID(), request.getVictim().getID());
        assertEquals(secondUser.getUsername(), request.getVictim().getUsername());
    }

    @Test
    void robberNewPosition() throws InterruptedException {
        MapPoint newRobberHex = MapPoint.HexMapPoint(1, 1);
        gameService.robberNewPosition(defaultLobbyName, newRobberHex);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RobberNewPositionChosenRequest);

        RobberNewPositionChosenRequest request = (RobberNewPositionChosenRequest) event;

        assertEquals(defaultLobbyName, request.getLobby());
        assertEquals(defaultUser, request.getPlayer());
        assertEquals(defaultUser.getID(), request.getPlayer().getID());
        assertEquals(defaultUser.getUsername(), request.getPlayer().getUsername());
        assertEquals(newRobberHex, request.getPosition());
        assertEquals(MapPoint.Type.HEX, request.getPosition().getType());
        assertEquals(newRobberHex.getX(), request.getPosition().getX());
        assertEquals(newRobberHex.getY(), request.getPosition().getY());
    }

    @Test
    void rollDice() throws InterruptedException {
        gameService.rollDice(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RollDiceRequest);

        RollDiceRequest request = (RollDiceRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
    }

    @Test
    void startSession() throws InterruptedException {
        gameService.startSession(defaultLobbyName, 0);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof StartSessionRequest);

        StartSessionRequest request = (StartSessionRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
    }

    @Test
    void taxPayed() throws InterruptedException {
        ResourceList resources = new ResourceList();
        gameService.taxPayed(defaultLobbyName, resources);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RobberTaxChosenRequest);

        RobberTaxChosenRequest request = (RobberTaxChosenRequest) event;

        assertEquals(defaultLobbyName, request.getLobby());
        assertEquals(defaultUser, request.getPlayer());
        assertEquals(defaultUser.getID(), request.getPlayer().getID());
        assertEquals(defaultUser.getUsername(), request.getPlayer().getUsername());
        assertEquals(resources, request.getResources());
    }

    @Test
    void updateGameMap() throws InterruptedException {
        gameService.updateGameMap(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UpdateGameMapRequest);

        UpdateGameMapRequest request = (UpdateGameMapRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
    }

    @Test
    void updateInventory() throws InterruptedException {
        gameService.updateInventory(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UpdateInventoryRequest);

        UpdateInventoryRequest request = (UpdateInventoryRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
    }

    @Subscribe
    private void onDeadEvent(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }
}
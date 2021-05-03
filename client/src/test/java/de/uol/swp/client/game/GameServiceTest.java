//package de.uol.swp.client.game;
//
//import com.google.common.eventbus.DeadEvent;
//import com.google.common.eventbus.EventBus;
//import com.google.common.eventbus.Subscribe;
//import de.uol.swp.client.user.IUserService;
//import de.uol.swp.client.user.UserService;
//import de.uol.swp.common.game.request.EndTurnRequest;
//import de.uol.swp.common.game.request.PlayCardRequest.*;
//import de.uol.swp.common.game.request.RollDiceRequest;
//import de.uol.swp.common.game.request.UpdateInventoryRequest;
//import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
//import de.uol.swp.common.lobby.request.StartSessionRequest;
//import de.uol.swp.common.user.User;
//import de.uol.swp.common.user.UserDTO;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SuppressWarnings("UnstableApiUsage")
//class GameServiceTest {
//
//    private static final User defaultUser = new UserDTO(42, "test", "test123", "test@test.test");
//    private static final String defaultLobbyName = "Test lobby";
//    private static final ResourceType defaultResource = ResourceType.BRICK;
//    private static final ResourceType secondResource = ResourceType.GRAIN;
//
//    private final EventBus eventBus = new EventBus();
//    private final CountDownLatch lock = new CountDownLatch(1);
//
//    private IGameService gameService;
//    private IUserService userService;
//    private Object event;
//
//    @BeforeEach
//    protected void setUp() {
//        userService = new UserService(eventBus);
//        userService.setLoggedInUser(defaultUser);
//        gameService = new GameService(eventBus, userService);
//        eventBus.register(this);
//    }
//
//    @AfterEach
//    protected void tearDown() {
//        event = null;
//        gameService = null;
//        userService = null;
//        eventBus.unregister(this);
//    }
//
//    @Test
//    void endTurn() throws InterruptedException {
//        gameService.endTurn(defaultLobbyName);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof EndTurnRequest);
//
//        EndTurnRequest request = (EndTurnRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//    }
//
//    @Test
//    void playKnightCard() throws InterruptedException {
//        gameService.playKnightCard(defaultLobbyName);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof PlayKnightCardRequest);
//
//        PlayKnightCardRequest request = (PlayKnightCardRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//    }
//
//    @Test
//    void playMonopolyCard() throws InterruptedException {
//        gameService.playMonopolyCard(defaultLobbyName, defaultResource);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof PlayMonopolyCardRequest);
//
//        PlayMonopolyCardRequest request = (PlayMonopolyCardRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//        assertEquals(defaultResource, request.getResource());
//    }
//
//    @Test
//    void playRoadBuildingCard() throws InterruptedException {
//        gameService.playRoadBuildingCard(defaultLobbyName);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof PlayRoadBuildingCardRequest);
//
//        PlayRoadBuildingCardRequest request = (PlayRoadBuildingCardRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//    }
//
//    @Test
//    void playYearOfPlentyCard() throws InterruptedException {
//        gameService.playYearOfPlentyCard(defaultLobbyName, defaultResource, secondResource);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof PlayYearOfPlentyCardRequest);
//
//        PlayYearOfPlentyCardRequest request = (PlayYearOfPlentyCardRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//        assertEquals(defaultResource, request.getResource1());
//        assertEquals(secondResource, request.getResource2());
//    }
//
//    @Test
//    void rollDice() throws InterruptedException {
//        gameService.rollDice(defaultLobbyName);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof RollDiceRequest);
//
//        RollDiceRequest request = (RollDiceRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//    }
//
//    @Test
//    void startSession() throws InterruptedException {
//        gameService.startSession(defaultLobbyName, 0);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof StartSessionRequest);
//
//        StartSessionRequest request = (StartSessionRequest) event;
//
//        assertEquals(defaultLobbyName, request.getName());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//    }
//
//    @Test
//    void updateInventory() throws InterruptedException {
//        gameService.updateInventory(defaultLobbyName);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof UpdateInventoryRequest);
//
//        UpdateInventoryRequest request = (UpdateInventoryRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//    }
//
//    @Subscribe
//    private void onDeadEvent(DeadEvent e) {
//        this.event = e.getEvent();
//        System.out.print(e.getEvent());
//        lock.countDown();
//    }
//}
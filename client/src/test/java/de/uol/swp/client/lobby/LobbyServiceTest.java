package de.uol.swp.client.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.Colour;
import de.uol.swp.common.game.request.ReturnToPreGameLobbyRequest;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.SimpleLobby;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.user.*;
import de.uol.swp.common.user.request.CheckUserInLobbyRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This a test of the class is used to hide the communication details
 *
 * @author Marvin Drees
 * @see de.uol.swp.client.lobby.LobbyService
 * @since 2020-11-26
 */
@SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
class LobbyServiceTest {

    private static final User defaultUser = new UserDTO(1, "chuck", "test",//
                                                        "chuck@norris.com");
    private static final User secondUser = new UserDTO(2, "chuck_testa", "testa",//
                                                       "testa@chuck.com");
    private static final LobbyName defaultLobbyName = new LobbyName("testlobby");
    private static final ISimpleLobby defaultLobby = new SimpleLobby(defaultLobbyName, false, defaultUser,//
                                                                     4, 60,//
                                                                     false, false,//
                                                                     false, null, null, 2);
    private static final int MAX_TRADE_DIFF = 2;

    private final EventBus eventBus = new EventBus();
    private final CountDownLatch lock = new CountDownLatch(1);

    private ILobbyService lobbyService;
    private IUserService userService;
    private Object event;

    /**
     * Helper method run before each test case
     *
     * @since 2020-11-26
     */
    @BeforeEach
    protected void setUp() {
        userService = new UserService(eventBus);
        userService.setLoggedInUser(defaultUser);
        lobbyService = new LobbyService(eventBus, userService);
        eventBus.register(this);
    }

    /**
     * Helper method run after each test case
     *
     * @since 2020-11-26
     */
    @AfterEach
    protected void tearDown() {
        lobbyService = null;
        userService = null;
        event = null;
        eventBus.unregister(this);
    }

    @Test
    void addAI() throws InterruptedException {
        AI ai = new AIDTO("DIO");
        lobbyService.addAI(defaultLobbyName, ai);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof AddAIRequest);

        AddAIRequest request = (AddAIRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(ai, request.getActor());
    }

    @Test
    void changeOwner() throws InterruptedException {
        lobbyService.changeOwner(defaultLobbyName, secondUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof ChangeOwnerRequest);

        ChangeOwnerRequest request = (ChangeOwnerRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
        assertEquals(secondUser, request.getNewOwner());
        assertEquals(secondUser.getID(), request.getNewOwner().getID());
        assertEquals(secondUser.getUsername(), request.getNewOwner().getUsername());
    }

    @Test
    void checkUserInLobby() throws InterruptedException {
        lobbyService.checkUserInLobby();

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof CheckUserInLobbyRequest);

        CheckUserInLobbyRequest request = (CheckUserInLobbyRequest) event;

        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
    }

    /**
     * Test for the createNewLobby routine
     * <p>
     * This Test calls the createNewLobby method of the LobbyService
     * and checks that a CreateLobbyRequest is correctly posted onto
     * the EventBus.
     * <p>
     * This Test fails if any of the attributes of the Request differ
     * from the attributes provided upon calling the method.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2020-11-26
     */
    @Test
    void createNewLobby() throws InterruptedException {
        lobbyService.createNewLobby(defaultLobbyName, null);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof CreateLobbyRequest);

        CreateLobbyRequest request = (CreateLobbyRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getOwner());
        assertEquals(defaultUser.getID(), request.getOwner().getID());
        assertEquals(defaultUser.getUsername(), request.getOwner().getUsername());
        assertEquals(defaultUser.getPassword(), request.getOwner().getPassword());
        assertEquals(defaultUser.getEMail(), request.getOwner().getEMail());
    }

    /**
     * Test for the joinLobby routine
     * <p>
     * This Test calls the joinLobby method of the LobbyService
     * and checks that a LobbyJoinUserRequest is correctly posted
     * onto the EventBus.
     * <p>
     * This Test fails if any of the attributes of the Request differ
     * from the attributes provided upon calling the method.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-04-10
     */
    @Test
    void joinLobby() throws InterruptedException {
        lobbyService.joinLobby(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof JoinLobbyRequest);

        JoinLobbyRequest request = (JoinLobbyRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
    }

    @Test
    void joinRandomLobby() throws InterruptedException {
        lobbyService.joinRandomLobby();

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof JoinRandomLobbyRequest);

        JoinRandomLobbyRequest request = (JoinRandomLobbyRequest) event;

        assertEquals(new LobbyName(""), request.getName());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
    }

    /**
     * Test fpr the kickUser routine
     * <p>
     * This Test calls the kickUser method of the LobbyService
     * and checks that a KickUserRequest is correctly posted onto
     * the EventBus.
     * <p>
     * This Test fails if any of the attributes of the Request differ
     * from the attributes provided upon calling the method.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-04-10
     */
    @Test
    void kickUser() throws InterruptedException {
        lobbyService.kickUser(defaultLobbyName, secondUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof KickUserRequest);

        KickUserRequest request = (KickUserRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
        assertEquals(secondUser, request.getToBeKickedUser());
        assertEquals(secondUser.getID(), request.getToBeKickedUser().getID());
        assertEquals(secondUser.getUsername(), request.getToBeKickedUser().getUsername());
    }

    /**
     * Test for the leaveLobby routine
     * <p>
     * This Test calls the leaveLobby method of the LobbyService
     * and checks that a LobbyLeaveUserRequest is correctly posted
     * onto the EventBus.
     * <p>
     * This Test fails if any of the attributes of the Request differ
     * from the attributes provided upon calling the method.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-04-10
     */
    @Test
    void leaveLobby() throws InterruptedException {
        lobbyService.leaveLobby(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof LeaveLobbyRequest);

        LeaveLobbyRequest request = (LeaveLobbyRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
    }

    /**
     * Test for the refreshLobbyPresenterFields routine
     * <p>
     * This Test calls the refreshLobbyPresenterFields method of the
     * LobbyService and checks that a LobbyUpdateEvent is correctly posted
     * onto the EventBus.
     * <p>
     * This Test fails if any of the attributes of the Request differ
     * from the attributes provided upon calling the method.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-01-03
     */
    @Test
    void refreshLobbyPresenterFields() throws InterruptedException {
        lobbyService.refreshLobbyPresenterFields(defaultLobby);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof LobbyUpdateEvent);

        LobbyUpdateEvent lobbyUpdateEvent = (LobbyUpdateEvent) event;

        assertEquals(defaultLobby, lobbyUpdateEvent.getLobby());
        assertEquals(defaultLobby.getName(), lobbyUpdateEvent.getLobby().getName());
        assertEquals(defaultLobby.isInGame(), lobbyUpdateEvent.getLobby().isInGame());
        assertEquals(defaultLobby.getMaxPlayers(), lobbyUpdateEvent.getLobby().getMaxPlayers());
        assertEquals(defaultLobby.getMoveTime(), lobbyUpdateEvent.getLobby().getMoveTime());
        assertEquals(defaultLobby.isStartUpPhaseEnabled(), lobbyUpdateEvent.getLobby().isStartUpPhaseEnabled());
        assertEquals(defaultLobby.isRandomPlayFieldEnabled(), lobbyUpdateEvent.getLobby().isRandomPlayFieldEnabled());
    }

    /**
     * Test for the removeFromLobbies routine
     * <p>
     * This Test calls the removeFromLobbies method of the LobbyService
     * and checks that a RemoveFromLobbiesRequest is correctly posted onto
     * the EventBus.
     * <p>
     * This Test fails if any of the attributes of the Request differ
     * from the attributes provided upon calling the method.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-04-10
     */
    @Test
    void removeFromAllLobbies() throws InterruptedException {
        lobbyService.removeFromAllLobbies();

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RemoveFromLobbiesRequest);

        RemoveFromLobbiesRequest request = (RemoveFromLobbiesRequest) event;

        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
    }

    /**
     * Test for the retrieveAllLobbies routine
     * <p>
     * This Test calls the retrieveAllLobbies method of the LobbyService
     * and checks that a RetrieveAllLobbiesRequest is correctly posted onto
     * the EventBus.
     * <p>
     * This Test fails if any of the attributes of the Request differ
     * from the attributes provided upon calling the method.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-04-10
     */
    @Test
    void retrieveAllLobbies() throws InterruptedException {
        lobbyService.retrieveAllLobbies();

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RetrieveAllLobbiesRequest);
    }

    /**
     * Test for the retrieveAllLobbyMembers routine
     * <p>
     * This Test calls the retrieveAllLobbyMembers method of the LobbyService
     * and checks that a RetrieveAllLobbyMembersRequest is correctly posted
     * onto the EventBus.
     * <p>
     * This Test fails if any of the attributes of the Request differ
     * from the attributes provided upon calling the method.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-04-10
     */
    @Test
    void retrieveAllLobbyMembers() throws InterruptedException {
        lobbyService.retrieveAllLobbyMembers(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RetrieveAllLobbyMembersRequest);

        RetrieveAllLobbyMembersRequest request = (RetrieveAllLobbyMembersRequest) event;

        assertEquals(defaultLobbyName, request.getLobbyName());
    }

    /**
     * Test for the returnToPreGameLobby routine
     * <p>
     * This Test calls the returnToPreGameLobby method of the LobbyService
     * and checks that a ReturnToPreGameLobbyRequest is correctly posted onto
     * the EventBus.
     * <p>
     * This Test fails if any of the attributes of the Request differ
     * from the attributes provided upon calling the method.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-04-10
     */
    @Test
    void returnToPreGameLobby() throws InterruptedException {
        lobbyService.returnToPreGameLobby(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof ReturnToPreGameLobbyRequest);

        ReturnToPreGameLobbyRequest request = (ReturnToPreGameLobbyRequest) event;

        assertEquals(defaultLobbyName, request.getLobbyName());
    }

    @Test
    void setColour() throws InterruptedException {
        Colour colour = Colour.AQUA;
        lobbyService.setColour(defaultLobbyName, colour);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof SetColourRequest);

        SetColourRequest request = (SetColourRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getActor());
        assertEquals(colour, request.getColour());
    }

    /**
     * Test for the updateLobbySettings routine
     * <p>
     * This Test calls the updateLobbySettings method of the LobbyService
     * and checks that a ChangeLobbySettingsRequest is correctly posted onto
     * the EventBus.
     * <p>
     * This Test fails if any of the attributes of the Request differ
     * from the attributes provided upon calling the method.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-04-10
     */
    @Test
    void updateLobbySettings() throws InterruptedException {
        lobbyService.updateLobbySettings(defaultLobbyName, 4, true, 60, true, 2);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof ChangeLobbySettingsRequest);

        ChangeLobbySettingsRequest request = (ChangeLobbySettingsRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
        assertEquals(4, request.getAllowedPlayers());
        assertTrue(request.isStartUpPhaseEnabled());
        assertEquals(60, request.getMoveTime());
        assertTrue(request.isRandomPlayFieldEnabled());
    }

    /**
     * Test for the userReady routine
     * <p>
     * This Test calls the userReady method of the LobbyService
     * and checks that a UserReadyRequest is correctly posted onto
     * the EventBus.
     * <p>
     * This Test fails if any of the attributes of the Request differ
     * from the attributes provided upon calling the method.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-04-10
     */
    @Test
    void userReady() throws InterruptedException {
        lobbyService.userReady(defaultLobbyName, true);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UserReadyRequest);

        UserReadyRequest request = (UserReadyRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
        assertTrue(request.isReady());
    }

    /**
     * Handles DeadEvents detected on the EventBus
     * <p>
     * If a DeadEvent is detected, the event variable of this class gets updated
     * to its event, and its event is printed to the console output.
     *
     * @param e The DeadEvent detected on the EventBus
     *
     * @since 2020-11-26
     */
    @Subscribe
    private void onDeadEvent(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }
}

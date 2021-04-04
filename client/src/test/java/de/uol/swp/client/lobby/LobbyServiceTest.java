package de.uol.swp.client.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
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
@SuppressWarnings("UnstableApiUsage")
class LobbyServiceTest {

    final User defaultUser = new UserDTO(1, "chuck", "test", "chuck@norris.com");
    final Lobby defaultLobby = new LobbyDTO("testlobby", defaultUser, false, 4, false, 60, true, true);

    final EventBus bus = new EventBus();
    final CountDownLatch lock = new CountDownLatch(1);
    Object event;
    private ILobbyService lobbyService;
    private IUserService userService;

    /**
     * Helper method run after each test case
     * <p>
     * This method only unregisters the object of this class from the EventBus.
     *
     * @since 2020-11-26
     */
    @AfterEach
    protected void deregisterBus() {
        bus.unregister(this);
        lobbyService = null;
        userService = null;
    }

    /**
     * Helper method run before each test case
     * <p>
     * This method resets the variable event to null and registers the object of
     * this class to the EventBus.
     *
     * @since 2020-11-26
     */
    @BeforeEach
    protected void registerBus() {
        event = null;
        bus.register(this);
        userService = new UserService(bus);
        userService.setLoggedInUser(defaultUser);
        lobbyService = new LobbyService(bus, userService);
    }

    /**
     * Test for the createNewLobby routine
     * <p>
     * This Test creates a new LobbyService object registered to the EventBus of
     * this test class. It then calls the createNewLobby function of the object using
     * the name "Test" and defaultUser as parameter, and waits for it to post a
     * CreateLobbyRequest object onto the EventBus.
     * If this happens within one second, it checks the lobby name and
     * whether the user in the request object is the same as the default user.
     * If any of these checks fail or the method takes too long, this test is unsuccessful.
     *
     * @throws InterruptedException thrown by lock.await()
     * @since 2020-11-26
     */
    @Test
    void createNewLobbyTest() throws InterruptedException {
        lobbyService.createNewLobby("Test", 4);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof CreateLobbyRequest);

        CreateLobbyRequest request = (CreateLobbyRequest) event;

        assertEquals("Test", request.getName());

        assertEquals(defaultUser.getUsername(), request.getOwner().getUsername());
        assertEquals(defaultUser.getPassword(), request.getOwner().getPassword());
        assertEquals(defaultUser.getEMail(), request.getOwner().getEMail());
    }

    /**
     * Test for the refreshLobbyPresenterFields routine
     * <p>
     * Test if the LobbyService creates a LobbyUpdateEvent with the given
     * lobbyName and User and posts it to the EventBus when
     * refreshLobbyPresenterFields is called.
     * <p>
     * This test fails if the lobbyName attribute of the LobbyUpdateEvent
     * or any of the User details are not equal to what was sent originally.
     *
     * @throws InterruptedException thrown by lock.await()
     * @since 2021-01-03
     */
    @Test
    void refreshLobbyPresenterFieldsTest() throws InterruptedException {
        lobbyService.refreshLobbyPresenterFields(defaultLobby);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof LobbyUpdateEvent);

        LobbyUpdateEvent lobbyUpdateEvent = (LobbyUpdateEvent) event;

        assertEquals(defaultLobby, lobbyUpdateEvent.getLobby());
        assertEquals(defaultLobby.getName(), lobbyUpdateEvent.getLobby().getName());
        assertEquals(defaultLobby.isInGame(), lobbyUpdateEvent.getLobby().isInGame());
        assertEquals(defaultLobby.getMaxPlayers(), lobbyUpdateEvent.getLobby().getMaxPlayers());
        assertEquals(defaultLobby.commandsAllowed(), lobbyUpdateEvent.getLobby().commandsAllowed());
        assertEquals(defaultLobby.getMoveTime(), lobbyUpdateEvent.getLobby().getMoveTime());
        assertEquals(defaultLobby.startUpPhaseEnabled(), lobbyUpdateEvent.getLobby().startUpPhaseEnabled());
        assertEquals(defaultLobby.randomPlayfieldEnabled(), lobbyUpdateEvent.getLobby().randomPlayfieldEnabled());
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

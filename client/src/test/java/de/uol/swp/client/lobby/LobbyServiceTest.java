package de.uol.swp.client.lobby;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This a test of the class is used to hide the communication details
 *
 * @author Marvin
 * @see LobbyService
 * @since 2020-11-26
 *
 */
@SuppressWarnings("UnstableApiUsage")
class LobbyServiceTest {

    final UserDTO defaultUser = new UserDTO("chuck", "test", "chuck@norris.com");

    final EventBus bus = new EventBus();
    final CountDownLatch lock = new CountDownLatch(1);
    Object event;

    /**
     * Handles DeadEvents detected on the EventBus
     *
     * If a DeadEvent is detected the event variable of this class gets updated
     * to its event and its event is printed to the console output.
     *
     * @param e The DeadEvent detected on the EventBus
     * @since 2020-11-26
     */
    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }

    /**
     * Helper method run before each test case
     *
     * This method resets the variable event to null and registers the object of
     * this class to the EventBus.
     *
     * @since 2020-11-26
     */
    @BeforeEach
    void registerBus() {
        event = null;
        bus.register(this);
    }

    /**
     * Helper method run after each test case
     *
     * This method only unregisters the object of this class from the EventBus.
     *
     * @since 2020-11-26
     */
    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    /**
     * Test for the createNewLobby routine
     *
     * This Test creates a new LobbyService object registered to the EventBus of
     * this test class. It then calls the createNewLobby function of the object using
     * the name "Test" and defaultUser as parameter and waits for it to post a
     * CreateLobbyRequest object on the EventBus.
     * If this happens within one second, it checks the lobby name and
     * if the user in the request object is the same as the default user.
     * If any of these checks fail or the method takes to long, this test is unsuccessful.
     *
     * @throws InterruptedException thrown by lock.await()
     * @since 2020-11-26
     */
    @Test
    void createNewLobbyTest() throws InterruptedException {
        LobbyService lobbyService = new LobbyService(bus);
        lobbyService.createNewLobby("Test", defaultUser);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof CreateLobbyRequest);

        CreateLobbyRequest request = (CreateLobbyRequest) event;

        assertEquals(request.getName(), "Test");

        assertEquals(request.getOwner().getUsername(), defaultUser.getUsername());
        assertEquals(request.getOwner().getPassword(), defaultUser.getPassword());
        assertEquals(request.getOwner().getEMail(), defaultUser.getEMail());

    }

}
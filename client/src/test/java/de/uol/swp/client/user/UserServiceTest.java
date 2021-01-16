package de.uol.swp.client.user;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This a test of the class is used to hide the communication details
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.user.UserService
 * @since 2019-10-10
 */
@SuppressWarnings("UnstableApiUsage")
class UserServiceTest {

    final User defaultUser = new UserDTO("Marco", "test", "marco@test.de");

    final EventBus bus = new EventBus();
    final CountDownLatch lock = new CountDownLatch(1);
    Object event;

    /**
     * Handles DeadEvents detected on the EventBus
     * <p>
     * If a DeadEvent is detected, the event variable of this class gets updated
     * to its event, and its event is printed to the console output.
     *
     * @param e The DeadEvent detected on the EventBus
     * @since 2019-10-10
     */
    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }

    /**
     * Helper method run before each test case
     * <p>
     * This method resets the variable event to null and registers the object of
     * this class to the EventBus.
     *
     * @since 2019-10-10
     */
    @BeforeEach
    void registerBus() {
        event = null;
        bus.register(this);
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method only unregisters the object of this class from the EventBus.
     *
     * @since 2019-10-10
     */
    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    /**
     * Subroutine used for tests that need a logged in user
     * <p>
     * This subroutine creates a new UserService object registered to the EventBus
     * of this test class, and calls its login method for the default user.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2019-10-10
     */
    private void loginUser() throws InterruptedException {
        ClientUserService userService = new UserService(bus);
        userService.login(defaultUser.getUsername(), defaultUser.getPassword());
        lock.await(1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Test for the login method
     * <p>
     * This test first calls the loginUser subroutine. Afterwards, it checks if a
     * LoginRequest object got posted onto the EventBus and if its content is the
     * default users information.
     * The test fails if any of the checks fail.
     *
     * @throws java.lang.InterruptedException thrown by loginUser()
     * @since 2019-10-10
     */
    @Test
    void loginTest() throws InterruptedException {
        loginUser();

        assertTrue(event instanceof LoginRequest);

        LoginRequest loginRequest = (LoginRequest) event;
        assertEquals(loginRequest.getUsername(), defaultUser.getUsername());
        assertEquals(loginRequest.getPassword(), defaultUser.getPassword());
    }

    /**
     * Test for the logout method
     * <p>
     * This test first calls the loginUser subroutine. Afterwards, it creates a new
     * UserService object registered to the EventBus of this test class.
     * It then calls the logout function of the object using the defaultUser as parameter
     * and waits for it to post an LogoutRequest object onto the EventBus.
     * It then checks if authorisation is needed to logout the user.
     * The test fails if no LogoutRequest is posted within one second or the request
     * says that no authorisation is needed
     *
     * @throws java.lang.InterruptedException thrown by loginUser() and lock.await()
     * @since 2019-10-10
     */
    @Test
    void logoutTest() throws InterruptedException {
        loginUser();
        event = null;

        ClientUserService userService = new UserService(bus);
        userService.logout(defaultUser);

       // lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof LogoutRequest);

        LogoutRequest request = (LogoutRequest) event;

        assertTrue(request.authorisationNeeded());
    }

    /**
     * Test for the createUser routine
     * <p>
     * This Test creates a new UserService object registered to the EventBus of
     * this test class. It then calls the createUser function of the object using
     * the defaultUser as parameter and waits for it to post an UpdateUserRequest
     * object onto the EventBus.
     * If this happens within one second, it checks if the user in the request object
     * is the same as the default user and if authorisation is needed.
     * Authorisation should not be needed.
     * If any of these checks fail or the method takes too long, this test is unsuccessful.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2019-10-10
     */
    @Test
    void createUserTest() throws InterruptedException {
        ClientUserService userService = new UserService(bus);
        userService.createUser(defaultUser);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RegisterUserRequest);

        RegisterUserRequest request = (RegisterUserRequest) event;

        assertEquals(request.getUser().getUsername(), defaultUser.getUsername());
        assertEquals(request.getUser().getPassword(), defaultUser.getPassword());
        assertEquals(request.getUser().getEMail(), defaultUser.getEMail());
        assertFalse(request.authorisationNeeded());
    }

    /**
     * Test for the updateUser routine
     * <p>
     * This Test creates a new UserService object registered to the EventBus of
     * this test class. It then calls the updateUser function of the object using
     * the defaultUser as parameter and waits for it to post an UpdateUserRequest
     * object onto the EventBus.
     * If this happens within one second, it checks if the user in the request object
     * is the same as the default user and if authorisation is needed.
     * Authorisation should be needed.
     * If any of these checks fail or the method takes too long, this test is unsuccessful.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2019-10-10
     */
    @Test
    void updateUserTest() throws InterruptedException {
        ClientUserService userService = new UserService(bus);
        userService.updateUser(defaultUser);

        lock.await(1, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UpdateUserRequest);

        UpdateUserRequest request = (UpdateUserRequest) event;

        assertEquals(request.getUser().getUsername(), defaultUser.getUsername());
        assertEquals(request.getUser().getPassword(), defaultUser.getPassword());
        assertEquals(request.getUser().getEMail(), defaultUser.getEMail());
        assertTrue(request.authorisationNeeded());
    }

    /**
     * Test for the dropUser routine
     * <p>
     * This Test creates a new UserService object registered to the EventBus of
     * this test class. It then calls the dropUser function of the object using
     * the defaultUser as parameter and waits for it to post a DeleteUserRequest
     * object onto the EventBus.
     * If this happens within one second, it checks if authorisation is needed.
     * Authorisation should be needed.
     * If any of these checks fail or the method takes too long, this test is unsuccessful.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @author Phillip-André Suhr
     * @since 2019-10-10
     */
    @Test
    void dropUserTest() throws InterruptedException {
        ClientUserService userService = new UserService(bus);

        userService.dropUser(defaultUser);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof DeleteUserRequest);

        DeleteUserRequest request = (DeleteUserRequest) event;

        assertEquals(request.getUser().getUsername(), defaultUser.getUsername());
        assertEquals(request.getUser().getPassword(), defaultUser.getPassword());
        assertEquals(request.getUser().getEMail(), defaultUser.getEMail());
        assertTrue(request.authorisationNeeded());
    }

    /**
     * Test for the retrieveAllUsers routine
     * <p>
     * This Test creates a new UserService object registered to the EventBus of
     * this test class. It then calls the retrieveAllUsers function of the object
     * and waits for it to post a retrieveAllUsersRequest object onto the EventBus.
     * If this happens within one second, the test is successful.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2019-10-10
     */
    @Test
    void retrieveAllUsersTest() throws InterruptedException {
        ClientUserService userService = new UserService(bus);
        userService.retrieveAllUsers();

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RetrieveAllOnlineUsersRequest);
    }
}

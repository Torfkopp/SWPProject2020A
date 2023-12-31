package de.uol.swp.client.user;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.hash.Hashing;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.request.*;
import de.uol.swp.common.user.response.ChangeAccountDetailsSuccessfulResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
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
@SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
class UserServiceTest {

    private final User defaultUser = new UserDTO(1, "Marco", "test", "marco@test.de");

    private final EventBus bus = new EventBus();
    private final CountDownLatch lock = new CountDownLatch(1);
    private Object event;
    private IUserService userService;

    /**
     * Helper method run before each test case
     * <p>
     * This method resets the variable event to null and registers the object of
     * this class to the EventBus.
     *
     * @since 2019-10-10
     */
    @BeforeEach
    protected void setUp() {
        event = null;
        bus.register(this);
        userService = new UserService(bus);
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method only unregisters the object of this class from the EventBus.
     *
     * @since 2019-10-10
     */
    @AfterEach
    protected void tearDown() {
        bus.unregister(this);
        userService = null;
    }

    /**
     * Test for the createUser routine
     * <p>
     * This Test creates a new UserService object registered to the EventBus of
     * this test class. It then calls the createUser function of the object using
     * the defaultUser as parameter and waits for it to post an RegisterUserRequest
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
        User userToCreate = new UserDTO(defaultUser.getID(), defaultUser.getUsername(),
                                        userService.hash(defaultUser.getPassword()), defaultUser.getEMail());
        userService.createUser(userToCreate);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RegisterUserRequest);

        RegisterUserRequest request = (RegisterUserRequest) event;

        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
        assertEquals(userToCreate.getPassword(), request.getUser().getPassword());
        assertNotEquals(defaultUser.getPassword(), request.getUser().getPassword());
        assertEquals(defaultUser.getEMail(), request.getUser().getEMail());
        assertFalse(request.authorisationNeeded());
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
     * @since 2020-11-22
     */
    @Test
    void dropUserTest() throws InterruptedException {
        User userToDrop = new UserDTO(defaultUser.getID(), defaultUser.getUsername(),
                                      userService.hash(defaultUser.getPassword()), defaultUser.getEMail());
        userService.dropUser(userToDrop, userService.hash(defaultUser.getPassword()));

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof DeleteUserRequest);

        DeleteUserRequest request = (DeleteUserRequest) event;

        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
        assertEquals(userService.hash(defaultUser.getPassword()), request.getUser().getPassword());
        assertNotEquals(defaultUser.getPassword(), request.getUser().getPassword());
        assertEquals(defaultUser.getEMail(), request.getUser().getEMail());
        assertTrue(request.authorisationNeeded());
    }

    /**
     * Test for the hash routine
     * <p>
     * This test hashes the defaultUser's password using the sha256().hashString()
     * method of the Hashing Google Guava module and compares the resulting
     * String with the String returned by the UserService.hash() method.
     * If the Strings are not equal, the test fails.
     *
     * @author Phillip-André Suhr
     * @since 2021-04-16
     */
    @Test
    void hashTest() {
        String hashedPassword = Hashing.sha256().hashString(defaultUser.getPassword(), StandardCharsets.UTF_8)
                                       .toString();

        assertEquals(hashedPassword, userService.hash(defaultUser.getPassword()));
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
        assertEquals(defaultUser.getUsername(), loginRequest.getUsername());
        assertEquals(userService.hash(defaultUser.getPassword()), loginRequest.getPassword());
        assertNotEquals(defaultUser.getPassword(), loginRequest.getPassword());
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

        userService.logout(false);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof LogoutRequest);

        LogoutRequest request = (LogoutRequest) event;

        assertTrue(request.authorisationNeeded());
    }

    @Test
    void onChangeAccountDetailsSuccessfulResponseTest() throws InterruptedException {
        User secondUser = new UserDTO(1, "second", "s2", "mail@mail.second");
        userService.setLoggedInUser(defaultUser);

        bus.post(new ChangeAccountDetailsSuccessfulResponse(secondUser));

        lock.await(250, TimeUnit.MILLISECONDS);
        assertEquals(secondUser, userService.getLoggedInUser());
    }

    @Test
    void onLoginSuccessfulResponseTest() throws InterruptedException {
        assertNull(userService.getLoggedInUser());
        bus.post(new LoginSuccessfulResponse(defaultUser));

        lock.await(250, TimeUnit.MILLISECONDS);

        assertNotNull(userService.getLoggedInUser());
        assertEquals(defaultUser, userService.getLoggedInUser());
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
        userService.retrieveAllUsers();

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof RetrieveAllOnlineUsersRequest);
    }

    /**
     * Test for the updateAccountDetails routine
     * <p>
     * This Test calls the updateAccountDetails function of the UserService with the
     * provided parameters and waits for it to post an UpdateUserAccountDetailsRequest
     * object onto the EventBus.
     * If this happens within one second, it checks if all parameters were correctly
     * added to the request and if authorisation is needed.
     * Authorisation should be needed.
     * If any of these checks fail or the method takes too long, this test is unsuccessful.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @author Phillip-André Suhr
     * @since 2021-04-16
     */
    @Test
    void updateAccountDetailsTest() throws InterruptedException {
        String newUsername = "Xx_goodName_xX";
        User updatedUser = new UserDTO(defaultUser.getID(), newUsername, userService.hash(defaultUser.getPassword()),
                                       defaultUser.getEMail());
        userService.updateAccountDetails(updatedUser, userService.hash(defaultUser.getPassword()),
                                         defaultUser.getUsername(), defaultUser.getEMail());

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof UpdateUserAccountDetailsRequest);

        UpdateUserAccountDetailsRequest request = (UpdateUserAccountDetailsRequest) event;

        assertEquals(updatedUser, request.getUser());
        assertEquals(updatedUser.getID(), request.getUser().getID());
        assertEquals(updatedUser.getPassword(), request.getOldPassword());
        assertNotEquals(defaultUser.getPassword(), request.getOldPassword());
        assertEquals(newUsername, request.getUser().getUsername());
        assertEquals(defaultUser.getEMail(), request.getOldEMail());
        assertEquals(defaultUser.getEMail(), request.getUser().getEMail());
        assertTrue(request.authorisationNeeded());
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
        userService.login(defaultUser.getUsername(), userService.hash(defaultUser.getPassword()), false);
        lock.await(250, TimeUnit.MILLISECONDS);
    }

    /**
     * Handles DeadEvents detected on the EventBus
     * <p>
     * If a DeadEvent is detected, the event variable of this class gets updated
     * to its event, and its event is printed to the console output.
     *
     * @param e The DeadEvent detected on the EventBus
     *
     * @since 2019-10-10
     */
    @Subscribe
    private void onDeadEvent(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }
}

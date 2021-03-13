package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for UserReadyRequest
 *
 * @author Eric Vuong
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.request.UserReadyRequest;
 * @since 2021-01-21
 */
class UserReadyRequestTest {

    private static final User defaultUser = new UserDTO(42, "chuck", "test", "chuck@norris.com");

    /**
     * Tests the userReadyRequest when isReady is false
     * <p>
     * This tests fails if any of the parameters is not as expected.
     */
    @Test
    void UserIsNotReadyTest() {
        UserReadyRequest userReadyRequest = new UserReadyRequest("Lobby", defaultUser, false);

        assertEquals("Lobby", userReadyRequest.getName());
        assertEquals(defaultUser, userReadyRequest.getUser());
        assertEquals(defaultUser.getUsername(), userReadyRequest.getUser().getUsername());
        assertEquals(defaultUser.getPassword(), userReadyRequest.getUser().getPassword());
        assertEquals(defaultUser.getEMail(), userReadyRequest.getUser().getEMail());
        assertFalse(userReadyRequest.isReady());
    }

    /**
     * Tests the userReadyRequest when isReady is true
     * <p>
     * This tests fails if any of the parameters is not as expected.
     */
    @Test
    void UserIsReadyTest() {
        UserReadyRequest userReadyRequest = new UserReadyRequest("Lobby", defaultUser, true);

        assertEquals("Lobby", userReadyRequest.getName());
        assertEquals(defaultUser, userReadyRequest.getUser());
        assertEquals(defaultUser.getUsername(), userReadyRequest.getUser().getUsername());
        assertEquals(defaultUser.getPassword(), userReadyRequest.getUser().getPassword());
        assertEquals(defaultUser.getEMail(), userReadyRequest.getUser().getEMail());
        assertTrue(userReadyRequest.isReady());
    }
}

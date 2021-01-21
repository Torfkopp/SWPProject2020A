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
    private static final User defaultUser = new UserDTO("chuck", "test", "chuck@norris.com");

    /**
     * Tests the userReadyRequest when isReady is true
     * <p>
     * This tests fails if any of the parameters is not as expected.
     */
    @Test
    void UserIsReadyTest() {
        UserReadyRequest userReadyRequest = new UserReadyRequest("Lobby", defaultUser, true);

        assertEquals(userReadyRequest.getName(), "Lobby");
        assertEquals(userReadyRequest.getUser(), defaultUser);
        assertEquals(userReadyRequest.getUser().getUsername(), defaultUser.getUsername());
        assertEquals(userReadyRequest.getUser().getPassword(), defaultUser.getPassword());
        assertEquals(userReadyRequest.getUser().getEMail(), defaultUser.getEMail());
        assertTrue(userReadyRequest.isReady());
    }

    /**
     * Tests the userReadyRequest when isReady is false
     * <p>
     * This tests fails if any of the parameters is as expected.
     */
    @Test
    void UserIsNotReadyTest() {
        UserReadyRequest userReadyRequest = new UserReadyRequest("Lobby", defaultUser, false);

        assertEquals(userReadyRequest.getName(), "Lobby");
        assertEquals(userReadyRequest.getUser(), defaultUser);
        assertEquals(userReadyRequest.getUser().getUsername(), defaultUser.getUsername());
        assertEquals(userReadyRequest.getUser().getPassword(), defaultUser.getPassword());
        assertEquals(userReadyRequest.getUser().getEMail(), defaultUser.getEMail());
        assertFalse(userReadyRequest.isReady());
    }
}

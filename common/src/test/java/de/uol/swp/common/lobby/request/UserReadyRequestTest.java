package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyName;
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

    private static final LobbyName defaultLobbyName = new LobbyName("Lobby");

    /**
     * Tests the userReadyRequest when isReady is false
     * <p>
     * This tests fails if any of the parameters is not as expected.
     */
    @Test
    void UserIsNotReadyTest() {
        UserReadyRequest userReadyRequest = new UserReadyRequest(defaultLobbyName, defaultUser, false);

        assertEquals(defaultLobbyName, userReadyRequest.getName());
        assertEquals(defaultUser, userReadyRequest.getUser());
        assertEquals(defaultUser.getUsername(), userReadyRequest.getUser().getUsername());
        assertFalse(userReadyRequest.isReady());
    }

    /**
     * Tests the userReadyRequest when isReady is true
     * <p>
     * This tests fails if any of the parameters is not as expected.
     */
    @Test
    void UserIsReadyTest() {
        UserReadyRequest userReadyRequest = new UserReadyRequest(defaultLobbyName, defaultUser, true);

        assertEquals(defaultLobbyName, userReadyRequest.getName());
        assertEquals(defaultUser, userReadyRequest.getUser());
        assertEquals(defaultUser.getUsername(), userReadyRequest.getUser().getUsername());
        assertTrue(userReadyRequest.isReady());
    }
}

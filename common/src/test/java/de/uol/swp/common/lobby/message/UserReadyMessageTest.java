package de.uol.swp.common.lobby.message;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for UserReadyMessage
 *
 * @author Eric Vuong
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.request.UserReadyRequest;
 * @since 2021-01-21
 */
class UserReadyMessageTest {

    private static final User defaultUser = new UserDTO(42, "chuck", "test", "chuck@norris.com");

    /**
     * Tests the userReadyMessage
     * <p>
     * This tests fails if any of the parameters is not as expected.
     */
    @Test
    void basicUserReadyMessageTest() {
        UserReadyMessage userReadyMessage = new UserReadyMessage("Lobby", defaultUser);

        assertEquals("Lobby", userReadyMessage.getName());
        assertEquals(defaultUser, userReadyMessage.getUser());
        assertEquals(defaultUser.getUsername(), userReadyMessage.getUser().getUsername());
        assertEquals(defaultUser.getPassword(), userReadyMessage.getUser().getPassword());
        assertEquals(defaultUser.getEMail(), userReadyMessage.getUser().getEMail());
    }
}

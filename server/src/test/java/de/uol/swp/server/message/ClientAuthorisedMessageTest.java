package de.uol.swp.server.message;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class ClientAuthorisedMessageTest {

    @Test
    void ClientAuthorisedMessage() {
        User user = mock(User.class);

        ClientAuthorisedMessage message = new ClientAuthorisedMessage(user, false);

        assertEquals(user, message.getUser());
        assertFalse(message.hasOldSession());
    }
}
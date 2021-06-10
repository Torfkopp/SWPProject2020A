package de.uol.swp.common.chat.message;

import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeletedChatMessageMessageTest {

    private final int id = 1;

    @Test
    void getLobbyName_isNull() {
        DeletedChatMessageMessage msg = new DeletedChatMessageMessage(id);

        assertEquals(id, msg.getId());
        assertFalse(msg.isLobbyChatMessage());
        assertNull(msg.getLobbyName());
    }

    @Test
    void testDeletedChatMessageMessage() {
        LobbyName lobbyName = new LobbyName("test");
        DeletedChatMessageMessage msg = new DeletedChatMessageMessage(id, lobbyName);

        assertEquals(id, msg.getId());
        assertTrue(msg.isLobbyChatMessage());
        assertEquals(lobbyName, msg.getLobbyName());
    }
}
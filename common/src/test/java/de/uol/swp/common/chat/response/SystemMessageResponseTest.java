package de.uol.swp.common.chat.response;

import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SystemMessageResponseTest {

    private final LobbyName lobbyName = new LobbyName("test");
    private final SystemMessage systemMessage = mock(SystemMessage.class);

    @Test
    void getLobbyName() {
        SystemMessageResponse response = new SystemMessageResponse(lobbyName, systemMessage);
        assertTrue(response.isLobbyChatMessage());
        assertEquals(systemMessage, response.getMsg());
        assertEquals(lobbyName, response.getLobbyName());
    }

    @Test
    void getLobbyName_IsNull() {
        SystemMessageResponse response = new SystemMessageResponse(null, systemMessage);

        assertNull(response.getLobbyName());
        assertFalse(response.isLobbyChatMessage());
        assertEquals(systemMessage, response.getMsg());
    }
}
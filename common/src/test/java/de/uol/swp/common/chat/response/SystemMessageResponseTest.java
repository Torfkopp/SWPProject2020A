package de.uol.swp.common.chat.response;

import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class SystemMessageResponseTest {

    private final LobbyName lobbyName = new LobbyName("test");
    private final SystemMessage systemMessage = mock(SystemMessage.class);
    private SystemMessageResponse response;

    @BeforeEach
    protected void setUp() {
        response = new SystemMessageResponse(lobbyName, systemMessage);
    }

    @AfterEach
    protected void tearDown() {
        response = null;
    }

    @Test
    void getLobbyName() {
        assertEquals(lobbyName, response.getLobbyName());
    }

    @Test
    void getLobbyName_IsNull() {
        response = new SystemMessageResponse(null, systemMessage);

        assertNull(response.getLobbyName());
        assertFalse(response.isLobbyChatMessage());
    }

    @Test
    void getMsg() {
        assertEquals(systemMessage, response.getMsg());
    }

    @Test
    void isLobbyChatMessage() {
        assertTrue(response.isLobbyChatMessage());
    }
}
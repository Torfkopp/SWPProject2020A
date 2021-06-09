package de.uol.swp.common.chat.request;

import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractChatMessageRequestTest {

    private final LobbyName lobbyName = new LobbyName("test");
    private AbstractChatMessageRequest request;

    @BeforeEach
    protected void setUp() {
        request = new AbstractChatMessageRequest(lobbyName) {
            @Override
            public LobbyName getOriginLobby() {
                return super.getOriginLobby();
            }

            @Override
            public boolean isFromLobby() {
                return super.isFromLobby();
            }
        };
    }

    @AfterEach
    protected void tearDown() {
        request = null;
    }

    @Test
    void getOriginLobby() {
        assertEquals(lobbyName, request.getOriginLobby());
    }

    @Test
    void isFromLobby() {
        assertTrue(request.isFromLobby());
    }

    @Test
    void isNotFromLobby() {
        request = new AbstractChatMessageRequest(null) {
            @Override
            public LobbyName getOriginLobby() {
                return super.getOriginLobby();
            }

            @Override
            public boolean isFromLobby() {
                return super.isFromLobby();
            }
        };

        assertNull(request.getOriginLobby());
        assertFalse(request.isFromLobby());
    }
}
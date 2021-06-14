package de.uol.swp.common.chat.request;

import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractChatMessageRequestTest {

    @Test
    void isNotFromLobby() {
        AbstractChatMessageRequest request = new AbstractChatMessageRequest(null) {
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

    @Test
    void testAbstractChatMessageRequest() {
        final LobbyName lobbyName = new LobbyName("test");
        AbstractChatMessageRequest request = new AbstractChatMessageRequest(lobbyName) {
            @Override
            public LobbyName getOriginLobby() {
                return super.getOriginLobby();
            }

            @Override
            public boolean isFromLobby() {
                return super.isFromLobby();
            }
        };

        assertEquals(lobbyName, request.getOriginLobby());
        assertTrue(request.isFromLobby());
    }
}
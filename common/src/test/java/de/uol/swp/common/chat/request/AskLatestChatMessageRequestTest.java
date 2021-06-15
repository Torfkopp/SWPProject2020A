package de.uol.swp.common.chat.request;

import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AskLatestChatMessageRequestTest {

    @Test
    void getAmount() {
        LobbyName lobbyName = new LobbyName("test");
        AskLatestChatMessageRequest request = new AskLatestChatMessageRequest(10, lobbyName);

        assertEquals(lobbyName, request.getOriginLobby());
        assertTrue(request.isFromLobby());
        assertEquals(10, request.getAmount());
    }

    @Test
    void getAmount_OriginLobbyIsNull() {
        AskLatestChatMessageRequest request = new AskLatestChatMessageRequest(10, null);

        assertNull(request.getOriginLobby());
        assertFalse(request.isFromLobby());
        assertEquals(10, request.getAmount());
    }
}
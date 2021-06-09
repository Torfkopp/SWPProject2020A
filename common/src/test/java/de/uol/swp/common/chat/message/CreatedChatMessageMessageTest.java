package de.uol.swp.common.chat.message;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class CreatedChatMessageMessageTest {

    private final ChatMessage mockedChatMessage = mock(ChatMessage.class);

    @Test
    void getLobbyName() {
        LobbyName lobbyName = new LobbyName("test lobby");
        CreatedChatMessageMessage msg = new CreatedChatMessageMessage(mockedChatMessage, lobbyName);

        assertTrue(msg.isLobbyChatMessage());
        assertEquals(lobbyName, msg.getLobbyName());
    }

    @Test
    void getLobbyName_isNull() {
        CreatedChatMessageMessage msg = new CreatedChatMessageMessage(mockedChatMessage);

        assertFalse(msg.isLobbyChatMessage());
        assertNull(msg.getLobbyName());
    }

    @Test
    void getMsg() {
        CreatedChatMessageMessage msg = new CreatedChatMessageMessage(mockedChatMessage);

        assertEquals(mockedChatMessage, msg.getMsg());
    }
}
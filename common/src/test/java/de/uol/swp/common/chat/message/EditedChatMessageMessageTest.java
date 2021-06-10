package de.uol.swp.common.chat.message;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class EditedChatMessageMessageTest {

    private final ChatMessage mockedChatMessage = mock(ChatMessage.class);

    @Test
    void getLobbyName_isNull() {
        EditedChatMessageMessage msg = new EditedChatMessageMessage(mockedChatMessage);

        assertFalse(msg.isLobbyChatMessage());
        assertNull(msg.getLobbyName());
        assertEquals(mockedChatMessage, msg.getMsg());
    }

    @Test
    void testEditedChatMessageMessage() {
        LobbyName lobbyName = new LobbyName("test");
        EditedChatMessageMessage msg = new EditedChatMessageMessage(mockedChatMessage, lobbyName);

        assertTrue(msg.isLobbyChatMessage());
        assertEquals(lobbyName, msg.getLobbyName());
        assertEquals(mockedChatMessage, msg.getMsg());
    }
}
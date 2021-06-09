package de.uol.swp.common.chat.message;

import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class SystemMessageMessageTest {

    @Test
    void getMsg() {
        LobbyName lobbyName = new LobbyName("test");
        SystemMessage systemMessage = mock(SystemMessage.class);
        SystemMessageMessage message = new SystemMessageMessage(lobbyName, systemMessage);

        assertEquals(lobbyName, message.getName());
        assertNull(message.getUser());
        assertEquals(systemMessage, message.getMsg());
    }
}
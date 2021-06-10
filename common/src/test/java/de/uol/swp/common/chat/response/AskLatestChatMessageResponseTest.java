package de.uol.swp.common.chat.response;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.dto.ChatMessageDTO;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class AskLatestChatMessageResponseTest {

    private final LobbyName lobbyName = new LobbyName("test");
    private final User defaultUser = mock(User.class);
    private final List<ChatMessage> history = new ArrayList<>();
    private final ChatMessage message1 = new ChatMessageDTO(1, defaultUser, "content");
    private final ChatMessage message2 = new ChatMessageDTO(2, defaultUser, "content 2 - electric boogaloo");
    private AskLatestChatMessageResponse response;

    @BeforeEach
    protected void setUp() {
        history.add(message1);
        history.add(message2);
    }

    @AfterEach
    protected void tearDown() {
        history.clear();
    }

    @Test
    void getChatHistory_LobbyNameIsNull() {
        response = new AskLatestChatMessageResponse(history);

        assertNull(response.getLobbyName());
        assertEquals(history, response.getChatHistory());
    }

    @Test
    void testAskLatestChatMessageResponse() {
        response = new AskLatestChatMessageResponse(history, lobbyName);

        assertEquals(lobbyName, response.getLobbyName());
        assertEquals(history, response.getChatHistory());
    }
}
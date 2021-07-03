package de.uol.swp.client.chat;

import de.uol.swp.common.lobby.LobbyName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class AsyncChatServiceTest {

    private static final long DURATION = 500L;
    private final LobbyName lobbyName = mock(LobbyName.class);
    private final ChatService syncChatService = mock(ChatService.class);
    private AsyncChatService chatService;

    @BeforeEach
    protected void setUp() {
        assertNotNull(syncChatService);
        chatService = new AsyncChatService(syncChatService);
    }

    @AfterEach
    protected void tearDown() {
        chatService = null;
    }

    @Test
    void askLatestMessages() {
        doNothing().when(syncChatService).askLatestMessages(isA(Integer.class));

        chatService.askLatestMessages(1);

        verify(syncChatService, timeout(DURATION)).askLatestMessages(1);
    }

    @Test
    void deleteMessage() {
        doNothing().when(syncChatService).deleteMessage(isA(Integer.class));

        chatService.deleteMessage(1);

        verify(syncChatService, timeout(DURATION)).deleteMessage(1);
    }

    @Test
    void editMessage() {
        doNothing().when(syncChatService).editMessage(isA(Integer.class), isA(String.class));

        chatService.editMessage(1, "test");

        verify(syncChatService, timeout(DURATION)).editMessage(1, "test");
    }

    @Test
    void newMessage() {
        doNothing().when(syncChatService).newMessage(isA(String.class));

        chatService.newMessage("test message");

        verify(syncChatService, timeout(DURATION)).newMessage("test message");
    }

    @Test
    void testAskLatestMessages() {
        doNothing().when(syncChatService).askLatestMessages(isA(Integer.class), isA(LobbyName.class));

        chatService.askLatestMessages(1, lobbyName);

        verify(syncChatService, timeout(DURATION)).askLatestMessages(1, lobbyName);
    }

    @Test
    void testDeleteMessage() {
        doNothing().when(syncChatService).deleteMessage(isA(Integer.class), isA(LobbyName.class));

        chatService.deleteMessage(1, lobbyName);

        verify(syncChatService, timeout(DURATION)).deleteMessage(1, lobbyName);
    }

    @Test
    void testEditMessage() {
        doNothing().when(syncChatService).editMessage(isA(Integer.class), isA(String.class), isA(LobbyName.class));

        chatService.editMessage(1, "test", lobbyName);

        verify(syncChatService, timeout(DURATION)).editMessage(1, "test", lobbyName);
    }

    @Test
    void testNewMessage() {
        doNothing().when(syncChatService).newMessage(isA(String.class), isA(LobbyName.class));

        chatService.newMessage("test message", lobbyName);

        verify(syncChatService, timeout(DURATION)).newMessage("test message", lobbyName);
    }
}
package de.uol.swp.client.chat;

import com.google.inject.Inject;
import de.uol.swp.client.util.ThreadManager;
import de.uol.swp.common.lobby.LobbyName;

/**
 * An asynchronous wrapper for the IChatService implementation
 * <p>
 * This class handles putting calls to an injected ChatService into
 * their own Task-Thread which is then executed away from the JavaFX
 * Application Thread, isolating non-UI calls onto their own threads.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.chat.IChatService
 * @since 2021-05-23
 */
public class AsyncChatService implements IChatService {

    private final ChatService syncChatService;

    @Inject
    public AsyncChatService(ChatService chatService) {
        this.syncChatService = chatService;
    }

    @Override
    public void askLatestMessages(int amount) {
        ThreadManager.runNow(() -> syncChatService.askLatestMessages(amount));
    }

    @Override
    public void askLatestMessages(int amount, LobbyName originLobby) {
        ThreadManager.runNow(() -> syncChatService.askLatestMessages(amount, originLobby));
    }

    @Override
    public void deleteMessage(int id) {
        ThreadManager.runNow(() -> syncChatService.deleteMessage(id));
    }

    @Override
    public void deleteMessage(int id, LobbyName originLobby) {
        ThreadManager.runNow(() -> syncChatService.deleteMessage(id, originLobby));
    }

    @Override
    public void editMessage(int id, String newContent) {
        ThreadManager.runNow(() -> syncChatService.editMessage(id, newContent));
    }

    @Override
    public void editMessage(int id, String newContent, LobbyName originLobby) {
        ThreadManager.runNow(() -> syncChatService.editMessage(id, newContent, originLobby));
    }

    @Override
    public void newMessage(String msg) {
        ThreadManager.runNow(() -> syncChatService.newMessage(msg));
    }

    @Override
    public void newMessage(String msg, LobbyName originLobby) {
        ThreadManager.runNow(() -> syncChatService.newMessage(msg, originLobby));
    }
}

package de.uol.swp.client.chat;

import com.google.inject.Inject;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.util.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger LOG = LogManager.getLogger(AsyncChatService.class);
    private final ChatService syncChatService;

    /**
     * Constructor
     *
     * @param syncChatService The synchronous ChatService (injected)
     */
    @Inject
    public AsyncChatService(ChatService syncChatService) {
        this.syncChatService = syncChatService;
        LOG.debug("AsyncChatService initialised");
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

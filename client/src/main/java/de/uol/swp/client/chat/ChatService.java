package de.uol.swp.client.chat;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.chat.request.*;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.Message;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is used to hide the communication implementation details
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.chat.IChatService
 * @since 2020-12-17
 */
@SuppressWarnings("UnstableApiUsage")
public class ChatService implements IChatService {

    private final EventBus bus;
    private final Logger LOG = LogManager.getLogger(ChatService.class);
    private final IUserService userService;

    /**
     * Constructor
     *
     * @param bus The EventBus set in ClientModule
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2020-12-17
     */
    @Inject
    public ChatService(EventBus bus, IUserService userService) {
        this.bus = bus;
        this.userService = userService;
        LOG.debug("ChatService started");
    }

    @Override
    public void askLatestMessages(int amount) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending AskLatestMessagesRequest");
                Message request = new AskLatestChatMessageRequest(amount);
                bus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void askLatestMessages(int amount, LobbyName originLobby) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending AskLatestMessagesRequest for Lobby {}", originLobby);
                Message request = new AskLatestChatMessageRequest(amount, originLobby);
                bus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void deleteMessage(int id) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending DeleteChatMessageRequest");
                Message request = new DeleteChatMessageRequest(id, userService.getLoggedInUser());
                bus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void deleteMessage(int id, LobbyName originLobby) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending DeleteChatMessageRequest for Lobby {}", originLobby);
                Message request = new DeleteChatMessageRequest(id, userService.getLoggedInUser(), originLobby);
                bus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void editMessage(int id, String newContent) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending EditChatMessageRequest");
                Message request = new EditChatMessageRequest(id, newContent, userService.getLoggedInUser());
                bus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void editMessage(int id, String newContent, LobbyName originLobby) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending EditChatMessageRequest for Lobby {}", originLobby);
                Message request = new EditChatMessageRequest(id, newContent, userService.getLoggedInUser(),
                                                             originLobby);
                bus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void newMessage(String msg) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending NewChatMessageRequest");
                Message request = new NewChatMessageRequest(userService.getLoggedInUser(), msg);
                bus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void newMessage(String msg, LobbyName originLobby) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending NewChatMessageRequest for Lobby {}", originLobby);
                Message request = new NewChatMessageRequest(userService.getLoggedInUser(), msg, originLobby);
                bus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
}

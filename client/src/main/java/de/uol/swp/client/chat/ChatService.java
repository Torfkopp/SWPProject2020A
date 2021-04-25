package de.uol.swp.client.chat;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.LobbyName;
import de.uol.swp.common.chat.request.*;
import de.uol.swp.common.message.Message;
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
        LOG.debug("Sending AskLatestMessagesRequest");
        Message request = new AskLatestChatMessageRequest(amount);
        bus.post(request);
    }

    @Override
    public void askLatestMessages(int amount, String originLobby) {
        LOG.debug("Sending AskLatestMessagesRequest for Lobby {}", originLobby);
        Message request = new AskLatestChatMessageRequest(amount, originLobby);
        bus.post(request);
    }

    @Override
    public void deleteMessage(int id) {
        LOG.debug("Sending DeleteChatMessageRequest");
        Message request = new DeleteChatMessageRequest(id, userService.getLoggedInUser());
        bus.post(request);
    }

    @Override
    public void deleteMessage(int id, String originLobby) {
        LOG.debug("Sending DeleteChatMessageRequest for Lobby {}", originLobby);
        Message request = new DeleteChatMessageRequest(id, userService.getLoggedInUser(), originLobby);
        bus.post(request);
    }

    @Override
    public void editMessage(int id, String newContent) {
        LOG.debug("Sending EditChatMessageRequest");
        Message request = new EditChatMessageRequest(id, newContent, userService.getLoggedInUser());
        bus.post(request);
    }

    @Override
    public void editMessage(int id, String newContent, String originLobby) {
        LOG.debug("Sending EditChatMessageRequest for Lobby {}", originLobby);
        Message request = new EditChatMessageRequest(id, newContent, userService.getLoggedInUser(), originLobby);
        bus.post(request);
    }

    @Override
    public void newMessage(String msg) {
        LOG.debug("Sending NewChatMessageRequest");
        Message request = new NewChatMessageRequest(userService.getLoggedInUser(), msg);
        bus.post(request);
    }

    @Override
    public void newMessage(String msg, String originLobby) {
        LOG.debug("Sending NewChatMessageRequest for Lobby {}", originLobby);
        Message request = new NewChatMessageRequest(userService.getLoggedInUser(), msg, originLobby);
        bus.post(request);
    }
}

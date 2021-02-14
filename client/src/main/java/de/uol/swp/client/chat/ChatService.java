package de.uol.swp.client.chat;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.chat.request.*;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
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

    /**
     * Constructor
     *
     * @param bus The EventBus set in ClientModule
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2020-12-17
     */
    @Inject
    public ChatService(EventBus bus) {
        LOG.debug("ChatService started");
        this.bus = bus;
    }

    @Override
    public void askLatestMessages(int amount) {
        LOG.debug("Sending AskLatestMessagesRequest");
        Message request = new AskLatestChatMessageRequest(amount);
        bus.post(request);
    }

    @Override
    public void askLatestMessages(int amount, String originLobby) {
        LOG.debug("Sending AskLatestMessagesRequest for Lobby " + originLobby);
        Message request = new AskLatestChatMessageRequest(amount, originLobby);
        bus.post(request);
    }

    @Override
    public void deleteMessage(int id, User requestingUser) {
        LOG.debug("Sending DeleteChatMessageRequest");
        Message request = new DeleteChatMessageRequest(id, requestingUser);
        bus.post(request);
    }

    @Override
    public void deleteMessage(int id, User requestingUser, String originLobby) {
        LOG.debug("Sending DeleteChatMessageRequest for Lobby " + originLobby);
        Message request = new DeleteChatMessageRequest(id, requestingUser, originLobby);
        bus.post(request);
    }

    @Override
    public void editMessage(int id, String newContent, User requestingUser) {
        LOG.debug("Sending EditChatMessageRequest");
        Message request = new EditChatMessageRequest(id, newContent, requestingUser);
        bus.post(request);
    }

    @Override
    public void editMessage(int id, String newContent, User requestingUser, String originLobby) {
        LOG.debug("Sending EditChatMessageRequest for Lobby " + originLobby);
        Message request = new EditChatMessageRequest(id, newContent, requestingUser, originLobby);
        bus.post(request);
    }

    @Override
    public void newMessage(User author, String msg) {
        LOG.debug("Sending NewChatMessageRequest");
        Message request = new NewChatMessageRequest(author, msg);
        bus.post(request);
    }

    @Override
    public void newMessage(User author, String msg, String originLobby) {
        LOG.debug("Sending NewChatMessageRequest for Lobby " + originLobby);
        Message request = new NewChatMessageRequest(author, msg, originLobby);
        bus.post(request);
    }
}

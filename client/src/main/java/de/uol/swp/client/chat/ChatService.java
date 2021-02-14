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
        LOG.debug("Send AskLatestMessagesRequest");
        Message request = new AskLatestChatMessageRequest(amount);
        bus.post(request);
    }

    @Override
    public void askLatestMessages(int amount, String originLobby) {
        LOG.debug("Send AskLatestMessagesRequest for Lobby " + originLobby);
        Message request = new AskLatestChatMessageRequest(amount, originLobby);
        bus.post(request);
    }

    @Override
    public void deleteMessage(int id) {
        LOG.debug("Send DeleteChatMessageRequest");
        Message request = new DeleteChatMessageRequest(id);
        bus.post(request);
    }

    @Override
    public void deleteMessage(int id, String originLobby) {
        LOG.debug("Send DeleteChatMessageRequest for Lobby " + originLobby);
        Message request = new DeleteChatMessageRequest(id, originLobby);
        bus.post(request);
    }

    @Override
    public void editMessage(int id, String newContent) {
        LOG.debug("Send EditChatMessageRequest");
        Message request = new EditChatMessageRequest(id, newContent);
        bus.post(request);
    }

    @Override
    public void editMessage(int id, String newContent, String originLobby) {
        LOG.debug("Send EditChatMessageRequest for Lobby " + originLobby);
        Message request = new EditChatMessageRequest(id, newContent, originLobby);
        bus.post(request);
    }

    @Override
    public void newMessage(User author, String msg) {
        LOG.debug("Send NewChatMessageRequest");
        Message request = new NewChatMessageRequest(author, msg);
        bus.post(request);
    }

    @Override
    public void newMessage(User author, String msg, String originLobby) {
        LOG.debug("Send NewChatMessageRequest for Lobby " + originLobby);
        Message request = new NewChatMessageRequest(author, msg, originLobby);
        bus.post(request);
    }
}

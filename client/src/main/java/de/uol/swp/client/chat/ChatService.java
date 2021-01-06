package de.uol.swp.client.chat;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.chat.request.AskLatestChatMessageRequest;
import de.uol.swp.common.chat.request.DeleteChatMessageRequest;
import de.uol.swp.common.chat.request.EditChatMessageRequest;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;

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

    /**
     * Constructor
     *
     * @param bus The EventBus set in ClientModule
     * @see de.uol.swp.client.di.ClientModule
     * @since 2020-12-17
     */
    @Inject
    public ChatService(EventBus bus) {
        this.bus = bus;
    }

    @Override
    public void newMessage(User author, String msg) {
        Message request = new NewChatMessageRequest(author, msg);
        bus.post(request);
    }

    @Override
    public void newMessage(User author, String msg, String originLobby) {
        Message request = new NewChatMessageRequest(author, msg, originLobby);
        bus.post(request);
    }

    @Override
    public void deleteMessage(int id) {
        Message request = new DeleteChatMessageRequest(id);
        bus.post(request);
    }

    @Override
    public void deleteMessage(int id, String originLobby) {
        Message request = new DeleteChatMessageRequest(id, originLobby);
        bus.post(request);
    }

    @Override
    public void editMessage(int id, String newContent) {
        Message request = new EditChatMessageRequest(id, newContent);
        bus.post(request);
    }

    @Override
    public void editMessage(int id, String newContent, String originLobby) {
        Message request = new EditChatMessageRequest(id, newContent, originLobby);
        bus.post(request);
    }

    @Override
    public void askLatestMessages(int amount) {
        Message request = new AskLatestChatMessageRequest(amount);
        bus.post(request);
    }

    @Override
    public void askLatestMessages(int amount, String originLobby) {
        Message request = new AskLatestChatMessageRequest(amount, originLobby);
        bus.post(request);
    }
}

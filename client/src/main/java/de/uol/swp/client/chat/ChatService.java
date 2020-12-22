package de.uol.swp.client.chat;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.chat.request.AskLatestChatMessageRequest;
import de.uol.swp.common.chat.request.DeleteChatMessageRequest;
import de.uol.swp.common.chat.request.EditChatMessageRequest;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
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
    public void newMessage(User currentUser, String msg) {
        NewChatMessageRequest request = new NewChatMessageRequest(currentUser, msg);
        bus.post(request);
    }

    @Override
    public void deleteMessage(int id) {
        DeleteChatMessageRequest request = new DeleteChatMessageRequest(id);
        bus.post(request);
    }

    @Override
    public void editMessage(int id, String newContent) {
        EditChatMessageRequest request = new EditChatMessageRequest(id, newContent);
        bus.post(request);
    }

    @Override
    public void askLatestMessages(int amount) {
        AskLatestChatMessageRequest request = new AskLatestChatMessageRequest(amount);
        bus.post(request);
    }
}

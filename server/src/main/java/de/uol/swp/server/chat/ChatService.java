package de.uol.swp.server.chat;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.request.AskLatestChatMessageRequest;
import de.uol.swp.common.chat.request.DeleteChatMessageRequest;
import de.uol.swp.common.chat.request.EditChatMessageRequest;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Mapping from EventBus calls to ChatManagement calls
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see AbstractService
 * @since 2020-12-16
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class ChatService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(ChatService.class);

    private final ChatManagement chatManagement;

    /**
     * Constructor
     *
     * @param eventBus       the EventBus used throughout the entire server (injected)
     * @param chatManagement the ChatManagement to use (injected)
     * @since 2020-12-16
     */
    @Inject
    public ChatService(EventBus eventBus, ChatManagement chatManagement) {
        super(eventBus);
        this.chatManagement = chatManagement;
    }

    /**
     * Handles NewChatMessageRequest found on the EventBus
     * <p>
     * If a NewChatMessageRequest is detected on the EventBus, this method is called.
     * It requests the ChatManagement to add a new message to the ChatMessageStore. If this succeeds, a
     * CreatedChatMessageMessage is posted on the EventBus, otherwise nothing happens.
     *
     * @param msg The NewChatMessageRequest found on the EventBus
     * @see ChatManagement#createChatMessage(User, String)
     * @see NewChatMessageRequest
     * @see CreatedChatMessageMessage
     * @since 2020-12-17
     */
    @Subscribe
    private void onNewChatMessageRequest(NewChatMessageRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got new ChatMessage message from " + msg.getAuthor().getUsername()
                    + " with content '" + msg.getContent() + '\'');
        }
        try {
            ChatMessage chatMessage = chatManagement.createChatMessage(msg.getAuthor(), msg.getContent());
            ServerMessage returnMessage = new CreatedChatMessageMessage(chatMessage);
            sendToAll(returnMessage);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * Handles DeleteChatMessageRequest found on the EventBus
     * <p>
     * If a DeleteChatMessageRequest is detected on the EventBus, this method is called.
     * It requests the ChatManagement to delete the message. If this succeeds, a
     * DeleteChatMessageMessage is posted on the EventBus, otherwise nothing happens.
     *
     * @param msg The DeleteChatMessageRequest found on the EventBus
     * @see ChatManagement#dropChatMessage(int)
     * @see DeleteChatMessageRequest
     * @see DeletedChatMessageMessage
     * @since 2020-12-17
     */
    @Subscribe
    private void onDeleteChatMessageRequest(DeleteChatMessageRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got new DeleteChatMessage message for the ChatMessage ID " + msg.getId());
        }
        try {
            chatManagement.dropChatMessage(msg.getId());
            ServerMessage returnMessage = new DeletedChatMessageMessage(msg.getId());
            sendToAll(returnMessage);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * Handles EditChatMessageRequest found on the EventBus
     * <p>
     * If a EditChatMessageRequest is detected on the EventBus, this method is called.
     * It requests the ChatManagement to update the message. If this succeeds, a
     * EditedChatMessageMessage is posted on the EventBus, otherwise nothing happens.
     *
     * @param msg The DeleteChatMessageRequest found on the EventBus
     * @see ChatManagement#updateChatMessage(int, String)
     * @see EditChatMessageRequest
     * @see EditedChatMessageMessage
     * @since 2020-12-17
     */
    @Subscribe
    private void onEditChatMessageRequest(EditChatMessageRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got new EditChatMessage message for the ChatMessage ID " + msg.getId()
                    + " and new content '" + msg.getContent() + '\'');
        }
        try {
            ChatMessage chatMessage = chatManagement.updateChatMessage(msg.getId(), msg.getContent());
            ServerMessage returnMessage = new EditedChatMessageMessage(chatMessage);
            sendToAll(returnMessage);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * Handles AskLatestChatMessageRequest found on the EventBus
     * <p>
     * If a AskLatestChatMessageRequest is detected on the EventBus, this method is called.
     * It requests the ChatManagement to retrieve the latest messages.
     * It then posts a AskLatestChatMessageResponse on the EventBus.
     *
     * @param msg The AskLatestChatMessageRequest found on the EventBus
     * @see ChatManagement#getLatestMessages(int)
     * @see AskLatestChatMessageRequest
     * @see AskLatestChatMessageResponse
     * @since 2020-12-17
     */
    @Subscribe
    private void onAskLatestChatMessageRequest(AskLatestChatMessageRequest msg) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Got new AskLatestMessage with " + msg.getAmount() + " messages");
        }
        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(msg.getAmount());
        ResponseMessage returnMessage = new AskLatestChatMessageResponse(latestMessages);
        if (msg.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(msg.getMessageContext().get());
        }
        post(returnMessage);
    }
}

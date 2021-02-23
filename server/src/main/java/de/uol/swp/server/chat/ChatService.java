package de.uol.swp.server.chat;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.request.*;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.lobby.message.LobbyDeletedMessage;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.devmenu.message.NewChatCommandMessage;
import de.uol.swp.server.lobby.LobbyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;

/**
 * Mapping EventBus calls to ChatManagement calls
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.server.AbstractService
 * @since 2020-12-16
 */
@SuppressWarnings("UnstableApiUsage")
public class ChatService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(ChatService.class);

    private final IChatManagement chatManagement;
    private final LobbyService lobbyService;

    /**
     * Constructor
     *
     * @param bus            the EventBus used throughout the entire server (injected)
     * @param chatManagement the ChatManagement to use (injected)
     * @param lobbyService   the LobbyService to use (injected)
     *
     * @since 2020-12-30
     */
    @Inject
    public ChatService(EventBus bus, ChatManagement chatManagement, LobbyService lobbyService) {
        super(bus);
        LOG.debug("ChatService started");
        this.chatManagement = chatManagement;
        this.lobbyService = lobbyService;
    }

    /**
     * Handles an AskLatestChatMessageRequest found on the EventBus
     * <p>
     * If a AskLatestChatMessageRequest is detected on the EventBus, this method is called.
     * It then requests the ChatManagement to retrieve the latest messages.
     * It then posts a AskLatestChatMessageResponse onto the EventBus.
     *
     * @param req The AskLatestChatMessageRequest found on the EventBus
     *
     * @see de.uol.swp.server.chat.ChatManagement#getLatestMessages(int)
     * @see de.uol.swp.common.chat.request.AskLatestChatMessageRequest
     * @see de.uol.swp.common.chat.response.AskLatestChatMessageResponse
     * @since 2020-12-17
     */
    @Subscribe
    private void onAskLatestChatMessageRequest(AskLatestChatMessageRequest req) {
        if (LOG.isDebugEnabled()) {
            if (req.isFromLobby()) LOG.debug("Received AskLatestChatMessageRequest from Lobby " + req.getOriginLobby());
            else LOG.debug("Received AskLatestChatMessageRequest");
        }
        ResponseMessage returnMessage;
        if (req.isFromLobby()) {
            List<ChatMessage> latestMessages = chatManagement.getLatestMessages(req.getAmount(), req.getOriginLobby());
            returnMessage = new AskLatestChatMessageResponse(latestMessages, req.getOriginLobby());
        } else {
            List<ChatMessage> latestMessages = chatManagement.getLatestMessages(req.getAmount());
            returnMessage = new AskLatestChatMessageResponse(latestMessages);
        }
        if (req.getMessageContext().isPresent()) {
            returnMessage.setMessageContext(req.getMessageContext().get());
        }
        post(returnMessage);
    }

    /**
     * Handles a DeleteChatMessageRequest found on the EventBus
     * <p>
     * If a DeleteChatMessageRequest is detected on the EventBus, this method
     * is called.
     * <p>
     * First, it calls the ChatManagement to find the ChatMessage with the
     * provided ID. If the returned Optional is empty or the User who sent the
     * DeleteChatMessageRequest is not the author of the ChatMessage, the
     * method returns immediately.
     * <p>
     * Otherwise, it requests the ChatManagement to delete the message. If this
     * succeeds, a DeleteChatMessageMessage is posted onto the EventBus.
     * Otherwise, nothing happens.
     *
     * @param req The DeleteChatMessageRequest found on the EventBus
     *
     * @see de.uol.swp.server.chat.ChatManagement#dropChatMessage(int)
     * @see de.uol.swp.common.chat.request.DeleteChatMessageRequest
     * @see de.uol.swp.common.chat.message.DeletedChatMessageMessage
     * @since 2020-12-17
     */
    @Subscribe
    private void onDeleteChatMessageRequest(DeleteChatMessageRequest req) {
        if (LOG.isDebugEnabled()) {
            if (req.isFromLobby()) LOG.debug("Received DeleteChatMessageRequest from Lobby " + req.getOriginLobby());
            else LOG.debug("Received DeleteChatMessageRequest");
        }
        try {
            Optional<ChatMessage> storedMsg = chatManagement.findChatMessage(req.getId(), req.getOriginLobby());
            if (storedMsg.isEmpty() || !storedMsg.get().getAuthor().equals(req.getRequestingUser())) return;
            if (req.isFromLobby()) {
                chatManagement.dropChatMessage(req.getId(), req.getOriginLobby());
                ServerMessage returnMessage = new DeletedChatMessageMessage(req.getId(), req.getOriginLobby());
                lobbyService.sendToAllInLobby(req.getOriginLobby(), returnMessage);
            } else {
                chatManagement.dropChatMessage(req.getId());
                ServerMessage returnMessage = new DeletedChatMessageMessage(req.getId());
                sendToAll(returnMessage);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * Handles an EditChatMessageRequest found on the EventBus
     * <p>
     * If an EditChatMessageRequest is detected on the EventBus, this method is
     * called.
     * <p>
     * First, it calls the ChatManagement to find the ChatMessage with the
     * provided ID. If the returned Optional is empty or the User who sent the
     * EditChatMessageRequest is not the author of the ChatMessage, the method
     * returns immediately.
     * <p>
     * Otherwise, it requests the ChatManagement to update the message. If this
     * succeeds, a EditedChatMessageMessage is posted onto the EventBus.
     * Otherwise, nothing happens.
     *
     * @param req The DeleteChatMessageRequest found on the EventBus
     *
     * @see de.uol.swp.server.chat.ChatManagement#updateChatMessage(int, String)
     * @see de.uol.swp.common.chat.request.EditChatMessageRequest
     * @see de.uol.swp.common.chat.message.EditedChatMessageMessage
     * @since 2020-12-17
     */
    @Subscribe
    private void onEditChatMessageRequest(EditChatMessageRequest req) {
        if (LOG.isDebugEnabled()) {
            if (req.isFromLobby()) LOG.debug("Received EditChatMessageRequest from Lobby " + req.getOriginLobby());
            else LOG.debug("Received EditChatMessageRequest");
        }
        try {
            Optional<ChatMessage> storedMsg = chatManagement.findChatMessage(req.getId(), req.getOriginLobby());
            if (storedMsg.isEmpty() || !storedMsg.get().getAuthor().equals(req.getRequestingUser())) return;
            if (req.isFromLobby()) {
                ChatMessage chatMessage = chatManagement
                        .updateChatMessage(req.getId(), req.getContent(), req.getOriginLobby());
                ServerMessage returnMessage = new EditedChatMessageMessage(chatMessage, req.getOriginLobby());
                lobbyService.sendToAllInLobby(req.getOriginLobby(), returnMessage);
            } else {
                ChatMessage chatMessage = chatManagement.updateChatMessage(req.getId(), req.getContent());
                ServerMessage returnMessage = new EditedChatMessageMessage(chatMessage);
                sendToAll(returnMessage);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    /**
     * Handles a LobbyDeletedMessage found on the EventBus
     * <p>
     * If a LobbyDeletedMessage is detected on the EventBus, this method is called.
     * It then requests the ChatManagement to drop the Chat History associated
     * with the now deleted Lobby.
     * <p>
     * Furthermore, if the LOG-level is set to DEBUG, the message "Lobby
     * {@literal <lobbyName>} was deleted, removing its chat history..." is
     * displayed in the LOG.
     *
     * @param msg The LobbyDeletedMessage found on the EventBus
     *
     * @author Phillip-André Suhr
     * @author Sven Ahrens
     * @see de.uol.swp.server.chat.IChatManagement#dropLobbyHistory(String)
     * @see de.uol.swp.common.lobby.message.LobbyDeletedMessage
     * @since 2021-01-16
     */
    @Subscribe
    private void onLobbyDeletedMessage(LobbyDeletedMessage msg) {
        LOG.debug("Received LobbyDeletedMessage for Lobby " + msg.getName());
        LOG.debug("---- Deleting Messages for the Lobby");
        chatManagement.dropLobbyHistory(msg.getName());
    }

    /**
     * Handles a NewChatMessageRequest found on the EventBus
     * <p>
     * If a NewChatMessageRequest is detected on the EventBus, this method is called.
     * It then requests the ChatManagement to add a new message to the ChatMessageStore. If this succeeds, a
     * CreatedChatMessageMessage is posted onto the EventBus. Otherwise, nothing happens.
     *
     * @param req The NewChatMessageRequest found on the EventBus
     *
     * @see de.uol.swp.server.chat.ChatManagement#createChatMessage(User, String)
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     * @see de.uol.swp.common.chat.message.CreatedChatMessageMessage
     * @since 2020-12-17
     */
    @Subscribe
    private void onNewChatMessageRequest(NewChatMessageRequest req) {
        if (req.getContent().startsWith("/")) {
            post(new NewChatCommandMessage(req.getAuthor(), req.getContent().substring(1), req));
            return;
        }
        if (LOG.isDebugEnabled()) {
            if (req.isFromLobby()) LOG.debug("Received NewChatMessageRequest from Lobby " + req.getOriginLobby());
            else LOG.debug("Received NewChatMessageRequest");
        }
        try {
            if (req.isFromLobby()) {
                ChatMessage chatMessage = chatManagement
                        .createChatMessage(req.getAuthor(), req.getContent(), req.getOriginLobby());
                ServerMessage returnMessage = new CreatedChatMessageMessage(chatMessage, req.getOriginLobby());
                lobbyService.sendToAllInLobby(req.getOriginLobby(), returnMessage);
            } else {
                ChatMessage chatMessage = chatManagement.createChatMessage(req.getAuthor(), req.getContent());
                ServerMessage returnMessage = new CreatedChatMessageMessage(chatMessage);
                sendToAll(returnMessage);
            }
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}

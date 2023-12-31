package de.uol.swp.server.chat;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.request.*;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.message.LobbyDeletedMessage;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.devmenu.message.NewChatCommandMessage;
import de.uol.swp.server.lobby.ILobbyManagement;
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
    private final ILobbyManagement lobbyManagement;
    private final CommandChatService commandChatService;
    private final LobbyService lobbyService;
    private final boolean commandsAllowed;

    /**
     * Constructor
     *
     * @param bus                The EventBus used throughout the entire server (injected)
     * @param chatManagement     The ChatManagement to use (injected)
     * @param lobbyManagement    The LobbyManagement to use (injected)
     * @param commandChatService The commandChatService to use (injected)
     * @param lobbyService       The LobbyService to use (injected)
     * @param commandsAllowed    Boolean whether Commands are allowed.
     *
     * @since 2020-12-30
     */
    @Inject
    public ChatService(EventBus bus, IChatManagement chatManagement, ILobbyManagement lobbyManagement,
                       CommandChatService commandChatService, LobbyService lobbyService,
                       @Named("commandsAllowed") boolean commandsAllowed) {
        super(bus);
        this.chatManagement = chatManagement;
        this.lobbyManagement = lobbyManagement;
        this.commandChatService = commandChatService;
        this.lobbyService = lobbyService;
        this.commandsAllowed = commandsAllowed;
        LOG.debug("ChatService started");
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
        if (req.isFromLobby()) LOG.debug("Received AskLatestChatMessageRequest for Lobby {}", req.getOriginLobby());
        else LOG.debug("Received AskLatestChatMessageRequest");
        ResponseMessage returnMessage;
        if (req.isFromLobby()) {
            List<ChatMessage> latestMessages = chatManagement.getLatestMessages(req.getAmount(), req.getOriginLobby());
            returnMessage = new AskLatestChatMessageResponse(latestMessages, req.getOriginLobby());
        } else {
            List<ChatMessage> latestMessages = chatManagement.getLatestMessages(req.getAmount());
            returnMessage = new AskLatestChatMessageResponse(latestMessages);
        }
        returnMessage.initWithMessage(req);
        LOG.debug("Sending AskLatestChatMessageResponse{}",
                  req.isFromLobby() ? " for Lobby " + req.getOriginLobby() : "");
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
        LobbyName originLobby = req.getOriginLobby();
        if (req.isFromLobby()) LOG.debug("Received DeleteChatMessageRequest for Lobby {}", originLobby);
        else LOG.debug("Received DeleteChatMessageRequest");
        try {
            Optional<ChatMessage> storedMsg = chatManagement.findChatMessage(req.getId(), originLobby);
            if (storedMsg.isEmpty() || !storedMsg.get().getAuthor().equals(req.getRequestingUser())) return;
            if (req.isFromLobby()) {
                chatManagement.dropChatMessage(req.getId(), originLobby);
                ServerMessage returnMessage = new DeletedChatMessageMessage(req.getId(), originLobby);
                LOG.debug("Sending DeletedChatMessageMessage for Lobby {}", originLobby);
                lobbyService.sendToAllInLobby(originLobby, returnMessage);
            } else {
                chatManagement.dropChatMessage(req.getId());
                ServerMessage returnMessage = new DeletedChatMessageMessage(req.getId());
                LOG.debug("Sending DeletedChatMessageMessage");
                sendToAll(returnMessage);
            }
        } catch (ChatManagementException e) {
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
        LobbyName originLobby = req.getOriginLobby();
        if (req.isFromLobby()) LOG.debug("Received EditChatMessageRequest for Lobby {}", originLobby);
        else LOG.debug("Received EditChatMessageRequest");
        try {
            Optional<ChatMessage> storedMsg = chatManagement.findChatMessage(req.getId(), originLobby);
            if (storedMsg.isEmpty() || !storedMsg.get().getAuthor().equals(req.getRequestingUser())) return;
            if (req.isFromLobby()) {
                ChatMessage chatMessage = chatManagement.updateChatMessage(req.getId(), req.getContent(), originLobby);
                ServerMessage returnMessage = new EditedChatMessageMessage(chatMessage, originLobby);
                LOG.debug("Sending EditedChatMessageMessage for Lobby {}", originLobby);
                lobbyService.sendToAllInLobby(originLobby, returnMessage);
            } else {
                ChatMessage chatMessage = chatManagement.updateChatMessage(req.getId(), req.getContent());
                ServerMessage returnMessage = new EditedChatMessageMessage(chatMessage);
                LOG.debug("Sending EditedChatMessageMessage");
                sendToAll(returnMessage);
            }
        } catch (ChatManagementException e) {
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
     * @see de.uol.swp.server.chat.IChatManagement#dropLobbyHistory(de.uol.swp.common.lobby.LobbyName)
     * @see de.uol.swp.common.lobby.message.LobbyDeletedMessage
     * @since 2021-01-16
     */
    @Subscribe
    private void onLobbyDeletedMessage(LobbyDeletedMessage msg) {
        LOG.debug("Received LobbyDeletedMessage for Lobby {}", msg.getName());
        LOG.debug("---- Deleting Messages for the Lobby");
        chatManagement.dropLobbyHistory(msg.getName());
    }

    /**
     * Handles a NewChatMessageRequest found on the EventBus
     * <p>
     * If a NewChatMessageRequest is detected on the EventBus, this method is called.
     * It then requests the ChatManagement to add a new message to the ChatMessageStore. If this succeeds, a
     * CreatedChatMessageMessage is posted onto the EventBus. Otherwise, nothing happens.
     * If the content starts with a "/", it will be treated as a command and
     * wrapped in a NewChatCommandMessage which will be picked up by the CommandService.
     * However, if the lobby from which this command attempt originated doesn't allow
     * commands, a LobbyExceptionMessage is sent to the client instead and this method
     * returns immediately.
     *
     * @param req The NewChatMessageRequest found on the EventBus
     *
     * @see de.uol.swp.server.chat.ChatManagement#createChatMessage(de.uol.swp.common.user.Actor, String)
     * @see de.uol.swp.common.chat.request.NewChatMessageRequest
     * @see de.uol.swp.common.chat.message.CreatedChatMessageMessage
     * @see de.uol.swp.server.devmenu.CommandService
     * @see de.uol.swp.common.exception.LobbyExceptionMessage
     * @since 2020-12-17
     */
    @Subscribe
    private void onNewChatMessageRequest(NewChatMessageRequest req) {
        if (req.getContent().startsWith("/")) { // this is a command, forward it to the CommandChatService
            if (!commandsAllowed) {
                commandChatService.newGameOrDevCommand(req);
                return;
            }
            LOG.debug("Sending NewChatCommandMessage");
            post(new NewChatCommandMessage(req.getAuthor(), req.getContent().substring(1), req));
            return;
        }
        LobbyName originLobby = req.getOriginLobby();
        if (req.isFromLobby()) LOG.debug("Received NewChatMessageRequest from Lobby {}", originLobby);
        else LOG.debug("Received NewChatMessageRequest");
        try {
            if (req.isFromLobby()) {
                ChatMessage chatMessage = chatManagement
                        .createChatMessage(req.getAuthor(), req.getContent(), originLobby);
                ServerMessage returnMessage = new CreatedChatMessageMessage(chatMessage, originLobby);
                LOG.debug("Sending CreatedChatMessageMessage for Lobby {}", originLobby);
                lobbyService.sendToAllInLobby(originLobby, returnMessage);
            } else {
                ChatMessage chatMessage = chatManagement.createChatMessage(req.getAuthor(), req.getContent());
                ServerMessage returnMessage = new CreatedChatMessageMessage(chatMessage);
                LOG.debug("Sending CreatedChatMessageMessage");
                sendToAll(returnMessage);
            }
        } catch (ChatManagementException e) {
            LOG.error(e);
        }
    }
}

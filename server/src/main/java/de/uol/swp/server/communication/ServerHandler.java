package de.uol.swp.server.communication;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.message.*;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.response.AlreadyLoggedInResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import de.uol.swp.server.message.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class handles all client/server communication
 *
 * @author Marco Grawunder
 * @see de.uol.swp.server.communication.ServerHandlerDelegate
 * @since 2017-03-17
 */
@SuppressWarnings("UnstableApiUsage")
public class ServerHandler implements ServerHandlerDelegate {

    private static final Logger LOG = LogManager.getLogger(ServerHandler.class);

    /**
     * Connected Clients
     */
    private final List<MessageContext> connectedClients = new CopyOnWriteArrayList<>();

    /**
     * Clients with logged in sessions
     */
    private final Map<MessageContext, Session> activeSessions = new HashMap<>();

    /**
     * Event bus (injected)
     */
    private final EventBus eventBus;

    /**
     * Constructor
     *
     * @param eventBus The EventBus used throughout the entire server
     * @see com.google.common.eventbus.EventBus
     */
    @Inject
    public ServerHandler(EventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Override
    public void process(RequestMessage msg) {
        LOG.debug("Received new message from client " + msg);
        try {
            //Code Analysis says: "'Optional.get()' without 'isPresent()' check" -Wario
            checkIfMessageNeedsAuthorisation(msg.getMessageContext().get(), msg);
            eventBus.post(msg);
        } catch (Exception e) {
            LOG.error("ServerException " + e.getClass().getName() + " " + e.getMessage());
            //same as above
            sendToClient(msg.getMessageContext().get(), new ExceptionMessage(e.getMessage()));
        }
    }

    /**
     * Helper method checking if a message has the required authorisation
     *
     * @param ctx The message's MessageContext to check
     * @param msg The message to check
     * @throws SecurityException Authorisation requirement not met
     * @since 2019-11-20
     */
    private void checkIfMessageNeedsAuthorisation(MessageContext ctx, RequestMessage msg) {
        if (msg.authorisationNeeded()) {
            if (getSession(ctx).isEmpty()) {
                throw new SecurityException("Authorisation required. Client not logged in!");
            }
            msg.setSession(getSession(ctx).get());
        }
    }

    /**
     * Handles errors produced by the EventBus
     * <p>
     * If an DeadEvent object is detected on the EventBus, this method is called.
     * It writes "DeadEvent detected " and the error message of the detected DeadEvent
     * object to the log if the loglevel is set to WARN or higher.
     *
     * @param deadEvent The DeadEvent object found on the EventBus
     * @since 2019-11-20
     */
    @Subscribe
    private void onDeadEvent(DeadEvent deadEvent) {
        LOG.error("DeadEvent detected " + deadEvent);
    }

    /**
     * Handles exceptions on the Server
     * <p>
     * If a ServerExceptionMessage is detected on the EventBus, this method is called.
     * It sends the ServerExceptionMessage to the affiliated client
     * if a client is affiliated.
     *
     * @param msg The ServerExceptionMessage found on the EventBus
     * @since 2019-11-20
     */
    @Subscribe
    private void onServerException(ServerExceptionMessage msg) {
        Optional<MessageContext> ctx = getCtx(msg);
        LOG.error(msg.getException());
        ctx.ifPresent(channelHandlerContext -> sendToClient(channelHandlerContext,
                new ExceptionMessage(msg.getException().getMessage())));
    }

    // -------------------------------------------------------------------------------
    // Handling of connected clients
    // -------------------------------------------------------------------------------

    @Override
    public void clientDisconnected(MessageContext ctx) {
        LOG.debug("Client disconnected");
        Session session = this.activeSessions.get(ctx);
        if (session != null) {
            Message msg = new ClientDisconnectedMessage();
            msg.setSession(session);
            eventBus.post(msg);
            removeSession(ctx);
        }
        connectedClients.remove(ctx);
    }

    @Override
    public void newClientConnected(MessageContext ctx) {
        LOG.debug("New client " + ctx + " connected");
        connectedClients.add(ctx);
    }

    // -------------------------------------------------------------------------------
    // User Management Events (from event bus)
    // -------------------------------------------------------------------------------

    /**
     * Handles ClientAuthorisedMessages found on the EventBus
     * <p>
     * If a ClientAuthorisedMessage is detected on the EventBus, this method is called.
     * It gets the MessageContext, then hands it over to sendToClient along with a new LoginSuccessfulResponse.
     * It then gives a new UserLoggedInMessage to sendMessage in order to notify all connected clients.
     *
     * @param msg The ClientAuthorisedMessage found on the EventBus
     * @see de.uol.swp.server.communication.ServerHandler#sendToClient(MessageContext, ResponseMessage)
     * @see de.uol.swp.server.communication.ServerHandler#sendMessage(ServerMessage)
     * @since 2019-11-20
     */
    @Subscribe
    private void onClientAuthorisedMessage(ClientAuthorisedMessage msg) {
        Optional<MessageContext> ctx = getCtx(msg);
        if (ctx.isPresent() && msg.getSession().isPresent()) {
            if (msg.getOldSession() != null) {
                sendToClient(ctx.get(), new AlreadyLoggedInResponse(msg.getUser()));
            } else {
                putSession(ctx.get(), msg.getSession().get());
                sendToClient(ctx.get(), new LoginSuccessfulResponse(msg.getUser()));
                sendMessage(new UserLoggedInMessage(msg.getUser().getUsername()));
            }
        } else {
            LOG.warn("No context for " + msg);
        }
    }

    /**
     * Handles a FetchUserContextInternalRequest found on the EventBus
     * <p>
     * If a FetchUserContextInternalRequest is detected on the EventBus
     * this method gets the MessageContext associated with the provided
     * Session object and sends the Message contained in the
     * FetchUserContextInternalRequest to the specified client.
     *
     * @param req FetchUserContextInternalRequest found on the EventBus
     * @author Phillip-André Suhr
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.server.message.FetchUserContextInternalRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onFetchUserContextInternalRequest(FetchUserContextInternalRequest req) {
        Optional<MessageContext> ctx = getCtx(req.getUserSession());
        ctx.ifPresent(messageContext -> sendToClient(messageContext, req.getReturnMessage()));
    }

    /**
     * Handles an UserLoggedOutMessages found on the EventBus
     * <p>
     * If an UserLoggedOutMessage is detected on the EventBus, this method is called.
     * It gets the MessageContext and then gives the message to sendMessage
     * in order to send it to the connected client.
     *
     * @param msg The UserLoggedOutMessage found on the EventBus
     * @see de.uol.swp.server.communication.ServerHandler#sendMessage(ServerMessage)
     * @since 2019-11-20
     */
    @Subscribe
    private void onUserLoggedOutMessage(UserLoggedOutMessage msg) {
        Optional<MessageContext> ctx = getCtx(msg);
        ctx.ifPresent(this::removeSession);
        sendMessage(msg);
    }

    // -------------------------------------------------------------------------------
    // ResponseEvents
    // -------------------------------------------------------------------------------

    /**
     * Handles a ResponseMessages found on the EventBus
     * <p>
     * If a ResponseMessage is detected on the EventBus, this method is called.
     * It gets the MessageContext, then gives it and the ResponseMessage to sendToClient.
     *
     * @param msg The ResponseMessage found on the EventBus
     * @see de.uol.swp.server.communication.ServerHandler#sendToClient(MessageContext, ResponseMessage)
     * @since 2019-11-20
     */
    @Subscribe
    private void onResponseMessage(ResponseMessage msg) {
        Optional<MessageContext> ctx = getCtx(msg);
        if (ctx.isPresent()) {
            msg.setSession(null);
            msg.setMessageContext(null);
            LOG.debug("Send to client " + ctx.get() + " message " + msg);
            sendToClient(ctx.get(), msg);
        }
    }

    // -------------------------------------------------------------------------------
    // ServerMessages
    // -------------------------------------------------------------------------------

    /**
     * Handles a ServerMessages found on the EventBus
     * <p>
     * If a ServerMessage is detected on the EventBus, this method is called.
     * It sets the Session and MessageContext to null, then gives the message
     * to sendMessage in order to send it to all connected clients.
     *
     * @param msg The ServerMessage found on the EventBus
     * @see de.uol.swp.server.communication.ServerHandler#sendMessage(ServerMessage)
     * @since 2019-11-20
     */
    @Subscribe
    private void onServerMessage(ServerMessage msg) {
        msg.setSession(null);
        msg.setMessageContext(null);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Send " + msg + " to " + (msg.getReceiver().isEmpty() || msg.getReceiver() == null ? "all" :
                    msg.getReceiver()));
        }
        sendMessage(msg);
    }

    // -------------------------------------------------------------------------------
    // Session Management (helper methods)
    // -------------------------------------------------------------------------------

    /**
     * Gets MessageContext from the message
     *
     * @param message Message to get the MessageContext from
     * @return Optional Object containing the MessageContext if there is any
     * @see de.uol.swp.common.message.Message
     * @see de.uol.swp.common.message.MessageContext
     * @since 2019-11-20
     */
    private Optional<MessageContext> getCtx(Message message) {
        if (message.getMessageContext().isPresent()) {
            return message.getMessageContext();
        }
        if (message.getSession().isPresent()) {
            return getCtx(message.getSession().get());
        }
        return Optional.empty();
    }

    /**
     * Gets MessageContext for a specified receiver
     *
     * @param session Session of the user to search
     * @return Optional Object containing the MessageContext if there is any
     * @see de.uol.swp.common.user.Session
     * @see de.uol.swp.common.message.MessageContext
     * @since 2019-11-20
     */
    private Optional<MessageContext> getCtx(Session session) {
        for (Map.Entry<MessageContext, Session> e : activeSessions.entrySet()) {
            if (e.getValue().equals(session)) {
                return Optional.of(e.getKey());
            }
        }
        return Optional.empty();
    }

    /**
     * Gets the MessageContexts for specified receivers
     *
     * @param receiver A list containing the sessions of the users to search
     * @return List of MessageContexts for the given sessions
     * @see de.uol.swp.common.user.Session
     * @see de.uol.swp.common.message.MessageContext
     * @since 2019-11-20
     */
    private List<MessageContext> getCtx(Iterable<Session> receiver) {
        List<MessageContext> ctxs = new ArrayList<>();
        receiver.forEach(r -> {
            Optional<MessageContext> s = getCtx(r);
            s.ifPresent(ctxs::add);
        });
        return ctxs;
    }

    /**
     * Gets the session for a given MessageContext
     *
     * @param ctx The MessageContext
     * @return Optional Object containing the session if found
     * @see de.uol.swp.common.user.Session
     * @see de.uol.swp.common.message.MessageContext
     * @since 2019-11-20
     */
    private Optional<Session> getSession(MessageContext ctx) {
        Session session = activeSessions.get(ctx);
        return session != null ? Optional.of(session) : Optional.empty();
    }

    /**
     * Adds a new session to the active sessions
     *
     * @param ctx        The MessageContext belonging to the session
     * @param newSession The Session to add
     * @since 2019-11-20
     */
    private void putSession(MessageContext ctx, Session newSession) {
        // TODO: check if session is already bound to connection
        activeSessions.put(ctx, newSession);
    }

    /**
     * Removes a session specified by MessageContext from the active sessions
     *
     * @param ctx the MessageContext
     * @since 2019-11-20
     */
    private void removeSession(MessageContext ctx) {
        activeSessions.remove(ctx);
    }

    // -------------------------------------------------------------------------------
    // Help methods: Send only objects of type Message
    // -------------------------------------------------------------------------------

    /**
     * Sends a ServerMessage either to a specified receiver or to all connected clients
     *
     * @param msg ServerMessage to send
     * @see de.uol.swp.common.message.ServerMessage
     * @since 2019-11-20
     */
    private void sendMessage(ServerMessage msg) {
        if (msg.getReceiver() == null || msg.getReceiver().isEmpty()) {
            sendToMany(connectedClients, msg);
        } else {
            sendToMany(getCtx(msg.getReceiver()), msg);
        }
    }

    /**
     * Sends a ResponseMessage to a client specified by a MessageContext
     *
     * @param ctx     The MessageContext containing the specified client
     * @param message The Message to send
     * @see de.uol.swp.common.message.ResponseMessage
     * @see de.uol.swp.common.message.MessageContext
     * @since 2019-11-20
     */
    private void sendToClient(MessageContext ctx, ResponseMessage message) {
        LOG.trace("Trying to sendMessage to client: " + ctx + " " + message);
        ctx.writeAndFlush(message);
    }

    /**
     * Sends a ServerMessage to multiple users specified by a list of MessageContexts
     *
     * @param sendTo List of MessageContexts to send the message to
     * @param msg    Message to send
     * @see de.uol.swp.common.message.MessageContext
     * @see de.uol.swp.common.message.ServerMessage
     * @since 2019-11-20
     */
    private void sendToMany(List<MessageContext> sendTo, ServerMessage msg) {
        for (MessageContext client : sendTo) {
            try {
                client.writeAndFlush(msg);
            } catch (Exception e) {
                // TODO: Handle exceptions for unreachable clients
                e.printStackTrace();
            }
        }
    }
}

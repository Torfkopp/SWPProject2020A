package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.game.message.CreateGameMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.*;
import de.uol.swp.common.message.ExceptionMessage;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Handles the lobby requests sent by the users
 *
 * @author Marco Grawunder
 * @since 2019-10-08
 */
@SuppressWarnings("UnstableApiUsage")
@Singleton
public class LobbyService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(LobbyService.class);
    private final LobbyManagement lobbyManagement;
    private final AuthenticationService authenticationService;
    private final List<Lobby> lobbyList = new ArrayList<>();

    /**
     * Constructor
     *
     * @param lobbyManagement       The management class for creating, storing, and deleting
     *                              lobbies
     * @param authenticationService The user management
     * @param eventBus              The server-wide EventBus
     *
     * @since 2019-10-08
     */
    @Inject
    public LobbyService(LobbyManagement lobbyManagement, AuthenticationService authenticationService,
                        EventBus eventBus) {
        super(eventBus);
        if (LOG.isDebugEnabled()) LOG.debug("LobbyService started");
        this.lobbyManagement = lobbyManagement;
        this.authenticationService = authenticationService;
    }

    /**
     * Handles a CreateLobbyRequest found on the EventBus
     * <p>
     * If a CreateLobbyRequest is detected on the EventBus, this method is called.
     * It creates a new Lobby via the LobbyManagement using the parameters from the
     * request and sends a LobbyCreatedMessage to every connected user.
     *
     * @param message The CreateLobbyRequest found on the EventBus
     *
     * @see de.uol.swp.server.lobby.LobbyManagement#createLobby(String, User)
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @since 2019-10-08
     */
    @Subscribe
    private void onCreateLobbyRequest(CreateLobbyRequest message) {
        if (LOG.isDebugEnabled()) LOG.debug("Received CreateLobbyRequest for Lobby " + message.getName());
        try {
            lobbyManagement.createLobby(message.getName(), message.getOwner());
            Message responseMessage = new CreateLobbyResponse(message.getName());
            if (message.getMessageContext().isPresent()) {
                responseMessage.setMessageContext(message.getMessageContext().get());
            }
            post(responseMessage);
            sendToAll(new LobbyCreatedMessage(message.getName(), message.getOwner()));
        } catch (IllegalArgumentException e) {
            Message exceptionMessage = new LobbyExceptionMessage(e.getMessage());
            if (message.getMessageContext().isPresent()) {
                exceptionMessage.setMessageContext(message.getMessageContext().get());
            }
            post(exceptionMessage);
            LOG.debug(e.getMessage());
        }
    }

    /**
     * Handles a LobbyJoinUserRequest found on the EventBus
     * <p>
     * If a LobbyJoinUserRequest is detected on the EventBus, this method is called.
     * It adds a user to a lobby stored in the LobbyManagement and
     * sends a UserJoinedLobbyMessage to every user in the lobby.
     *
     * @param message The LobbyJoinUserRequest found on the EventBus
     *
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-12-19
     */
    @Subscribe
    private void onLobbyJoinUserRequest(LobbyJoinUserRequest message) {
        if (LOG.isDebugEnabled()) LOG.debug("Received LobbyJoinUserRequest for Lobby " + message.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(message.getName());

        if (lobby.isPresent()) {
            if (lobby.get().getUsers().size() < 4) {
                if (!lobby.get().getUsers().contains(message.getUser())) {
                    lobby.get().joinUser(message.getUser());
                    Message responseMessage = new JoinLobbyResponse(message.getName());
                    if (message.getMessageContext().isPresent()) {
                        responseMessage.setMessageContext(message.getMessageContext().get());
                    }
                    post(responseMessage);
                    sendToAllInLobby(message.getName(),
                                     new UserJoinedLobbyMessage(message.getName(), message.getUser()));
                } else {
                    ExceptionMessage exceptionMessage = new LobbyExceptionMessage("You're already in this lobby!");
                    if (message.getMessageContext().isPresent()) {
                        exceptionMessage.setMessageContext(message.getMessageContext().get());
                    }
                    post(exceptionMessage);
                    LOG.debug(exceptionMessage.getException());
                }
            } else {
                ExceptionMessage exceptionMessage = new LobbyExceptionMessage("This lobby is full!");
                if (message.getMessageContext().isPresent()) {
                    exceptionMessage.setMessageContext(message.getMessageContext().get());
                }
                post(exceptionMessage);
                LOG.debug(exceptionMessage.getException());
            }
        } else {
            ExceptionMessage exceptionMessage = new LobbyExceptionMessage("This lobby does not exist!");
            if (message.getMessageContext().isPresent()) {
                exceptionMessage.setMessageContext(message.getMessageContext().get());
            }
            post(exceptionMessage);
            LOG.debug(exceptionMessage.getException());
        }
    }

    /**
     * Handles a LobbyLeaveUserRequest found on the EventBus
     * <p>
     * If a LobbyLeaveUserRequest is detected on the EventBus, this method is called.
     * It marks a user as not ready, removes them from a lobby stored in the
     * LobbyManagement and sends a UserLeftLobbyMessage to every user in the lobby.
     *
     * @param message The LobbyJoinUserRequest found on the EventBus
     *
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2019-10-08
     */
    @Subscribe
    private void onLobbyLeaveUserRequest(LobbyLeaveUserRequest message) {
        if (LOG.isDebugEnabled()) LOG.debug("Received LobbyLeaveUserRequest for Lobby " + message.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(message.getName());

        if (lobby.isPresent()) {
            lobby.get().unsetUserReady(message.getUser());
            try {
                lobby.get().leaveUser(message.getUser());
                sendToAllInLobby(message.getName(), new UserLeftLobbyMessage(message.getName(), message.getUser()));
            } catch (IllegalArgumentException exception) {
                lobbyManagement.dropLobby(lobby.get().getName());
                sendToAll(new LobbyDeletedMessage(message.getName()));
            }
        }
    }

    /**
     * Handles a RemoveFromLobbiesRequest found on the EventBus
     * <p>
     * If a RemoveFromLobbiesRequest is detected on the EventBus, this method is called.
     * It removes a user from a lobby stored in the LobbyManagement and sends a
     * UserLeftLobbyMessage to every user in the lobby.
     * It posts a RemoveFromLobbiesResponse containing a Map of the Lobbies where the user is in.
     *
     * @param message The RemoveFromLobbiesRequest found on the EventBus
     *
     * @author Finn Haase
     * @author Aldin Dervisi
     * @see de.uol.swp.common.lobby.response.RemoveFromLobbiesResponse
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2021-01-28
     */
    @Subscribe
    private void onRemoveFromLobbiesRequest(RemoveFromLobbiesRequest message) {
        if (LOG.isDebugEnabled()) LOG.debug("Received RemoveFromLobbiesRequest");
        User user = message.getUser();
        Map<String, Lobby> lobbies = lobbyManagement.getLobbies();
        Map<String, Lobby> lobbiesWithUser = new HashMap<>();
        for (Map.Entry<String, Lobby> entry : lobbies.entrySet()) {
            if (entry.getValue().getUsers().contains(user)) {
                Lobby lobby = entry.getValue();
                String lobbyName = entry.getKey();
                lobbiesWithUser.put(entry.getKey(), lobby);
                try {
                    lobby.leaveUser(user);
                    sendToAllInLobby(lobbyName, new UserLeftLobbyMessage(lobbyName, user));
                } catch (IllegalArgumentException exception) {
                    lobbyManagement.dropLobby(lobbyName);
                    sendToAll(new LobbyDeletedMessage(lobbyName));
                }
            }
        }
        Message response = new RemoveFromLobbiesResponse(Collections.unmodifiableMap(lobbiesWithUser));
        post(response);
    }

    /**
     * Handles a RetrieveAllLobbiesRequest found on the EventBus
     * <p>
     * If a RetrieveAllLobbiesRequest is detected on the EventBus, this method is called.
     * It posts an AllLobbiesResponse containing a list of all lobby names
     *
     * @param retrieveAllLobbiesRequest The RetrieveAllLobbiesRequest found on the EventBus
     *
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
     * @since 2020-12-12
     */
    @Subscribe
    private void onRetrieveAllLobbiesRequest(RetrieveAllLobbiesRequest retrieveAllLobbiesRequest) {
        if (LOG.isDebugEnabled()) LOG.debug("Received RetrieveAllLobbiesRequest");
        Message response = new AllLobbiesResponse(lobbyManagement.getLobbies());
        response.initWithMessage(retrieveAllLobbiesRequest);
        post(response);
    }

    /**
     * Handles a RetrieveAllLobbyMembersRequest found on the EventBus
     * <p>
     * If a RetrieveAllLobbyMembersRequest is detected on the EventBus, this method is called.
     * It posts an AllLobbyMembersResponse containing a list of the requested lobby's
     * current members onto the EventBus.
     *
     * @param message The RetrieveAllLobbyMembersRequest found on the EventBus
     *
     * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
     * @since 2020-12-20
     */
    @Subscribe
    private void onRetrieveAllLobbyMembersRequest(RetrieveAllLobbyMembersRequest message) {
        if (LOG.isDebugEnabled())
            LOG.debug("Received RetrieveAllLobbyMembersRequest for Lobby " + message.getLobbyName());
        String lobbyName = message.getLobbyName();
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyName);
        if (lobby.isPresent()) {
            Set<User> lobbyMembers = lobby.get().getUsers();
            Message response = new AllLobbyMembersResponse(lobby.get().getName(), lobbyMembers, lobby.get().getOwner(),
                                                           lobby.get().getReadyUsers());
            response.initWithMessage(message);
            post(response);
        } else {
            LOG.error("---- Lobby " + lobbyName + " not found.");
        }
    }

    /**
     * Handles a StartSessionRequest found on the EventBus
     * <p>
     * If a StartSessionRequest is detected on the EventBus, this method is called.
     * It posts a StartSessionMessage including the lobby name and the user onto the EventBus if there are
     * at least 3 player in the lobby and every player is ready.
     *
     * @param message The StartSessionMessage found on the EventBus
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @see de.uol.swp.common.lobby.message.StartSessionMessage
     * @since 2021-01-21
     */
    @Subscribe
    private void onStartSessionRequest(StartSessionRequest message) {
        if (LOG.isDebugEnabled()) LOG.debug("Received StartSessionRequest for Lobby " + message.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(message.getName());
        if (lobby.isPresent()) {
            if (lobby.get().getUsers().size() >= 3 && (lobby.get().getReadyUsers().equals(lobby.get().getUsers()))) {
                LOG.debug("---- All Members are ready, proceeding with sending of StartSessionMessage...");
                ServerMessage startSessionMessage = new StartSessionMessage(lobby.get().getName(), message.getUser());
                post(new CreateGameMessage(lobby.get(), message.getUser()));
                sendToAllInLobby(lobby.get().getName(), startSessionMessage);
            }
        }
    }

    /**
     * Handles a UserReadyRequest found on the EventBus
     * <p>
     * If a UserReadyRequest is detected on the EventBus, this method is called.
     * It posts a UserReadyMessage containing a set of all the lobby's members
     * that are marked as ready onto the EventBus.
     *
     * @param message The UserReadyRequest found on the EventBus
     *
     * @author Maxmilian Lindner
     * @author Eric Vuong
     * @see de.uol.swp.common.lobby.message.UserReadyMessage
     * @since 2021-01-19
     */
    @Subscribe
    private void onUserReadyRequest(UserReadyRequest message) {
        if (LOG.isDebugEnabled()) LOG.debug(
                "Received UserReadyRequest for User " + message.getUser().getUsername() + " in Lobby " + message
                        .getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(message.getName());
        if (lobby.isPresent()) {
            if (message.isReady()) {
                lobby.get().setUserReady(message.getUser());
            } else {
                lobby.get().unsetUserReady(message.getUser());
            }
            ServerMessage msg = new UserReadyMessage(message.getName(), message.getUser());
            sendToAllInLobby(message.getName(), msg);
        }
    }

    /**
     * Prepares a given ServerMessage to be sent to all players in the lobby and
     * posts it onto the EventBus
     *
     * @param lobbyName Name of the lobby the players are in
     * @param message   The message to be sent to the users
     *
     * @see de.uol.swp.common.message.ServerMessage
     * @since 2019-10-08
     */
    public void sendToAllInLobby(String lobbyName, ServerMessage message) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyName);

        if (lobby.isPresent()) {
            message.setReceiver(authenticationService.getSessions(lobby.get().getUsers()));
            post(message);
        }
        // TODO: error handling for a not existing lobby
    }
}

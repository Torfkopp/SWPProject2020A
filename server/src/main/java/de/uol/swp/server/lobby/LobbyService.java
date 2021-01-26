package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import de.uol.swp.common.game.message.CreateGameMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.AllLobbiesResponse;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.lobby.response.CreateLobbyResponse;
import de.uol.swp.common.lobby.response.JoinLobbyResponse;
import de.uol.swp.common.message.ExceptionMessage;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
     * @since 2019-10-08
     */
    @Inject
    public LobbyService(LobbyManagement lobbyManagement, AuthenticationService authenticationService, EventBus eventBus) {
        super(eventBus);
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
     * @param createLobbyRequest The CreateLobbyRequest found on the EventBus
     * @see de.uol.swp.server.lobby.LobbyManagement#createLobby(String, User)
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @since 2019-10-08
     */
    @Subscribe
    private void onCreateLobbyRequest(CreateLobbyRequest createLobbyRequest) {
        try {
            lobbyManagement.createLobby(createLobbyRequest.getName(), createLobbyRequest.getOwner());
            Message responseMessage = new CreateLobbyResponse(createLobbyRequest.getName());
            if (createLobbyRequest.getMessageContext().isPresent()) {
                responseMessage.setMessageContext(createLobbyRequest.getMessageContext().get());
            }
            post(responseMessage);
            sendToAll(new LobbyCreatedMessage(createLobbyRequest.getName(), createLobbyRequest.getOwner()));
        } catch (IllegalArgumentException e) {
            Message exceptionMessage = new LobbyExceptionMessage(e.getMessage());
            if (createLobbyRequest.getMessageContext().isPresent()) {
                exceptionMessage.setMessageContext(createLobbyRequest.getMessageContext().get());
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
     * @param lobbyJoinUserRequest The LobbyJoinUserRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-12-19
     */
    @Subscribe
    private void onLobbyJoinUserRequest(LobbyJoinUserRequest lobbyJoinUserRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyJoinUserRequest.getName());

        if (lobby.isPresent()) {
            if (lobby.get().getUsers().size() < 4) {
                if (!lobby.get().getUsers().contains(lobbyJoinUserRequest.getUser())) {
                    lobby.get().joinUser(lobbyJoinUserRequest.getUser());
                    Message responseMessage = new JoinLobbyResponse(lobbyJoinUserRequest.getName());
                    if (lobbyJoinUserRequest.getMessageContext().isPresent()) {
                        responseMessage.setMessageContext(lobbyJoinUserRequest.getMessageContext().get());
                    }
                    post(responseMessage);
                    sendToAllInLobby(lobbyJoinUserRequest.getName(), new UserJoinedLobbyMessage(lobbyJoinUserRequest.getName(), lobbyJoinUserRequest.getUser()));
                } else {
                    ExceptionMessage exceptionMessage = new LobbyExceptionMessage("You're already in this lobby!");
                    if (lobbyJoinUserRequest.getMessageContext().isPresent()) {
                        exceptionMessage.setMessageContext(lobbyJoinUserRequest.getMessageContext().get());
                    }
                    post(exceptionMessage);
                    LOG.debug(exceptionMessage.getException());
                }
            } else {
                ExceptionMessage exceptionMessage = new LobbyExceptionMessage("This lobby is full!");
                if (lobbyJoinUserRequest.getMessageContext().isPresent()) {
                    exceptionMessage.setMessageContext(lobbyJoinUserRequest.getMessageContext().get());
                }
                post(exceptionMessage);
                LOG.debug(exceptionMessage.getException());
            }
        } else {
            ExceptionMessage exceptionMessage = new LobbyExceptionMessage("This lobby does not exist!");
            if (lobbyJoinUserRequest.getMessageContext().isPresent()) {
                exceptionMessage.setMessageContext(lobbyJoinUserRequest.getMessageContext().get());
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
     * @param lobbyLeaveUserRequest The LobbyJoinUserRequest found on the EventBus
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2019-10-08
     */
    @Subscribe
    private void onLobbyLeaveUserRequest(LobbyLeaveUserRequest lobbyLeaveUserRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyLeaveUserRequest.getName());

        if (lobby.isPresent()) {
            lobby.get().unsetUserReady(lobbyLeaveUserRequest.getUser());
            try {
                lobby.get().leaveUser(lobbyLeaveUserRequest.getUser());
                sendToAllInLobby(lobbyLeaveUserRequest.getName(), new UserLeftLobbyMessage(lobbyLeaveUserRequest.getName(), lobbyLeaveUserRequest.getUser()));
            } catch (IllegalArgumentException exception) {
                lobbyManagement.dropLobby(lobby.get().getName());
                sendToAll(new LobbyDeletedMessage(lobbyLeaveUserRequest.getName()));
            }
        }
    }

    /**
     * Prepares a given ServerMessage to be sent to all players in the lobby and
     * posts it onto the EventBus
     *
     * @param lobbyName Name of the lobby the players are in
     * @param message   The message to be sent to the users
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

    /**
     * Handles a RetrieveAllLobbiesRequest found on the EventBus
     * <p>
     * If a RetrieveAllLobbiesRequest is detected on the EventBus, this method is called.
     * It posts an AllLobbiesResponse containing a list of all lobby names
     *
     * @param retrieveAllLobbiesRequest The RetrieveAllLobbiesRequest found on the EventBus
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
     * @since 2020-12-12
     */
    @Subscribe
    private void onRetrieveAllLobbiesRequest(RetrieveAllLobbiesRequest retrieveAllLobbiesRequest) {
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
     * @param retrieveAllLobbyMembersRequest The RetrieveAllLobbyMembersRequest found on the EventBus
     * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
     * @since 2020-12-20
     */
    @Subscribe
    private void onRetrieveAllLobbyMembersRequest(RetrieveAllLobbyMembersRequest retrieveAllLobbyMembersRequest) {
        String lobbyName = retrieveAllLobbyMembersRequest.getLobbyName();
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyName);
        if (lobby.isPresent()) {
            Set<User> lobbyMembers = lobby.get().getUsers();
            Message response = new AllLobbyMembersResponse(lobbyMembers, lobby.get().getOwner(), lobby.get().getReadyUsers());
            response.initWithMessage(retrieveAllLobbyMembersRequest);
            post(response);
        } else {
            LOG.error("Lobby " + lobbyName + " not found.");
        }
    }

    /**
     * Handles a StartSessionRequest found on the EventBus
     * <p>
     * If a StartSessionRequest is detected on the EventBus, this method is called.
     * It posts a StartSessionMessage including the lobby name and the user onto the EventBus if there are
     * at least 3 player in the lobby and every player is ready.
     *
     * @param startSessionRequest The StartSessionMessage found on the EventBus
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @see de.uol.swp.common.lobby.message.StartSessionMessage
     * @since 2021-01-21
     */
    @Subscribe
    private void onStartSessionRequest(StartSessionRequest startSessionRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(startSessionRequest.getName());
        if (lobby.isPresent()) {
            //TODO: Change back to 3
            if (lobby.get().getUsers().size() >= 1 && (lobby.get().getReadyUsers().equals(lobby.get().getUsers()))) {
                LOG.debug("All Members are ready, proceeding with sending of StartSessionMessage...");
                ServerMessage startSessionMessage = new StartSessionMessage(lobby.get().getName(), startSessionRequest.getUser());
                post(new CreateGameMessage(lobby.get(), startSessionRequest.getUser()));
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
     * @param userReadyRequest The UserReadyRequest found on the EventBus
     * @author Maxmilian Lindner
     * @author Eric Vuong
     * @see de.uol.swp.common.lobby.message.UserReadyMessage
     * @since 2021-01-19
     */
    @Subscribe
    private void onUserReadyRequest(UserReadyRequest userReadyRequest) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(userReadyRequest.getName());
        if (lobby.isPresent()) {
            if (userReadyRequest.isReady()) {
                lobby.get().setUserReady(userReadyRequest.getUser());
            } else {
                lobby.get().unsetUserReady(userReadyRequest.getUser());
            }
            ServerMessage msg = new UserReadyMessage(userReadyRequest.getName(), userReadyRequest.getUser());
            sendToAllInLobby(userReadyRequest.getName(), msg);
        }
    }
}

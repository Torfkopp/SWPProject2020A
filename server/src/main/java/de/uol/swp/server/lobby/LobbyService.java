package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.game.message.CreateGameMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.*;
import de.uol.swp.common.message.*;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.User;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.event.GetUserSessionEvent;
import de.uol.swp.server.game.event.KickUserEvent;
import de.uol.swp.server.message.FetchUserContextInternalRequest;
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
public class LobbyService extends AbstractService {

    private static final Logger LOG = LogManager.getLogger(LobbyService.class);
    private final ILobbyManagement lobbyManagement;
    private final AuthenticationService authenticationService;

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
    public LobbyService(ILobbyManagement lobbyManagement, AuthenticationService authenticationService,
                        EventBus eventBus) {
        super(eventBus);
        if (LOG.isDebugEnabled()) LOG.debug("LobbyService started");
        this.lobbyManagement = lobbyManagement;
        this.authenticationService = authenticationService;
    }

    /**
     * Prepares a given ServerMessage to be sent to all players in the lobby and
     * posts it onto the EventBus
     *
     * @param lobbyName Name of the lobby the players are in
     * @param msg       The message to be sent to the users
     *
     * @see de.uol.swp.common.message.ServerMessage
     * @since 2019-10-08
     */
    public void sendToAllInLobby(String lobbyName, ServerMessage msg) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyName);

        if (lobby.isPresent()) {
            msg.setReceiver(authenticationService.getSessions(lobby.get().getUsers()));
            post(msg);
        }
        // TODO: error handling for a not existing lobby
    }

    /**
     * Handles a ChangeLobbySettingsRequest found on the EventBus
     * <p>
     * If a ChangeLobbySettingsRequest is detected on the EventBus this method
     * updates the pre-game settings of a specific lobby if the requesting user
     * is the owner and the lobby has not started a game.
     * A AllowedAmountOfPlayersChangedMessage is posted onto the EventBus to update
     * all clients and a UpdateLobbyMessage is posted onto the EventBus to update
     * the lobby members about the changes.
     *
     * @param req The ChangeLobbySettingsRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @see de.uol.swp.common.lobby.request.ChangeLobbySettingsRequest
     * @see de.uol.swp.common.lobby.message.AllowedAmountOfPlayersChangedMessage
     * @see de.uol.swp.common.lobby.message.UpdateLobbyMessage
     * @since 2021-03-15
     */
    @Subscribe
    private void onChangeLobbySettingsRequest(ChangeLobbySettingsRequest req) {
        LOG.debug("Received a ChangeLobbySettingsRequest");
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
        if (lobby.isEmpty() || !lobby.get().getOwner().equals(req.getUser())) return;
        if (lobby.get().getUsers().size() > req.getAllowedPlayers()) return;
        if (lobby.get().isInGame()) return;
        lobbyManagement
                .updateLobbySettings(req.getName(), req.getAllowedPlayers(), req.isCommandsAllowed(), req.getMoveTime(),
                                     req.isStartUpPhaseEnabled(), req.isRandomPlayfieldEnabled());
        post(new AllowedAmountOfPlayersChangedMessage(req.getName(), req.getUser()));
        Optional<Lobby> updatedLobby = lobbyManagement.getLobby(req.getName());
        if (updatedLobby.isEmpty()) return;
        ServerMessage msg = new UpdateLobbyMessage(req.getName(), req.getUser(), updatedLobby.get());
        sendToAllInLobby(req.getName(), msg);
    }

    /**
     * Handles a CreateLobbyRequest found on the EventBus
     * <p>
     * If a CreateLobbyRequest is detected on the EventBus, this method is called.
     * It creates a new Lobby via the LobbyManagement using the parameters from the
     * request and sends a LobbyCreatedMessage to every connected user.
     *
     * @param req The CreateLobbyRequest found on the EventBus
     *
     * @see de.uol.swp.server.lobby.LobbyManagement#createLobby(String, User, int)
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @since 2019-10-08
     */
    @Subscribe
    private void onCreateLobbyRequest(CreateLobbyRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received CreateLobbyRequest for Lobby " + req.getName());
        try {
            lobbyManagement.createLobby(req.getName(), req.getOwner(), req.getMaxPlayer());
            Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
            if (lobby.isEmpty()) return;
            Message responseMessage = new CreateLobbyResponse(req.getName(), lobby.get());
            responseMessage.initWithMessage(req);
            post(responseMessage);
            sendToAll(new LobbyCreatedMessage(req.getName(), req.getOwner()));
        } catch (IllegalArgumentException e) {
            Message exceptionMessage = new LobbyExceptionMessage(e.getMessage());
            exceptionMessage.initWithMessage(req);
            post(exceptionMessage);
            LOG.debug(e.getMessage());
        }
    }

    /**
     * Handles a GetUserSessionEvent found on the EventBus
     * <p>
     * If a GetUserSessionEvent is found on the EventBus this
     * method gets the Session of the User contained in the GetUserSessionEvent.
     * Then it posts a FetchUserContextInternalRequest with the session of the
     * User and .the ResponseMessage contained in the GetUserSessionEvent,
     * which will be handled by the ServerHandler.
     *
     * @param event GetUserSessionEvent found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.server.game.event.GetUserSessionEvent
     * @see de.uol.swp.server.message.FetchUserContextInternalRequest
     * @since 2021-02-25
     */
    @Subscribe
    private void onGetUserSessionEvent(GetUserSessionEvent event) {
        Optional<Session> session = authenticationService.getSession(event.getTargetUser());
        if (session.isEmpty()) throw new RuntimeException("UserSession not found");
        post(new FetchUserContextInternalRequest(session.get(), event.getResponseMessage()));
    }

    /**
     * Handles a KickUserEvent found on the EventBus
     * <p>
     * If a KickUserEvent is detected on the EventBus a
     * KickUserResponse for the to be kicked user is posted
     * onto the EventBus to close his lobby window, a UserLeftLobbyMessage
     * is posted onto the EventBus to the according lobby and a
     * AllLobbiesMessage is posted onto the EventBus.
     *
     * @param event KickUserEvent found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Sven Ahrens
     * @see de.uol.swp.server.game.event.KickUserEvent
     * @see de.uol.swp.common.lobby.request.KickUserRequest
     * @see de.uol.swp.common.lobby.response.KickUserResponse
     * @see de.uol.swp.server.game.event.GetUserSessionEvent
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @see de.uol.swp.common.lobby.message.AllLobbiesMessage
     * @since 2021-03-02
     */
    @Subscribe
    private void onKickUserEvent(KickUserEvent event) {
        KickUserRequest req = event.getRequest();
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
        if (req.getToBeKickedUserName().equals(req.getUser().getUsername())) return;
        if (lobby.isEmpty() || !lobby.get().getOwner().equals(req.getUser())) return;
        Set<User> lobbyMembers = lobby.get().getUsers();
        User toBeKickedUser = null;
        for (User temp : lobbyMembers) {
            if (temp.getUsername().equals(req.getToBeKickedUserName())) toBeKickedUser = temp;
        }
        if (toBeKickedUser == null) return;
        lobby.get().unsetUserReady(toBeKickedUser);
        lobby.get().leaveUser(toBeKickedUser);
        ResponseMessage kickResponse = new KickUserResponse(req.getName(), toBeKickedUser);
        post(new GetUserSessionEvent(toBeKickedUser, kickResponse));
        sendToAllInLobby(req.getName(), new UserLeftLobbyMessage(req.getName(), toBeKickedUser));
        post(new AllLobbiesMessage(lobbyManagement.getLobbies()));
    }

    /**
     * Handles a LobbyJoinUserRequest found on the EventBus
     * <p>
     * If a LobbyJoinUserRequest is detected on the EventBus, this method is called.
     * It adds a user to a lobby stored in the LobbyManagement and
     * sends a UserJoinedLobbyMessage to every user in the lobby.
     *
     * @param req The LobbyJoinUserRequest found on the EventBus
     *
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-12-19
     */
    @Subscribe
    private void onLobbyJoinUserRequest(LobbyJoinUserRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received LobbyJoinUserRequest for Lobby " + req.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());

        if (lobby.isPresent()) {
            if (lobby.get().getUsers().size() < lobby.get().getMaxPlayers()) {
                if (!lobby.get().getUsers().contains(req.getUser())) {
                    if (!lobby.get().isInGame()) {
                        lobby.get().joinUser(req.getUser());
                        lobby = lobbyManagement.getLobby(req.getName());
                        if (lobby.isEmpty()) return;
                        Message responseMessage = new JoinLobbyResponse(req.getName(), lobby.get());
                        responseMessage.initWithMessage(req);
                        post(responseMessage);
                        sendToAllInLobby(req.getName(), new UserJoinedLobbyMessage(req.getName(), req.getUser()));
                        post(new AllLobbiesMessage(lobbyManagement.getLobbies()));
                    } else {
                        ExceptionMessage exceptionMessage = new LobbyExceptionMessage("Game session started already!");
                        exceptionMessage.initWithMessage(req);
                        post(exceptionMessage);
                        LOG.debug(exceptionMessage.getException());
                    }
                } else {
                    ExceptionMessage exceptionMessage = new LobbyExceptionMessage("You're already in this lobby!");
                    exceptionMessage.initWithMessage(req);
                    post(exceptionMessage);
                    LOG.debug(exceptionMessage.getException());
                }
            } else {
                ExceptionMessage exceptionMessage = new LobbyExceptionMessage("This lobby is full!");
                exceptionMessage.initWithMessage(req);
                post(exceptionMessage);
                LOG.debug(exceptionMessage.getException());
            }
        } else {
            ExceptionMessage exceptionMessage = new LobbyExceptionMessage("This lobby does not exist!");
            exceptionMessage.initWithMessage(req);
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
     * @param req The LobbyJoinUserRequest found on the EventBus
     *
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2019-10-08
     */
    @Subscribe
    private void onLobbyLeaveUserRequest(LobbyLeaveUserRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received LobbyLeaveUserRequest for Lobby " + req.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());

        if (lobby.isPresent()) {
            lobby.get().unsetUserReady(req.getUser());
            try {
                lobby.get().leaveUser(req.getUser());
                sendToAllInLobby(req.getName(), new UserLeftLobbyMessage(req.getName(), req.getUser()));
                post(new AllLobbiesMessage(lobbyManagement.getLobbies()));
            } catch (IllegalArgumentException exception) {
                lobbyManagement.dropLobby(lobby.get().getName());
                sendToAll(new LobbyDeletedMessage(req.getName()));
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
     * @param req The RemoveFromLobbiesRequest found on the EventBus
     *
     * @author Finn Haase
     * @author Aldin Dervisi
     * @see de.uol.swp.common.lobby.response.RemoveFromLobbiesResponse
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2021-01-28
     */
    @Subscribe
    private void onRemoveFromLobbiesRequest(RemoveFromLobbiesRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received RemoveFromLobbiesRequest");
        User user = req.getUser();
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
        post(new AllLobbiesMessage(lobbyManagement.getLobbies()));
    }

    /**
     * Handles a RetrieveAllLobbiesRequest found on the EventBus
     * <p>
     * If a RetrieveAllLobbiesRequest is detected on the EventBus, this method is called.
     * It posts an AllLobbiesResponse containing a list of all lobby names
     *
     * @param req The RetrieveAllLobbiesRequest found on the EventBus
     *
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
     * @since 2020-12-12
     */
    @Subscribe
    private void onRetrieveAllLobbiesRequest(RetrieveAllLobbiesRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received RetrieveAllLobbiesRequest");
        Message response = new AllLobbiesResponse(lobbyManagement.getLobbies());
        response.initWithMessage(req);
        post(response);
    }

    /**
     * Handles a RetrieveAllLobbyMembersRequest found on the EventBus
     * <p>
     * If a RetrieveAllLobbyMembersRequest is detected on the EventBus, this method is called.
     * It posts an AllLobbyMembersResponse containing a list of the requested lobby's
     * current members onto the EventBus.
     *
     * @param req The RetrieveAllLobbyMembersRequest found on the EventBus
     *
     * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
     * @since 2020-12-20
     */
    @Subscribe
    private void onRetrieveAllLobbyMembersRequest(RetrieveAllLobbyMembersRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received RetrieveAllLobbyMembersRequest for Lobby " + req.getLobbyName());
        String lobbyName = req.getLobbyName();
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyName);
        if (lobby.isPresent()) {
            Set<User> lobbyMembers = lobby.get().getUsers();
            Message response = new AllLobbyMembersResponse(lobby.get().getName(), lobbyMembers, lobby.get().getOwner(),
                                                           lobby.get().getReadyUsers());
            response.initWithMessage(req);
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
     * @param req The StartSessionMessage found on the EventBus
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @see de.uol.swp.common.lobby.message.StartSessionMessage
     * @since 2021-01-21
     */
    @Subscribe
    private void onStartSessionRequest(StartSessionRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug("Received StartSessionRequest for Lobby " + req.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
        if (lobby.isPresent()) {
            if (lobby.get().getUsers().size() >= 1 && (lobby.get().getReadyUsers().equals(lobby.get().getUsers()))) {
                LOG.debug("---- All Members are ready, proceeding with sending of StartSessionMessage...");
                ServerMessage startSessionMessage = new StartSessionMessage(lobby.get().getName(), req.getUser());
                post(new CreateGameMessage(lobby.get(), req.getUser()));
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
     * @param req The UserReadyRequest found on the EventBus
     *
     * @author Maxmilian Lindner
     * @author Eric Vuong
     * @see de.uol.swp.common.lobby.message.UserReadyMessage
     * @since 2021-01-19
     */
    @Subscribe
    private void onUserReadyRequest(UserReadyRequest req) {
        if (LOG.isDebugEnabled()) LOG.debug(
                "Received UserReadyRequest for User " + req.getUser().getUsername() + " in Lobby " + req.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
        if (lobby.isPresent()) {
            if (req.isReady()) {
                lobby.get().setUserReady(req.getUser());
            } else {
                lobby.get().unsetUserReady(req.getUser());
            }
            ServerMessage msg = new UserReadyMessage(req.getName(), req.getUser());
            sendToAllInLobby(req.getName(), msg);
        }
    }
}

package de.uol.swp.server.lobby;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.exception.ExceptionMessage;
import de.uol.swp.common.exception.LobbyExceptionMessage;
import de.uol.swp.common.game.message.ReturnToPreGameLobbyMessage;
import de.uol.swp.common.game.request.CheckForGameRequest;
import de.uol.swp.common.game.request.ReturnToPreGameLobbyRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.*;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.user.request.CheckUserInLobbyRequest;
import de.uol.swp.common.user.request.GetOldSessionsRequest;
import de.uol.swp.common.user.response.CheckUserInLobbyResponse;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.event.*;
import de.uol.swp.server.message.ServerInternalMessage;
import de.uol.swp.server.sessionmanagement.ISessionManagement;
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
    private final ISessionManagement sessionManagement;

    /**
     * Constructor
     *
     * @param lobbyManagement   The management class for creating, storing, and deleting lobbies
     * @param sessionManagement The session management
     * @param eventBus          The server-wide EventBus
     *
     * @since 2019-10-08
     */
    @Inject
    public LobbyService(ILobbyManagement lobbyManagement, ISessionManagement sessionManagement, EventBus eventBus) {
        super(eventBus);
        this.lobbyManagement = lobbyManagement;
        this.sessionManagement = sessionManagement;
        LOG.debug("LobbyService started");
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
            msg.setReceiver(sessionManagement.getSessions(lobby.get().getRealUsers()));
            post(msg);
        }
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
        LOG.debug("Received ChangeLobbySettingsRequest");
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
        if (lobby.isEmpty() || !lobby.get().getOwner().equals(req.getUser())) return;
        if (lobby.get().getUserOrDummies().size() > req.getAllowedPlayers()) return;
        if (lobby.get().isInGame()) return;
        lobbyManagement
                .updateLobbySettings(req.getName(), req.getAllowedPlayers(), req.isCommandsAllowed(), req.getMoveTime(),
                                     req.isStartUpPhaseEnabled(), req.isRandomPlayFieldEnabled());
        post(new AllowedAmountOfPlayersChangedMessage(req.getName(), req.getUser()));
        Optional<Lobby> updatedLobby = lobbyManagement.getLobby(req.getName());
        if (updatedLobby.isEmpty()) return;
        ServerMessage msg = new UpdateLobbyMessage(req.getName(), req.getUser(), updatedLobby.get());
        sendToAllInLobby(req.getName(), msg);
    }

    /**
     * Handles a ChangeOwnerRequest found on the EventBus
     * <p>
     * If a ChangeOwnerRequest is detected on the EventBus, this
     * method checks if the new owner is an user or not.
     * Accordingly a LobbyExceptionMessage is posted onto the EventBus
     * if the new owner should be dummy, otherwise the owner gets changed
     * and a UpdateLobbyMessage is posted onto the EventBus
     *
     * @param req ChangeOwnerRequest found on the EventBus
     *
     * @author Maximilian Lindner
     * @since 2021-04-15
     */
    @Subscribe
    private void onChangeOwnerRequest(ChangeOwnerRequest req) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
        if (lobby.isEmpty() || !lobby.get().getOwner().equals(req.getUser())) return;
        if (!(req.getNewOwner() instanceof User)) {
            ExceptionMessage exceptionMessage = new LobbyExceptionMessage("Just User can be Owner");
            exceptionMessage.initWithMessage(req);
            LOG.debug("Sending ExceptionMessage");
            post(exceptionMessage);
        } else {
            User newOwner = (User) req.getNewOwner();
            lobby.get().updateOwner(newOwner);
            Optional<Lobby> updatedLobby = lobbyManagement.getLobby(req.getName());
            if (updatedLobby.isEmpty()) return;
            ServerMessage msg = new UpdateLobbyMessage(req.getName(), req.getUser(), updatedLobby.get());
            sendToAllInLobby(req.getName(), msg);
        }
    }

    /**
     * Handles a CheckForGameRequest found on the EventBus
     * <p>
     * If the lobby contained in the request is ingame, an ActivePlayerEvent
     * is posted onto the EventBus.
     *
     * @param req The CheckForGameRequest on the EventBus
     *
     * @author Marvin Drees
     * @author Maximilian Lindner
     * @since 2021-04-09
     */
    @Subscribe
    private void onCheckForGameRequest(CheckForGameRequest req) {
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getOriginLobby());
        if (lobby.isPresent() && lobby.get().isInGame())
            post(new TransferLobbyStateEvent(lobby.get(), req.getUser(), req.getMessageContext()));
    }

    /**
     * Handles a CheckUserInLobbyRequest found on the EventBus
     * If a CheckUserInLobbyRequest is detected on the EventBus, this method is
     * called. It checks if the logged in user is currently in a lobby.
     *
     * @param req The CheckUserInLobbyRequest on the EventBus
     *
     * @author Alwin Bossert
     * @author Finn Haase
     * @see de.uol.swp.common.user.request.CheckUserInLobbyRequest
     * @since 2021-04-09
     */
    @Subscribe
    private void onCheckUserInLobbyRequest(CheckUserInLobbyRequest req) {
        LOG.debug("Received CheckUserInLobbyRequest");
        boolean isInLobby = false;
        User user = req.getUser();
        Map<String, Lobby> lobbies = lobbyManagement.getLobbies();
        for (Map.Entry<String, Lobby> entry : lobbies.entrySet()) {
            isInLobby = entry.getValue().getUserOrDummies().contains(user);
        }
        Message responseMessage = new CheckUserInLobbyResponse(user, isInLobby);
        responseMessage.initWithMessage(req);
        post(responseMessage);
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
     * @see de.uol.swp.server.lobby.ILobbyManagement#createLobby(String, de.uol.swp.common.user.User, int, String)
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @since 2019-10-08
     */
    @Subscribe
    private void onCreateLobbyRequest(CreateLobbyRequest req) {
        LOG.debug("Received CreateLobbyRequest for Lobby {}", req.getName());
        try {
            lobbyManagement.createLobby(req.getName(), req.getOwner(), req.getMaxPlayers(), req.getPassword());
            Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
            if (lobby.isEmpty()) return;
            Message responseMessage;
            if (Strings.isNullOrEmpty(req.getPassword())) {
                responseMessage = new CreateLobbyResponse(req.getName(), lobby.get());
            } else {
                lobby.get().setHasPassword(true);
                responseMessage = new CreateLobbyWithPasswordResponse(req.getName(), lobby.get(), req.getPassword());
            }
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
     * Handles a GetOldSessionRequest found on the EventBus
     * <p>
     * When a GetOldSessionRequest is found on the EventBus, this method
     * is called. It checks the LobbyManagement for all the lobbies
     * the requesting user is in and posts JoinLobbyResponses for each.
     * This method is used to reopen lobby windows on a new client.
     *
     * @param req The GetOldSessionRequest on the EventBus
     *
     * @author Marvin Drees
     * @author Maximilian Lindner
     * @since 2021-04-09
     */
    @Subscribe
    private void onGetOldSessionsRequest(GetOldSessionsRequest req) {
        LOG.debug("Received GetOldSessionsRequest");
        User user = req.getUser();
        Map<String, Lobby> lobbies = lobbyManagement.getLobbies();
        for (Map.Entry<String, Lobby> entry : lobbies.entrySet()) {
            if (entry.getValue().getUserOrDummies().contains(user)) {
                ResponseMessage responseMessage = new JoinLobbyResponse(entry.getKey(), entry.getValue());
                responseMessage.initWithMessage(req);
                post(responseMessage);
            }
        }
    }

    /**
     * Handles a JoinLobbyWithPasswordConfirmationRequest found on the EventBus
     * <p>
     * When a JoinLobbyWihPasswordConfirmationRequest is found on the EventBus, this method
     * is called. If the password is correct, it adds a user
     * to a lobby stored in the LobbyManagement and
     * sends a UserJoinedLobbyMessage to every user in the lobby.
     *
     * @param req The JoinLobbyWithPasswordConfirmationRequest found on the EventBus
     *
     * @author Alwin Bossert
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2021-04-21
     */
    @Subscribe
    private void onJoinLobbyWithPasswordConfirmationRequest(JoinLobbyWithPasswordConfirmationRequest req) {
        LOG.debug("Received JoinLobbyWithPasswordConfirmationRequest for Lobby {}", req.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName(), req.getPassword());
        if (lobby.isPresent()) {
            if (req.getPassword().equals(lobby.get().getPassword())) {
                lobby.get().joinUser(req.getUser());
                Message responseMessage = new JoinLobbyResponse(req.getName(), lobby.get());
                responseMessage.initWithMessage(req);
                post(responseMessage);
                sendToAllInLobby(req.getName(), new UserJoinedLobbyMessage(req.getName(), req.getUser()));
                post(new AllLobbiesMessage(lobbyManagement.getLobbies()));
            } else {
                ExceptionMessage exceptionMessage = new LobbyExceptionMessage("Wrong Password!");
                exceptionMessage.initWithMessage(req);
                post(exceptionMessage);
                LOG.debug(exceptionMessage.getException());
            }
        } else {
            ExceptionMessage exceptionMessage = new LobbyExceptionMessage("Lobby not found!");
            exceptionMessage.initWithMessage(req);
            post(exceptionMessage);
            LOG.debug(exceptionMessage.getException());
        }
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
     * @see de.uol.swp.server.game.event.ForwardToUserInternalRequest
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @see de.uol.swp.common.lobby.message.AllLobbiesMessage
     * @since 2021-03-02
     */
    @Subscribe
    private void onKickUserEvent(KickUserEvent event) {
        KickUserRequest req = event.getRequest();
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
        if (req.getToBeKickedUser().equals(req.getUser())) return;
        if (lobby.isEmpty() || !lobby.get().getOwner().equals(req.getUser())) return;
        UserOrDummy toBeKickedUser = req.getToBeKickedUser();
        lobby.get().leaveUser(toBeKickedUser);
        ResponseMessage kickResponse = new KickUserResponse(req.getName(), toBeKickedUser);
        post(new ForwardToUserInternalRequest(toBeKickedUser, kickResponse));
        sendToAllInLobby(req.getName(), new UserLeftLobbyMessage(req.getName(), toBeKickedUser));
        post(new AllLobbiesMessage(lobbyManagement.getLobbies()));
    }

    /**
     * Handles a LobbyJoinRandomUserRequest found on the EventBus
     * <p>
     * If a LobbyJoinRandomUserRequest is detected on the EventBus, this method is called.
     * It adds a user to a random lobby stored in the LobbyManagement and
     * sends a UserJoinedLobbyMessage to every user in the lobby.
     *
     * @param req The LobbyJoinRandomUserRequest found on the EventBus
     *
     * @author Finn Haase
     * @author Sven Ahrens
     * @see de.uol.swp.common.lobby.Lobby
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2021-04-08
     */
    @Subscribe
    private void onLobbyJoinRandomUserRequest(LobbyJoinRandomUserRequest req) {
        Map<String, Lobby> lobbies = lobbyManagement.getLobbies();
        List<Lobby> filteredLobbies = new ArrayList<>();

        lobbies.forEach((String, lobby) -> {
            if (lobby.getUserOrDummies().size() < lobby.getMaxPlayers() && !lobby.getUserOrDummies()
                                                                                 .contains(req.getUser()) && !lobby
                    .isInGame()) {
                filteredLobbies.add(lobby);
            }
        });
        if (!filteredLobbies.isEmpty()) {
            int i = (int) (Math.random() * filteredLobbies.size());
            Lobby randomLobby = filteredLobbies.get(i);

            randomLobby.joinUser(req.getUser());

            Message responseMessage = new JoinLobbyResponse(randomLobby.getName(), randomLobby);
            responseMessage.initWithMessage(req);
            post(responseMessage);
            sendToAllInLobby(randomLobby.getName(), new UserJoinedLobbyMessage(randomLobby.getName(), req.getUser()));
            post(new AllLobbiesMessage(lobbyManagement.getLobbies()));
        } else {
            Message responseMessage = new JoinRandomLobbyFailedResponse();
            responseMessage.initWithMessage(req);
            post(responseMessage);
        }
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
        LOG.debug("Received LobbyJoinUserRequest for Lobby {}", req.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
        if (lobby.isPresent()) {
            if (lobby.get().getUserOrDummies().size() < lobby.get().getMaxPlayers()) {
                if (!lobby.get().getUserOrDummies().contains(req.getUser())) {
                    if (!lobby.get().isInGame()) {
                        if (lobby.get().hasAPassword() == true) {
                            Message responseMessage = new JoinLobbyWithPasswordResponse(req.getName(), lobby.get());
                            responseMessage.initWithMessage(req);
                            post(responseMessage);
                        } else if (lobby.get().hasAPassword() == false) {
                            lobby.get().joinUser(req.getUser());
                            lobby = lobbyManagement.getLobby(req.getName());
                            if (lobby.isEmpty()) return;

                            Message responseMessage = new JoinLobbyResponse(req.getName(), lobby.get());
                            responseMessage.initWithMessage(req);
                            post(responseMessage);
                            sendToAllInLobby(req.getName(), new UserJoinedLobbyMessage(req.getName(), req.getUser()));
                            post(new AllLobbiesMessage(lobbyManagement.getLobbies()));
                        }
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
        LOG.debug("Received LobbyLeaveUserRequest for Lobby {}", req.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
        if (lobby.isEmpty()) return;
        try {
            lobby.get().leaveUser(req.getUser());
            sendToAllInLobby(req.getName(), new UserLeftLobbyMessage(req.getName(), req.getUser()));
            post(new AllLobbiesMessage(lobbyManagement.getLobbies()));
        } catch (IllegalArgumentException exception) {
            lobbyManagement.dropLobby(lobby.get().getName());
            sendToAll(new LobbyDeletedMessage(req.getName()));
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
        LOG.debug("Received RemoveFromLobbiesRequest");
        User user = req.getUser();
        Map<String, Lobby> lobbies = lobbyManagement.getLobbies();
        Map<String, Lobby> lobbiesWithUser = new HashMap<>();
        for (Map.Entry<String, Lobby> entry : lobbies.entrySet()) {
            if (entry.getValue().getUserOrDummies().contains(user)) {
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
        LOG.debug("Received RetrieveAllLobbiesRequest");
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
        LOG.debug("Received RetrieveAllLobbyMembersRequest for Lobby {}", req.getLobbyName());
        String lobbyName = req.getLobbyName();
        Optional<Lobby> lobby = lobbyManagement.getLobby(lobbyName);
        if (lobby.isPresent()) {
            Set<UserOrDummy> lobbyMembers = lobby.get().getUserOrDummies();
            Message response = new AllLobbyMembersResponse(lobby.get().getName(), lobbyMembers, lobby.get().getOwner(),
                                                           lobby.get().getReadyUsers());
            response.initWithMessage(req);
            post(response);
        } else {
            LOG.error("---- Lobby {} not found.", lobbyName);
        }
    }

    /**
     * Handles a ReturnToPreGameLobbyRequest found on the EventBus
     * <p>
     * If a ReturnToPreGameLobbyRequest is found on the EventBus, this method
     * sets InGame to false in LobbyManagement. It also posts a new UserReadyRequest
     * for every user in the lobby and a ReturnToPreGameLobbyMessage to every user
     * in the lobby onto the EventBus.
     *
     * @param req The ReturnToPreGameLobbyRequest found on the EventBus
     *
     * @author Steven Luong
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.ReturnToPreGameLobbyRequest
     * @see de.uol.swp.common.lobby.request.UserReadyRequest
     * @see de.uol.swp.common.game.message.ReturnToPreGameLobbyMessage
     * @since 2021-03-22
     */
    @Subscribe
    private void onReturnToPreGameLobbyRequest(ReturnToPreGameLobbyRequest req) {
        LOG.debug("Received ReturnToPreGameLobbyRequest for Lobby {}", req.getLobbyName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getLobbyName());
        if (lobby.isEmpty()) return;
        lobbyManagement.setInGame(req.getLobbyName(), false);
        for (User user : lobby.get().getRealUsers()) {
            post(new UserReadyRequest(req.getLobbyName(), user, false));
        }
        sendToAllInLobby(req.getLobbyName(),
                         new ReturnToPreGameLobbyMessage(req.getLobbyName(), lobby.get().getOwner()));
        sendToAll(new AllLobbiesMessage(lobbyManagement.getLobbies()));
    }

    /**
     * Handles a StartSessionRequest found on the EventBus
     * <p>
     * If a StartSessionRequest is detected on the EventBus, this method is called.
     * It posts a CreateGameInternalRequest including the lobby and the user onto
     * the EventBus if there are at least 3 players in the lobby and every player
     * is ready.
     *
     * @param req The StartSessionMessage found on the EventBus
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @see de.uol.swp.server.game.event.CreateGameInternalRequest
     * @since 2021-01-21
     */
    @Subscribe
    private void onStartSessionRequest(StartSessionRequest req) {
        LOG.debug("Received StartSessionRequest for Lobby {}", req.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
        if (lobby.isEmpty()) return;
        if (!req.getUser().equals(lobby.get().getOwner())) return;
        if (lobby.get().getUserOrDummies().size() < 3 || (!lobby.get().getReadyUsers()
                                                                .equals(lobby.get().getUserOrDummies()))) return;
        LOG.debug("---- All Members are ready, proceeding with sending of CreateGameInternalRequest...");
        ServerInternalMessage msg = new CreateGameInternalRequest(lobby.get(), req.getUser());
        post(msg);
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
        LOG.debug("Received UserReadyRequest for User {} in Lobby {}", req.getUser().getUsername(), req.getName());
        Optional<Lobby> lobby = lobbyManagement.getLobby(req.getName());
        if (lobby.isEmpty()) return;
        if (req.isReady()) {
            lobby.get().setUserReady(req.getUser());
        } else {
            lobby.get().unsetUserReady(req.getUser());
        }
        ServerMessage msg = new UserReadyMessage(req.getName(), req.getUser());
        sendToAllInLobby(req.getName(), msg);
    }
}

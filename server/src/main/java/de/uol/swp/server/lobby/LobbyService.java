package de.uol.swp.server.lobby;

import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.exception.ExceptionMessage;
import de.uol.swp.common.exception.LobbyExceptionMessage;
import de.uol.swp.common.game.message.ReturnToPreGameLobbyMessage;
import de.uol.swp.common.game.request.ReturnToPreGameLobbyRequest;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.lobby.response.*;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.message.ServerMessage;
import de.uol.swp.common.specialisedUtil.ActorSet;
import de.uol.swp.common.specialisedUtil.SimpleLobbyMap;
import de.uol.swp.common.user.*;
import de.uol.swp.common.user.request.CheckUserInLobbyRequest;
import de.uol.swp.common.user.response.CheckUserInLobbyResponse;
import de.uol.swp.common.util.Util;
import de.uol.swp.server.AbstractService;
import de.uol.swp.server.game.event.CreateGameInternalRequest;
import de.uol.swp.server.game.event.ForwardToUserInternalRequest;
import de.uol.swp.server.game.event.KickUserEvent;
import de.uol.swp.server.message.ServerInternalMessage;
import de.uol.swp.server.sessionmanagement.ISessionManagement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public void sendToAllInLobby(LobbyName lobbyName, ServerMessage msg) {
        Optional<ILobby> lobby = lobbyManagement.getLobby(lobbyName);
        if (lobby.isPresent()) {
            msg.setReceiver(sessionManagement.getSessions(lobby.get().getRealUsers()));
            post(msg);
        }
    }

    /**
     * Handles a AddAIRequest found on the EventBus
     * <p>
     * If a AddAIRequest is detected on the EventBus, this method is called.
     * It adds the AI to the lobby.
     *
     * @param req The AddAIRequest found on the EventBus
     *
     * @author Mario Fokken
     * @see de.uol.swp.common.lobby.request.AddAIRequest
     * @since 2021-05-21
     */
    @Subscribe
    private void onAddAIRequest(AddAIRequest req) {
        LobbyName lobbyName = req.getName();
        LOG.debug("Received AddAIRequest for Lobby {}", lobbyName);
        LOG.debug("Sending JoinLobbyRequest for Lobby {}", lobbyName);
        post(new JoinLobbyRequest(lobbyName, req.getActor()));
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
        LobbyName lobbyName = req.getName();
        LOG.debug("Received ChangeLobbySettingsRequest for Lobby {}", lobbyName);
        Optional<ILobby> lobbyOptional = lobbyManagement.getLobby(lobbyName);
        if (lobbyOptional.isEmpty() || !lobbyOptional.get().getOwner().equals(req.getActor())) return;
        ILobby lobby = lobbyOptional.get();
        int oldMaxPlayers = lobby.getMaxPlayers();
        if (lobby.getActors().size() > req.getAllowedPlayers() || lobby.isInGame()) return;
        lobbyManagement
                .updateLobbySettings(lobbyName, req.getAllowedPlayers(), req.getMoveTime(), req.isStartUpPhaseEnabled(),
                                     req.isRandomPlayFieldEnabled(), req.getMaxTradeDiff());

        if (lobby.getMaxPlayers() != oldMaxPlayers) {
            LOG.debug("Sending AllowedAmountsOfPlayersChangedMessage for Lobby {}", lobbyName);
            post(new AllowedAmountOfPlayersChangedMessage(lobbyName, req.getActor()));
        }
        ServerMessage msg = new UpdateLobbyMessage(lobbyName, req.getActor(), ILobby.getSimpleLobby(lobby));
        LOG.debug("Sending UpdateLobbyMessage for Lobby {}", lobbyName);
        sendToAllInLobby(lobbyName, msg);
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
        LobbyName lobbyName = req.getName();
        LOG.debug("Received ChangeOwnerRequest for Lobby {}", lobbyName);
        Optional<ILobby> lobby = lobbyManagement.getLobby(lobbyName);
        if (lobby.isEmpty() || !lobby.get().getOwner().equals(req.getActor())) return;
        if (!(req.getNewOwner() instanceof User)) {
            ExceptionMessage exceptionMessage = new LobbyExceptionMessage("Just User can be Owner");
            exceptionMessage.initWithMessage(req);
            LOG.debug("Sending ExceptionMessage [{}]", exceptionMessage.getException());
            post(exceptionMessage);
        } else {
            User newOwner = (User) req.getNewOwner();
            lobby.get().updateOwner(newOwner);
            ServerMessage msg = new UpdateLobbyMessage(lobbyName, req.getActor(), ILobby.getSimpleLobby(lobby.get()));
            LOG.debug("Sending UpdateLobbyMessage for Lobby {}", lobbyName);
            sendToAllInLobby(lobbyName, msg);
        }
    }

    /**
     * Handles a CheckUserInLobbyRequest found on the EventBus
     * <p>
     * If a CheckUserInLobbyRequest is detected on the EventBus, this method is
     * called. It checks if the logged in user is currently in a lobby.
     *
     * @param req The CheckUserInLobbyRequest on the EventBus
     *
     * @author Alwin Bossert
     * @author Mario Fokken
     * @see de.uol.swp.common.user.request.CheckUserInLobbyRequest
     * @since 2021-04-09
     */
    @Subscribe
    private void onCheckUserInLobbyRequest(CheckUserInLobbyRequest req) {
        LOG.debug("Received CheckUserInLobbyRequest");
        User user = req.getUser();
        boolean isInLobby = lobbyManagement.getLobbies().isInALobby(user);
        Message responseMessage = new CheckUserInLobbyResponse(user, isInLobby);
        responseMessage.initWithMessage(req);
        LOG.debug("Sending CheckUserInLobbyResponse");
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
     * @see de.uol.swp.server.lobby.ILobbyManagement#createLobby(de.uol.swp.common.lobby.LobbyName, de.uol.swp.common.user.User, String)
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @since 2019-10-08
     */
    @Subscribe
    private void onCreateLobbyRequest(CreateLobbyRequest req) {
        LOG.debug("Received CreateLobbyRequest for Lobby {}", req.getName());
        try {
            lobbyManagement.createLobby(req.getName(), req.getOwner(), req.getPassword());
            Optional<ILobby> lobby = lobbyManagement.getLobby(req.getName());
            if (lobby.isEmpty()) return;
            Message responseMessage;
            if (!Strings.isNullOrEmpty(req.getPassword())) {
                lobby.get().setHasPassword(true);
            }
            responseMessage = new CreateLobbyResponse(req.getName(), ILobby.getSimpleLobby(lobby.get()),
                                                      req.getPassword());
            responseMessage.initWithMessage(req);
            LOG.debug("Sending CreateLobbyResponse for Lobby {}", req.getName());
            post(responseMessage);
            LOG.debug("Sending LobbyCreatedMessage");
            sendToAll(new LobbyCreatedMessage(req.getName(), req.getOwner()));
        } catch (IllegalArgumentException e) {
            ExceptionMessage exceptionMessage = new LobbyExceptionMessage(e.getMessage());
            exceptionMessage.initWithMessage(req);
            LOG.debug("Sending ExceptionMessage [{}]", exceptionMessage.getException());
            post(exceptionMessage);
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
     * @see ILobby
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-12-19
     */
    @Subscribe
    private void onJoinLobbyRequest(JoinLobbyRequest req) {
        LobbyName lobbyName = req.getName();
        LOG.debug("Received LobbyJoinUserRequest for Lobby {}", lobbyName);
        Actor actor = req.getActor();
        Optional<ILobby> lobby = lobbyManagement.getLobby(lobbyName);
        if (lobby.isPresent()) {
            if (lobby.get().getActors().size() < lobby.get().getMaxPlayers()) {
                if (!lobby.get().getActors().contains(actor)) {
                    if (!lobby.get().isInGame()) {
                        if (lobby.get().hasPassword() && actor instanceof User) {
                            Message responseMessage = new JoinLobbyWithPasswordResponse(lobbyName,
                                                                                        ILobby.getSimpleLobby(
                                                                                                lobby.get()));
                            responseMessage.initWithMessage(req);
                            LOG.debug("Sending JoinLobbyWithPasswordResponse for Lobby {}", lobbyName);
                            post(responseMessage);
                        } else {
                            Actor user = actor;
                            //To ensure that the AI's name is unique for that lobby
                            if (user instanceof AI) {
                                for (Actor u : lobby.get().getActors()) {
                                    if (u instanceof AI) {
                                        AI.Difficulty diff = ((AI) user).getDifficulty();
                                        while (u.getUsername().equals(user.getUsername())) user = new AIDTO(diff);
                                    }
                                }
                            }
                            lobby.get().joinUser(user);
                            lobby = lobbyManagement.getLobby(lobbyName);
                            if (lobby.isEmpty()) return;
                            Message responseMessage = new JoinLobbyResponse(lobbyName,
                                                                            ILobby.getSimpleLobby(lobby.get()));
                            responseMessage.initWithMessage(req);
                            LOG.debug("Sending JoinLobbyResponse for Lobby {}", lobbyName);
                            post(responseMessage);

                            LOG.debug("Sending UserJoinedLobbyMessage for Lobby {}", lobbyName);
                            sendToAllInLobby(lobbyName, new UserJoinedLobbyMessage(lobbyName, user));

                            LOG.debug("Sending AllLobbiesMessage");
                            post(new AllLobbiesMessage(lobbyManagement.getSimpleLobbies()));
                            sendColourChangedMessage(lobby.get(), user);
                        }
                    } else {
                        ExceptionMessage exceptionMessage = new LobbyExceptionMessage("Game session started already!");
                        exceptionMessage.initWithMessage(req);
                        LOG.debug("Sending ExceptionMessage [{}]", exceptionMessage.getException());
                        post(exceptionMessage);
                    }
                } else {
                    ExceptionMessage exceptionMessage = new LobbyExceptionMessage("You're already in this lobby!");
                    exceptionMessage.initWithMessage(req);
                    LOG.debug("Sending ExceptionMessage [{}]", exceptionMessage.getException());
                    post(exceptionMessage);
                }
            } else {
                ExceptionMessage exceptionMessage = new LobbyExceptionMessage("This lobby is full!");
                exceptionMessage.initWithMessage(req);
                LOG.debug("Sending ExceptionMessage [{}]", exceptionMessage.getException());
                post(exceptionMessage);
            }
        } else {
            ExceptionMessage exceptionMessage = new LobbyExceptionMessage("This lobby does not exist!");
            exceptionMessage.initWithMessage(req);
            LOG.debug("Sending ExceptionMessage [{}]", exceptionMessage.getException());
            post(exceptionMessage);
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
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2021-04-21
     */
    @Subscribe
    private void onJoinLobbyWithPasswordConfirmationRequest(JoinLobbyWithPasswordConfirmationRequest req) {
        LobbyName lobbyName = req.getName();
        LOG.debug("Received JoinLobbyWithPasswordConfirmationRequest for Lobby {}", lobbyName);
        Optional<ILobby> lobby = lobbyManagement.getLobby(lobbyName, req.getPassword());
        if (lobby.isPresent()) {
            if (req.getPassword().equals(lobby.get().getPassword())) {
                lobby.get().joinUser(req.getActor());
                Message responseMessage = new JoinLobbyResponse(lobbyName, ILobby.getSimpleLobby(lobby.get()));
                responseMessage.initWithMessage(req);
                LOG.debug("Sending JoinLobbyResponse for Lobby {}", lobbyName);
                post(responseMessage);
                LOG.debug("Sending UserJoinedLobbyMessage for Lobby {}", lobbyName);
                sendToAllInLobby(lobbyName, new UserJoinedLobbyMessage(lobbyName, req.getActor()));

                LOG.debug("Sending AllLobbiesMessage");
                post(new AllLobbiesMessage(lobbyManagement.getSimpleLobbies()));
                sendColourChangedMessage(lobby.get(), req.getActor());
            } else {
                ExceptionMessage exceptionMessage = new LobbyExceptionMessage("Wrong Password!");
                exceptionMessage.initWithMessage(req);
                LOG.debug("Sending ExceptionMessage [{}]", exceptionMessage.getException());
                post(exceptionMessage);
            }
        } else {
            ExceptionMessage exceptionMessage = new LobbyExceptionMessage("Lobby not found!");
            exceptionMessage.initWithMessage(req);
            LOG.debug("Sending ExceptionMessage [{}]", exceptionMessage.getException());
            post(exceptionMessage);
        }
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
     * @see ILobby
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2021-04-08
     */
    @Subscribe
    private void onJoinRandomLobbyRequest(JoinRandomLobbyRequest req) {
        LOG.debug("Received JoinRandomLobbyRequest");
        LobbyMap lobbies = lobbyManagement.getLobbies();
        List<ILobby> filteredLobbies = new ArrayList<>();

        lobbies.forEach((String, lobby) -> {
            if (lobby.getActors().size() < lobby.getMaxPlayers() && !lobby.getActors()
                                                                          .contains(req.getActor()) && !lobby
                    .isInGame() && !lobby.hasPassword()) {
                filteredLobbies.add(lobby);
            }
        });
        if (!filteredLobbies.isEmpty()) {
            int i = Util.randomInt(filteredLobbies.size());
            ILobby randomLobby = filteredLobbies.get(i);

            randomLobby.joinUser(req.getActor());

            Message responseMessage = new JoinLobbyResponse(randomLobby.getName(), ILobby.getSimpleLobby(randomLobby));
            responseMessage.initWithMessage(req);
            LOG.debug("Sending JoinLobbyResponse for Lobby {}", randomLobby.getName());
            post(responseMessage);

            LOG.debug("Sending UserJoinedLobbyMessage for Lobby {}", randomLobby.getName());
            sendToAllInLobby(randomLobby.getName(), new UserJoinedLobbyMessage(randomLobby.getName(), req.getActor()));

            LOG.debug("Sending AllLobbiesMessage");
            post(new AllLobbiesMessage(lobbyManagement.getSimpleLobbies()));
            sendColourChangedMessage(randomLobby, req.getActor());
        } else {
            Message responseMessage = new JoinRandomLobbyFailedResponse();
            responseMessage.initWithMessage(req);
            LOG.debug("Sending JoinRandomLobbyFailedResponse");
            post(responseMessage);
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
        LobbyName lobbyName = req.getName();
        LOG.debug("Received KickUserEvent for Lobby {}", lobbyName);
        Optional<ILobby> lobby = lobbyManagement.getLobby(lobbyName);
        if (req.getToBeKickedUser().equals(req.getActor())) return;
        if (lobby.isEmpty() || !lobby.get().getOwner().equals(req.getActor())) return;
        Actor toBeKickedUser = req.getToBeKickedUser();
        lobby.get().leaveUser(toBeKickedUser);

        ResponseMessage kickResponse = new KickUserResponse(lobbyName, toBeKickedUser);
        LOG.debug("Sending ForwardToUserInternalRequest containing KickUserResponse for Lobby {}", lobbyName);
        post(new ForwardToUserInternalRequest(toBeKickedUser, kickResponse));

        LOG.debug("Sending UserLeftLobbyMessage for Lobby {}", lobbyName);
        sendToAllInLobby(lobbyName, new UserLeftLobbyMessage(lobbyName, toBeKickedUser));

        LOG.debug("Sending AllLobbiesMessage");
        post(new AllLobbiesMessage(lobbyManagement.getSimpleLobbies()));
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
     * @see ILobby
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2019-10-08
     */
    @Subscribe
    private void onLeaveLobbyRequest(LeaveLobbyRequest req) {
        LobbyName lobbyName = req.getName();
        LOG.debug("Received LeaveLobbyRequest for Lobby {}", lobbyName);
        Optional<ILobby> lobby = lobbyManagement.getLobby(lobbyName);
        if (lobby.isEmpty()) return;
        try {
            lobby.get().leaveUser(req.getActor());
            LOG.debug("Sending UserLeftLobbyMessage for Lobby {}", lobbyName);
            sendToAllInLobby(lobbyName, new UserLeftLobbyMessage(lobbyName, req.getActor()));

            LOG.debug("Sending AllLobbiesMessage");
            post(new AllLobbiesMessage(lobbyManagement.getSimpleLobbies()));
        } catch (IllegalArgumentException exception) {
            lobbyManagement.dropLobby(lobbyName);
            LOG.debug("Sending LobbyDeletedMessage for Lobby {}", lobbyName);
            sendToAll(new LobbyDeletedMessage(lobbyName));
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
     * @see ILobby
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2021-01-28
     */
    @Subscribe
    private void onRemoveFromLobbiesRequest(RemoveFromLobbiesRequest req) {
        LOG.debug("Received RemoveFromLobbiesRequest");
        User user = req.getUser();
        LobbyMap lobbies = lobbyManagement.getLobbies();
        SimpleLobbyMap lobbiesWithUser = new SimpleLobbyMap();
        for (ILobby lobby : lobbies.values()) {
            if (lobby.getActors().contains(user)) {
                LobbyName name = lobby.getName();
                lobbiesWithUser.put(name, ILobby.getSimpleLobby(lobby));
                try {
                    lobby.leaveUser(user);
                    LOG.debug("Sending UserLeftLobbyMessage for Lobby {}", name);
                    sendToAllInLobby(name, new UserLeftLobbyMessage(name, user));
                } catch (IllegalArgumentException exception) {
                    lobbyManagement.dropLobby(name);
                    LOG.debug("Sending LobbyDeletedMessage for Lobby {}", name);
                    sendToAll(new LobbyDeletedMessage(name));
                }
            }
        }
        Message response = new RemoveFromLobbiesResponse(lobbiesWithUser);
        LOG.debug("Sending RemoveFromLobbiesResponse");
        post(response);

        LOG.debug("Sending AllLobbiesMessage");
        post(new AllLobbiesMessage(lobbyManagement.getSimpleLobbies()));
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
        Message response = new AllLobbiesResponse(lobbyManagement.getSimpleLobbies());
        response.initWithMessage(req);
        LOG.debug("Sending AllLobbiesResponse");
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
        LobbyName lobbyName = req.getLobbyName();
        Optional<ILobby> lobbyOptional = lobbyManagement.getLobby(lobbyName);
        if (lobbyOptional.isPresent()) {
            ILobby lobby = lobbyOptional.get();
            ActorSet lobbyMembers = lobby.getActors();
            int maxPlayers = lobby.getMaxPlayers();
            Message response = new AllLobbyMembersResponse(lobby.getName(), lobbyMembers, lobby.getOwner(),
                                                           lobby.getReadyUsers(), maxPlayers);
            response.initWithMessage(req);
            LOG.debug("Sending AllLobbyMembersResponse for Lobby {}", lobbyName);
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
        LobbyName lobbyName = req.getLobbyName();
        LOG.debug("Received ReturnToPreGameLobbyRequest for Lobby {}", lobbyName);
        Optional<ILobby> lobby = lobbyManagement.getLobby(lobbyName);
        if (lobby.isEmpty()) return;
        lobbyManagement.setInGame(lobbyName, false);
        for (User user : lobby.get().getRealUsers()) {
            LOG.debug("Sending UserReadyRequest for Lobby {}", lobbyName);
            post(new UserReadyRequest(lobbyName, user, false));
        }
        LOG.debug("Sending ReturnToPreGameLobbyMessage for Lobby {}", lobbyName);
        sendToAllInLobby(lobbyName, new ReturnToPreGameLobbyMessage(lobbyName, lobby.get().getOwner()));

        LOG.debug("Sending AllLobbiesMessage");
        sendToAll(new AllLobbiesMessage(lobbyManagement.getSimpleLobbies()));
    }

    /**
     * Handles a SetColourRequest found on the EventBus
     * <p>
     * This request gets sent in the lobby, after the user changes the desired colour.
     * It calls setUserColour method of the user's lobby and sends a ColourChangedMessage
     * back to the aforementioned lobby.
     *
     * @param req The SetColourRequest found on the EventBus
     *
     * @author Mario Fokken
     * @see de.uol.swp.common.lobby.request.SetColourRequest
     * @since 2021-06-02
     */
    @Subscribe
    private void onSetColourRequest(SetColourRequest req) {
        LOG.debug("Received SetColourRequest for Lobby {}", req.getName());
        ILobby lobby = lobbyManagement.getLobby(req.getName()).get();
        if (req.getColour() != null && !lobby.getUserColourMap().containsValue(req.getColour()))
            lobby.setUserColour(req.getActor(), req.getColour());
        sendColourChangedMessage(lobby, req.getActor());
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
        Optional<ILobby> lobby = lobbyManagement.getLobby(req.getName());
        if (lobby.isEmpty()) return;
        if (!req.getActor().equals(lobby.get().getOwner())) return;
        if (lobby.get().getActors().size() < 3 || (!lobby.get().getReadyUsers().equals(lobby.get().getActors())))
            return;
        LOG.debug("---- All Members are ready, proceeding with sending of CreateGameInternalRequest...");
        LOG.debug("Sending CreateGameInternalRequest for Lobby {}", req.getName());
        ServerInternalMessage msg = new CreateGameInternalRequest(lobby.get(), req.getActor(), req.getMoveTime());
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
        LOG.debug("Received UserReadyRequest for User {} in Lobby {}", req.getActor().getUsername(), req.getName());
        Optional<ILobby> lobby = lobbyManagement.getLobby(req.getName());
        if (lobby.isEmpty()) return;
        if (req.isReady()) {
            lobby.get().setUserReady(req.getActor());
        } else {
            lobby.get().unsetUserReady(req.getActor());
        }
        ServerMessage msg = new UserReadyMessage(req.getName(), req.getActor());
        LOG.debug("Sending UserReadyMessage for Lobby {}", req.getName());
        sendToAllInLobby(req.getName(), msg);
    }

    /**
     * Helper method to send a ColourChangedMessage
     * <p>
     * To let the Client show the right colours, a
     * ColourChangedMessage has to be sent everytime
     * a Lobby is created, a User joins, or a User changes
     * the colour
     *
     * @param lobby The affected lobby
     * @param user  The user triggering this message
     *
     * @author Mario Fokken
     * @see de.uol.swp.common.lobby.message.ColourChangedMessage
     * @since 2021-06-04
     */
    private void sendColourChangedMessage(ILobby lobby, Actor user) {
        LobbyName name = lobby.getName();
        LOG.debug("Sending ColourChangedMessage for Lobby {}", name);
        sendToAllInLobby(name, new ColourChangedMessage(name, user, lobby.getUserColourMap()));
    }
}

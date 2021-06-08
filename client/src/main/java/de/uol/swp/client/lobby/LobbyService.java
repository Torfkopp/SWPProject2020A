package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.event.LobbyErrorEvent;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.game.request.ReturnToPreGameLobbyRequest;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.user.request.CheckUserInLobbyRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The LobbyService is responsible for posting requests and events regarding
 * the state of a Lobby, like joining or Lobby setting updates.
 *
 * @author Marco Grawunder
 * @since 2019-11-20
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyService implements ILobbyService {

    private static final Logger LOG = LogManager.getLogger(LobbyService.class);

    private final EventBus eventBus;
    private final IUserService userService;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2019-11-20
     */
    @Inject
    public LobbyService(EventBus eventBus, IUserService userService) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.userService = userService;
        LOG.debug("LobbyService started");
    }

    @Override
    public void changeOwner(LobbyName lobbyName, UserOrDummy newOwner) {
        LOG.debug("Sending ChangeOwnerRequest");
        Message req = new ChangeOwnerRequest(lobbyName, userService.getLoggedInUser(), newOwner);
        eventBus.post(req);
    }

    @Override
    public void checkUserInLobby() {
        LOG.debug("Sending CheckUserInLobbyRequest");
        Message msg = new CheckUserInLobbyRequest(userService.getLoggedInUser());
        eventBus.post(msg);
    }

    @Override
    public void createNewLobby(LobbyName name, String password) {
        LOG.debug("Sending CreateLobbyRequest");
        Message createLobbyRequest = new CreateLobbyRequest(name, userService.getLoggedInUser(), password);
        eventBus.post(createLobbyRequest);
    }

    @Override
    public void joinLobby(LobbyName lobbyName) {
        LOG.debug("Sending LobbyJoinUserRequest");
        Message joinUserRequest = new JoinLobbyRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(joinUserRequest);
    }

    @Override
    public void addAI(LobbyName name, AI ai) {
        LOG.debug("Sending AddAIRequest");
        Message addAIRequest = new AddAIRequest(name, ai);
        eventBus.post(addAIRequest);
    }

    @Override
    public void joinRandomLobby() {
        LOG.debug("Sending JoinRandomLobbyRequest");
        Message joinRandomLobbyRequest = new JoinRandomLobbyRequest(null, userService.getLoggedInUser());
        eventBus.post(joinRandomLobbyRequest);
    }

    @Override
    public void kickUser(LobbyName lobbyName, UserOrDummy userToKick) {
        LOG.debug("Sending KickUserRequest");
        Message kickUserRequest = new KickUserRequest(lobbyName, userService.getLoggedInUser(), userToKick);
        eventBus.post(kickUserRequest);
    }

    @Override
    public void leaveLobby(LobbyName lobbyName) {
        LOG.debug("Sending LobbyLeaveUserRequest");
        Message lobbyLeaveUserRequest = new LeaveLobbyRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(lobbyLeaveUserRequest);
    }

    @Override
    public void refreshLobbyPresenterFields(ISimpleLobby lobby) {
        LOG.debug("Sending LobbyUpdateEvent");
        eventBus.post(new LobbyUpdateEvent(lobby));
    }

    @Override
    public void removeFromAllLobbies() {
        LOG.debug("Sending RemoveFromLobbiesRequest");
        Message removeFromLobbiesRequest = new RemoveFromLobbiesRequest(userService.getLoggedInUser());
        eventBus.post(removeFromLobbiesRequest);
    }

    @Override
    public void retrieveAllLobbies() {
        LOG.debug("Sending RetrieveAllLobbiesRequest");
        Message retrieveAllLobbiesRequest = new RetrieveAllLobbiesRequest();
        eventBus.post(retrieveAllLobbiesRequest);
    }

    @Override
    public void retrieveAllLobbyMembers(LobbyName lobbyName) {
        LOG.debug("Sending RetrieveAllLobbyMembersRequest for Lobby {}", lobbyName);
        Message retrieveAllLobbyMembersRequest = new RetrieveAllLobbyMembersRequest(lobbyName);
        eventBus.post(retrieveAllLobbyMembersRequest);
    }

    @Override
    public void returnToPreGameLobby(LobbyName lobbyName) {
        LOG.debug("Sending ReturnToPreGameLobbyRequest for Lobby {}", lobbyName);
        Message returnToPreGameLobbyRequest = new ReturnToPreGameLobbyRequest(lobbyName);
        eventBus.post(returnToPreGameLobbyRequest);
    }

    @Override
    public void showLobbyError(String message) {
        LOG.debug("Sending LobbyErrorEvent");
        eventBus.post(new LobbyErrorEvent(message));
    }

    @Override
    public void updateLobbySettings(LobbyName lobbyName, int maxPlayers, boolean startUpPhaseEnabled, int moveTime,
                                    boolean randomPlayFieldEnabled, int maxTradeDiff) {
        LOG.debug("Sending ChangeLobbySettingsRequest");
        eventBus.post(new ChangeLobbySettingsRequest(lobbyName, userService.getLoggedInUser(), maxPlayers,
                                                     startUpPhaseEnabled, moveTime, randomPlayFieldEnabled, maxTradeDiff));
    }

    @Override
    public void userReady(LobbyName lobbyName, boolean isReady) {
        LOG.debug("Sending UserReadyRequest");
        Message userReadyRequest = new UserReadyRequest(lobbyName, userService.getLoggedInUser(), isReady);
        eventBus.post(userReadyRequest);
    }
}

package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.game.request.ReturnToPreGameLobbyRequest;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.UserOrDummy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

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
    public void createNewLobby(String name, int maxPlayers) {
        LOG.debug("Sending CreateLobbyRequest");
        Message createLobbyRequest = new CreateLobbyRequest(name, userService.getLoggedInUser(), maxPlayers);
        eventBus.post(createLobbyRequest);
    }

    @Override
    public void joinLobby(String name) {
        LOG.debug("Sending LobbyJoinUserRequest");
        Message joinUserRequest = new LobbyJoinUserRequest(name, userService.getLoggedInUser());
        eventBus.post(joinUserRequest);
    }

    @Override
    public void kickUser(String lobbyName, UserOrDummy userToKick) {
        LOG.debug("Sending KickUserRequest");
        Message kickUserRequest = new KickUserRequest(lobbyName, userService.getLoggedInUser(), userToKick);
        eventBus.post(kickUserRequest);
    }

    @Override
    public void leaveLobby(String lobbyName) {
        LOG.debug("Sending LobbyLeaveUserRequest");
        Message lobbyLeaveUserRequest = new LobbyLeaveUserRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(lobbyLeaveUserRequest);
    }

    @Override
    public void refreshLobbyPresenterFields(Lobby lobby) {
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
    public void retrieveAllLobbyMembers(String lobbyName) {
        LOG.debug("Sending RetrieveAllLobbyMembersRequest for Lobby " + lobbyName);
        Message retrieveAllLobbyMembersRequest = new RetrieveAllLobbyMembersRequest(lobbyName);
        eventBus.post(retrieveAllLobbyMembersRequest);
    }

    @Override
    public void returnToPreGameLobby(String lobbyName) {
        LOG.debug("Sending ReturnToPreGameLobbyRequest for Lobby " + lobbyName);
        Message returnToPreGameLobbyRequest = new ReturnToPreGameLobbyRequest(lobbyName);
        eventBus.post(returnToPreGameLobbyRequest);
    }

    @Override
    public void updateLobbySettings(String lobbyName, int maxPlayers, boolean startUpPhaseEnabled,
                                    boolean commandsAllowed, int moveTime, boolean randomPlayFieldEnabled) {
        LOG.debug("Sending ChangeLobbySettingsRequest");
        eventBus.post(new ChangeLobbySettingsRequest(lobbyName, userService.getLoggedInUser(), maxPlayers,
                                                     startUpPhaseEnabled, commandsAllowed, moveTime,
                                                     randomPlayFieldEnabled));
    }

    @Override
    public void userReady(String lobbyName, boolean isReady) {
        LOG.debug("Sending UserReadyRequest");
        Message userReadyRequest = new UserReadyRequest(lobbyName, userService.getLoggedInUser(), isReady);
        eventBus.post(userReadyRequest);
    }
}

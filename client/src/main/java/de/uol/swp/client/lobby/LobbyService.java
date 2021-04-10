package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.game.request.ReturnToPreGameLobbyRequest;
import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
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

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2019-11-20
     */
    @Inject
    public LobbyService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
        LOG.debug("LobbyService started");
    }

    @Override
    public void updateGameMap(String lobbyName) {
        LOG.debug("Sending UpdateGameMapRequest");
        Message msg = new UpdateGameMapRequest(lobbyName);
        eventBus.post(msg);
    }

    @Override
    public void createNewLobby(String name, User user, int maxPlayers) {
        LOG.debug("Sending CreateLobbyRequest");
        Message createLobbyRequest = new CreateLobbyRequest(name, user, maxPlayers);
        eventBus.post(createLobbyRequest);
    }

    @Override
    public void buildRequest(String lobbyName, User user, MapPoint mapPoint) {
        LOG.debug("Sending BuildRequest");
        Message buildRequest = new BuildRequest(lobbyName, user, mapPoint);
        eventBus.post(buildRequest);
    }

    @Override
    public void joinLobby(String name, User user) {
        LOG.debug("Sending LobbyJoinUserRequest");
        Message joinUserRequest = new LobbyJoinUserRequest(name, user);
        eventBus.post(joinUserRequest);
    }

    @Override
    public void kickUser(String lobbyName, User loggedInUser, UserOrDummy userToKick) {
        LOG.debug("Sending KickUserRequest");
        Message kickUserRequest = new KickUserRequest(lobbyName, loggedInUser, userToKick);
        eventBus.post(kickUserRequest);
    }

    @Override
    public void leaveLobby(String lobbyName, User user) {
        LOG.debug("Sending LobbyLeaveUserRequest");
        Message lobbyLeaveUserRequest = new LobbyLeaveUserRequest(lobbyName, user);
        eventBus.post(lobbyLeaveUserRequest);
    }

    @Override
    public void refreshLobbyPresenterFields(String lobbyName, User user, Lobby lobby) {
        LOG.debug("Sending LobbyUpdateEvent");
        eventBus.post(new LobbyUpdateEvent(lobbyName, user, lobby));
    }

    @Override
    public void removeFromLobbies(User user) {
        LOG.debug("Sending RemoveFromLobbiesRequest");
        Message removeFromLobbiesRequest = new RemoveFromLobbiesRequest(user);
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
    public void updateLobbySettings(String lobbyName, User user, int maxPlayers, boolean startUpPhaseEnabled,
                                    boolean commandsAllowed, int moveTime, boolean randomPlayFieldEnabled) {
        LOG.debug("Sending a ChangeLobbySettingsRequest");
        eventBus.post(new ChangeLobbySettingsRequest(lobbyName, user, maxPlayers, startUpPhaseEnabled, commandsAllowed,
                                                     moveTime, randomPlayFieldEnabled));
    }

    @Override
    public void userReady(String lobbyName, User loggedInUser, boolean isReady) {
        LOG.debug("Sending UserReadyRequest");
        Message userReadyRequest = new UserReadyRequest(lobbyName, loggedInUser, isReady);
        eventBus.post(userReadyRequest);
    }
}

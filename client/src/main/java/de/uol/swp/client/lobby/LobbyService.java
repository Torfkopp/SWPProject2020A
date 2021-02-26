package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.request.EndTurnRequest;
import de.uol.swp.common.game.request.PlayCardRequest.*;
import de.uol.swp.common.game.request.UpdateInventoryRequest;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that manages lobbies
 *
 * @author Marco Grawunder
 * @since 2019-11-20
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyService implements ILobbyService {

    private final EventBus eventBus;
    private final Logger LOG = LogManager.getLogger(LobbyService.class);

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
        LOG.debug("LobbyService started");
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    @Override
    public void createNewLobby(String name, User user) {
        LOG.debug("Sending CreateLobbyRequest");
        Message createLobbyRequest = new CreateLobbyRequest(name, user);
        eventBus.post(createLobbyRequest);
    }

    @Override
    public void endTurn(User user, String lobbyName) {
        LOG.debug("Sending EndTurnRequest");
        Message endTurnRequest = new EndTurnRequest(user, lobbyName);
        eventBus.post(endTurnRequest);
    }

    @Override
    public void joinLobby(String name, User user) {
        LOG.debug("Sending LobbyJoinUserRequest");
        Message joinUserRequest = new LobbyJoinUserRequest(name, user);
        eventBus.post(joinUserRequest);
    }

    @Override
    public void leaveLobby(String lobbyName, User user) {
        LOG.debug("Sending LobbyLeaveUserRequest");
        Message lobbyLeaveUserRequest = new LobbyLeaveUserRequest(lobbyName, user);
        eventBus.post(lobbyLeaveUserRequest);
    }

    @Override
    public void refreshLobbyPresenterFields(String lobbyName, User user) {
        LOG.debug("Sending LobbyUpdateEvent");
        eventBus.post(new LobbyUpdateEvent(lobbyName, user));
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
    public void updateInventory(String lobbyName, User user) {
        LOG.debug("Sending UpdateInventoryRequest");
        Message updateInventoryRequest = new UpdateInventoryRequest(user, lobbyName);
        eventBus.post(updateInventoryRequest);
    }

    @Override
    public void playKnightCard(String lobbyName, User user) {
        LOG.debug("Sending PlayKnightCardRequest");
        Message msg = new PlayKnightCardRequest(lobbyName, user);
        eventBus.post(msg);
    }

    @Override
    public void playMonopolyCard(String lobbyName, User user, Resources resource) {
        LOG.debug("Sending PlayMonopolyCardRequest");
        Message msg = new PlayMonopolyCardRequest(lobbyName, user, resource);
        eventBus.post(msg);
    }

    @Override
    public void playYearOfPlentyCard(String lobbyName, User user, Resources resource1, Resources resource2) {
        LOG.debug("Sending PlayYearOfPlentyCardRequest");
        Message msg = new PlayYearOfPlentyCardRequest(lobbyName, user, resource1, resource2);
        eventBus.post(msg);
    }

    @Override
    public void playRoadBuildingCard(String lobbyName, User user) {
        LOG.debug("Sending PlayRoadBuildingCardRequest");
        Message msg = new PlayRoadBuildingCardRequest(lobbyName, user);
        eventBus.post(msg);
    }
}

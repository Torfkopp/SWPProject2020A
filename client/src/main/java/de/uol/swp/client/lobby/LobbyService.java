package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.game.request.EndTurnRequest;
import de.uol.swp.common.game.request.UpdateInventoryRequest;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;

/**
 * Class that manages lobbies
 *
 * @author Marco Grawunder
 * @since 2019-11-20
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyService implements ILobbyService {

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
    }

    @Override
    public void createNewLobby(String name, User user) {
        Message createLobbyRequest = new CreateLobbyRequest(name, user);
        eventBus.post(createLobbyRequest);
    }

    @Override
    public void endTurn(User user, String lobbyName) {
        Message endTurnRequest = new EndTurnRequest(user, lobbyName);
        eventBus.post(endTurnRequest);
    }

    @Override
    public void joinLobby(String name, User user) {
        Message joinUserRequest = new LobbyJoinUserRequest(name, user);
        eventBus.post(joinUserRequest);
    }

    @Override
    public void leaveLobby(String lobbyName, User user) {
        Message lobbyLeaveUserRequest = new LobbyLeaveUserRequest(lobbyName, user);
        eventBus.post(lobbyLeaveUserRequest);
    }

    @Override
    public void refreshLobbyPresenterFields(String lobbyName, User user) {
        eventBus.post(new LobbyUpdateEvent(lobbyName, user));
    }

    @Override
    public void removeFromLobbies(User user) {
        Message removeFromLobbiesRequest = new RemoveFromLobbiesRequest(user);
        eventBus.post(removeFromLobbiesRequest);
    }

    @Override
    public void retrieveAllLobbies() {
        Message retrieveAllLobbiesRequest = new RetrieveAllLobbiesRequest();
        eventBus.post(retrieveAllLobbiesRequest);
    }

    @Override
    public void retrieveAllLobbyMembers(String lobbyName) {
        Message retrieveAllLobbyMembersRequest = new RetrieveAllLobbyMembersRequest(lobbyName);
        eventBus.post(retrieveAllLobbyMembersRequest);
    }

    @Override
    public void updateInventory(String lobbyName, User user) {
        Message updateInventoryRequest = new UpdateInventoryRequest(user, lobbyName);
        eventBus.post(updateInventoryRequest);
    }
}

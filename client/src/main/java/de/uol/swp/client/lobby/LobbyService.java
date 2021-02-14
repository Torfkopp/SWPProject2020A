package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.game.request.EndTurnRequest;
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
public class LobbyService {

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

    /**
     * Posts a request to create a lobby onto the EventBus
     *
     * @param name Name chosen for the new lobby
     * @param user User wanting to create the new lobby
     *
     * @see de.uol.swp.common.lobby.request.CreateLobbyRequest
     * @since 2019-11-20
     */
    public void createNewLobby(String name, User user) {
        LOG.debug("Sending CreateLobbyRequest");
        Message createLobbyRequest = new CreateLobbyRequest(name, user);
        eventBus.post(createLobbyRequest);
    }

    /**
     * Posts a request to end the turn onto the Event
     *
     * @param user User who wants to end the turn
     *
     * @see de.uol.swp.common.game.request.EndTurnRequest
     * @since 2021-1-15
     */
    public void endTurn(User user, String lobbyName) {
        LOG.debug("Sending EndTurnRequest");
        Message endTurnRequest = new EndTurnRequest(user, lobbyName);
        eventBus.post(endTurnRequest);
    }

    /**
     * Posts a request to join a specified lobby onto the EventBus
     *
     * @param name Name of the lobby the user wants to join
     * @param user User who wants to join the lobby
     *
     * @see de.uol.swp.common.lobby.request.LobbyJoinUserRequest
     * @since 2019-11-20
     */
    public void joinLobby(String name, User user) {
        LOG.debug("Sending LobbyJoinUserRequest");
        Message joinUserRequest = new LobbyJoinUserRequest(name, user);
        eventBus.post(joinUserRequest);
    }

    public void leaveLobby(String lobbyName, User user) {
        LOG.debug("Sending LobbyLeaveUserRequest");
        Message lobbyLeaveUserRequest = new LobbyLeaveUserRequest(lobbyName, user);
        eventBus.post(lobbyLeaveUserRequest);
    }

    /**
     * Posts a new instance of the LobbyUpdateEvent onto the Eventbus
     * <p>
     * This ensures that a new LobbyPresenter will know what lobby it is
     * presenting and who the currently logged in User is.
     *
     * @param lobbyName The name of the Lobby
     * @param user      The currently logged in User
     *
     * @since 2020-12-30
     */
    public void refreshLobbyPresenterFields(String lobbyName, User user) {
        eventBus.post(new LobbyUpdateEvent(lobbyName, user));
    }

    /**
     * Posts a request to remove the user from all lobbies
     *
     * @param user the logged in user
     *
     * @see de.uol.swp.common.lobby.request.RemoveFromLobbiesRequest
     * @since 2021-01-28
     */
    public void removeFromLobbies(User user) {
        LOG.debug("Send RemoveFromLobbiesRequest");
        Message removeFromLobbiesRequest = new RemoveFromLobbiesRequest(user);
        eventBus.post(removeFromLobbiesRequest);
    }

    /**
     * Posts a request to retrieve all lobby names
     *
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
     * @since 2020-12-12
     */
    public void retrieveAllLobbies() {
        LOG.debug("Send RetrieveAllLobbiesRequest");
        Message retrieveAllLobbiesRequest = new RetrieveAllLobbiesRequest();
        eventBus.post(retrieveAllLobbiesRequest);
    }

    public void retrieveAllLobbyMembers(String lobbyName) {
        LOG.debug("Send RetrieveAllLobbyMembersRequest for Lobby " + lobbyName);
        Message retrieveAllLobbyMembersRequest = new RetrieveAllLobbyMembersRequest(lobbyName);
        eventBus.post(retrieveAllLobbyMembersRequest);
    }

    /**
     * Posts a request to update ones Inventory
     *
     * @param lobbyName name of the lobby the user wants to update his Inventory in
     * @param user      User who wants to update his Inventory.
     *
     * @author Sven Ahrens
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.UpdateInventoryRequest
     * @since 2021-1-25
     */
    public void updateInventory(String lobbyName, User user) {
        LOG.debug("Send UpdateInventoryRequest");
        Message updateInventoryRequest = new UpdateInventoryRequest(user, lobbyName);
        eventBus.post(updateInventoryRequest);
    }
}

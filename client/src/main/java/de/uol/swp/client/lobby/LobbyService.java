package de.uol.swp.client.lobby;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.common.lobby.message.CreateLobbyRequest;
import de.uol.swp.common.lobby.message.DeleteLobbyRequest;
import de.uol.swp.common.lobby.message.LobbyJoinUserRequest;
import de.uol.swp.common.lobby.message.LobbyLeaveUserRequest;
import de.uol.swp.common.user.UserDTO;

/**
 * Classes that manages lobbies
 *
 * @author Marco Grawunder
 * @since 2019-11-20
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyService {

    private final EventBus eventBus;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     * @see de.uol.swp.client.di.ClientModule
     * @since 2019-11-20
     */
    @Inject
    public LobbyService(EventBus eventBus) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
    }

    /**
     * Posts a request to create a lobby on the EventBus
     *
     * @param name Name chosen for the new lobby
     * @param user User who wants to create the new lobby
     * @see de.uol.swp.common.lobby.message.CreateLobbyRequest
     * @since 2019-11-20
     */
    public void createNewLobby(String name, UserDTO user) {
        CreateLobbyRequest createLobbyRequest = new CreateLobbyRequest(name, user);
        eventBus.post(createLobbyRequest);
    }

    /**
     * Posts a request to join a specified lobby on the EventBus
     *
     * @param name Name of the lobby the user wants to join
     * @param user User who wants to join the lobby
     * @see de.uol.swp.common.lobby.message.LobbyJoinUserRequest
     * @since 2019-11-20
     */
    public void joinLobby(String name, UserDTO user) {
        LobbyJoinUserRequest joinUserRequest = new LobbyJoinUserRequest(name, user);
        eventBus.post(joinUserRequest);
    }

    /**
     * Posts a request to leave a specified lobby on the EventBus
     *
     * @see de.uol.swp.common.lobby.message.LobbyLeaveUserRequest
     * @since 2020-12-05
     */
    public void leaveLobby(String lobbyName, UserDTO user) {
        LobbyLeaveUserRequest lobbyLeaveUserRequest = new LobbyLeaveUserRequest(lobbyName, user);
        eventBus.post(lobbyLeaveUserRequest);
    }

    /**
     * Posts a request to delete a specified lobby on the EventBus
     *
     * @param lobbyName The name of the lobby
     * @see de.uol.swp.common.lobby.message.DeleteLobbyRequest
     * @since 2020-12-14
     */
    public void deleteLobby(String lobbyName) {
        DeleteLobbyRequest delete = new DeleteLobbyRequest(lobbyName);
        eventBus.post(delete);
    }
}

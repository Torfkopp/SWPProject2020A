package de.uol.swp.client.lobby;

import de.uol.swp.common.user.User;

/**
 * An interface for all methods of the clientLobbyService
 *
 * @author Steven Luong
 * @see de.uol.swp.client.lobby.LobbyService
 * @since 2021-02-12
 */
public interface ILobbyService {

    /**
     * Posts a request to create a lobby onto the EventBus
     *
     * @param name The name chosen for the new lobby
     * @param user The user wanting to create the new lobby
     *
     * @see de.uol.swp.common.lobby.request.CreateLobbyRequest
     * @since 2019-11-20
     */
    void createNewLobby(String name, User user);

    /**
     * Posts a request to end the turn onto the Event
     *
     * @param user The user who wants to end the turn
     *
     * @see de.uol.swp.common.game.request.EndTurnRequest
     * @since 2021-01-15
     */
    void endTurn(User user, String lobbyName);

    /**
     * Posts a request to join a specified lobby onto the EventBus
     *
     * @param name The name of the lobby the user wants to join
     * @param user The user who wants to join the lobby
     *
     * @see de.uol.swp.common.lobby.request.LobbyJoinUserRequest
     * @since 2019-11-20
     */
    void joinLobby(String name, User user);

    /**
     * Posts a request to leave a specified lobby onto the EventBus
     *
     * @param lobbyName The name of the lobby the User wants to leave
     * @param user      The user who wants to leave the lobby
     *
     * @see de.uol.swp.common.lobby.request.LobbyLeaveUserRequest
     * @since 2020-12-05
     */
    void leaveLobby(String lobbyName, User user);

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
    void refreshLobbyPresenterFields(String lobbyName, User user);

    /**
     * Posts a request to remove the user from all lobbies
     *
     * @param user The logged in user
     *
     * @see de.uol.swp.common.lobby.request.RemoveFromLobbiesRequest
     * @since 2021-01-28
     */
    void removeFromLobbies(User user);

    /**
     * Posts a request to retrieve all lobby names
     *
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbiesRequest
     * @since 2020-12-12
     */
    void retrieveAllLobbies();

    /**
     * Posts a request to retrieve all members of a lobby
     *
     * @param lobbyName The name of the lobby whose member list to request
     *
     * @author Alwin Bossert
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbyMembersRequest
     * @since 2020-12-20
     */
    void retrieveAllLobbyMembers(String lobbyName);

    /**
     * Posts a request to update ones Inventory
     *
     * @param lobbyName The name of the lobby the user wants to update his Inventory in
     * @param user      The user who wants to update his Inventory.
     *
     * @author Sven Ahrens
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.UpdateInventoryRequest
     * @since 2021-01-25
     */
    void updateInventory(String lobbyName, User user);

    /**
     * Posts a message to play a KnightCard
     *
     * @param lobbyName The name of the lobby
     * @param user The user
     */
    void playKnightCard (String lobbyName,User user);

    /**
     * Posts a message to play a MonopolyCard
     *
     * @param lobbyName The name of the lobby
     * @param user The user
     * @param resource The resource the user wants
     */
    void playMonopolyCard(String lobbyName, User user, Resources resource);

    /**
     * Posts a message to play a YearOfPlentyCard
     *
     * @param lobbyName The name of the lobby
     * @param user The user
     * @param resource1 The resource the user wants
     * @param resource2 The resource the user wants
     */
    void playYearOfPlentyCard(String lobbyName, User user, Resources resource1, Resources resource2);

    /**
     * Posts a message to play a RoadBuildingCard
     *
     * @param lobbyName The name of the lobby
     * @param user The user
     */
    void playRoadBuildingCard(String lobbyName, User user);

}

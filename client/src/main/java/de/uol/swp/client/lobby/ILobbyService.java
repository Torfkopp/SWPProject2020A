package de.uol.swp.client.lobby;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.UserOrDummy;

/**
 * An interface for all methods of the LobbyService
 *
 * @author Steven Luong
 * @see de.uol.swp.client.lobby.LobbyService
 * @since 2021-02-12
 */
public interface ILobbyService {

    /**
     * Posts a request to check if the user is currently in a lobby
     *
     * @param user The logged in user
     *
     * @author Alwin Bossert
     * @author Finn Haase
     * @since 2021-04-09
     */
    void checkUserInLobby(User user);

    /**
     * Posts a request to create a lobby onto the EventBus
     *
     * @param name      The name chosen for the new lobby
     * @param maxPlayer The maximum amount of players for the new lobby
     *
     * @see de.uol.swp.common.lobby.request.CreateLobbyRequest
     * @since 2019-11-20
     */
    void createNewLobby(String name, int maxPlayer);

    /**
     * Posts a request to join a specified lobby onto the EventBus
     *
     * @param name The name of the lobby the user wants to join
     *
     * @see de.uol.swp.common.lobby.request.LobbyJoinUserRequest
     * @since 2019-11-20
     */
    void joinLobby(String name);

    /**
     * Posts a request to kick a user
     *
     * @param lobbyName  The name of the lobby the user should be kicked out.
     * @param userToKick The user who should be kicked.
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @since 2021-03-23
     */
    void kickUser(String lobbyName, UserOrDummy userToKick);

    /**
     * Posts a request to leave a specified lobby onto the EventBus
     *
     * @param lobbyName The name of the lobby the User wants to leave
     *
     * @see de.uol.swp.common.lobby.request.LobbyLeaveUserRequest
     * @since 2020-12-05
     */
    void leaveLobby(String lobbyName);

    /**
     * Posts a new instance of the LobbyUpdateEvent onto the Eventbus
     * <p>
     * This ensures that a new LobbyPresenter will know what lobby it is
     * presenting and who the currently logged in User is.
     *
     * @param lobby The Lobby to present
     *
     * @since 2020-12-30
     */
    void refreshLobbyPresenterFields(Lobby lobby);

    /**
     * Posts a request to remove the user from all lobbies
     *
     * @see de.uol.swp.common.lobby.request.RemoveFromLobbiesRequest
     * @since 2021-01-28
     */
    void removeFromAllLobbies();

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
     * Posts a request to return to the Pre-Game Lobby.
     *
     * @param lobbyName The name of the lobby.
     *
     * @author Steven Luong
     * @author Finn Haase
     * @see de.uol.swp.common.game.request.ReturnToPreGameLobbyRequest
     * @since 2021-03-22
     */
    void returnToPreGameLobby(String lobbyName);

    /**
     * This method is used to update the pre-game settings of a specific lobby.
     *
     * @param lobbyName              The name of the lobby
     * @param maxPlayers             The maximum amount of players for a lobby
     * @param startUpPhaseEnabled    Whether the startUpPhase is allowed or not
     * @param commandsAllowed        Whether commands are allowed or not
     * @param moveTime               The maximum time of a move
     * @param randomPlayFieldEnabled Whether the randomPlayField is enabled or not
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    void updateLobbySettings(String lobbyName, int maxPlayers, boolean startUpPhaseEnabled, boolean commandsAllowed,
                             int moveTime, boolean randomPlayFieldEnabled);

    /**
     * Posts a request to change the ready status of a user
     *
     * @param lobbyName The name of the lobby the user wants to change his ready status in.
     * @param isReady   The ready status the user wants to change to.
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @since 2021-03-23
     */
    void userReady(String lobbyName, boolean isReady);
}

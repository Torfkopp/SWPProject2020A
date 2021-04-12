package de.uol.swp.client.lobby;

import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
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
     * Checks if the lobby is in a game
     *
     * @param lobbyName     LobbyName to check
     * @param loggendInUser Currently logged in user
     *
     * @author Marvin Drees
     * @author Maximilian Lindner
     * @since 2021-04-09
     */
    void checkForGame(String lobbyName, User loggendInUser);

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
     * @param user      The user wanting to create the new lobby
     * @param maxPlayer The maximum amount of players for the new lobby
     *
     * @see de.uol.swp.common.lobby.request.CreateLobbyRequest
     * @since 2019-11-20
     */
    void createNewLobby(String name, User user, int maxPlayer);

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
     * Posts a request to join a random lobby onto the EventBus
     *
     * @param user The user who wants to join a random lobby
     *
     * @author Finn Haase
     * @author Sven Ahrens
     * @see de.uol.swp.common.lobby.request.LobbyJoinRandomUserRequest
     * @since 2021-04-08
     */
    void joinRandomLobby(User user);

    /**
     * Posts a request to kick a user
     *
     * @param lobbyName    The name of the lobby the user should be kicked out.
     * @param loggedInUser The user who wants to kick another user.
     * @param userToKick   The user who should be kicked.
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @since 2021-03-23
     */
    void kickUser(String lobbyName, User loggedInUser, UserOrDummy userToKick);

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
     * @param lobby     The Lobby to present
     *
     * @since 2020-12-30
     */
    void refreshLobbyPresenterFields(String lobbyName, User user, Lobby lobby);

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
     * Posts a RobberChosenVictimRequest
     *
     * @param lobbyName The name of the lobby
     * @param user      The user to receive the resource card
     * @param victim    The user to lose the resource card
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-06
     */
    void robberChooseVictim(String lobbyName, User user, UserOrDummy victim);

    /**
     * Posts a RobberNewPositionMessage
     *
     * @param lobby    The name of the lobby
     * @param user     The user
     * @param mapPoint The robber's new position
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-06
     */
    void robberNewPosition(String lobby, User user, MapPoint mapPoint);

    /**
     * This method is used to update the pre-game settings of a specific lobby.
     *
     * @param lobbyName              The name of the lobby
     * @param user                   The User who wants to update the pre-game settings
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
    void updateLobbySettings(String lobbyName, User user, int maxPlayers, boolean startUpPhaseEnabled,
                             boolean commandsAllowed, int moveTime, boolean randomPlayFieldEnabled);

    /**
     * Posts a request to change the ready status of a user
     *
     * @param lobbyName    The name of the lobby the user wants to change his ready status in.
     * @param loggedInUser The user who wants to change his ready status.
     * @param isReady      The ready status the user wants to change to.
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @since 2021-03-23
     */
    void userReady(String lobbyName, User loggedInUser, boolean isReady);
}

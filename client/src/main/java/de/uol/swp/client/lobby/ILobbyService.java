package de.uol.swp.client.lobby;

import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
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
     * Posts a request to change the owner status of a user in a lobby
     *
     * @param lobbyName The name of the lobby the user wants to change the owner
     * @param newOwner  The new owner
     *
     * @author Maximillian Lindner
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @since 2021-04-15
     */
    void changeOwner(LobbyName lobbyName, UserOrDummy newOwner);

    /**
     * Checks if the lobby is in a game
     *
     * @param lobbyName LobbyName to check
     *
     * @author Marvin Drees
     * @author Maximilian Lindner
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @since 2021-04-09
     */
    void checkForGame(LobbyName lobbyName);

    /**
     * Posts a request to check if the user is currently in a lobby
     *
     * @author Alwin Bossert
     * @author Finn Haase
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @since 2021-04-09
     */
    void checkUserInLobby();

    /**
     * Posts a request to create a lobby onto the EventBus
     *
     * @param name     The name chosen for the new lobby
     * @param password The password chosen for the new lobby
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @see de.uol.swp.common.lobby.request.CreateLobbyRequest
     * @since 2019-11-20
     */
    void createNewLobby(LobbyName name, String password);

    /**
     * Posts a request to join a specified lobby onto the EventBus
     *
     * @param name The name of the lobby the user wants to join
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @see de.uol.swp.common.lobby.request.JoinLobbyRequest
     * @since 2019-11-20
     */
    void joinLobby(LobbyName name);

    /**
     * Posts a request to join a random lobby onto the EventBus
     *
     * @author Finn Haase
     * @author Sven Ahrens
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @see de.uol.swp.common.lobby.request.JoinRandomLobbyRequest
     * @since 2021-04-08
     */
    void joinRandomLobby();

    /**
     * Posts a request to kick a user
     *
     * @param lobbyName  The name of the lobby the user should be kicked out.
     * @param userToKick The user who should be kicked.
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @since 2021-03-23
     */
    void kickUser(LobbyName lobbyName, UserOrDummy userToKick);

    /**
     * Posts a request to leave a specified lobby onto the EventBus
     *
     * @param lobbyName The name of the lobby the User wants to leave
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @see de.uol.swp.common.lobby.request.LeaveLobbyRequest
     * @since 2020-12-05
     */
    void leaveLobby(LobbyName lobbyName);

    /**
     * Posts a new instance of the LobbyUpdateEvent onto the Eventbus
     * <p>
     * This ensures that a new LobbyPresenter will know what lobby it is
     * presenting and who the currently logged in User is.
     *
     * @param lobby The Lobby to present
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @since 2020-12-30
     */
    void refreshLobbyPresenterFields(ISimpleLobby lobby);

    /**
     * Posts a request to remove the user from all lobbies
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @see de.uol.swp.common.lobby.request.RemoveFromLobbiesRequest
     * @since 2021-01-28
     */
    void removeFromAllLobbies();

    /**
     * Posts a request to retrieve all lobby names
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
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
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @see de.uol.swp.common.lobby.request.RetrieveAllLobbyMembersRequest
     * @since 2020-12-20
     */
    void retrieveAllLobbyMembers(LobbyName lobbyName);

    /**
     * Posts a request to return to the Pre-Game Lobby.
     *
     * @param lobbyName The name of the lobby.
     *
     * @author Steven Luong
     * @author Finn Haase
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @see de.uol.swp.common.game.request.ReturnToPreGameLobbyRequest
     * @since 2021-03-22
     */
    void returnToPreGameLobby(LobbyName lobbyName);

    /**
     * Posts an event to show a Lobby Error alert with the provided message
     *
     * @param message The message to display
     *
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     */
    void showLobbyError(String message);

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
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @since 2021-03-15
     */
    void updateLobbySettings(LobbyName lobbyName, int maxPlayers, boolean startUpPhaseEnabled, boolean commandsAllowed,
                             int moveTime, boolean randomPlayFieldEnabled);

    /**
     * Posts a request to change the ready status of a user
     *
     * @param lobbyName The name of the lobby the user wants to change his ready status in.
     * @param isReady   The ready status the user wants to change to.
     *
     * @author Maximillian Lindner
     * @author Temmo Junkhoff
     * @implNote The method contents are executed on a separate Thread from the JavaFX Application Thread
     * @since 2021-03-23
     */
    void userReady(LobbyName lobbyName, boolean isReady);
}

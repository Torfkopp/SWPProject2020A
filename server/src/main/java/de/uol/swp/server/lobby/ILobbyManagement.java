package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

import java.util.Map;
import java.util.Optional;

/**
 * An interface for all methods of the LobbyManagement
 *
 * @author Steven Luong
 * @see de.uol.swp.server.lobby.LobbyManagement
 * @since 2021-02-12
 */
public interface ILobbyManagement {

    /**
     * Creates a new lobby and adds it to the list
     *
     * @param name      The name of the lobby to create
     * @param owner     The user who wants to create a lobby
     * @param maxPlayer The maximum amount of players for this lobby
     *
     * @throws java.lang.IllegalArgumentException Name already taken
     * @implNote The primary key of the lobbies is the name, therefore the name has
     * to be unique
     * @see de.uol.swp.common.user.User
     * @since 2019-10-08
     */
    void createLobby(String name, User owner, int maxPlayer, String lobbyPassword) throws IllegalArgumentException;

    /**
     * Deletes a lobby with a requested name
     *
     * @param name String containing the name of the lobby to delete
     *
     * @throws java.lang.IllegalArgumentException There is no lobby with the requested name
     * @since 2019-10-08
     */
    void dropLobby(String name) throws IllegalArgumentException;

    /**
     * Gets the map
     *
     * @return Map with the lobby's name and its Lobby object
     *
     * @since 2020-12-12
     */
    Map<String, Lobby> getLobbies();

    /**
     * Searches for the lobby with the requested name
     *
     * @param name String containing the name of the lobby to search for
     *
     * @return Either an empty Optional or an Optional containing the lobby
     *
     * @see java.util.Optional
     * @since 2019-10-08
     */
    Optional<Lobby> getLobby(String name);

    Optional<Lobby> getLobby(String name, String password);

    /**
     * Sets the hasPassword attribute of a lobby according to the boolean provided
     *
     * @param lobbyName
     * @param hasPassword
     *
     * @author Alwin Bossert
     * @since 2021-04-20
     */
    void setHasPassword(String lobbyName, boolean hasPassword);

    /**
     * Sets the inGame attribute of a lobby according to the boolean provided
     *
     * @param lobbyName The name of the lobby
     * @param inGame    Whether the lobby is currently in a game or not
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @author Phillip-André Suhr
     * @since 2021-03-01
     */
    void setInGame(String lobbyName, boolean inGame);

    /**
     * This method is used to update the pre-game settings of a specific lobby.
     *
     * @param lobbyName              The name of the lobby
     * @param maxPlayers             The maximum amount of players for a lobby
     * @param commandsAllowed        Whether commands are allowed or not
     * @param moveTime               The maximum time of a move
     * @param startUpPhaseEnabled    Whether the startUpPhase is allowed or not
     * @param randomPlayfieldEnabled Whether the randomPlayfield is enabled or not
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    void updateLobbySettings(String lobbyName, int maxPlayers, boolean commandsAllowed, int moveTime,
                             boolean startUpPhaseEnabled, boolean randomPlayfieldEnabled);
}

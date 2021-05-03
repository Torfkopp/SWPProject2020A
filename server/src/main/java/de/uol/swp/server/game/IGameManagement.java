package de.uol.swp.server.game;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.server.game.map.IGameMapManagement;
import de.uol.swp.server.lobby.Lobby;

import java.util.Map;

/**
 * An interface for all methods of the GameManagement
 *
 * @author Steven Luong
 * @see de.uol.swp.server.game.GameManagement
 * @since 2021-02-12
 */
public interface IGameManagement {

    /**
     * Creates a new game and adds it to the list
     *
     * @param lobby   The game's lobby
     * @param first   The User or Dummy who will be first
     * @param gameMap The IGameMap the game will use
     *
     * @throws java.lang.IllegalArgumentException Lobby already has a game
     * @implNote The primary key of games is the lobby's name, therefore
     * only one game per lobby is possible
     * @since 2021-01-24
     */
    void createGame(Lobby lobby, UserOrDummy first, IGameMapManagement gameMap) throws IllegalArgumentException;

    /**
     * Deletes a game with its lobby's name
     *
     * @param lobbyName The name of the lobby
     *
     * @throws java.lang.IllegalArgumentException There is no game with the requested name
     * @since 2021-01-24
     */
    void dropGame(LobbyName lobbyName) throws IllegalArgumentException;

    /**
     * Searches for the game with the requested name
     *
     * @param lobbyName The name of the lobby
     *
     * @return The requested game
     *
     * @since 2021-01-24
     */
    Game getGame(LobbyName lobbyName);

    /**
     * Gets the map
     *
     * @return Map with the lobby's name and its Game object
     *
     * @since 2021-01-24
     */
    Map<LobbyName, Game> getGames();
}

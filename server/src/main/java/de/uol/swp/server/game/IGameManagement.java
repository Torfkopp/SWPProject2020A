package de.uol.swp.server.game;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

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
     * @param lobby The game's lobby
     *
     * @throws java.lang.IllegalArgumentException Lobby already has a game
     * @implNote The primary key of games is the lobby's name, therefore
     * only one game per lobby is possible
     * @since 2021-01-24
     */
    void createGame(Lobby lobby, User first) throws IllegalArgumentException;

    /**
     * Deletes a game with its lobby's name
     *
     * @param lobbyName The name of the lobby
     *
     * @throws java.lang.IllegalArgumentException There is no game with the requested name
     * @since 2021-01-24
     */
    void dropGame(String lobbyName) throws IllegalArgumentException;

    /**
     * Searches for the game with the requested name
     *
     * @param lobbyName The name of the lobby
     *
     * @return The requested game
     *
     * @since 2021-01-24
     */
    Game getGame(String lobbyName);

    /**
     * Gets the map
     *
     * @return Map with the lobby's name and its Game object
     *
     * @since 2021-01-24
     */
    Map<String, Game> getGames();
}
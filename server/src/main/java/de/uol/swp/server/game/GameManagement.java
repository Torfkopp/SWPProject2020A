package de.uol.swp.server.game;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages creation, deletion, and storing of games
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @since 2021-01-15
 */
public class GameManagement {

    private final Map<String, Game> games = new HashMap<>();

    /**
     * Creates a new game and adds it to the list
     *
     * @param lobby The game's lobby
     * @throws java.lang.IllegalArgumentException Lobby already has a game
     * @implNote The primary key of games is the lobby's name, therefore
     * only one game per lobby is possible
     * @since 2021-01-24
     */
    public void createGame(Lobby lobby, User first) throws IllegalArgumentException {
        if (games.containsKey(lobby.getName())) {
            throw new IllegalArgumentException("Game of lobby " + lobby.getName() + " already exists!");
        }
        games.put(lobby.getName(), new Game(lobby, first));
    }

    /**
     * Deletes a game with its lobby's name
     *
     * @param lobbyName The name of the lobby
     * @throws java.lang.IllegalArgumentException There is no game with the requested name
     * @since 2021-01-24
     */
    public void dropGame(String lobbyName) throws IllegalArgumentException {
        if (!games.containsKey(lobbyName)) {
            throw new IllegalArgumentException("Game of lobby " + lobbyName + " not found!");
        }
        games.remove(lobbyName);
    }

    /**
     * Searches for the game with the requested name
     *
     * @param lobbyName The name of the lobby
     * @return The requested game
     * @since 2021-01-24
     */
    public Game getGame(String lobbyName) {
        return games.get(lobbyName);
    }

    /**
     * Gets the map
     *
     * @return Map with the lobby's name and its Game object
     * @since 2021-01-24
     */
    public Map<String, Game> getGames() {
        return games;
    }

}

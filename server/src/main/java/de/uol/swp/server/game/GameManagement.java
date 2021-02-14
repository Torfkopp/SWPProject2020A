package de.uol.swp.server.game;

import de.uol.swp.common.game.Game;
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
public class GameManagement implements IGameManagement {

    private final Map<String, Game> games = new HashMap<>();

    @Override
    public void createGame(Lobby lobby, User first) throws IllegalArgumentException {
        if (games.containsKey(lobby.getName())) {
            throw new IllegalArgumentException("Game of lobby " + lobby.getName() + " already exists!");
        }
        games.put(lobby.getName(), new Game(lobby, first));
    }

    @Override
    public void dropGame(String lobbyName) throws IllegalArgumentException {
        if (!games.containsKey(lobbyName)) {
            throw new IllegalArgumentException("Game of lobby " + lobbyName + " not found!");
        }
        games.remove(lobbyName);
    }

    @Override
    public Game getGame(String lobbyName) {
        return games.get(lobbyName);
    }

    @Override
    public Map<String, Game> getGames() {
        return games;
    }
}

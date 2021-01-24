package de.uol.swp.server.game;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import de.uol.swp.server.game.map.GameMapManagement;
import de.uol.swp.server.game.map.IGameMapManagement;

/**
 * Class for a game
 *
 * @author Mario
 * @since 2021-01-21
 */
public class Game {

    private Lobby lobby;
    private Inventory[] inventories;
    private IGameMapManagement map;

    private int activePlayer;
    private User[] players;

    public Game(Lobby lobby, User first) {
        this.lobby = lobby;
        players = lobby.getUsers().toArray(new User[0]);
        for (int i = 0; i < players.length; i++) {
            if (players[i].equals(first)) {
                activePlayer = i;
                break;
            }
        }
        map = new GameMapManagement();
        inventories = new Inventory[players.length];
        int i = 0;
        for (User u : players) {
            inventories[i++] = new Inventory(u);
        }
    }

    /**
     * Gets the next player
     *
     * @return User object of the next player
     */
    public User nextPlayer() {
        activePlayer = (activePlayer + 1) % players.length;
        return players[activePlayer];
    }

    public Lobby getLobby() {
        return lobby;
    }

    public User[] getPlayers() { return players; }
}

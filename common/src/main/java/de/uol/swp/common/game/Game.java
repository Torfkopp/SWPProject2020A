package de.uol.swp.common.game;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.user.User;
import de.uol.swp.common.game.map.GameMapManagement;
import de.uol.swp.common.game.map.IGameMapManagement;

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

    public Game(Lobby lobby) {
        this.lobby = lobby;
        map = new GameMapManagement();
        int i = 0;
        for (User u : lobby.getUsers()) {
            inventories[i++] = new Inventory(u);
        }
    }

    public Lobby getLobby() {
        return lobby;
    }
}

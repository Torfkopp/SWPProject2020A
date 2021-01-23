package de.uol.swp.server.game;

import de.uol.swp.common.user.User;

/**
 * The game management class.
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @since 2021-01-15
 */
public class GameManagement extends AbstractGameManagement {

    private String lobby;
    private User[] players;
    private User ActivePlayer;
    private int turn = 0;

    /**
     * Constructor
     *
     * @since 2021-01-15
     */
    public GameManagement(String lobby) {
        this.lobby = lobby;
    }

    /**
     * Returns the next player
     *
     * @return User
     */
    @Override
    public User nextPlayer() {
        ActivePlayer = players[++turn % players.length];
        //ActivePlayer = new UserDTO("Geralt", "", "");
        return ActivePlayer;
    }

    /**
     * Gets the active player
     *
     * @return User
     */
    @Override
    public User getActivePlayer() {
        return ActivePlayer;
    }

    /**
     * Gets the lobby
     *
     * @return String The lobby
     */
    @Override
    public String getLobby() {
        return lobby;
    }

    private void setPlayers(User[] players) {
        this.players = players;
    }
}

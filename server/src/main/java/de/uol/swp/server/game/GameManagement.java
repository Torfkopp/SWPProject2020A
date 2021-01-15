package de.uol.swp.server.game;

import com.google.inject.Inject;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.chat.store.ChatMessageStore;

import java.util.List;

/**
 * The game management class.
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @since 2021-01-15
 */
public class GameManagement {

    private User[] players;
    private User ActivePlayer;
    private int turn;

    /**
     * Constructor
     *
     * @since 2021-01-15
     */
    @Inject
    public GameManagement() {
    }

    public GameManagement(List<User> players) {
        turn = 0;
        this.players = players.toArray(new User[0]);
        ActivePlayer = this.players[0];
    }

    /**
     * Returns the next player
     *
     * @return User
     */
    public User nextPlayer() {
        //ActivePlayer = players[++turn % players.length];
        ActivePlayer = new UserDTO("Geralt", "", "");
        return ActivePlayer;
    }

    /**
     * Gets the active player
     *
     * @return User
     */
    public User getActivePlayer() {
        return ActivePlayer;
    }
}

package de.uol.swp.common.game.message;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Message sent to declare a winner and end the game.
 *
 * @author Steven Luong
 * @author Finn Haase
 * @since 2021-03-22
 */
public class PlayerWonGameMessage extends AbstractGameMessage {

    /**
     * Constructor
     *
     * @param lobbyName The lobby this game is taking place in
     * @param user      The user that won the game.
     */
    public PlayerWonGameMessage(LobbyName lobbyName, UserOrDummy user) {
        super(lobbyName, user);
    }
}

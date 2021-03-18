package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * Message sent when a new game was created
 *
 * @author Mario Fokken
 * @since 2021-01-24
 */
public class GameCreatedMessage extends AbstractGameMessage {

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby a game was started in
     * @param first     The first player
     */
    public GameCreatedMessage(String lobbyName, User first) {
        super(lobbyName, first);
    }

    /**
     * Gets the first player
     *
     * @return The first player
     */
    public User getFirst() {
        return super.getUser();
    }
}

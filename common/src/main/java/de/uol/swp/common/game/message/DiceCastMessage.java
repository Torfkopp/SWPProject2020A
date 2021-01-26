package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * This message gets sent when the player
 * has done the mandatory part of a turn.
 *
 * @see de.uol.swp.common.game.message.AbstractGameMessage
 * @since 2021-01-15
 */
public class DiceCastMessage extends AbstractGameMessage {

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user      Active user
     */
    public DiceCastMessage(String lobbyName, User user) {
        super(lobbyName, user);
    }
}

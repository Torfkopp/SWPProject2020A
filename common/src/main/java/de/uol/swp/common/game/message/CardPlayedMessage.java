package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * This message gets sent when the player
 * wants to play a card.
 *
 * @author Mario Fokken
 * @author Eric Vuong
 * @see de.uol.swp.common.game.message.AbstractGameMessage
 * @since 2021-02-25
 */
abstract class CardPlayedMessage extends AbstractGameMessage {

    CardPlayedMessage(String lobbyName, User user) {
        super(lobbyName, user);
    }
}

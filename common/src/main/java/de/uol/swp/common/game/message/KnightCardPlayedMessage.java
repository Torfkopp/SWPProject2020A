package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * This message gets sent when the player
 * wants to play a KnightCard
 */
public class KnightCardPlayedMessage extends CardPlayedMessage {

    public KnightCardPlayedMessage(String lobbyName, User user) {
        super(lobbyName, user);
    }
}

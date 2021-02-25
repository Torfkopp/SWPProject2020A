package de.uol.swp.common.game.message.CardPlayedMessage;

import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.user.User;

/**
 * This message gets sent when the player
 * wants to play a YearOfPlentyCard
 */
public class YearOfPlentyCardPlayedMessage extends CardPlayedMessage {

    Resources resource1;
    Resources resource2;

    public YearOfPlentyCardPlayedMessage(String lobbyName, User user, Resources resource1, Resources resource2) {
        super(lobbyName, user);
        this.resource1 = resource1;
        this.resource2 = resource2;
    }

    public Resources getResource1() {
        return resource1;
    }

    public Resources getResource2() {
        return resource2;
    }
}

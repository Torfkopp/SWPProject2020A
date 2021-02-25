package de.uol.swp.common.game.message;

import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.user.User;

/**
 * This message gets sent when the player
 * wants to play a MonopolyCard
 */
public class MonopolyCardPlayedMessage extends CardPlayedMessage {

    Resources resource;

    public MonopolyCardPlayedMessage(String lobbyName, User user, Resources resource){
        super(lobbyName, user);
        this.resource = resource;
    }

    public Resources getResource() {
        return resource;
    }
}

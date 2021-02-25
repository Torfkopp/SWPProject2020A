package de.uol.swp.common.game.message;

import de.uol.swp.common.user.User;

/**
 * This message gets sent when the player
 * wants to play a RoadBuildingCard
 */
public class RoadBuildingCardPlayedMessage extends CardPlayedMessage {

    public RoadBuildingCardPlayedMessage(String lobbyName, User user){
        super(lobbyName, user);
    }
}

package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.user.User;

/**
 * This request gets sent when the player
 * wants to play a KnightCard
 */
public class PlayKnightCardRequest extends PlayCardRequest {

    public PlayKnightCardRequest(String originLobby, User user) {
        super(originLobby, user);
    }
}

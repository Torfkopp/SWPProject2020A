package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.user.User;

/**
 * This request gets sent when the player
 * wants to play a RoadBuildingCard
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
public class PlayRoadBuildingCardRequest extends PlayCardRequest {

    public PlayRoadBuildingCardRequest(String originLobby, User user) {
        super(originLobby, user);
    }
}

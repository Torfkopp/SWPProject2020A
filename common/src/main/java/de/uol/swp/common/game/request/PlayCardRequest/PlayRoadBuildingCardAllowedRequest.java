package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

/**
 * This request gets sent when the player
 * wants to play a RoadBuildingCard
 *
 * @author Alwin Bossert
 * @since 2021-05-16
 */
public class PlayRoadBuildingCardAllowedRequest extends AbstractPlayCardRequest {

    /**
     * Constructor.
     *
     * @param originLobby The origin lobby
     * @param user        The user
     */
    public PlayRoadBuildingCardAllowedRequest(LobbyName originLobby, User user) {
        super(originLobby, user);
    }
}

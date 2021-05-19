package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

/**
 * This request gets sent when the player
 * is allowed to play a RoadBuildingCard
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
public class PlayRoadBuildingCardRequest extends AbstractPlayCardRequest {

    /**
     * Constructor
     *
     * @param originLobby The lobby
     * @param user        The user
     */
    public PlayRoadBuildingCardRequest(LobbyName originLobby, User user) {
        super(originLobby, user);
    }
}

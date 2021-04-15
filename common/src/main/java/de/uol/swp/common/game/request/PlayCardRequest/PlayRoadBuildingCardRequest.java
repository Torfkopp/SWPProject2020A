package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.user.User;

import java.util.List;

/**
 * This request gets sent when the player
 * wants to play a RoadBuildingCard
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
public class PlayRoadBuildingCardRequest extends PlayCardRequest {

    private final List<MapPoint> roads;

    /**
     * Constructor
     *
     * @param originLobby The lobby
     * @param user        The user
     * @param roads       The road choice
     */
    public PlayRoadBuildingCardRequest(String originLobby, User user, List<MapPoint> roads) {
        super(originLobby, user);
        this.roads = roads;
    }

    public List<MapPoint> getRoads() {
        return roads;
    }
}

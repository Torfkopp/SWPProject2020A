package de.uol.swp.common.game.request;

import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.user.User;

public class BuildRequest extends AbstractGameRequest {
    private final User user;
    private final MapPoint mapPoint;

    public BuildRequest(String lobbyName, User user, MapPoint mapPoint) {
        super(lobbyName);
        this.user = user;
        this.mapPoint = mapPoint;
    }

    public User getUser() {
        return user;
    }

    public MapPoint getMapPoint() {
        return mapPoint;
    }
}

package de.uol.swp.common.game.message;

import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.user.UserOrDummy;

public class BuildingSuccessfulMessage extends AbstractGameMessage {
    MapPoint mapPoint;

    public enum Structure {
        ROAD,
        SETTLEMENT,
        CITY
    }

    public BuildingSuccessfulMessage(String lobbyName, UserOrDummy user, MapPoint mapPoint, Structure structure) {
        super(lobbyName, user);
        this.mapPoint = mapPoint;
    }

    public MapPoint getMapPoint() {
        return mapPoint;
    }
}

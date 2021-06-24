package de.uol.swp.common.game.request;

import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Request sent to the server, when the user wants to build something
 *
 * @author Aldin Dervisi
 * @author Temmo Junkhoff
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @since 2021-04-07
 */
public class BuildRequest extends AbstractGameRequest {

    private final Actor user;
    private final MapPoint mapPoint;

    /**
     * Constructor
     *
     * @param lobbyName The Lobbyname
     * @param user      The requesting user
     * @param mapPoint  The MapPoint at which should be build
     */
    public BuildRequest(LobbyName lobbyName, Actor user, MapPoint mapPoint) {
        super(lobbyName);
        this.user = user;
        this.mapPoint = mapPoint;
    }

    /**
     * Gets the mappoint
     *
     * @return The Mappoint
     */
    public MapPoint getMapPoint() {
        return mapPoint;
    }

    /**
     * Gets the requesting user
     *
     * @return The requesting user
     */
    public Actor getActor() {
        return user;
    }
}
package de.uol.swp.common.game.request;

import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.user.User;

/**
 * Request sent to the server, when the user wants to build something
 *
 * @author Aldin Dervisi
 * @author Temmo Junkhoff
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @since 2021-04-07
 */
public class BuildRequest extends AbstractGameRequest {

    private final User user;
    private final MapPoint mapPoint;

    /**
     * Constructor
     *
     * @param lobbyName The Lobbyname
     * @param user      The requesting user
     * @param mapPoint  The MapPoint at which should be build
     */
    public BuildRequest(String lobbyName, User user, MapPoint mapPoint) {
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
    public User getUser() {
        return user;
    }
}
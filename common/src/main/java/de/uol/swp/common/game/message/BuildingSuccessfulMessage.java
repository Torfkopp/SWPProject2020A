package de.uol.swp.common.game.message;

import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Sent to all clients in the lobby on a successful building attempt
 *
 * @author Aldin Dervisi
 * @author Temmo Junkhoff
 * @see de.uol.swp.common.game.message.AbstractGameMessage
 * @since 2021-04-07
 */
public class BuildingSuccessfulMessage extends AbstractGameMessage {

    private final MapPoint mapPoint;
    private final Type type;

    /**
     * An enum to indicate what was built
     */
    public enum Type {
        ROAD,
        SETTLEMENT,
        CITY
    }

    /**
     * Constructor
     *
     * @param lobbyName The lobbyname
     * @param user      The user that built something
     * @param mapPoint  The mappoint where something was built
     * @param type      The type of structure that was built
     */
    public BuildingSuccessfulMessage(LobbyName lobbyName, UserOrDummy user, MapPoint mapPoint, Type type) {
        super(lobbyName, user);
        this.mapPoint = mapPoint;
        this.type = type;
    }

    /**
     * Gets the mappoint at which something was built
     *
     * @return The mappoint at which something was built
     */
    public MapPoint getMapPoint() {
        return mapPoint;
    }

    /**
     * Gets the type of structure that was built
     *
     * @return The type of structure that was built
     */
    public Type getType() {
        return type;
    }
}

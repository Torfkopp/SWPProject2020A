package de.uol.swp.common.game.robber;

import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.message.AbstractGameMessage;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Message used to give the clients the robber's new position
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @since 2021-04-08
 */
public class RobberPositionMessage extends AbstractGameMessage {

    private final MapPoint position;

    /**
     * Constructor
     *
     * @param lobbyName The lobby's name
     * @param user      The user
     */
    public RobberPositionMessage(LobbyName lobbyName, UserOrDummy user, MapPoint position) {
        super(lobbyName, user);
        this.position = position;
    }

    /**
     * Gets the robber's position
     *
     * @return MapPoint position
     */
    public MapPoint getPosition() {
        return position;
    }
}

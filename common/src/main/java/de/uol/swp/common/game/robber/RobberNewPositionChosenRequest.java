package de.uol.swp.common.game.robber;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Answer to the RobberNewPositionResponse containing
 * the player's desired robber position
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.common.game.robber.RobberNewPositionResponse
 * @since 2021-04-05
 */
public class RobberNewPositionChosenRequest extends AbstractRequestMessage {

    private final LobbyName lobby;
    private final MapPoint position;
    private final User player;

    /**
     * Constructor
     *
     * @param lobby    The lobby's name
     * @param player   The player who's chosen the position
     * @param position The robber's new position
     */
    public RobberNewPositionChosenRequest(LobbyName lobby, User player, MapPoint position) {
        this.lobby = lobby;
        this.player = player;
        this.position = position;
    }

    /**
     * Gets the lobby's name
     *
     * @return String lobby
     */
    public LobbyName getLobby() {
        return lobby;
    }

    /**
     * Gets the player
     *
     * @return User player
     */
    public User getPlayer() {
        return player;
    }

    /**
     * Gets the position
     *
     * @return MapPoint robber's position
     */
    public MapPoint getPosition() {
        return position;
    }
}

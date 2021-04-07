package de.uol.swp.common.game.message.robber;

import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

/**
 * Answer to the RobberNewPositionResponse containing
 * the player's desired robber position
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.common.game.message.robber.RobberNewPositionResponse
 * @since 2021-04-05
 */
public class RobberNewPositionChosenRequest extends AbstractRequestMessage {

    private final String lobby;
    private final MapPoint position;
    private final User player;

    public RobberNewPositionChosenRequest(String lobby, User player, MapPoint position) {
        this.lobby = lobby;
        this.player = player;
        this.position = position;
    }

    public String getLobby() {
        return lobby;
    }

    public User getPlayer() {
        return player;
    }

    public MapPoint getPosition() {
        return position;
    }
}

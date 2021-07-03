package de.uol.swp.common.game.robber;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * Message sent to a client to ask where
 * the player wants to put the robber.
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.common.game.robber.RobberNewPositionChosenRequest
 * @since 2021-04-05
 */
public class RobberNewPositionResponse extends AbstractResponseMessage {

    private final LobbyName lobbyName;
    private final User player;

    /**
     * Constructor
     *
     * @param player The player to choose the robber's new position
     */
    public RobberNewPositionResponse(LobbyName lobbyName, User player) {
        this.lobbyName = lobbyName;
        this.player = player;
    }

    /**
     * Gets the Name of the Lobby in which the Robber has to be moved
     *
     * @return The Name of the Lobby in which the Robber has to be moved
     *
     * @author Phillip-André Suhr
     * @since 2021-07-01
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the User who has to move the Robber
     *
     * @return The User who has to move the Robber
     *
     * @author Phillip-André Suhr
     * @since 2021-07-01
     */
    public User getPlayer() {
        return player;
    }
}

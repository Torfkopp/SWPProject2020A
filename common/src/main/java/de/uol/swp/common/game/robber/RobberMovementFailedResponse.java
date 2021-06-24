package de.uol.swp.common.game.robber;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

/**
 * Response sent to the Client informing him that his attempt to move the robber failed
 *
 * @author Sven Ahrens
 * @since 2021-06-24
 */
public class RobberMovementFailedResponse extends AbstractResponseMessage {

    private final User player;
    private final LobbyName lobbyName;

    /**
     * Constructor
     *
     * @param player    The player who tried to move the Robber
     * @param lobbyName The lobby in which the player tried to move the Robber
     */
    public RobberMovementFailedResponse(User player, LobbyName lobbyName) {
        this.player = player;
        this.lobbyName = lobbyName;
    }

    /**
     * Gets the lobby
     *
     * @return lobbyName    The lobby in which the player tried to move the Robber
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the player
     *
     * @return User player
     */
    public User getPlayer() {
        return player;
    }
}

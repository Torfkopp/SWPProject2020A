package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Response sent by the server when a user wants to kick another user
 *
 * @author Maximilian Lindner
 * @author Sven Ahrens
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-03-02
 */
public class KickUserResponse extends AbstractLobbyResponse {

    private final Actor toBeKickedUser;

    /**
     * Constructor
     *
     * @param lobbyName      Name of the lobby
     * @param toBeKickedUser The user about to be kicked
     */
    public KickUserResponse(LobbyName lobbyName, Actor toBeKickedUser) {
        super(lobbyName);
        this.toBeKickedUser = toBeKickedUser;
    }

    /**
     * Gets the user about to be kicked
     *
     * @return The user about to be kicked
     */
    public Actor getToBeKickedUser() {
        return toBeKickedUser;
    }
}

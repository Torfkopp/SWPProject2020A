package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Request sent to the server when a user wants to kick another user
 *
 * @author Maximilian Lindner
 * @author Sven Ahrens
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-03-02
 */
public class KickUserRequest extends AbstractLobbyRequest {

    private final Actor toBeKickedUser;

    /**
     * Constructor
     *
     * @param name           Name of the lobby
     * @param user           The user who wants to kick someone
     * @param toBeKickedUser The user about to be kicked
     */
    public KickUserRequest(LobbyName name, Actor user, Actor toBeKickedUser) {
        super(name, user);
        this.toBeKickedUser = toBeKickedUser;
    }

    /**
     * Gets the to be kicked user´s name
     *
     * @return Name of the User
     */
    public Actor getToBeKickedUser() {
        return toBeKickedUser;
    }
}

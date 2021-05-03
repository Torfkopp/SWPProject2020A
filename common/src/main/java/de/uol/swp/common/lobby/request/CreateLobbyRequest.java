package de.uol.swp.common.lobby.request;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a user wants to create a new lobby
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2019-10-08
 */
public class CreateLobbyRequest extends AbstractLobbyRequest {

    /**
     * Constructor
     *
     * @param name  Name of the lobby
     * @param owner User trying to create the lobby
     *
     * @since 2019-10-08
     */
    public CreateLobbyRequest(LobbyName name, User owner) {
        super(name, owner);
    }

    /**
     * Gets the user variable
     *
     * @return User trying to create the lobby
     *
     * @since 2019-10-08
     */
    public User getOwner() {
        if (getUser() instanceof User) return (User) getUser();
        return null;
    }
}

package de.uol.swp.common.lobby.request;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Request sent to the server when a user wants to change the owner of a lobby
 *
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @since 2021-04-15
 */
public class ChangeOwnerRequest extends AbstractLobbyRequest {

    private final UserOrDummy newOwner;

    /**
     * Constructor
     *
     * @param name     Name of the lobby
     * @param user     Requesting user
     * @param newOwner New owner
     */
    public ChangeOwnerRequest(LobbyName name, UserOrDummy user, UserOrDummy newOwner) {
        super(name, user);
        this.newOwner = newOwner;
    }

    /**
     * Gets the new owner
     *
     * @return The new owner
     */
    public UserOrDummy getNewOwner() {
        return newOwner;
    }
}

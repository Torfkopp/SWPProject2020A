package de.uol.swp.common.lobby.request;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;

/**
 * Request sent to the server when a user wants to change the owner of a lobby
 *
 * @author Maximilian Lindner
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @since 2021-04-15
 */
public class ChangeOwnerRequest extends AbstractLobbyRequest {

    private final Actor newOwner;

    /**
     * Constructor
     *
     * @param name     Name of the lobby
     * @param user     Requesting user
     * @param newOwner New owner
     */
    public ChangeOwnerRequest(LobbyName name, Actor user, Actor newOwner) {
        super(name, user);
        this.newOwner = newOwner;
    }

    /**
     * Gets the new owner
     *
     * @return The new owner
     */
    public Actor getNewOwner() {
        return newOwner;
    }
}

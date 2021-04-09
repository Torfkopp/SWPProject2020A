package de.uol.swp.common.lobby.request;

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

    private final int maxPlayers;

    /**
     * Constructor
     *
     * @param name       Name of the lobby
     * @param owner      User trying to create the lobby
     * @param maxPlayers Maximum amount of players for the new lobby
     *
     * @since 2019-10-08
     */
    public CreateLobbyRequest(String name, User owner, int maxPlayers) {
        super(name, owner);
        this.maxPlayers = maxPlayers;
    }

    /**
     * Gets the maximum amount of players for the new lobby
     *
     * @return maximum amount of players
     */
    public int getMaxPlayers() {
        return maxPlayers;
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

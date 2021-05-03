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

    private final int maxPlayers;
    private final String password;

    /**
     * Constructor
     *
     * @param name       Name of the lobby
     * @param owner      User trying to create the lobby
     * @param password   Password of the lobby
     *
     * @since 2019-10-08
     */
    public CreateLobbyRequest(LobbyName name, User owner, String password) {
        super(name, owner);
        this.password = password;
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

    /**
     * Gets the password of the lobby
     *
     * @return Password of the lobby
     *
     * @author Alwin Bossert
     * @since 2021-04-22
     */
    public String getPassword() {
        return password;
    }
}

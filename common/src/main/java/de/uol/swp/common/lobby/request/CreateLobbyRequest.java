package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;

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
     * Default constructor
     *
     * @implNote This constructor is needed for serialisation
     * @since 2019-10-08
     */
    public CreateLobbyRequest() {
    }

    /**
     * Constructor
     *
     * @param name  Name of the lobby
     * @param owner User trying to create the lobby
     * @since 2019-10-08
     */
    public CreateLobbyRequest(String name, User owner) {
        super(name, owner);
    }

    /**
     * Gets the user variable
     *
     * @return User trying to create the lobby
     * @since 2019-10-08
     */
    public User getOwner() {
        return getUser();
    }

    /**
     * Sets the user variable
     *
     * @param owner User trying to create the lobby
     * @since 2019-10-08
     */
    public void setOwner(User owner) {
        setUser(owner);
    }
}

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
     * @implNote this constructor is needed for serialization
     * @since 2019-10-08
     */
    public CreateLobbyRequest() {
    }

    /**
     * Constructor
     *
     * @param name  name of the lobby
     * @param owner User trying to create the lobby
     * @since 2019-10-08
     */
    public CreateLobbyRequest(String name, UserDTO owner) {
        super(name, owner);
    }

    /**
     * Getter for the user variable
     *
     * @return User trying to create the lobby
     * @since 2019-10-08
     */
    public User getOwner() {
        return getUser();
    }

    /**
     * Setter for the user variable
     *
     * @param owner User trying to create the lobby
     * @since 2019-10-08
     */
    public void setOwner(UserDTO owner) {
        setUser(owner);
    }
}

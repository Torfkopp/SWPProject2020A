package de.uol.swp.common.lobby.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.UserDTO;

import java.util.Objects;

/**
 * Base class of all lobby request messages. Basic handling of lobby data.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @since 2019-10-08
 */
public class AbstractLobbyRequest extends AbstractRequestMessage {

    String name;
    UserDTO user;

    /**
     * Default constructor
     *
     * @implNote This constructor is needed for serialisation
     * @since 2019-10-08
     */
    public AbstractLobbyRequest() {
    }

    /**
     * Constructor
     *
     * @param name Name of the lobby
     * @param user User responsible for the creation of this message
     * @since 2019-10-08
     */
    public AbstractLobbyRequest(String name, UserDTO user) {
        this.name = name;
        this.user = user;
    }

    /**
     * Gets the name variable
     *
     * @return String containing the lobby's name
     * @since 2019-10-08
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name variable
     *
     * @param name String containing the lobby's name
     * @since 2019-10-08
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user variable
     *
     * @return User responsible for the creation of this message
     * @since 2019-10-08
     */
    public UserDTO getUser() {
        return user;
    }

    /**
     * Sets the user variable
     *
     * @param user User responsible for the creation of this message
     * @since 2019-10-08
     */
    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractLobbyRequest that = (AbstractLobbyRequest) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(user, that.user);
    }
}

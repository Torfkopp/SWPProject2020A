package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * A request sent from client to server to delete the current user
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @since 2020-11-02
 */
public class DeleteUserRequest extends AbstractRequestMessage {

    private static final long serialVersionUID = 7375314481654981321L;
    private final User toDelete;
    private final String password;

    /**
     * Constructor
     *
     * @param toDelete The user to delete
     * @param password
     *
     * @since 2020-11-02
     */
    public DeleteUserRequest(User toDelete, String password) {
        this.toDelete = toDelete;
        this.password = password;
    }

    @Override
    public boolean authorisationNeeded() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(toDelete);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteUserRequest that = (DeleteUserRequest) o;
        return Objects.equals(toDelete, that.toDelete);
    }

    /**
     * Gets the users password
     *
     * @return The users password
     *
     * @author Timo Gerken
     * @since 2021-04-19
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the user variable
     *
     * @return The user to delete
     *
     * @since 2020-11-02
     */
    public User getUser() {
        return toDelete;
    }
}
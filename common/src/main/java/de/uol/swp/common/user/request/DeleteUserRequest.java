package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

import java.util.Objects;

/**
 * A request sent from client to server to delete the current user
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.user.User
 * @since 2020-11-02
 */
public class DeleteUserRequest extends AbstractRequestMessage {

    private static final long serialVersionUID = 7375314481654981321L;
    final private User toDelete;

    /**
     * Constructor
     *
     * @param toDelete The user to delete
     * @since 2020-11-02
     */
    public DeleteUserRequest(User toDelete) {
        this.toDelete = toDelete;
    }

    @Override
    public boolean authorisationNeeded() {
        return true;
    }

    /**
     * Gets the user variable
     *
     * @return The user to delete
     * @since 2020-11-02
     */
    public User getUser() {
        return toDelete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeleteUserRequest that = (DeleteUserRequest) o;
        return Objects.equals(toDelete, that.toDelete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toDelete);
    }
}
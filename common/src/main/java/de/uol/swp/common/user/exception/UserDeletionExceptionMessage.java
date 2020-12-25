package de.uol.swp.common.user.exception;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;

/**
 * This exception is thrown if something went wrong during the deletion process,
 * e.g. the username doesn't exist.
 *
 * @author Phillip-André Suhr
 * @since 2020-11-02
 */
public class UserDeletionExceptionMessage extends AbstractResponseMessage {

    private final String message;

    /**
     * Constructor
     *
     * @param message String containing the reason why the deletion failed
     * @since 2020-11-02
     */
    public UserDeletionExceptionMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "DeletionExceptionMessage " + message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDeletionExceptionMessage that = (UserDeletionExceptionMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}
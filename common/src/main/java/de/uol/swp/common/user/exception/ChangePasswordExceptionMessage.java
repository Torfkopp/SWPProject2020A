package de.uol.swp.common.user.exception;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;

/**
 * This exception is thrown if something went wrong during the ChangePassword process.
 * e.g.: The old password is not correct and not verified.
 *
 * @author Steven Luong
 * @author Eric Vuong
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2020-12-03
 */
public class ChangePasswordExceptionMessage extends AbstractResponseMessage {

    private final String message;

    /**
     * Constructor
     *
     * @param message String containing the reason why the ChangePassword process failed
     * @since 2020-12-03
     */
    public ChangePasswordExceptionMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ChangePasswordExceptionMessage " + message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChangePasswordExceptionMessage that = (ChangePasswordExceptionMessage) o;
        return Objects.equals(message, that.message);
    }
}

package de.uol.swp.common.user.exception;

import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Objects;

/**
 * This exception is thrown if something went wrong during the ConfirmPassword process,
 * e.g. the old password is not correct and not verified.
 *
 * @author Alwin Bossert
 * @author Eric Vuong
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2021-03-16
 */
public class ConfirmPasswordExceptionMessage extends AbstractResponseMessage {

    private final String message;

    /**
     * Constructor
     *
     * @param message String containing the reason why the ConfirmPassword process failed
     *
     * @since 2021-03-16
     */
    public ConfirmPasswordExceptionMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ConfirmPasswordExceptionMessage: " + message;
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfirmPasswordExceptionMessage that = (ConfirmPasswordExceptionMessage) o;
        return Objects.equals(message, that.message);
    }
}

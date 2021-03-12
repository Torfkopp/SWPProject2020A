package de.uol.swp.common.user.exception;

import de.uol.swp.common.message.ExceptionMessage;

/**
 * This exception is thrown if something went wrong during the ChangePassword process,
 * e.g. the old password is not correct and not verified.
 *
 * @author Steven Luong
 * @author Eric Vuong
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2020-12-03
 */
public class ChangePasswordExceptionMessage extends ExceptionMessage {

    /**
     * Constructor
     *
     * @param message String containing the reason why the ChangePassword process failed
     *
     * @since 2020-12-03
     */
    public ChangePasswordExceptionMessage(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "ChangePasswordExceptionMessage";
    }

}

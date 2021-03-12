package de.uol.swp.common.user.exception;

import de.uol.swp.common.message.ExceptionMessage;

/**
 * This exception is thrown if something went wrong during the registration process,
 * e.g. the username is already taken.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2019-09-02
 */
public class RegistrationExceptionMessage extends ExceptionMessage {

    /**
     * Constructor
     *
     * @param message String containing the reason why the registration failed
     *
     * @since 2019-09-02
     */
    public RegistrationExceptionMessage(String message) {
        super(message);
    }
}

package de.uol.swp.common.user.exception;

import de.uol.swp.common.message.ExceptionMessage;

/**
 * This exception is thrown if something went wrong during the deletion process,
 * e.g. the username doesn't exist.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2020-11-02
 */
public class UserDeletionExceptionMessage extends ExceptionMessage {

    /**
     * Constructor
     *
     * @param message String containing the reason why the deletion failed
     *
     * @since 2020-11-02
     */
    public UserDeletionExceptionMessage(String message) {
        super(message);
    }
}

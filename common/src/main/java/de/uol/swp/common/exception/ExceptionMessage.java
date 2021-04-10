package de.uol.swp.common.exception;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * Encapsulates an exception in a message object
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2017-03-17
 */
public class ExceptionMessage extends AbstractResponseMessage {

    private static final long serialVersionUID = -7739395567707525535L;
    private final String exception;

    /**
     * Constructor
     *
     * @param message String containing the cause of the exception
     *
     * @since 2017-03-17
     */
    public ExceptionMessage(String message) {
        this.exception = message;
    }

    /**
     * Gets the exception message
     *
     * @return String containing the cause of the exception
     *
     * @since 2017-03-17
     */
    public String getException() {
        return exception;
    }

    @Override
    public String toString() {
        return exception;
    }
}

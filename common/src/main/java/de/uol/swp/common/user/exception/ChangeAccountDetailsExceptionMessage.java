package de.uol.swp.common.user.exception;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * This exception is thrown if something went wrong during the ChangeAccountDetails process,
 * e.g. the old password is not correct and not verified.
 *
 * @author Steven Luong
 * @author Eric Vuong
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2020-12-03
 */
public class ChangeAccountDetailsExceptionMessage extends AbstractResponseMessage {

    private final String message;

    /**
     * Constructor
     *
     * @param message String containing the reason why the ChangePassword process failed
     *
     * @since 2020-12-03
     */
    public ChangeAccountDetailsExceptionMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}

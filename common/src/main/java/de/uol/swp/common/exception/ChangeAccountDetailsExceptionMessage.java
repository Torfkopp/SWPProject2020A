package de.uol.swp.common.exception;

/**
 * This exception is thrown if something went wrong during the ChangeAccountDetails process,
 * e.g. the old password is not correct and not verified.
 *
 * @author Steven Luong
 * @author Eric Vuong
 * @see de.uol.swp.common.exception.ExceptionMessage
 * @since 2020-12-03
 */
public class ChangeAccountDetailsExceptionMessage extends ExceptionMessage {

    /**
     * Constructor
     *
     * @param message String containing the reason why the ChangePassword process failed
     *
     * @since 2020-12-03
     */
    public ChangeAccountDetailsExceptionMessage(String message) {
        super(message);
    }
}

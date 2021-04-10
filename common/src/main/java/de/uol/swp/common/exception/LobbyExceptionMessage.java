package de.uol.swp.common.exception;

/**
 * This class returns exceptions to the client caused by
 * various lobby related errors on the server's side.
 *
 * @author Marvin Drees
 * @see de.uol.swp.common.exception.ExceptionMessage
 * @since 2020-12-19
 */
public class LobbyExceptionMessage extends ExceptionMessage {

    /**
     * Constructor
     *
     * @param message Exception message returned by the server
     *
     * @since 2020-12-19
     */
    public LobbyExceptionMessage(String message) {
        super(message);
    }
}

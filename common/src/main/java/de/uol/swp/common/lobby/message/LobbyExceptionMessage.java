package de.uol.swp.common.lobby.message;

import de.uol.swp.common.message.ExceptionMessage;
import de.uol.swp.common.message.ResponseMessage;

/**
 * This class returns exceptions to the client caused by
 * various lobby related errors on the server's side.
 *
 * @author Marvin
 * @see de.uol.swp.common.message.ResponseMessage
 * @see de.uol.swp.common.message.ExceptionMessage
 * @since 2020-12-19
 */

public class LobbyExceptionMessage extends ExceptionMessage implements ResponseMessage {

    /**
     * Constructor
     *
     * @param message Exception message returned by the server
     * @since 2020-12-19
     */
    public LobbyExceptionMessage(String message) {
        super(message);
    }
}

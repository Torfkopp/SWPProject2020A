package de.uol.swp.common.lobby.message;

import de.uol.swp.common.message.ExceptionMessage;
import de.uol.swp.common.message.ResponseMessage;

/**
 * This class returns exceptions to the client caused by various
 * serverside lobby related errors
 *
 * @see de.uol.swp.common.message.ResponseMessage
 * @see de.uol.swp.common.message.ExceptionMessage
 * @author Marvin
 * @since 2020-12-19
 */

public class LobbyExceptionMessage extends ExceptionMessage implements ResponseMessage {

    public LobbyExceptionMessage(String message) {
        super(message);
    }

}

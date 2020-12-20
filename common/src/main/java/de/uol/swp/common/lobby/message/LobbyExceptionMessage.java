package de.uol.swp.common.lobby.message;

import de.uol.swp.common.message.ExceptionMessage;
import de.uol.swp.common.message.ResponseMessage;

/**
 * Message sent by the server when the user can't join a specific lobby
 */

public class LobbyExceptionMessage extends ExceptionMessage implements ResponseMessage {

    public LobbyExceptionMessage(String message) {
        super(message);
    }

}

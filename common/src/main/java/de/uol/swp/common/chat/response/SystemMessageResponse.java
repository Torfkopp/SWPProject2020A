package de.uol.swp.common.chat.response;

import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
import de.uol.swp.common.message.AbstractResponseMessage;

public class SystemMessageResponse extends AbstractResponseMessage {

    private final String lobbyName;
    private final SystemMessage msg;

    public SystemMessageResponse(String lobbyName, String content) {
        this.lobbyName = lobbyName;
        this.msg = new SystemMessageDTO(content);
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public SystemMessage getMsg() {
        return msg;
    }

    public boolean isLobbyChatMessage() {
        return lobbyName != null;
    }
}

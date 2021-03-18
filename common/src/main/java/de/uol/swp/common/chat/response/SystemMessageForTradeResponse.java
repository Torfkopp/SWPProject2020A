package de.uol.swp.common.chat.response;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
import de.uol.swp.common.message.AbstractResponseMessage;

public class SystemMessageForTradeResponse extends AbstractResponseMessage {

    private final String lobbyName;
    private final SystemMessage msg;
    private final String offeringUser;
    private final String respondingUser;

    public SystemMessageForTradeResponse(String lobbyName, String offeringUser, String respondingUser, I18nWrapper contentWrapper) {
        this.lobbyName = lobbyName;
        this.msg = new SystemMessageDTO(contentWrapper);
        this.offeringUser = offeringUser;
        this.respondingUser = respondingUser;
    }


    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the SystemMessage object.
     *
     * @return The encapsulated SystemMessage
     */
    public SystemMessage getMsg() {
        return msg;
    }

    /**
     * Check if the ChatMessage message is destined for a lobby chat
     *
     * @return True, if the SystemMessage message is meant for a lobby chat; False if not
     */
    public boolean isLobbyChatMessage() {
        return lobbyName != null;
    }
}

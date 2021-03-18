package de.uol.swp.common.chat.message;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
import de.uol.swp.common.message.AbstractServerMessage;

import java.util.Map;

public class SystemMessageForTradeMessage extends AbstractServerMessage {

    private final String lobbyName;
    private final String respondingUser;
    private final String offeringUser;
    private final Map<String, Integer> respondingResourceMap;
    private final Map<String, Integer> offeringResourceMap;
    private final SystemMessage msg;

    /**
     * Constructor
     * <p>
     * This constructor sets the ChatMessage message's isLobbyChatMessage and lobbyName
     * attributes to the parameters provided upon calling the constructor.
     *
     * @param lobbyName             The lobby name
     * @param respondingUser        The responding User
     * @param offeringUser          The offering User
     * @param respondingResourceMap The demanded resources
     * @param offeringResourceMap   The offered resources
     */
    public SystemMessageForTradeMessage(String lobbyName, String respondingUser, String offeringUser,
                                        Map<String, Integer> respondingResourceMap,
                                        Map<String, Integer> offeringResourceMap, I18nWrapper content) {
        this.lobbyName = lobbyName;
        this.respondingUser = respondingUser;
        this.offeringUser = offeringUser;
        this.respondingResourceMap = respondingResourceMap;
        this.offeringResourceMap = offeringResourceMap;
        this.msg = new SystemMessageDTO(content);
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

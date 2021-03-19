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
    private final Map<I18nWrapper, Integer> respondingResourceMap;
    private final Map<I18nWrapper, Integer> offeringResourceMap;

    /**
     * Constructor
     * <p>
     * This constructor sets the ChatMessage message's isLobbyChatMessage and lobbyName
     * attributes to the parameters provided upon calling the constructor.
     *
     * @param lobbyName             The lobby name
     * @param offeringUser          The offering User
     * @param respondingUser        The responding User
     * @param offeringResourceMap   The offered resources
     * @param respondingResourceMap The demanded resources
     */
    public SystemMessageForTradeMessage(String lobbyName, String offeringUser, String respondingUser,
                                        Map<I18nWrapper, Integer> offeringResourceMap,
                                        Map<I18nWrapper, Integer> respondingResourceMap) {
        this.lobbyName = lobbyName;
        this.offeringUser = offeringUser;
        this.respondingUser = respondingUser;
        this.respondingResourceMap = respondingResourceMap;
        this.offeringResourceMap = offeringResourceMap;
    }

    public String getRespondingUser() {
        return respondingUser;
    }

    public String getOfferingUser() {
        return offeringUser;
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
        return new SystemMessageDTO(
                makeSingularI18nWrapper(this.offeringUser, this.respondingUser, this.offeringResourceMap,
                                        this.respondingResourceMap));
    }

    /**
     * Check if the ChatMessage message is destined for a lobby chat
     *
     * @return True, if the SystemMessage message is meant for a lobby chat; False if not
     */
    public boolean isLobbyChatMessage() {
        return lobbyName != null;
    }

    private I18nWrapper makeSingularI18nWrapper(String offeringUser, String respondingUser,
                                                Map<I18nWrapper, Integer> offeringResourceMap,
                                                Map<I18nWrapper, Integer> respondingResourceMap) {
        StringBuilder offerString = new StringBuilder();
        for (Map.Entry<I18nWrapper, Integer> entry : offeringResourceMap.entrySet()) {
            offerString.append(", ");
            if (entry.getValue() > 0) offerString.append(entry.getValue()).append(" ");
            offerString.append(entry.getKey().toString());
        }
        StringBuilder demandString = new StringBuilder();
        for (Map.Entry<I18nWrapper, Integer> entry : respondingResourceMap.entrySet()) {
            demandString.append(", ");
            if (entry.getValue() > 0) demandString.append(entry.getValue()).append(" ");
            demandString.append(entry.getKey().toString());
        }
        return new I18nWrapper("lobby.trade.withuser.systemmessage", offeringUser, respondingUser,
                               offerString.toString().replaceFirst("^, ", ""),
                               demandString.toString().replaceFirst("^, ", ""));
    }
}

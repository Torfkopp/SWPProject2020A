package de.uol.swp.common.chat.message;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
import de.uol.swp.common.message.AbstractServerMessage;

import java.util.Map;

/**
 * Message sent by the server when a Trade between the bank and one User was successfully.
 *
 * @author Alwin Bossert
 * @author Sven Ahrens
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2021-03-23
 */

public class SystemMessageForTradeWithBankMessage extends AbstractServerMessage {

    private final String lobbyName;
    private final String user;
    private final Map<I18nWrapper, Integer> offeringResourceMap;

    /**
     * Constructor
     * <p>
     * This constructor sets the attributes to the parameters provided upon calling the constructor.
     *
     * @param user            The user, that traded with the bank
     * @param lobbyName       The lobby name
     * @param offeringResourceMap The offeringResourceMap, that the user bought a card successfully from the bank
     *                            /TODO Doku anpassen
     */
    public SystemMessageForTradeWithBankMessage(String user, String lobbyName, Map<I18nWrapper, Integer> offeringResourceMap) {
        this.lobbyName = lobbyName;
        this.user = user;
        this.offeringResourceMap=offeringResourceMap;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public SystemMessage getMsg() {
        return new SystemMessageDTO(makeSingularI18nWrapper(this.user, this.offeringResourceMap));
    }

    /**
     * Check if the ChatMessage message is destined for a lobby chat
     *
     * @return True, if the SystemMessage message is meant for a lobby chat; False if not
     */
    public boolean isLobbyChatMessage() {
        return lobbyName != null;
    }

    private I18nWrapper makeSingularI18nWrapper(String user, Map<I18nWrapper, Integer> offeringResourceMap) {
        StringBuilder offerString = new StringBuilder();
        for (Map.Entry<I18nWrapper, Integer> entry : offeringResourceMap.entrySet()) {
            offerString.append(", ");
            if (entry.getValue() > 0) offerString.append(entry.getValue()).append(" ");
            offerString.append(entry.getKey().toString());
        }
        return new I18nWrapper("lobby.trade.withbank.systemmessage", user, offerString.toString().replaceFirst("^, ", ""));
    }
}


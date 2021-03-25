package de.uol.swp.common.chat.message;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
import de.uol.swp.common.message.AbstractServerMessage;

import java.util.Map;

/**
 * Message sent by the server when a Trade between two Users was successfully.
 *
 * @author Alwin Bossert
 * @author Sven Ahrens
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2021-03-23
 */
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

    /**
     * Gets the responding User
     *
     * @return The responding User
     */
    public String getRespondingUser() {
        return respondingUser;
    }

    /**
     * Gets the offering User
     *
     * @return The offering User
     */
    public String getOfferingUser() {
        return offeringUser;
    }

    /**
     * Gets the lobbyName
     *
     * @return The lobbyName
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the SystemMessage object
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

    /**
     * Helper method to create a singular I18nWrapper from the resource maps
     *
     * @param offeringUser          The name of the offering user
     * @param respondingUser        The name of the responding user
     * @param offeringResourceMap   The Map of resources that were offered as a
     *                              Map of I18nWrappers to amount
     * @param respondingResourceMap The Map of resources that were demanded as
     *                              a Map of I18nWrappers to amount
     *
     * @return An I18nWrapper that contains all the details provided and will
     * be displayed in the client's chosen language
     */
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
        return new I18nWrapper("lobby.trade.resources.systemmessage", offeringUser, respondingUser,
                               offerString.toString().replaceFirst("^, ", ""),
                               demandString.toString().replaceFirst("^, ", ""));
    }
}

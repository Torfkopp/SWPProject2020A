package de.uol.swp.common.chat.message;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.LobbyName;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.user.UserOrDummy;

import java.util.Map;

/**
 * Message sent by the server when a trade between two Users was successful.
 *
 * @author Alwin Bossert
 * @author Sven Ahrens
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2021-03-23
 */
public class SystemMessageForTradeMessage extends AbstractLobbyMessage {

    private final UserOrDummy respondingUser;
    private final ResourceList respondingResourceMap;
    private final ResourceList offeringResourceMap;

    /**
     * Constructor
     *
     * @param lobbyName             The lobby name
     * @param offeringUser          The offering User
     * @param respondingUser        The responding User
     * @param offeringResourceMap   The offered resources
     * @param respondingResourceMap The demanded resources
     */
    public SystemMessageForTradeMessage(LobbyName lobbyName, UserOrDummy offeringUser, UserOrDummy respondingUser,
                                        ResourceList offeringResourceMap, ResourceList respondingResourceMap) {
        super(lobbyName, offeringUser);
        this.respondingUser = respondingUser;
        this.respondingResourceMap = respondingResourceMap;
        this.offeringResourceMap = offeringResourceMap;
    }

    /**
     * Gets the SystemMessage object
     *
     * @return The encapsulated SystemMessage
     */
    public SystemMessage getMsg() {
        return null;
        //respondingUser == null : bank

        //return new SystemMessageDTO(makeSingularI18nWrapper(getUser(), this.respondingUser, this.offeringResourceMap,
        //                                                    this.respondingResourceMap));
    }

    /**
     * Gets the responding User
     *
     * @return The responding User
     */
    public UserOrDummy getRespondingUser() {
        return respondingUser;
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
    private I18nWrapper makeSingularI18nWrapper(UserOrDummy offeringUser, String respondingUser,
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
        return new I18nWrapper("lobby.trade.resources.systemmessage", offeringUser.getUsername(), respondingUser,
                               offerString.toString().replaceFirst("^, ", ""),
                               demandString.toString().replaceFirst("^, ", ""));
    }
}

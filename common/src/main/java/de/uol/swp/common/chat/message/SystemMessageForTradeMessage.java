package de.uol.swp.common.chat.message;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.InGameSystemMessageDTO;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.IResource;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.IResourceList;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.user.UserOrDummy;

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
    private final IResourceList respondingResourceMap;
    private final IResourceList offeringResourceMap;

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
                                        IResourceList offeringResourceMap, IResourceList respondingResourceMap) {
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
        return new InGameSystemMessageDTO(makeSingularI18nWrapper(getUser(), this.respondingUser == null ? "bank" :
                                                                             this.respondingUser.getUsername(),
                                                                  this.offeringResourceMap,
                                                                  this.respondingResourceMap));
    }

    /**
     * Helper method to transform a resource list into the corresponding string.
     *
     * @param resourceMap The resource list containing the traded resources
     *
     * @return The string containing the traded resources
     *
     * @author Marvin Drees
     * @since 2021-05-11
     */
    private String buildTradeString(IResourceList resourceMap) {
        StringBuilder tradeString = new StringBuilder();
        for (IResource entry : resourceMap) {
            if (entry.getAmount() > 0) {
                tradeString.append(", ");
                tradeString.append(entry.getAmount()).append(" ");
                tradeString.append(entry.getType().toString());
            }
        }
        return tradeString.toString().replaceFirst("^, ", "");
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
                                                IResourceList offeringResourceMap,
                                                IResourceList respondingResourceMap) {
        String offerString = buildTradeString(offeringResourceMap);
        String demandString = buildTradeString(respondingResourceMap);
        return new I18nWrapper("lobby.trade.resources.systemmessage", offeringUser.getUsername(), respondingUser,
                               offerString, demandString);
    }
}

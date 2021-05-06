package de.uol.swp.common.chat.response;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.InGameSystemMessageDTO;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

/**
 * Response sent by the server when a Trade between the bank and one User was successful.
 *
 * @author Alwin Bossert
 * @author Sven Ahrens
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2021-03-23
 */
public class SystemMessageForTradeWithBankResponse extends AbstractLobbyResponse {

    private final SystemMessage msg;

    /**
     * Constructor
     *
     * @param lobbyName       The lobby name
     * @param developmentCard The developmentCard that the user bought
     */
    public SystemMessageForTradeWithBankResponse(LobbyName lobbyName, DevelopmentCardType developmentCard) {
        super(lobbyName);
        this.msg = new InGameSystemMessageDTO(new I18nWrapper("lobby.trade.withbank.systemresponse", developmentCard));
    }

    /**
     * Gets the SystemMessage object
     *
     * @return The encapsulated SystemMessage
     */
    public SystemMessage getMsg() {
        return msg;
    }
}

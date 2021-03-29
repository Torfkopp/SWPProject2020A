package de.uol.swp.common.chat.response;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
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

    private final SystemMessageDTO msg;

    /**
     * Constructor
     *
     * @param lobbyName       The lobby name
     * @param developmentCard The developmentCard that the user bought
     */
    public SystemMessageForTradeWithBankResponse(String lobbyName, String developmentCard) {
        super(lobbyName);
        this.msg = new SystemMessageDTO(
                new I18nWrapper("lobby.trade.withbank.systemresponse", new I18nWrapper(developmentCard)));
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
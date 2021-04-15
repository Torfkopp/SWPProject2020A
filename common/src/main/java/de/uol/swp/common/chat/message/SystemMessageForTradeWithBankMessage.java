package de.uol.swp.common.chat.message;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.LobbyName;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Message sent by the server when a Trade between the bank and one User was successful.
 *
 * @author Alwin Bossert
 * @author Sven Ahrens
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2021-03-23
 */
public class SystemMessageForTradeWithBankMessage extends AbstractLobbyMessage {

    /**
     * Constructor
     *
     * @param lobbyName The lobby name
     * @param user      The user that traded with the bank
     */
    public SystemMessageForTradeWithBankMessage(LobbyName lobbyName, UserOrDummy user) {
        super(lobbyName, user);
    }

    /**
     * Gets the SystemMessage object.
     *
     * @return The encapsulated SystemMessage
     */
    public SystemMessage getMsg() {
        return new SystemMessageDTO(new I18nWrapper("lobby.trade.withbank.systemmessage", getUser().getUsername()));
    }
}

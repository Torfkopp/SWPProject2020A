package de.uol.swp.common.chat.message;

import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.message.AbstractLobbyMessage;

/**
 * Message sent by the server when a Card was played successfully.
 *
 * @author Alwin Bossert
 * @author Sven Ahrens
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2021-03-23
 */
public class SystemMessageMessage extends AbstractLobbyMessage {

    private final SystemMessage msg;

    /**
     * Constructor
     *
     * @param lobbyName     The lobby in which the card is played
     * @param systemMessage The SystemMessage that should be show to the players
     */
    public SystemMessageMessage(LobbyName lobbyName, SystemMessage systemMessage) {
        super(lobbyName, null);
        this.msg = systemMessage;
    }

    /**
     * Gets the SystemMessage object.
     *
     * @return The encapsulated SystemMessage
     */
    public SystemMessage getMsg() {
        return msg;
    }
}

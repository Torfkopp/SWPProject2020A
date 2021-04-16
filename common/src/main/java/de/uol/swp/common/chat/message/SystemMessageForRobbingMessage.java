package de.uol.swp.common.chat.message;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Message sent by the server when a player robs
 * a resource card by moving the robber
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2021-04-07
 */
public class SystemMessageForRobbingMessage extends AbstractLobbyMessage {

    private final UserOrDummy victim;

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param robber    User getting the card
     * @param victim    User losing the card
     */
    public SystemMessageForRobbingMessage(String lobbyName, UserOrDummy robber, UserOrDummy victim) {
        super(lobbyName, robber);
        this.victim = victim;
    }

    public SystemMessage getMsg() {
        return new SystemMessageDTO(new I18nWrapper("game.robber.rob", getUser(), victim));
    }

    public UserOrDummy getVictim() {
        return victim;
    }
}
package de.uol.swp.common.game.robber;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.InGameSystemMessageDTO;
import de.uol.swp.common.lobby.LobbyName;
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
public class RobbingMessage extends AbstractLobbyMessage {

    private final UserOrDummy victim;

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param robber    User getting the card
     * @param victim    User losing the card
     */
    public RobbingMessage(LobbyName lobbyName, UserOrDummy robber, UserOrDummy victim) {
        super(lobbyName, robber);
        this.victim = victim;
    }

    /**
     * Gets the InGameSystemMessage object.
     *
     * @return The encapsulated InGameSystemMessage
     */
    public SystemMessage getMsg() {
        return new InGameSystemMessageDTO(new I18nWrapper("game.robber.rob", getUser(), victim));
    }

    /**
     * Gets the User losing the card
     *
     * @return User losing the card
     */
    public UserOrDummy getVictim() {
        return victim;
    }
}

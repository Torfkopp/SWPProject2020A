package de.uol.swp.common.chat.message;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.LobbyName;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.InGameSystemMessageDTO;
import de.uol.swp.common.lobby.message.AbstractLobbyMessage;
import de.uol.swp.common.user.User;

/**
 * Message sent by the server when a Card was played successfully.
 *
 * @author Alwin Bossert
 * @author Sven Ahrens
 * @see de.uol.swp.common.message.AbstractServerMessage
 * @since 2021-03-23
 */
public class SystemMessageForPlayingCardsMessage extends AbstractLobbyMessage {

    private final SystemMessage msg;

    /**
     * Constructor
     *
     * @param lobbyName   The lobby in which the card is played
     * @param user        The User who played the card
     * @param playingCard The card that is about to be played
     */
    public SystemMessageForPlayingCardsMessage(LobbyName lobbyName, User user, I18nWrapper playingCard) {
        super(lobbyName, user);
        this.msg = new InGameSystemMessageDTO(new I18nWrapper("lobby.play.card.systemmessage", user, playingCard));
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

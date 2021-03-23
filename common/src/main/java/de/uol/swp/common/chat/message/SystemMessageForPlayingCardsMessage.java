package de.uol.swp.common.chat.message;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
import de.uol.swp.common.message.AbstractServerMessage;

public class SystemMessageForPlayingCardsMessage extends AbstractServerMessage {

    private final String lobbyName;
    private final String user;
    private final I18nWrapper playingCard;

    /**
     * Constructor
     * <p>
     * This constructor sets the ChatMessage message's isLobbyChatMessage and lobbyName
     * attributes to the parameters provided upon calling the constructor.
     *
     * @param lobbyName   The lobby name
     * @param user        The User
     * @param playingCard The card, that is about to be played
     */
    public SystemMessageForPlayingCardsMessage(String lobbyName, String user, I18nWrapper playingCard) {
        this.lobbyName = lobbyName;
        this.user = user;
        this.playingCard = playingCard;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public String getUser() {
        return user;
    }

    public I18nWrapper getPlayingCard() {
        return playingCard;
    }

    /**
     * Gets the SystemMessage object.
     *
     * @return The encapsulated SystemMessage
     */
    public SystemMessage getMsg() {
        return new SystemMessageDTO(makeSingularI18nWrapper(user, playingCard));
    }

    /**
     * Check if the ChatMessage message is destined for a lobby chat
     *
     * @return True, if the SystemMessage message is meant for a lobby chat; False if not
     */
    public boolean isLobbyChatMessage() {
        return lobbyName != null;
    }

    private I18nWrapper makeSingularI18nWrapper(String user, I18nWrapper playingCard) {
        StringBuilder playingString = new StringBuilder();
        //playingString.append(user);
        //playingString.append(playingCard);

        return new I18nWrapper("lobby.play.card.systemmessage", user, playingCard);
    }
}

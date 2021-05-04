package de.uol.swp.common.game.message;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourceThingies.uniqueCards.UniqueCard;
import de.uol.swp.common.message.AbstractServerMessage;

import java.util.List;

/**
 * Message send to update the UniqueCardsList
 *
 * @author Eric Vuong
 * @author Temmo Junkhoff
 * @since 2021-04-10
 */
public class UpdateUniqueCardsListMessage extends AbstractServerMessage {

    private final LobbyName lobbyName;
    private final List<UniqueCard> uniqueCardsList;

    /**
     * Constructor
     *
     * @param lobbyName       The name of the lobby
     * @param uniqueCardsList The list of unique cards
     */
    public UpdateUniqueCardsListMessage(LobbyName lobbyName, List<UniqueCard> uniqueCardsList) {
        this.lobbyName = lobbyName;
        this.uniqueCardsList = uniqueCardsList;
    }

    /**
     * Gets the lobbyname
     *
     * @return The lobbyname
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the unique cards list
     *
     * @return The list of unique cards
     */
    public List<UniqueCard> getUniqueCardsList() {
        return uniqueCardsList;
    }
}

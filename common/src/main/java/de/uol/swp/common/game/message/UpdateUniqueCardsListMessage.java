package de.uol.swp.common.game.message;

import de.uol.swp.common.message.AbstractServerMessage;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.util.Triple;

import java.util.List;

/**
 * Message send to update the UniqueCardsList
 *
 * @author Eric Vuong
 * @author Temmo Junkhoff
 * @since 2021-04-10
 */
public class UpdateUniqueCardsListMessage extends AbstractServerMessage {

    private final String lobbyName;
    private final List<Triple<String, UserOrDummy, Integer>> uniqueCardsList;

    /**
     * Constructor
     *
     * @param lobbyName       The name of the lobby
     * @param uniqueCardsList The list of unique cards
     */
    public UpdateUniqueCardsListMessage(String lobbyName, List<Triple<String, UserOrDummy, Integer>> uniqueCardsList) {
        this.lobbyName = lobbyName;
        this.uniqueCardsList = uniqueCardsList;
    }

    /**
     * Gets the lobbyname
     *
     * @return The lobbyname
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the unique cards list
     *
     * @return The list of unique cards
     */
    public List<Triple<String, UserOrDummy, Integer>> getUniqueCardsList() {
        return uniqueCardsList;
    }
}

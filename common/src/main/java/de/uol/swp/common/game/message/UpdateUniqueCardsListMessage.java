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
 *
 */
public class UpdateUniqueCardsListMessage extends AbstractServerMessage {

    private final String lobbyName;
    private final List<Triple<String, UserOrDummy, Integer>> uniqueCardsList;

    public List<Triple<String, UserOrDummy, Integer>> getUniqueCardsList() {
        return uniqueCardsList;
    }

    public UpdateUniqueCardsListMessage(String lobbyName, List<Triple<String, UserOrDummy, Integer>> uniqueCardsList) {
        this.lobbyName = lobbyName;
        this.uniqueCardsList = uniqueCardsList;
    }

    public String getLobbyName() {
        return lobbyName;
    }
}

package de.uol.swp.common.game.message;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.AbstractServerMessage;
import de.uol.swp.common.user.UserOrDummy;

import java.util.Map;

/**
 * Message send to update the UniqueCardsList
 *
 * @author Steven Luong
 * @since 2021-05-21
 */
public class UpdateVictoryPointsMessage extends AbstractServerMessage {

    private final LobbyName lobbyName;
    private final Map<UserOrDummy, Integer> victoryPointMap;

    /**
     * Constructor
     *
     * @param lobbyName       The name of the lobby
     * @param victoryPointMap The Map of the UserOrDummy and it's Victory Points
     */
    public UpdateVictoryPointsMessage(LobbyName lobbyName, Map<UserOrDummy, Integer> victoryPointMap) {
        this.lobbyName = lobbyName;
        this.victoryPointMap = victoryPointMap;
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
     * Gets the Map of the UserOrDummy and it's Victory Points
     *
     * @return the Map UserOrDummy and it's Victory Points
     */
    public Map<UserOrDummy, Integer> getVictoryPointMap() {
        return victoryPointMap;
    }
}
package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Map;

public class RemoveFromLobbiesResponse extends AbstractResponseMessage {

    private final Map<String, Lobby> lobbiesWithUser;

    /**
     * Constructor
     *
     * @param lobbiesWithUser The Map with the Lobbies
     * @author Finn Haase
     * @author Aldin Dervisi
     * @since 2020-01-28
     **/
    public RemoveFromLobbiesResponse(Map<String, Lobby> lobbiesWithUser) {
        this.lobbiesWithUser = lobbiesWithUser;
    }

    /**
     * Gets the Map of the Lobbies
     *
     * @return Map getLobbiesWithUser
     * @since 2020-01-28
     */
    public Map<String, Lobby> getLobbiesWithUser() {
        return lobbiesWithUser;
    }
}

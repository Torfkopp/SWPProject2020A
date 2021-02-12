package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.message.AbstractResponseMessage;

import java.util.Map;

/**
 * Response sent by the server when a user wants to be removed from all lobbies
 *
 * @author Aldin Dervisi
 * @author Finn Haase
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.lobby.request.RemoveFromLobbiesRequest
 * @since 2020-01-28
 */
public class RemoveFromLobbiesResponse extends AbstractResponseMessage {

    private final Map<String, Lobby> lobbiesWithUser;

    /**
     * Constructor
     *
     * @param lobbiesWithUser The Map with the Lobbies
     **/
    public RemoveFromLobbiesResponse(Map<String, Lobby> lobbiesWithUser) {
        this.lobbiesWithUser = lobbiesWithUser;
    }

    /**
     * Gets the Map of the Lobbies
     *
     * @return Map getLobbiesWithUser
     */
    public Map<String, Lobby> getLobbiesWithUser() {
        return lobbiesWithUser;
    }
}

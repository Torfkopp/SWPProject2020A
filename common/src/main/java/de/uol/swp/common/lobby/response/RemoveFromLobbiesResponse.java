package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
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

    private final Map<LobbyName, ISimpleLobby> lobbiesWithUser;

    /**
     * Constructor
     *
     * @param lobbiesWithUser The Map with the Lobbies
     **/
    public RemoveFromLobbiesResponse(Map<LobbyName, ISimpleLobby> lobbiesWithUser) {
        this.lobbiesWithUser = lobbiesWithUser;
    }

    /**
     * Gets the Map of the Lobbies
     *
     * @return Map getLobbiesWithUser
     */
    public Map<LobbyName, ISimpleLobby> getLobbiesWithUser() {
        return lobbiesWithUser;
    }
}

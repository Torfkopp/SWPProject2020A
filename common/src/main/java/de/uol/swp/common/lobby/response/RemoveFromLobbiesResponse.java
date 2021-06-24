package de.uol.swp.common.lobby.response;

import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.specialisedUtil.SimpleLobbyMap;

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

    private final SimpleLobbyMap lobbiesWithUser;

    /**
     * Constructor
     *
     * @param lobbiesWithUser The Map with the Lobbies
     **/
    public RemoveFromLobbiesResponse(SimpleLobbyMap lobbiesWithUser) {
        this.lobbiesWithUser = lobbiesWithUser;
    }

    /**
     * Gets the Map of the Lobbies
     *
     * @return Map getLobbiesWithUser
     */
    public SimpleLobbyMap getLobbiesWithUser() {
        return lobbiesWithUser;
    }
}

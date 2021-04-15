package de.uol.swp.common.game.request;

import de.uol.swp.common.LobbyName;

/**
 * Request sent to the server to request an update for the game map
 *
 * @author Aldin Dervisi
 * @author Temmo Junkhoff
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @since 2021-04-08
 */
public class UpdateGameMapRequest extends AbstractGameRequest {

    /**
     * Constructor
     *
     * @param originLobby The Origin lobby
     */
    public UpdateGameMapRequest(LobbyName originLobby) {
        super(originLobby);
    }
}
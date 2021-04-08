package de.uol.swp.common.game.response;

import de.uol.swp.common.game.map.IGameMap;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

/**
 * Sent to the client to update the gamemap
 *
 * @author Aldin Dervisi
 * @author Temmo Junkhoff
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-04-08
 */
public class UpdateGameMapResponse extends AbstractLobbyResponse {

    private final IGameMap gameMapDTO;

    /**
     * Constructor
     *
     * @param lobbyName  The lobbyname
     * @param gameMapDTO The new gamemap
     */
    public UpdateGameMapResponse(String lobbyName, IGameMap gameMapDTO) {
        super(lobbyName);
        this.gameMapDTO = gameMapDTO;
    }

    /**
     * Gets the new gamemap
     *
     * @return
     */
    public IGameMap getGameMapDTO() {
        return gameMapDTO;
    }
}

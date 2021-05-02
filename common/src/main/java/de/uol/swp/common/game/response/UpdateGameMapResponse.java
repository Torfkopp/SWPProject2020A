package de.uol.swp.common.game.response;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.map.IGameMap;
import de.uol.swp.common.game.map.gamemapDTO.IGameMap;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

/**
 * Sent to the client to update the game map
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
     * @param lobbyName  The name of the lobby
     * @param gameMapDTO The new game map
     */
    public UpdateGameMapResponse(LobbyName lobbyName, IGameMap gameMapDTO) {
        super(lobbyName);
        this.gameMapDTO = gameMapDTO;
    }

    /**
     * Gets the new game map
     *
     * @return The new game map
     */
    public IGameMap getGameMapDTO() {
        return gameMapDTO;
    }
}

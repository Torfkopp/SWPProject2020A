package de.uol.swp.common.game.response;

import de.uol.swp.common.game.map.IGameMapDTO;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;

public class UpdateGameMapResponse extends AbstractLobbyResponse {

    private final IGameMapDTO gameMapDTO;

    public UpdateGameMapResponse(String lobbyName, IGameMapDTO gameMapDTO) {
        super(lobbyName);
        this.gameMapDTO = gameMapDTO;
    }

    public IGameMapDTO getGameMapDTO() {
        return gameMapDTO;
    }
}

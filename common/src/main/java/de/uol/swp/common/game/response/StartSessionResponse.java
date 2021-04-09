package de.uol.swp.common.game.response;

import de.uol.swp.common.game.map.configuration.IConfiguration;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.UserOrDummy;

public class StartSessionResponse extends AbstractLobbyResponse {
    private final UserOrDummy player;
    private final IConfiguration configuration;

    public StartSessionResponse(String lobbyName, UserOrDummy player, IConfiguration configuration) {
        super(lobbyName);
        this.player = player;
        this.configuration = configuration;
    }

    public UserOrDummy getPlayer() {
        return player;
    }

    public IConfiguration getConfiguration() {
        return configuration;
    }
}

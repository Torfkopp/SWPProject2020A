package de.uol.swp.common.lobby.response;

public class JoinLobbyResponse extends AbstractLobbyResponse {

    /**
     * Constructor
     *
     * @param lobbyName The name for the new lobby
     *
     * @since 2020-12-21
     **/
    public JoinLobbyResponse(String lobbyName) {
        super(lobbyName);
    }
}

package de.uol.swp.common.lobby.response;

public class CreateLobbyResponse extends AbstractLobbyResponse {

    /**
     * Constructor
     *
     * @param lobbyName The name for the new lobby
     *
     * @since 2020-12-21
     **/
    public CreateLobbyResponse(String lobbyName) {
        super(lobbyName);
    }
}

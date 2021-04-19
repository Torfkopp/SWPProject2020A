package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;

public class JoinLobbyWithPasswordResponse extends AbstractLobbyResponse{

    private final Lobby lobby;

    /**
     * Constructor
     *
     * @param lobbyName The name for the new lobby
     * @param lobby     The object of the joined lobby
     **/
    public JoinLobbyWithPasswordResponse(String lobbyName, Lobby lobby) {
        super(lobbyName);
        this.lobby = lobby;
    }

    /**
     * Gets the lobby where the user joined.
     *
     * @return The lobby the user joined
     *
     * @author Maximilian Lindner
     * @author AldinDervisi
     * @since 2021-03-14
     */
    public Lobby getLobby() {
        return lobby;
    }
}

package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;

public class CreateLobbyWithPasswordResponse extends AbstractLobbyResponse{

    private final Lobby lobby;
    private final String password;

    /**
     * Constructor
     *
     * @param lobbyName The name for the new lobby
     * @param lobby     The object of the created lobby
     **/
    public CreateLobbyWithPasswordResponse(String lobbyName, Lobby lobby, String password) {
        super(lobbyName);
        this.lobby = lobby;
        this.password = password;
    }

    /**
     * Gets the created lobby
     *
     * @return The created lobby
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @since 2021-03-15
     */
    public Lobby getLobby() {
        return lobby;
    }

    public String getPassword(){return password;}
}

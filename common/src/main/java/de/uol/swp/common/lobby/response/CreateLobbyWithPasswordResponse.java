package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.Lobby;

/**
 * Response sent by the server to a user who created a lobby with a password
 *
 * @author Alwin Bossert
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @see de.uol.swp.common.lobby.request.CreateLobbyRequest
 * @since 2021-04-22
 */
public class CreateLobbyWithPasswordResponse extends AbstractLobbyResponse {

    private final Lobby lobby;
    private final String password;

    /**
     * Constructor
     *
     * @param lobbyName The name for the new lobby
     * @param lobby     The object of the created lobby
     * @param password  The password for the new lobby
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
     */
    public Lobby getLobby() {
        return lobby;
    }

    /**
     * Gets the password for the new lobby
     *
     * @return The new password
     */
    public String getPassword() {return password;}
}

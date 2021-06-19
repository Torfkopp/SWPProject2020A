package de.uol.swp.common.lobby.response;

import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;

/**
 * Response sent by the server to a user who created a lobby
 *
 * @author Alwin Bossert
 * @author Steven Luong
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @see de.uol.swp.common.lobby.request.CreateLobbyRequest
 * @since 2020-12-21
 */
public class CreateLobbyResponse extends AbstractLobbyResponse {

    private final ISimpleLobby lobby;
    private final String password;

    /**
     * Constructor
     *
     * @param lobbyName The name for the new lobby
     * @param lobby     The object of the created lobby
     * @param password  The password og the created lobby
     **/
    public CreateLobbyResponse(LobbyName lobbyName, ISimpleLobby lobby, String password) {
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
    public ISimpleLobby getLobby() {
        return lobby;
    }

    /**
     * Gets the password of the created lobby
     *
     * @return The password of the lobby
     *
     * @author Alwin Bossert
     * @since 2021-06-19
     */
    public String getPassword() {
        return password;
    }
}

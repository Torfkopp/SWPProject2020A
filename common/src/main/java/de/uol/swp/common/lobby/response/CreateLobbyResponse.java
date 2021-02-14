package de.uol.swp.common.lobby.response;

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

    /**
     * Constructor
     *
     * @param lobbyName The name for the new lobby
     **/
    public CreateLobbyResponse(String lobbyName) {
        super(lobbyName);
    }
}

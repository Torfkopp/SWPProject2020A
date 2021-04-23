package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a user wants to join a lobby with a password
 *
 * @author Alwin Bossert
 * @see de.uol.swp.common.lobby.request.AbstractLobbyRequest
 * @see de.uol.swp.common.user.User
 * @since 2021-04-22
 */
public class JoinLobbyWithPasswordConfirmationRequest extends AbstractLobbyRequest {

    private String password;

    /**
     * Constructor
     *
     * @param lobbyName Name of the lobby
     * @param user      User who wants to join the lobby
     * @param password  Password of the lobby
     *
     * @since 2021-04-22
     */
    public JoinLobbyWithPasswordConfirmationRequest(String lobbyName, User user, String password) {
        super(lobbyName, user);
        this.password = password;
    }

    /**
     * Gets the password of the lobby
     *
     * @return password of the lobby
     *
     * @since 2021-04-22
     */
    public String getPassword() {
        return password;
    }
}

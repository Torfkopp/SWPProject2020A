package de.uol.swp.common.lobby.request;

import de.uol.swp.common.user.User;

public class JoinLobbyWithPasswordConfirmationRequest extends AbstractLobbyRequest {

    private String password;

    public JoinLobbyWithPasswordConfirmationRequest(String lobbyName, User user, String password) {
        super(lobbyName, user);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}

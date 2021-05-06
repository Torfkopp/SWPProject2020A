package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.User;

/**
 * This response is sent when a card
 * was successfully played
 *
 * @author Mario Fokken
 * @since 2021-03-02
 */
public class PlayCardSuccessResponse extends AbstractLobbyResponse {

    private final User user;

    /**
     * Constructor.
     *
     * @param lobbyName The lobby name
     * @param user      The user
     */
    public PlayCardSuccessResponse(LobbyName lobbyName, User user) {
        super(lobbyName);
        this.user = user;
    }

    /**
     * Gets the user.
     *
     * @return The user
     */
    public User getUser() {
        return user;
    }
}

package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.User;

/**
 * This response is sent when a card
 * could not be played
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
public class PlayCardFailureResponse extends AbstractLobbyResponse {

    User user;
    Reasons reason;

    public enum Reasons {
        NO_CARDS,
        TOO_MANY_RESOURCES
    }

    public PlayCardFailureResponse(String lobbyName, User user, Reasons reason) {
        super(lobbyName);
        this.user = user;
        this.reason = reason;
    }

    public Reasons getReason() {
        return reason;
    }

    public User getUser() {
        return user;
    }
}

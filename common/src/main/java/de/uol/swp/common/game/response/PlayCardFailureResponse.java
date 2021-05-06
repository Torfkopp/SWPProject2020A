package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.LobbyName;
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

    private final User user;
    private final Reasons reason;

    /**
     * The enum containing the possible reasons.
     */
    public enum Reasons {
        NO_CARDS,
        TOO_MANY_RESOURCES
    }

    /**
     * Constructor.
     *
     * @param lobbyName The lobby name
     * @param user      The user
     * @param reason    The reason
     */
    public PlayCardFailureResponse(LobbyName lobbyName, User user, Reasons reason) {
        super(lobbyName);
        this.user = user;
        this.reason = reason;
    }

    /**
     * Gets the reason.
     *
     * @return The reason
     */
    public Reasons getReason() {
        return reason;
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

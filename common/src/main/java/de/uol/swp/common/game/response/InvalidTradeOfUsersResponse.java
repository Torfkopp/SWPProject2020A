package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.User;

/**
 * This method is called if a trade between two users
 * is invalid.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-25
 */
public class InvalidTradeOfUsersResponse extends AbstractLobbyResponse {

    private final User offeringUserName;

    /**
     * Constructor
     *
     * @param lobbyName        Name of the lobby
     * @param offeringUserName Name of the offering user
     */
    public InvalidTradeOfUsersResponse(String lobbyName, User offeringUserName) {
        super(lobbyName);
        this.offeringUserName = offeringUserName;
    }

    /**
     * Gets the name of the offering user
     *
     * @return The name of the offering user
     */
    public User getOfferingUserName() {
        return offeringUserName;
    }
}
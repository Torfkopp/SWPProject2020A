package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.Actor;

/**
 * This method is called if a trade between two users
 * is invalid, e.g. too many resources were demanded.
 *
 * @author Maximilian Lindner
 * @author Finn Haase
 * @see de.uol.swp.common.lobby.response.AbstractLobbyResponse
 * @since 2021-02-25
 */
public class InvalidTradeOfUsersResponse extends AbstractLobbyResponse {

    private final Actor offeringUser;

    /**
     * Constructor
     *
     * @param lobbyName        Name of the lobby
     * @param offeringUserName Name of the offering user
     */
    public InvalidTradeOfUsersResponse(LobbyName lobbyName, Actor offeringUserName) {
        super(lobbyName);
        this.offeringUser = offeringUserName;
    }

    /**
     * Gets the name of the offering user
     *
     * @return The name of the offering user
     */
    public Actor getOfferingUser() {
        return offeringUser;
    }
}
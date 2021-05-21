package de.uol.swp.common.game.response;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AbstractLobbyResponse;
import de.uol.swp.common.user.User;

/**
 * This response is sent when the user
 * is allowed to play a RoadBuildingCard
 *
 * @author Alwin Bossert
 * @since 2021-05-16
 */
public class PlayRoadBuildingCardAllowedResponse extends AbstractLobbyResponse {

    private final User user;

    /**
     * Constructor.
     *
     * @param lobbyName The lobby name
     * @param user      The user
     */
    public PlayRoadBuildingCardAllowedResponse(LobbyName lobbyName, User user) {
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

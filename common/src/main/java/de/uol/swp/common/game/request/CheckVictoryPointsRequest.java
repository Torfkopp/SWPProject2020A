package de.uol.swp.common.game.request;

import de.uol.swp.common.user.User;

/**
 * Request sent to check if the Player has enough victory points to win
 *
 * @author Steven Luong
 * @author Finn Haase
 * @since 2021-03-22
 */
public class CheckVictoryPointsRequest extends AbstractGameRequest {

    private final User user;

    /**
     * Constructor
     *
     * @param originLobby The lobby this game takes place in
     * @param user        The user whose victory points should be checked
     */
    public CheckVictoryPointsRequest(String originLobby, User user) {
        super(originLobby);
        this.user = user;
    }

    /**
     * Gets the user
     *
     * @return The user
     */
    public User getUser() {return user;}
}

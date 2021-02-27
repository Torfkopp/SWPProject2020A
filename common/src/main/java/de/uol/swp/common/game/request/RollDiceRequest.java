package de.uol.swp.common.game.request;

import de.uol.swp.common.user.User;

/**
 * Request sent to the server when a user wants to roll the dices
 *
 * @author Mario Fokken
 * @author Sven Ahrens
 * @since 2021-02-22
 */
public class RollDiceRequest extends AbstractGameRequest {

    private final User user;

    public RollDiceRequest(User user, String originLobby) {
        super(originLobby);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}

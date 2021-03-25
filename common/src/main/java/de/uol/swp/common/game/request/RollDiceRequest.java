package de.uol.swp.common.game.request;

import de.uol.swp.common.user.UserOrDummy;

/**
 * Request sent to the server when a user wants to roll the dices
 *
 * @author Mario Fokken
 * @author Sven Ahrens
 * @since 2021-02-22
 */
public class RollDiceRequest extends AbstractGameRequest {

    private final UserOrDummy user;

    public RollDiceRequest(UserOrDummy user, String originLobby) {
        super(originLobby);
        this.user = user;
    }

    public UserOrDummy getUser() {
        return user;
    }
}

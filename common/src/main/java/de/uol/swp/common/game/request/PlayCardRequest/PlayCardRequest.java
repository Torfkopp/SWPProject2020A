package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.game.request.AbstractGameRequest;
import de.uol.swp.common.user.User;

/**
 * Abstract class for all PlayCard requests
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
abstract class PlayCardRequest extends AbstractGameRequest {

    User user;

    public PlayCardRequest(String originLobby, User user) {
        super(originLobby);
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.user.User;

/**
 * This request gets sent when the player
 * wants to play a YearOfPlentyCard
 */
public class PlayYearOfPlentyCardRequest extends PlayCardRequest {

    Resources resource1;
    Resources resource2;

    public PlayYearOfPlentyCardRequest(String originLobby, User user, Resources resource1, Resources resource2) {
        super(originLobby, user);
        this.resource1 = resource1;
        this.resource2 = resource2;
    }

    public Resources getResource1() {
        return resource1;
    }

    public Resources getResource2() {
        return resource2;
    }
}

package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.user.User;

/**
 * This request gets sent when the player
 * wants to play a MonopolyCard
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
public class PlayMonopolyCardRequest extends PlayCardRequest {

    Resources resource;

    public PlayMonopolyCardRequest(String originLobby, User user, Resources resource) {
        super(originLobby, user);
        this.resource = resource;
    }

    public Resources getResource() {
        return resource;
    }
}

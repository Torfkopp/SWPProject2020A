package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.Resource;
import de.uol.swp.common.user.User;

/**
 * This request gets sent when the player
 * wants to play a YearOfPlentyCard
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
public class PlayYearOfPlentyCardRequest extends PlayCardRequest {

    Resource.ResourceType resource1;
    Resource.ResourceType resource2;

    public PlayYearOfPlentyCardRequest(LobbyName originLobby, User user, Resource.ResourceType resource1, Resource.ResourceType resource2) {
        super(originLobby, user);
        this.resource1 = resource1;
        this.resource2 = resource2;
    }

    public Resource.ResourceType getResource1() {
        return resource1;
    }

    public Resource.ResourceType getResource2() {
        return resource2;
    }
}

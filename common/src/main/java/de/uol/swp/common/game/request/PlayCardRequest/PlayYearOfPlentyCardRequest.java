package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourceThingies.resource.ResourceType;
import de.uol.swp.common.user.User;

/**
 * This request gets sent when the player
 * wants to play a YearOfPlentyCard
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
public class PlayYearOfPlentyCardRequest extends PlayCardRequest {

    ResourceType resource1;
    ResourceType resource2;

    public PlayYearOfPlentyCardRequest(LobbyName originLobby, User user, ResourceType resource1, ResourceType resource2) {
        super(originLobby, user);
        this.resource1 = resource1;
        this.resource2 = resource2;
    }

    public ResourceType getResource1() {
        return resource1;
    }

    public ResourceType getResource2() {
        return resource2;
    }
}

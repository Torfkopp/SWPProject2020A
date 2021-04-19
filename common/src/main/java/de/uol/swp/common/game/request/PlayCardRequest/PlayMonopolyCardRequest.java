package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.Resource;
import de.uol.swp.common.user.User;

/**
 * This request gets sent when the player
 * wants to play a MonopolyCard
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
public class PlayMonopolyCardRequest extends PlayCardRequest {

    Resource.ResourceType resource;

    public PlayMonopolyCardRequest(LobbyName originLobby, User user, Resource.ResourceType resource) {
        super(originLobby, user);
        this.resource = resource;
    }

    public Resource.ResourceType getResource() {
        return resource;
    }
}

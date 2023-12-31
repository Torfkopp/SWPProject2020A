package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

/**
 * This request gets sent when the player
 * wants to play a MonopolyCard
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
public class PlayMonopolyCardRequest extends AbstractPlayCardRequest {

    private final ResourceType resource;

    /**
     * Constructor.
     *
     * @param originLobby The origin lobby
     * @param user        The user
     * @param resource    The resource
     */
    public PlayMonopolyCardRequest(LobbyName originLobby, User user, ResourceType resource) {
        super(originLobby, user);
        this.resource = resource;
    }

    /**
     * Gets the resource.
     *
     * @return The resource
     */
    public ResourceType getResource() {
        return resource;
    }
}

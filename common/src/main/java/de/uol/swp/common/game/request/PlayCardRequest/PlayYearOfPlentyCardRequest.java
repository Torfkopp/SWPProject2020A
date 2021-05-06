package de.uol.swp.common.game.request.PlayCardRequest;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;

/**
 * This request gets sent when the player
 * wants to play a YearOfPlentyCard
 *
 * @author Mario Fokken
 * @since 2021-02-26
 */
public class PlayYearOfPlentyCardRequest extends AbstractPlayCardRequest {

    private final ResourceType firstResource;
    private final ResourceType secondResource;

    /**
     * Constructor.
     *
     * @param originLobby    The origin lobby
     * @param user           The user
     * @param firstResource  The resource 1
     * @param secondResource The resource 2
     */
    public PlayYearOfPlentyCardRequest(LobbyName originLobby, User user, ResourceType firstResource,
                                       ResourceType secondResource) {
        super(originLobby, user);
        this.firstResource = firstResource;
        this.secondResource = secondResource;
    }

    /**
     * Gets the first resource.
     *
     * @return The first resource
     */
    public ResourceType getFirstResource() {
        return firstResource;
    }

    /**
     * Gets the second resource.
     *
     * @return The second resource
     */
    public ResourceType getSecondResource() {
        return secondResource;
    }
}

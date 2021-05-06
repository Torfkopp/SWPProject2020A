package de.uol.swp.common.game.request;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

/**
 * Request used to change the amount of a resource in a player's inventory
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.game.request.AbstractGameRequest
 * @since 2021-03-07
 */
public class EditInventoryRequest extends AbstractGameRequest {

    private final UserOrDummy user;
    private final ResourceType resource;
    private final DevelopmentCardType developmentCard;
    private final int amount;

    /**
     * Constructor
     *
     * @param originLobby The Lobby name the inventory is associated with
     * @param user        The user whose inventory should be updated
     * @param resource    The resource to be changed
     * @param amount      The amount to be added (substracted if negative)
     *                    to/from the resource
     */
    public EditInventoryRequest(LobbyName originLobby, UserOrDummy user, ResourceType resource,
                                DevelopmentCardType developmentCard, int amount) {
        super(originLobby);
        this.user = user;
        this.resource = resource;
        this.developmentCard = developmentCard;
        this.amount = amount;
    }

    /**
     * Gets the amount by which to change the resource amount in the player's
     * inventory
     *
     * @return The int amount to change the resource amount by
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Gets the development card.
     *
     * @return The development card
     */
    public DevelopmentCardType getDevelopmentCard() {
        return developmentCard;
    }

    /**
     * Gets the resource that should be changed
     *
     * @return The name of the resource
     */
    public ResourceType getResource() {
        return resource;
    }

    /**
     * Gets the user whose inventory should be updated
     *
     * @return The user whose inventory to update
     */
    public UserOrDummy getUser() {
        return user;
    }
}

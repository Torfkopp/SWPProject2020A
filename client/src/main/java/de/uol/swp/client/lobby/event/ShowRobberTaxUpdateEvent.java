package de.uol.swp.client.lobby.event;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.lobby.LobbyName;

/**
 * Event used to trigger an update of the RobberTaxPresenter
 * To give a RobberTaxPresenter its values, post an instance
 * of this event onto the EventBus the RobberTaxPresenter is subscribed to.
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.client.lobby.RobberTaxPresenter
 * @since 2021-04-08
 */
public class ShowRobberTaxUpdateEvent {

    private final LobbyName lobbyName;
    private final int taxAmount;
    private final ResourceList inventory;

    /**
     * Constructor
     *
     * @param lobbyName The lobby's name
     * @param taxAmount The amount of cards to pay
     * @param inventory The inventory of the user
     */
    public ShowRobberTaxUpdateEvent(LobbyName lobbyName, int taxAmount, ResourceList inventory) {
        this.lobbyName = lobbyName;
        this.taxAmount = taxAmount;
        this.inventory = inventory;
    }

    /**
     * Gets the inventory
     *
     * @return Map with the resource and its amount as integer
     */
    public ResourceList getInventory() {
        return inventory.create();
    }

    /**
     * Gets the lobby's name
     *
     * @return LobbyName lobbyName
     */
    public LobbyName getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the amount of tax to pay
     *
     * @return int taxAmount
     */
    public int getTaxAmount() {
        return taxAmount;
    }
}

package de.uol.swp.client.lobby.event;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.Resource;

import java.util.Map;

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
    private final Map<Resource.ResourceType, Integer> inventory;

    /**
     * Constructor
     *
     * @param lobbyName The lobby's name
     * @param taxAmount The amount of cards to pay
     * @param inventory The inventory of the user
     */
    public ShowRobberTaxUpdateEvent(LobbyName lobbyName, int taxAmount, Map<Resource.ResourceType, Integer> inventory) {
        this.lobbyName = lobbyName;
        this.taxAmount = taxAmount;
        this.inventory = inventory;
    }

    /**
     * Gets the inventory
     *
     * @return Map with the resource and its amount as integer
     */
    public Map<Resource.ResourceType, Integer> getInventory() {
        return inventory;
    }

    /**
     * Gets the lobby's name
     *
     * @return String lobbyName
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

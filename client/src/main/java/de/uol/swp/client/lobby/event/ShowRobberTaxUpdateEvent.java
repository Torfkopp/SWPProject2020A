package de.uol.swp.client.lobby.event;

import de.uol.swp.common.game.map.Resources;

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

    private final String lobbyName;
    private final int taxAmount;
    private final Map<Resources, Integer> inventory;

    /**
     * Constructor
     *
     * @param lobbyName The lobby's name
     * @param taxAmount The amount of cards to pay
     * @param inventory The inventory of the user
     */
    public ShowRobberTaxUpdateEvent(String lobbyName, int taxAmount, Map<Resources, Integer> inventory) {
        this.lobbyName = lobbyName;
        this.taxAmount = taxAmount;
        this.inventory = inventory;
    }

    /**
     * Gets the inventory
     *
     * @return Map with the resource and its amount as integer
     */
    public Map<Resources, Integer> getInventory() {
        return inventory;
    }

    /**
     * Gets the lobby's name
     *
     * @return String lobbyName
     */
    public String getLobbyName() {
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

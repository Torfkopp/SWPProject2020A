package de.uol.swp.client.lobby.event;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.Resource;

import java.util.Map;

/**
 * Event used to show the window for the robberTax
 * <p>
 * In order to show the previous window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.client.SceneManager
 * @see de.uol.swp.common.game.robber.RobberTaxMessage
 * @since 2021-04-07
 */
public class ShowRobberTaxViewEvent {

    private final String lobbyName;
    private final int taxAmount;
    private final Map<Resource.ResourceType, Integer> inventory;

    /**
     * Constructor
     *
     * @param lobbyName The lobby's name
     * @param taxAmount The amount of cards to pay
     * @param inventory The inventory
     */
    public ShowRobberTaxViewEvent(LobbyName lobbyName, int taxAmount, Map<Resource.ResourceType, Integer> inventory) {
        this.lobbyName = lobbyName;
        this.taxAmount = taxAmount;
        this.inventory = inventory;
    }

    /**
     * Gets the Inventory
     *
     * @return Map of resources and its amount
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

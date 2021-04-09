package de.uol.swp.client.lobby.event;

import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.user.User;

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
    private final User user;
    private final int taxAmount;
    private final Map<Resources, Integer> inventory;

    public ShowRobberTaxViewEvent(String lobbyName, User user, int taxAmount, Map<Resources, Integer> inventory) {
        this.lobbyName = lobbyName;
        this.user = user;
        this.taxAmount = taxAmount;
        this.inventory = inventory;
    }

    public Map<Resources, Integer> getInventory() {
        return inventory;
    }

    public String getLobbyName() {
        return lobbyName;
    }

    public int getTaxAmount() {
        return taxAmount;
    }

    public User getUser() {
        return user;
    }
}

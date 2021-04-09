package de.uol.swp.client.lobby.event;

import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.user.User;

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
    private final User user;
    private final int taxAmount;
    private final Map<Resources, Integer> inventory;

    public ShowRobberTaxUpdateEvent(String lobbyName, User user, int taxAmount, Map<Resources, Integer> inventory) {
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

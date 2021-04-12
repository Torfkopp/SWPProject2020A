package de.uol.swp.common.game.robber;

import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

import java.util.Map;

/**
 * Answer to the RobberTaxMessage containing
 * the player's chosen resource cards.
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.common.game.robber.RobberTaxMessage
 * @since 2021-04-05
 */
public class RobberTaxChosenRequest extends AbstractRequestMessage {

    private final User player;
    private final Map<Resources, Integer> resources;
    private final String lobby;

    /**
     * Constructor
     *
     * @param resources Map of the resources and its amount
     * @param player    The player paying the tax
     * @param lobby     The lobby's name
     */
    public RobberTaxChosenRequest(Map<Resources, Integer> resources, User player, String lobby) {
        this.resources = resources;
        this.player = player;
        this.lobby = lobby;
    }

    /**
     * Gets the lobby's name
     *
     * @return String lobby
     */
    public String getLobby() {
        return lobby;
    }

    /**
     * Gets the player
     *
     * @return User player
     */
    public User getPlayer() {
        return player;
    }

    /**
     * Gets the resources
     *
     * @return Map of a resource and its amount
     */
    public Map<Resources, Integer> getResources() {
        return resources;
    }
}

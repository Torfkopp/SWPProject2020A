package de.uol.swp.common.game.robber;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.resourceThingies.resource.ResourceList;
import de.uol.swp.common.message.AbstractRequestMessage;
import de.uol.swp.common.user.User;

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
    private final ResourceList resources;
    private final LobbyName lobby;

    /**
     * Constructor
     *
     * @param resources Map of the resources and its amount
     * @param player    The player paying the tax
     * @param lobby     The lobby's name
     */
    public RobberTaxChosenRequest(ResourceList resources, User player, LobbyName lobby) {
        this.resources = resources;
        this.player = player;
        this.lobby = lobby;
    }

    /**
     * Gets the lobby's name
     *
     * @return String lobby
     */
    public LobbyName getLobby() {
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
    public ResourceList getResources() {
        return resources;
    }
}

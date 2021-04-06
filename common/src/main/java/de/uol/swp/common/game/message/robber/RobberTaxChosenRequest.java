package de.uol.swp.common.game.message.robber;

import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.message.AbstractResponseMessage;
import de.uol.swp.common.user.User;

import java.util.Map;

/**
 * Gets sent to the server.
 * Contains the resource cards the player picked
 * to use for the robber tax.
 *
 * @author Mario Fokken
 * @since 2021-04-05
 */
public class RobberTaxChosenRequest extends AbstractResponseMessage {

    private final User player;
    private final Map<Resources, Integer> resources;
    private final String lobby;

    public RobberTaxChosenRequest(Map<Resources, Integer> resources, User player, String lobby) {
        this.resources = resources;
        this.player = player;
        this.lobby = lobby;
    }

    public String getLobby() {
        return lobby;
    }

    public User getPlayer() {
        return player;
    }

    public Map<Resources, Integer> getResources() {
        return resources;
    }
}

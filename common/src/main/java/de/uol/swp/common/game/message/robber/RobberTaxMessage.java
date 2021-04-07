package de.uol.swp.common.game.message.robber;

import de.uol.swp.common.game.message.AbstractGameMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;

import java.util.HashMap;
import java.util.Map;

/**
 * Gets sent to all players with more than seven resource
 * cards when the dice shows a seven.
 * These players have to chose which
 * resource cards to give up on.
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.common.game.message.robber.RobberTaxChosenRequest
 * @since 2021-04-05
 */
public class RobberTaxMessage extends AbstractGameMessage {

    private final Map<User, Integer> players;
    private final Map<User, Map<String, Integer>> inventory;

    public RobberTaxMessage(String lobbyName, UserOrDummy user, Map<User, Map<String, Integer>> inventory) {
        super(lobbyName, user);
        players = new HashMap<>();
        this.inventory = inventory;
    }

    public void add(User player, int amount) {
        players.put(player, amount);
    }

    public Map<User, Map<String, Integer>> getInventory() {
        return inventory;
    }

    public Map<User, Integer> getPlayers() {
        return players;
    }
}

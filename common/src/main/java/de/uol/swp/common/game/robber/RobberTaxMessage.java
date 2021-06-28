package de.uol.swp.common.game.robber;

import de.uol.swp.common.game.message.AbstractGameMessage;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.specialisedUtil.ActorIntegerMap;
import de.uol.swp.common.specialisedUtil.ActorResourceListMap;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.user.User;

/**
 * Gets sent to all players with more than seven resource
 * cards when the dice shows a seven.
 * These players have to chose which
 * resource cards to give up on.
 *
 * @author Mario Fokken
 * @author Timo Gerken
 * @see de.uol.swp.common.game.robber.RobberTaxChosenRequest
 * @since 2021-04-05
 */
public class RobberTaxMessage extends AbstractGameMessage {

    private final ActorIntegerMap players;
    private final ActorResourceListMap inventories;

    /**
     * Constructor
     *
     * @param lobbyName   The lobby's name
     * @param user        The Actor who triggered this message
     * @param players     Map of User to pay the tax and the amount of card to pay
     * @param inventories Map of user and the inventory as map with resources and its amount
     */
    public RobberTaxMessage(LobbyName lobbyName, Actor user, ActorIntegerMap players,
                            ActorResourceListMap inventories) {
        super(lobbyName, user);
        this.players = players;
        this.inventories = inventories;
    }

    /**
     * Adds a user to the players
     *
     * @param player User to pay the tax
     * @param amount Amount of cards to pay
     */
    public void add(User player, int amount) {
        players.put(player, amount);
    }

    /**
     * Gets the inventory
     *
     * @return Map of user and a map of a resource and its amount
     */
    public ActorResourceListMap getInventories() {
        return inventories;
    }

    /**
     * Gets the players
     *
     * @return Map of user and the amount to pay
     */
    public ActorIntegerMap getPlayers() {
        return players;
    }
}

package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.specialisedUtil.ActorPlayerMap;
import de.uol.swp.common.specialisedUtil.ActorSet;
import de.uol.swp.common.user.Actor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A class to store the mapping of Actor, Player and Inventory
 *
 * @author Temmo Junkhoff
 * @since 2021-03-19
 */
public class InventoryMap implements Serializable {

    private final List<UserPlayerInventoryMapping> map = new LinkedList<>();

    /**
     * Gets the inventory for an actor
     *
     * @param actor The actor whose inventory is needed
     *
     * @return The requested inventory
     */
    public Inventory get(Actor actor) {
        for (UserPlayerInventoryMapping entry : map)
            if (entry.getActor().equals(actor)) {
                return entry.getInventory();
            }
        return null;
    }

    /**
     * Gets the inventory for a player
     *
     * @param player The player whose inventory is needed
     *
     * @return The requested inventory
     */
    public Inventory get(Player player) {
        for (UserPlayerInventoryMapping entry : map)
            if (Objects.equals(entry.getPlayer(), player)) return entry.getInventory();
        return null;
    }

    /**
     * Gets a list of all inventories
     *
     * @return A list of all inventories
     */
    public List<Inventory> getInventories() {
        LinkedList<Inventory> returnList = new LinkedList<>();
        map.forEach(entry -> returnList.add(entry.getInventory()));
        return returnList;
    }

    /**
     * Gets an array of the Actor objects
     *
     * @return The array of Actor objects
     */
    public Actor[] getActorArray() {
        ActorSet returnArray = new ActorSet();
        map.forEach((key -> returnArray.add(key.getActor())));
        return returnArray.toArray(new Actor[0]);
    }

    /**
     * Gets the Actor for a given player
     *
     * @param player The player whose matching Actor is needed
     *
     * @return The requested Actor
     */
    public Actor getActorFromPlayer(Player player) {
        for (UserPlayerInventoryMapping entry : map)
            if (Objects.equals(player, entry.getPlayer())) return entry.getActor();
        return null;
    }

    /**
     * Gets the player for a given actor
     *
     * @param actor The actor whose matching Player is needed
     *
     * @return The requested Player
     */
    public Player getPlayerFromActor(Actor actor) {
        for (UserPlayerInventoryMapping entry : map)
            if (Objects.equals(actor, entry.getActor())) return entry.getPlayer();
        return null;
    }

    /**
     * Gets a map of users or dummies and their corresponding players
     *
     * @return A map containing users or dummies and their corresponding players
     *
     * @since 2021-05-20
     */
    public ActorPlayerMap getUserToPlayerMap() {
        ActorPlayerMap temp = new ActorPlayerMap();
        for (UserPlayerInventoryMapping entry : map)
            temp.put(entry.getActor(), entry.getPlayer());
        return temp;
    }

    /**
     * Puts a new entry in the list
     *
     * @param actor     The actor key
     * @param player    The player key
     * @param inventory The inventory value
     */
    public void put(Actor actor, Player player, Inventory inventory) {
        for (int i = 0; i < map.size(); i++) {
            UserPlayerInventoryMapping entry = map.get(i);
            if ((Objects.equals(entry.getActor(), actor) && !Objects
                    .equals(entry.getPlayer(), player)) || (Objects.equals(entry.getPlayer(), player) && !Objects
                    .equals(entry.getActor(), actor))) {
                throw new IllegalArgumentException("Keys are not matching!");
            } else if (Objects.equals(entry.getActor(), actor) && Objects.equals(entry.getPlayer(), player)) {
                map.set(i, new UserPlayerInventoryMapping(actor, player, inventory));
                return;
            }
        }
        map.add(new UserPlayerInventoryMapping(actor, player, inventory));
    }

    /**
     * Replace a User who left a Lobby in Game with an AI
     *
     * @param userToBeReplaced  The user who left the lobby and should be replaced
     * @param userToReplaceWith The AI that replaces the user who left the lobby
     *
     * @author Eric Vuong
     * @since 2021-06-10
     */
    public void replace(Actor userToBeReplaced, Actor userToReplaceWith) throws IllegalArgumentException {
        for (int i = 0; i < map.size(); i++) {
            UserPlayerInventoryMapping entry = map.get(i);
            if (Objects.equals(entry.getActor(), userToBeReplaced)) {
                map.set(i, new UserPlayerInventoryMapping(userToReplaceWith, entry.getPlayer(), entry.getInventory()));
                return;
            }
        }
        throw new IllegalArgumentException("Unknown User");
    }

    /**
     * Gets the size of the map
     *
     * @return The size of the map
     */
    public int size() {
        return map.size();
    }

    /**
     * A class to store a mapping of an user, a player and an inventory.
     *
     * @author Temmo Junkhoff
     * @since 2021-05-04
     */
    private class UserPlayerInventoryMapping {

        private final Actor user;
        private final Player player;
        private final Inventory inventory;

        /**
         * Constructor.
         *
         * @param user      The user
         * @param player    The player
         * @param inventory The inventory
         *
         * @author Temmo Junkhoff
         * @since 2021-05-04
         */
        public UserPlayerInventoryMapping(Actor user, Player player, Inventory inventory) {
            this.user = user;
            this.player = player;
            this.inventory = inventory;
        }

        /**
         * Gets the inventory.
         *
         * @return The inventory
         *
         * @author Temmo Junkhoff
         * @since 2021-05-04
         */
        public Inventory getInventory() {
            return inventory;
        }

        /**
         * Gets the player.
         *
         * @return The player
         *
         * @author Temmo Junkhoff
         * @since 2021-05-04
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * Gets the user.
         *
         * @return The user
         *
         * @author Temmo Junkhoff
         * @since 2021-05-04
         */
        public Actor getActor() {
            return user;
        }
    }
}

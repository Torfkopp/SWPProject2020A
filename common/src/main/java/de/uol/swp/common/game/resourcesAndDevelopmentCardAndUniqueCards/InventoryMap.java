package de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.UserOrDummy;

import java.io.Serializable;
import java.util.*;

/**
 * A class to store the mapping of UserOrDummy, Player and Inventory
 *
 * @author Temmo Junkhoff
 * @since 2021-03-19
 */
public class InventoryMap implements Serializable {

    private final List<UserPlayerInventoryMapping> map = new LinkedList<>();

    /**
     * Gets the inventory for a user or dummy
     *
     * @param userOrDummy The user or dummy whose inventory is needed
     *
     * @return The requested inventory
     */
    public Inventory get(UserOrDummy userOrDummy) {
        for (UserPlayerInventoryMapping entry : map)
            if (entry.getUser().equals(userOrDummy)) {
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
     * Gets the player for a given UserOrDummy
     *
     * @param userOrDummy The user or dummy whose matching Player is needed
     *
     * @return The requested Player
     */
    public Player getPlayerFromUserOrDummy(UserOrDummy userOrDummy) {
        for (UserPlayerInventoryMapping entry : map)
            if (Objects.equals(userOrDummy, entry.getUser())) return entry.getPlayer();
        return null;
    }

    /**
     * Gets an array of the UserOrDummy objects
     *
     * @return The array of UserOrDummy objects
     */
    public UserOrDummy[] getUserOrDummyArray() {
        List<UserOrDummy> returnArray = new LinkedList<>();
        map.forEach((key -> returnArray.add(key.getUser())));
        return returnArray.toArray(new UserOrDummy[0]);
    }

    /**
     * Gets the UserOrDummy for a given player
     *
     * @param player The player whose matching UserOrDummy is needed
     *
     * @return The requested UserOrDummy
     */
    public UserOrDummy getUserOrDummyFromPlayer(Player player) {
        for (UserPlayerInventoryMapping entry : map)
            if (Objects.equals(player, entry.getPlayer())) return entry.getUser();
        return null;
    }

    /**
     * Gets a map of users or dummies and their corresponding players
     *
     * @return A map containing users or dummies and their corresponding players
     *
     * @since 2021-05-20
     */
    public Map<UserOrDummy, Player> getUserToPlayerMap() {
        Map<UserOrDummy, Player> temp = new HashMap<>();
        for (UserPlayerInventoryMapping entry : map)
            temp.put(entry.getUser(), entry.getPlayer());
        return temp;
    }

    /**
     * Puts a new entry in the list
     *
     * @param userOrDummy The userOrDummy key
     * @param player      The player key
     * @param inventory   The inventory value
     */
    public void put(UserOrDummy userOrDummy, Player player, Inventory inventory) {
        for (int i = 0; i < map.size(); i++) {
            UserPlayerInventoryMapping entry = map.get(i);
            if ((Objects.equals(entry.getUser(), userOrDummy) && !Objects
                    .equals(entry.getPlayer(), player)) || (Objects.equals(entry.getPlayer(), player) && !Objects
                    .equals(entry.getUser(), userOrDummy))) {
                throw new IllegalArgumentException("Keys are not matching!");
            } else if (Objects.equals(entry.getUser(), userOrDummy) && Objects.equals(entry.getPlayer(), player)) {
                map.set(i, new UserPlayerInventoryMapping(userOrDummy, player, inventory));
                return;
            }
        }
        map.add(new UserPlayerInventoryMapping(userOrDummy, player, inventory));
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

        private final UserOrDummy user;
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
        public UserPlayerInventoryMapping(UserOrDummy user, Player player, Inventory inventory) {
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
        public UserOrDummy getUser() {
            return user;
        }
    }
}

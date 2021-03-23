package de.uol.swp.common.game;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.util.Triple;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * A class to store the mapping of UserOrDummy, Player and Inventory
 */
public class InventoryMap {

    private final List<Triple<UserOrDummy, Player, Inventory>> map = new LinkedList<>();

    /**
     * Constructor
     */
    public InventoryMap() {}

    /**
     * Gets a list of all inventories
     *
     * @return A list of all inventories
     */
    public List<Inventory> getInventories() {
        LinkedList<Inventory> returnList = new LinkedList<>();
        map.forEach(entry -> returnList.add(entry.getValue3()));
        return returnList;
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
     * Gets the inventory for a user or dummy
     *
     * @param userOrDummy The user or dummy whose inventory is needed
     *
     * @return The requested inventory
     */
    public Inventory get(UserOrDummy userOrDummy) {
        for (Triple<UserOrDummy, Player, Inventory> entry : map)
            if (entry.getValue1().equals(userOrDummy)) {
                return entry.getValue3();
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
        for (Triple<UserOrDummy, Player, Inventory> entry : map)
            if (Objects.equals(entry.getValue2(), player)) return entry.getValue3();
        return null;
    }

    /**
     * Puts a new tuple in the list
     *
     * @param userOrDummy The userOrDummy key
     * @param player      The player key
     * @param inventory   The inventory value
     */
    public void put(UserOrDummy userOrDummy, Player player, Inventory inventory) {
        for (int i = 0; i < map.size(); i++) {
            Triple<UserOrDummy, Player, Inventory> entry = map.get(i);
            if ((Objects.equals(entry.getValue1(), userOrDummy) && !Objects
                    .equals(entry.getValue2(), player)) || (Objects.equals(entry.getValue2(), player) && !Objects
                    .equals(entry.getValue1(), userOrDummy))) {
                throw new IllegalArgumentException("Keys are not matching!");
            } else if (Objects.equals(entry.getValue1(), userOrDummy) && Objects.equals(entry.getValue2(), player)) {
                map.set(i, new Triple<>(userOrDummy, player, inventory));
                return;
            }
        }
        map.add(new Triple<>(userOrDummy, player, inventory));
    }

    /**
     * Gets the player for a given UserOrDummy
     *
     * @param userOrDummy The user or dummy whose matching Player is needed
     *
     * @return The requested Player
     */
    public Player getPlayerFromUserOrDummy(UserOrDummy userOrDummy) {
        for (Triple<UserOrDummy, Player, Inventory> entry : map)
            if (Objects.equals(userOrDummy, entry.getValue1())) return entry.getValue2();
        return null;
    }

    /**
     * Gets the UserOrDummy for a given player
     *
     * @param player The player whose matching UserOrDummy is needed
     *
     * @return The requested UserOrDummy
     */
    public UserOrDummy getUserOrDummyFromPlayer(Player player) {
        for (Triple<UserOrDummy, Player, Inventory> entry : map)
            if (Objects.equals(player, entry.getValue2())) return entry.getValue1();
        return null;
    }

    /**
     * Gets an array of the UserOrDummy objects
     *
     * @return The array of UserOrDummy objects
     */
    public UserOrDummy[] getUserOrDummyArray() {
        List<UserOrDummy> returnArray = new LinkedList<>();
        map.forEach((key -> returnArray.add(key.getValue1())));
        return returnArray.toArray(new UserOrDummy[0]);
    }
}

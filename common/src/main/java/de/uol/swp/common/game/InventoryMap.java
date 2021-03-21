package de.uol.swp.common.game;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.Dummy;
import de.uol.swp.common.user.DummyDTO;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.util.Tuple3;

import java.util.*;

public class InventoryMap {

    private final List<Tuple3<UserOrDummy, Player, Inventory>> map = new LinkedList<>();

    public InventoryMap() {}

    public Map<UserOrDummy, Inventory> getUserOrDummyInventoryMap() {
        Map<UserOrDummy, Inventory> returnMap = new LinkedHashMap<>();
        map.forEach(entry -> returnMap.put(entry.getValue1(), entry.getValue3()));
        return returnMap;
    }

    public List<Inventory> getInventories() {
        LinkedList<Inventory> returnList = new LinkedList<>();
        map.forEach(entry -> returnList.add(entry.getValue3()));
        return returnList;
    }

    public Map<Player, Inventory> getPlayerInventoryMap() {
        Map<Player, Inventory> returnMap = new LinkedHashMap<>();
        map.forEach(entry -> returnMap.put(entry.getValue2(), entry.getValue3()));
        return returnMap;
    }

    public int size() {
        return map.size();
    }

    public Map<UserOrDummy, Player> getUserOrDummyPlayerMap() {
        Map<UserOrDummy, Player> returnMap = new LinkedHashMap<>();
        map.forEach(entry -> {
            System.out.println(entry.getValue1() + " ;;; " + entry.getValue2() + " ;;; " + entry.getValue3());
            returnMap.put(entry.getValue1(), entry.getValue2());
        });
        return returnMap;
    }

    public Map<Player, UserOrDummy> getPlayerUserOrDummyMap() {
        Map<Player, UserOrDummy> returnMap = new LinkedHashMap<>();
        map.forEach(entry -> returnMap.put(entry.getValue2(), entry.getValue1()));
        return returnMap;
    }

    public Inventory get(UserOrDummy userOrDummy) {
        System.out.println("InventoryMap get");
        System.out.println(userOrDummy);
        if (userOrDummy instanceof Dummy)
            System.out.println(userOrDummy.equals(new DummyDTO(userOrDummy.getID())));
        for (Tuple3<UserOrDummy, Player, Inventory> entry : map)
            if (entry.getValue1().equals(userOrDummy)) {
                System.out.println("Found something");
                return entry.getValue3();
            }
        return null;
    }

    public Inventory get(Player player) {
        for (Tuple3<UserOrDummy, Player, Inventory> entry : map)
            if (Objects.equals(entry.getValue2(), player)) return entry.getValue3();
        return null;
    }

    public void put(UserOrDummy userOrDummy, Player player, Inventory inventory) {
        for (int i = 0; i < map.size(); i++) {
            Tuple3<UserOrDummy, Player, Inventory> entry = map.get(i);
            if ((Objects.equals(entry.getValue1(), userOrDummy) && !Objects
                    .equals(entry.getValue2(), player)) || (Objects.equals(entry.getValue2(), player) && !Objects
                    .equals(entry.getValue1(), userOrDummy))) {
                throw new IllegalArgumentException("Keys are not matching!");
            } else if (Objects.equals(entry.getValue1(), userOrDummy) && Objects.equals(entry.getValue2(), player)) {
                map.set(i, new Tuple3<>(userOrDummy, player, inventory));
                return;
            }
        }
        map.add(new Tuple3<>(userOrDummy, player, inventory));
        for (Tuple3<UserOrDummy, Player, Inventory> entry : map) {
            System.out.println("entry");
            System.out.println(entry.getValue1());
            System.out.println(entry.getValue2());
            System.out.println(entry.getValue3());
        }
    }

    public Player getPlayerFromUserOrDummy(UserOrDummy userOrDummy) {
        for (Tuple3<UserOrDummy, Player, Inventory> entry : map)
            if (Objects.equals(userOrDummy, entry.getValue1())) return entry.getValue2();
        return null;
    }

    public UserOrDummy getUserOrDummyFromPlayer(Player player) {
        return getPlayerUserOrDummyMap().get(player);
    }

    public UserOrDummy[] getUserOrDummyArray() {
        List<UserOrDummy> returnArray = new LinkedList<>();
        map.forEach((key -> returnArray.add(key.getValue1())));
        return returnArray.toArray(new UserOrDummy[0]);
    }
}

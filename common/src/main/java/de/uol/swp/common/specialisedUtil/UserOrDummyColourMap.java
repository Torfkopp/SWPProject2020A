package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.Colour;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.util.Util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Specialised class to map
 * a UserOrDummy to a Colour
 *
 * @author Mario Fokken
 * @since 2021-06-16
 */
public class UserOrDummyColourMap extends LinkedHashMap<UserOrDummy, Colour> {

    /**
     * Creates a PlayerColourMap using
     * a UserOrDummyPlayerMap
     *
     * @param map UserOrDummyPlayerMap
     *
     * @return A mapping of Player and Colour
     *
     * @author Mario Fokken
     * @since 2021-06-02
     */
    public Map<Player, Colour> makePlayerColourMap(UserOrDummyPlayerMap map) {
        Map<Player, Colour> result = new HashMap<>();
        for (UserOrDummy u : keySet()) result.put(map.get(u), get(u));
        return result;
    }

    /**
     * Puts the user and a random
     * colour into the map
     *
     * @param user The UserOrDummy to be put into the map
     */
    public void put(UserOrDummy user) {
        super.put(user, Util.randomColour());
    }
}

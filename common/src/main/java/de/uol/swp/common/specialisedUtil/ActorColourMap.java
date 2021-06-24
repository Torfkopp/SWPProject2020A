package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.Colour;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.util.Util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Specialised class to map
 * an Actor to a Colour
 *
 * @author Mario Fokken
 * @since 2021-06-16
 */
public class ActorColourMap extends LinkedHashMap<Actor, Colour> {

    /**
     * Creates a PlayerColourMap using
     * a ActorPlayerMap
     *
     * @param map ActorPlayerMap
     *
     * @return A mapping of Player and Colour
     *
     * @author Mario Fokken
     * @since 2021-06-02
     */
    public Map<Player, Colour> makePlayerColourMap(ActorPlayerMap map) {
        Map<Player, Colour> result = new HashMap<>();
        for (Actor a : keySet()) result.put(map.get(a), get(a));
        return result;
    }

    /**
     * Puts the user and a random
     * colour into the map
     *
     * @param user The Actor to be put into the map
     */
    public void put(Actor user) {
        super.put(user, Util.randomColour());
    }
}

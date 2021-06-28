package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.Colour;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActorColourMapTest {

    @Test
    void test() {
        ActorColourMap map = new ActorColourMap();
        Actor user = new AIDTO(AI.Difficulty.EASY);
        Colour colour = Colour.GREEN;
        map.put(user);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(map.get(user)));
        map.clear();
        map.put(user, colour);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(colour));

        ActorPlayerMap map2 = new ActorPlayerMap();
        map2.put(user, Player.PLAYER_1);
        assertTrue(map.makePlayerColourMap(map2).containsKey(Player.PLAYER_1));
        assertTrue(map.makePlayerColourMap(map2).containsValue(colour));
    }
}

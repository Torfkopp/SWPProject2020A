package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.Colour;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserOrDummyColourMapTest {

    @Test
    void test() {
        UserOrDummyColourMap map = new UserOrDummyColourMap();
        UserOrDummy user = new AIDTO(AI.Difficulty.EASY);
        Colour colour = Colour.GREEN;
        map.put(user);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(map.get(user)));
        map.clear();
        map.put(user, colour);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(colour));

        UserOrDummyPlayerMap map2 = new UserOrDummyPlayerMap();
        map2.put(user, Player.PLAYER_1);
        assertTrue(map.makePlayerColourMap(map2).containsKey(Player.PLAYER_1));
        assertTrue(map.makePlayerColourMap(map2).containsValue(colour));
    }
}

package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserOrDummyPlayerMapTest {

    @Test
    void test() {
        UserOrDummyPlayerMap map = new UserOrDummyPlayerMap();
        UserOrDummy user = new AIDTO(AI.Difficulty.EASY);
        map.put(user, Player.PLAYER_1);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(Player.PLAYER_1));
    }
}

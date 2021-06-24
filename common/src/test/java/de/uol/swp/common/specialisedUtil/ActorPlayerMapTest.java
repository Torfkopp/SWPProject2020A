package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActorPlayerMapTest {

    @Test
    void test() {
        ActorPlayerMap map = new ActorPlayerMap();
        Actor user = new AIDTO(AI.Difficulty.EASY);
        map.put(user, Player.PLAYER_1);
        assertTrue(map.containsKey(user));
        assertTrue(map.containsValue(Player.PLAYER_1));
    }
}

package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActorPairTest {

    @Test
    void test() {
        Actor user = new AIDTO(AI.Difficulty.EASY);
        Actor user2 = new AIDTO(AI.Difficulty.EASY);
        ActorPair pair = new ActorPair(user, user2);

        assertEquals(user, pair.getActor1());
        assertEquals(user2, pair.getActor2());
    }
}

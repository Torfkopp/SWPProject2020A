package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserOrDummyPairTest {

    @Test
    void test() {
        UserOrDummy user = new AIDTO(AI.Difficulty.EASY);
        UserOrDummy user2 = new AIDTO(AI.Difficulty.EASY);
        UserOrDummyPair pair = new UserOrDummyPair(user, user2);

        assertEquals(pair.getUser1(), user);
        assertEquals(pair.getUser2(), user2);
    }
}

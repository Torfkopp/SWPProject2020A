package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserOrDummySetTest {

    @Test
    void test() {
        UserOrDummySet set = new UserOrDummySet();
        UserOrDummy user = new AIDTO(AI.Difficulty.EASY);
        set.add(user);
        assertTrue(set.remove(user));
    }
}

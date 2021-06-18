package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserOrDummyListTest {

    @Test
    void test() {
        UserOrDummyList list = new UserOrDummyList();
        UserOrDummy user = new AIDTO(AI.Difficulty.EASY);
        list.add(user);
        assertTrue(list.size() > 0);
        assertTrue(list.contains(user));
    }
}

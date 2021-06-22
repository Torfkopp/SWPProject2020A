package de.uol.swp.common.specialisedUtil;

import de.uol.swp.common.user.AIDTO;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserOrDummySetTest {

    @Test
    void test() {
        UserOrDummySet set = new UserOrDummySet();
        UserOrDummy user = new AIDTO("Iggy");
        UserOrDummy user2 = new AIDTO("Danny");
        UserOrDummy user3 = new AIDTO("Coco Jumbo");

        set.add(user);
        set.add(user2);
        set.add(user3);
        assertEquals(user, set.get(0));
        assertEquals(user2, set.get(1));
        assertEquals(user3, set.get(2));
        assertTrue(set.remove(user2));
        assertEquals(user3, set.get(1));
        assertTrue(set.remove(user));
        assertEquals(user3, set.get(0));
        assertTrue(set.remove(user3));
        assertFalse(set.remove(user));
        assertFalse(set.remove(user2));
        assertFalse(set.remove(user3));
    }
}

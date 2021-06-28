package de.uol.swp.client.specialisedUtil;

import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsersListTest {

    @Test
    void test() {
        UsersList list = new UsersList();
        List<User> l = new ArrayList<>();
        User user1 = new UserDTO(69, "Günther", "", "");
        User user2 = new UserDTO(42, "Peter", "", "");
        l.add(user1);
        l.add(user2);

        list.add(user1.getUsername());
        assertTrue(list.remove(user1.getUsername()));
        assertFalse(list.remove(user1.getUsername()));

        list.update(l);
        assertTrue(list.remove(user1.getUsername()));
        assertTrue(list.remove(user2.getUsername()));
        assertFalse(list.remove(user1.getUsername()));
    }
}

package de.uol.swp.server.lobby;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LobbyMapTest {

    @Test
    void test() {
        LobbyMap map = new LobbyMap();
        LobbyName name = new LobbyName("Test");
        User user = new UserDTO(69, "Rüdiger", "", "");
        map.create(name, user, "");
        assertTrue(map.containsKey(name));
        try {
            map.create(name, null, "asfuiasdfh");
            fail();
        } catch (IllegalArgumentException exception) { assertTrue(true); }

        assertEquals(map.getLobby(name).get(), map.getLobby(name, "").get());
        map.getSimpleLobbies();

        assertTrue(map.isInALobby(user));
        assertFalse(map.isInALobby(new UserDTO(42, "Karl Mags", "", "")));

        map.drop(name);
        assertFalse(map.containsKey(name));
        try {
            map.drop(name);
            fail();
        } catch (IllegalArgumentException exception) { assertTrue(true); }
    }
}

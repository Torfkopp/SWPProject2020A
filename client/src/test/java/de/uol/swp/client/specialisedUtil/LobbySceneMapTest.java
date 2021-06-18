package de.uol.swp.client.specialisedUtil;

import de.uol.swp.common.lobby.LobbyName;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LobbySceneMapTest {

    @Test
    void test(){
        LobbySceneMap map = new LobbySceneMap();
        LobbyName name = new LobbyName("Test");
        LobbyName name2 = new LobbyName("Test2");
        Scene scene = new Scene(new Parent() {});
        map.put(name, scene);
        assertFalse(map.isEmpty());
        assertTrue(map.containsKey(name));
        assertEquals(map.get(name), scene);
        List<LobbyName> list = new ArrayList<>();
        list.add(name);
        list.add(name2);
        map.update(list);
        assertNotNull(map.get(name));
        assertNull(map.get(name2));
    }
}

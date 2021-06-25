package de.uol.swp.client.specialisedUtil;

import de.uol.swp.common.lobby.LobbyName;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LobbyStageMapTest {

    @Test
    void test() {
        LobbyStageMap map = new LobbyStageMap();
        Stage stage = null;
        LobbyName name = new LobbyName("test");

        map.put(name, stage);
        assertFalse(map.isEmpty());
        map.remove(name, stage);
        assertTrue(map.isEmpty());
        map.put(name, stage);
        map.clear();
        assertTrue(map.isEmpty());
    }
}

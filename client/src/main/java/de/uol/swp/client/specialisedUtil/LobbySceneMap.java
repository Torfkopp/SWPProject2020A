package de.uol.swp.client.specialisedUtil;

import de.uol.swp.common.lobby.LobbyName;
import javafx.scene.Scene;

import java.util.HashMap;
import java.util.List;

/**
 * Specialised Class to map a
 * LobbyName to its scene
 *
 * @author Mario Fokken
 * @since 2021-06-18
 */
public class LobbySceneMap extends HashMap<LobbyName, Scene> {

    /**
     * Updates the map by adding a mapping of
     * every not contained lobbyName and null
     *
     * @param names A list of lobbyNames
     */
    public void update(List<LobbyName> names) {
        for (LobbyName name : names) if (!containsKey(name)) put(name, null);
    }
}

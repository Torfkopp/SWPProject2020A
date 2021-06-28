package de.uol.swp.client.specialisedUtil;

import de.uol.swp.common.lobby.LobbyName;
import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.util.HashMap;

/**
 * Specialised Class to map a
 * LobbyName to its stage
 *
 * @author Mario Fokken
 * @since 2021-06-17
 */
public class LobbyStageMap extends HashMap<LobbyName, Stage> {

    /**
     * Closes the window of a lobby
     *
     * @param lobby The name of the lobby to close
     */
    public void close(LobbyName lobby) {
        if (containsKey(lobby)) {
            Platform.runLater(() -> {
                Window window = get(lobby);
                if (window != null) window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
                remove(lobby);
            });
        }
    }
}

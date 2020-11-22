package de.uol.swp.client.lobby;

import de.uol.swp.client.AbstractPresenter;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

/**
 * Manages the lobby menu
 *
 * @author Mario Fokken and Marvin Drees
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-21
 *
 */
public class LobbyPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LobbyView.fxml";

    @FXML
    private ListView<String> usersView;

    /**
     * Default Constructor
     *
     * @since 2020-11-21
     */
    public LobbyPresenter() {

    }

}

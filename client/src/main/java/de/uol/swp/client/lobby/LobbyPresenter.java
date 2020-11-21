package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger LOG = LogManager.getLogger(de.uol.swp.client.lobby.LobbyPresenter.class);

    private ObservableList<String> users;

    private User joinedUser;

    @Inject
    private LobbyService lobbyService;

    @FXML
    private ListView<String> usersJoinedView;


    /**
     * Handles new joined users
     *
     * If a new UserJoinedLobbyMessage object is posted to the EventBus the name of the newly
     * joined user is appended to the user list in the main menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "New user {@literal
     * <Username>} joined." is displayed in the log.
     *
     * @param message the UserJoinedLobbyMessage object seen on the EventBus
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-11-21
     */
    @Subscribe
    public void newUser(UserJoinedLobbyMessage message) {

        LOG.debug("New user " + message.getUser().getUsername() + " joined");
        Platform.runLater(() -> {
                    if (users != null && joinedUser != null && !joinedUser.getUsername().equals(message.getUser().getUsername()))
                        users.add(message.getUser().getUsername());
            });
    }

}

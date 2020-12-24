package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Manages the lobby's menu
 *
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-21
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<String> lobbyMembers;

    private User creator;

    @FXML
    private ListView<String> chatView;

    @FXML
    private ListView<String> membersView;

    /**
     * Default Constructor
     *
     * @since 2020-11-21
     */
    public LobbyPresenter() {
    }

    /**
     * Handles a new list of users
     * <p>
     * If a new AllOnlineUsersResponse object is posted to the EventBus, the names
     * of the currently logged in members are put into the list of lobby members.
     * Furthermore, if the LOG-Level is set to DEBUG, the message "Update of user
     * list" with the names of all currently logged in users is displayed in the
     * log.
     *
     * @param allLobbyMembersResponse AllLobbyMembersResponse object seen on the EventBus
     * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
     * @since 2020-11-22
     */
    @Subscribe
    public void onAllLobbyMembersResponse(AllLobbyMembersResponse allLobbyMembersResponse) {
        LOG.debug("Update of lobby member list " + allLobbyMembersResponse.getUsers());
        updateUsersList(allLobbyMembersResponse.getUsers());
    }

    /**
     * Handles new joined users
     * <p>
     * If a new UserJoinedLobbyMessage object is posted to the EventBus, the name of the newly
     * joined user is appended to the user list in the lobby menu.
     * Furthermore, if the LOG-Level is set to DEBUG, the message "New user {@literal
     * <Username>} joined Lobby." is displayed in the log.
     *
     * @param message the UserJoinedLobbyMessage object seen on the EventBus
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-11-22
     */
    @Subscribe
    public void onUserJoinedLobbyMessage(UserJoinedLobbyMessage message) {
        LOG.debug("New user " + message.getUser().getUsername() + " joined Lobby " + message.getName());
        Platform.runLater(() -> {
            if (lobbyMembers != null && creator != null && !creator.getUsername().equals(message.getUser().getUsername()))
                lobbyMembers.add(message.getUser().getUsername());
        });
    }

    /**
     * Updates the lobby's member list according to the list given
     * <p>
     * This method clears the entire member list and then adds the name of each user
     * in the list given to the lobby's member list.
     * If there is no member list, it creates one.
     *
     * @param userLobbyList A list of UserDTO objects including all currently logged in
     *                      users
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.user.UserDTO
     * @since 2020-11-22
     */
    private void updateUsersList(List<UserDTO> userLobbyList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (lobbyMembers == null) {
                lobbyMembers = FXCollections.observableArrayList();
                membersView.setItems(lobbyMembers);
            }
            lobbyMembers.clear();
            userLobbyList.forEach(u -> lobbyMembers.add(u.getUsername()));
        });
    }

    @FXML
    private void onSendMessageButton(ActionEvent event) {
    }

    @FXML
    private void onDeleteMessageButton(ActionEvent event) {
    }

    @FXML
    private void onEditMessageButton(ActionEvent event) {
    }
}

package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.AbstractPresenter;
import de.uol.swp.client.lobby.event.LobbyReadyEvent;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.lobby.response.UserJoinLobbyResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages the lobby menu
 *
 * @see de.uol.swp.client.AbstractPresenter
 * @since 2020-11-21
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyPresenter extends AbstractPresenter {

    public static final String fxml = "/fxml/LobbyView.fxml";

    @Inject
    private LobbyService lobbyService;

    private static final Logger LOG = LogManager.getLogger(LobbyPresenter.class);

    private ObservableList<String> lobbyMembers;

    private User loggedInUser;

    private String lobbyName;
    private String test;

    @FXML
    private ListView<String> membersView = new ListView<String>();

    @FXML
    private ListView<String> chatView;

    /**
     * Default Constructor
     *
     * @since 2020-11-21
     */
    public LobbyPresenter() {
        lobbyName = "Baum";
    }

    /**
     * Handles new list of users
     *
     * If a new AllOnlineUsersResponse object is posted to the EventBus the names
     * of currently logged in members are put onto the list of lobby members.
     * Furthermore if the LOG-Level is set to DEBUG the message "Update of user
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
     * If a new UserJoinedLobbyMessage object is posted to the EventBus the name of the newly
     * joined user is appended to the user list in the lobby menu.
     * Furthermore if the LOG-Level is set to DEBUG the message "New user {@literal
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
            if (lobbyMembers != null)
                lobbyMembers.add(message.getUser().getUsername());
        });
    }

    @Subscribe
    private void onUserJoinLobbyResponse(UserJoinLobbyResponse rsp){
        if (lobbyName == null && loggedInUser == null) {
                lobbyName = rsp.getName();
                loggedInUser = rsp.getUser();
                test = "ggfdgreg";

                System.out.println("Gesetzt");
                System.out.println(lobbyName);
                System.out.println(loggedInUser.getUsername());
        } else {
            System.out.println("NOTNULL");
        }
        eventBus.post(new LobbyReadyEvent(rsp.getName()));

    }
    /**
     * Updates the lobby's member list according to the list given
     *
     * This method clears the entire member list and then adds the name of each user
     * in the list given to the lobby's member list. If there is no member list
     * this it creates one.
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @param userLobbyList A list of UserDTO objects including all currently logged in
     *                 users
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

    /**
     * Handles a click on the LeaveLobby button
     * <p>
     * Method called when the leaveLobby button is pressed.
     * If the leaveLobby button is pressed by the creator of the lobby,
     * this method requests the lobby service to delete the lobby.
     * If it is not the creator this method requests
     * the lobby service to leave the lobby.
     *
     * @param event The ActionEvent created by pressing the leave lobby Button
     * @since 2020-12-14
     */
    @FXML
    void onLeaveLobby(ActionEvent event) {
        System.out.println(lobbyName);
        System.out.println(loggedInUser);

        if (lobbyName != null || loggedInUser != null) {
            lobbyService.leaveLobby(lobbyName, (UserDTO) loggedInUser);
        }
        ((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
    }

    @FXML
    private void onSendMessageButton(ActionEvent event){
        System.out.println("fsd");
        System.out.println(lobbyName); // prints null?????????????
        lobbyName = "Weird";
        System.out.println(lobbyName); //now it's set again
    }

    @FXML
    private void onDeleteMessageButton(ActionEvent event){
        System.out.println(lobbyName);
    }

    @FXML
    private void onEditMessageButton(ActionEvent event){
        Platform.runLater(()-> {
            System.out.println(test);
        });
    }
}

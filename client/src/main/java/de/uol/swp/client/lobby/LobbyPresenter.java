package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenterWithChat;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;

import java.util.List;

/**
 * Manages the lobby's menu
 *
 * @see de.uol.swp.client.AbstractPresenter
 * @see de.uol.swp.client.AbstractPresenterWithChat
 * @since 2020-11-21
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyPresenter extends AbstractPresenterWithChat {

    public static final String fxml = "/fxml/LobbyView.fxml";

    private ObservableList<Pair<String, String>> lobbyMembers;
    private User owner;

    @FXML
    private ListView<Pair<String, String>> membersView;

    @FXML
    private Canvas gameMapCanvas;

    private Window window;

    /**
     * Constructor
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.AbstractPresenterWithChat
     * @since 2021-01-02
     */
    public LobbyPresenter() {
        super.init(LogManager.getLogger(LobbyPresenter.class));
    }

    /**
     * Initialises the Presenter by setting up the membersView.
     *
     * @implNote Called automatically by JavaFX
     * @author Temmo Junkhoff
     * @author Timo Gerken
     * @since 2021-01-18
     */
    @Override
    @FXML
    public void initialize() {
        super.initialize();
        membersView.setCellFactory(lv -> new ListCell<Pair<String, String>>() {
            @Override
            protected void updateItem(Pair<String, String> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getValue());
            }
        });
        renderGameMap();
    }

    @Override
    @Subscribe
    protected void onCreatedChatMessageMessage(CreatedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(super.lobbyName)) {
            super.onCreatedChatMessageMessage(msg);
        }
    }

    @Override
    @Subscribe
    protected void onDeletedChatMessageMessage(DeletedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(super.lobbyName)) {
            super.onDeletedChatMessageMessage(msg);
        }
    }

    @Override
    @Subscribe
    protected void onEditedChatMessageMessage(EditedChatMessageMessage msg) {
        if (msg.isLobbyChatMessage() && msg.getLobbyName().equals(super.lobbyName)) {
            super.onEditedChatMessageMessage(msg);
        }
    }

    @Override
    @Subscribe
    protected void onAskLatestChatMessageResponse(AskLatestChatMessageResponse rsp) {
        if (rsp.getLobbyName() != null && rsp.getLobbyName().equals(super.lobbyName)) {
            super.onAskLatestChatMessageResponse(rsp);
        }
    }

    /**
     * Handles LobbyUpdateEvents on the EventBus
     * <p>
     * If a new LobbyUpdateEvent is posted to the EventBus, this method checks
     * whether the lobbyName and/or loggedInUser attributes of the current
     * LobbyPresenter are null. If they are, it sets these attributes to the
     * values found in the LobbyUpdateEvent.
     * Also makes sure that the lobby will be left gracefully should the window
     * be closed without using the Leave Lobby button.
     *
     * @param lobbyUpdateEvent the lobby update event
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.lobby.event.LobbyUpdateEvent
     * @since 2021-01-07
     */
    @Subscribe
    private void onLobbyUpdateEvent(LobbyUpdateEvent lobbyUpdateEvent) {
        if (super.lobbyName == null || loggedInUser == null) {
            super.lobbyName = lobbyUpdateEvent.getLobbyName();
            super.loggedInUser = lobbyUpdateEvent.getUser();
            super.chatService.askLatestMessages(10, super.lobbyName);
        }
        if (this.window == null) {
            this.window = membersView.getScene().getWindow();
        }
        this.window.setOnCloseRequest(event -> closeWindow());
    }

    /**
     * Handles a new list of users
     * <p>
     * If a new AllOnlineUsersResponse object is posted onto the EventBus, the names
     * of the currently logged in members are put into the list of lobby members.
     * Furthermore, if the LOG-Level is set to DEBUG, the message "Update of user
     * list" with the names of all currently logged in users as well as the message
     * "Owner of this lobby: " with the name of the lobby's owner is displayed in the
     * log.
     *
     * @param allLobbyMembersResponse AllLobbyMembersResponse object seen on the EventBus
     * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
     * @since 2021-01-05
     */
    @Subscribe
    private void onAllLobbyMembersResponse(AllLobbyMembersResponse allLobbyMembersResponse) {
        LOG.debug("Update of lobby member list " + allLobbyMembersResponse.getUsers());
        LOG.debug("Owner of this lobby: " + allLobbyMembersResponse.getOwner().getUsername());
        this.owner = allLobbyMembersResponse.getOwner();
        updateUsersList(allLobbyMembersResponse.getUsers());
    }

    /**
     * Handles new joined users
     * <p>
     * If a new UserJoinedLobbyMessage object is posted onto the EventBus, the name of the newly
     * joined user is appended to the user list in the lobby menu.
     * Furthermore, if the LOG-Level is set to DEBUG, the message "New user {@literal
     * <Username>} joined Lobby." is displayed in the log.
     *
     * @param message the UserJoinedLobbyMessage object seen on the EventBus
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-11-22
     */
    @Subscribe
    private void onUserJoinedLobbyMessage(UserJoinedLobbyMessage message) {
        LOG.debug("New user " + message.getUser().getUsername() + " joined Lobby " + message.getName());
        Platform.runLater(() -> {
            if (lobbyMembers != null && loggedInUser != null && !loggedInUser.getUsername().equals(message.getUser().getUsername()))
                lobbyMembers.add(new Pair(message.getUser().getUsername(), message.getUser().getUsername()));
        });
    }

    /**
     * Handles users leaving a lobby
     * <p>
     * If a new UserLeftLobbyMessage object is posted onto the EventBus and
     * the leaving user was the lobby owner, the entire list of member is
     * requested from the server to ensure a new owner is decided. If the
     * leaving user wasn't the owner, their name is removed from the list
     * of members.
     * Furthermore, if the LOG-Level is set to DEBUG, the message "Owner/User
     * {@literal <Username>} left Lobby {@literal <Lobbyname>}" is displayed
     * in the log, depending on whether the owner or a normal user left.
     *
     * @param message the UserLeftLobbyMessage object seen on the EventBus
     * @author Temmo Junkhoff
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2021-01-05
     */
    @Subscribe
    private void onUserLeftLobbyMessage(UserLeftLobbyMessage message) {
        if (message.getUser().getUsername().equals(owner.getUsername())) {
            LOG.debug("Owner " + message.getUser().getUsername() + " left Lobby " + message.getName());
            lobbyService.retrieveAllLobbyMembers(lobbyName);
        } else {
            LOG.debug("User " + message.getUser().getUsername() + " left Lobby " + message.getName());
        }
        Platform.runLater(() -> lobbyMembers.remove(findMember(message.getUser().getUsername())));
    }

    /**
     * Updates the lobby's member list according to the list given
     * <p>
     * This method clears the entire member list and then adds the name of each user
     * in the list given to the lobby's member list.
     * If there is no member list, it creates one.
     * If the owner is found among the users, their username is appended with a
     * crown emoji.
     *
     * @param userLobbyList A list of UserDTO objects including all currently logged in
     *                      users
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.user.UserDTO
     * @since 2021-01-05
     */
    private void updateUsersList(List<User> userLobbyList) {
        // Attention: This must be done on the FX Thread!
        Platform.runLater(() -> {
            if (lobbyMembers == null) {
                lobbyMembers = FXCollections.observableArrayList();
                membersView.setItems(lobbyMembers);
            }
            lobbyMembers.clear();

            userLobbyList.forEach(u -> {
                String username = u.getUsername();
                Pair<String, String> item = new Pair<>(username,
                        username.equals(this.owner.getUsername()) ? username + "\uD83D\uDC51" : username);
                lobbyMembers.add(item);
            });
        });
    }

    /**
     * Handles a click on the LeaveLobby button
     * <p>
     * Method called when the leaveLobby button is pressed.
     * If the leaveLobby button is pressed this method requests
     * the lobby service to leave the lobby.
     *
     * @param event The ActionEvent created by pressing the leave lobby Button
     * @since 2020-12-14
     */
    @FXML
    private void onLeaveLobbyButtonPressed(ActionEvent event) {
        closeWindow();
    }

    /**
     * Helper function to let the user leave the lobby and close the window
     * Also clears the EventBus of the instance to avoid NullPointerExceptions.
     *
     * @author Temmo Junkhoff
     * @since 2021-01-06
     */
    private void closeWindow() {
        if (lobbyName != null || loggedInUser != null) {
            lobbyService.leaveLobby(lobbyName, loggedInUser);
        }
        ((Stage) window).close();
        clearEventBus();
    }

    /**
     * Helper function to find the Pair for a given key
     *
     * @param name the key of the pair that should be returned
     * @return the pair matched by the name
     * @author Temmo Junkhoff
     * @author Timo Gerken
     * @since 2021-01-18
     */
    private Pair<String, String> findMember(String name) {
        for (int i = 0; i < lobbyMembers.size(); i++) {
            if (lobbyMembers.get(i).getKey().equals(name)) return lobbyMembers.get(i);
        }
        return null;
    }

    private void renderGameMap() {
        //Terrains

        //Game Grid?
        //Edges
        //Intersections
        //Points and Robber

    }
}

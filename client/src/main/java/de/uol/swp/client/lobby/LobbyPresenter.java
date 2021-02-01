package de.uol.swp.client.lobby;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenterWithChat;
import de.uol.swp.client.IGameRendering;
import de.uol.swp.client.lobby.event.CloseLobbiesViewEvent;
import de.uol.swp.client.lobby.event.LobbyUpdateEvent;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.game.map.GameMapManagement;
import de.uol.swp.common.game.message.DiceCastMessage;
import de.uol.swp.common.game.message.NextPlayerMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.StartSessionRequest;
import de.uol.swp.common.lobby.request.UserReadyRequest;
import de.uol.swp.common.lobby.response.AllLobbyMembersResponse;
import de.uol.swp.common.lobby.response.RemoveFromLobbiesResponse;
import de.uol.swp.common.lobby.response.UpdateInventoryResponse;
import de.uol.swp.common.message.RequestMessage;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;

import java.util.*;

/**
 * Manages the lobby's menu
 *
 * @see de.uol.swp.client.AbstractPresenter
 * @see de.uol.swp.client.AbstractPresenterWithChat
 * @since 2020-11-21
 */
@SuppressWarnings("UnstableApiUsage")
public class LobbyPresenter extends AbstractPresenterWithChat implements IGameRendering {

    public static final String fxml = "/fxml/LobbyView.fxml";
    private static final CloseLobbiesViewEvent closeLobbiesViewEvent = new CloseLobbiesViewEvent();
    private ObservableList<Pair<String, String>> lobbyMembers;
    private ObservableList<Pair<String, String>> resourceList;
    private User owner;
    private Set<User> readyUsers;

    @FXML
    private ListView<Pair<String, String>> membersView;
    @FXML
    private CheckBox readyCheckBox;
    @FXML
    private Button startSession;
    @FXML
    private Button endTurn;
    @FXML
    private Text text;
    @FXML
    private Canvas gameMapCanvas;
    @FXML
    private VBox playField;
    @FXML
    private ListView<Pair<String, String>> inventoryView;

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
        membersView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, String> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getValue());
            }
        });
        inventoryView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, String> item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getValue() + " " + item.getKey()); // looks like: "1 Brick"
            }
        });
    }

    @Override
    @Subscribe
    protected void onAskLatestChatMessageResponse(AskLatestChatMessageResponse rsp) {
        if (rsp.getLobbyName() != null && rsp.getLobbyName().equals(super.lobbyName)) {
            super.onAskLatestChatMessageResponse(rsp);
        }
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
     *
     * @return the pair matched by the name
     *
     * @author Temmo Junkhoff
     * @author Timo Gerken
     * @since 2021-01-19
     */
    private Pair<String, String> findMember(String name) {
        for (Pair<String, String> lobbyMember : lobbyMembers) {
            if (lobbyMember.getKey().equals(name)) return lobbyMember;
        }
        return null;
    }

    /**
     * Handles a new list of users
     * <p>
     * If a new AllOnlineUsersResponse object is posted onto the EventBus, the names
     * of the currently logged in members are put into the list of lobby members.
     * The owner attribute is set and the set of ready Users is updated.
     * Additionally, the state of the "Start Session" button is set
     * appropriately.
     * <p>
     * Furthermore, if the LOG-Level is set to DEBUG, the messages "Update of user
     * list" with the names of all currently logged in users, "Owner of this
     * lobby: " with the name of the lobby's owner, and "Update of ready users "
     * are displayed in the log.
     *
     * @param allLobbyMembersResponse AllLobbyMembersResponse object seen on the EventBus
     *
     * @see de.uol.swp.common.lobby.response.AllLobbyMembersResponse
     * @since 2021-01-19
     */
    @Subscribe
    private void onAllLobbyMembersResponse(AllLobbyMembersResponse allLobbyMembersResponse) {
        LOG.debug("Update of lobby member list " + allLobbyMembersResponse.getUsers());
        LOG.debug("Owner of this lobby: " + allLobbyMembersResponse.getOwner().getUsername());
        LOG.debug("Update of ready users " + allLobbyMembersResponse.getReadyUsers());
        this.owner = allLobbyMembersResponse.getOwner();
        this.readyUsers = allLobbyMembersResponse.getReadyUsers();
        updateUsersList(allLobbyMembersResponse.getUsers());
        setStartSessionButtonState();
    }

    /**
     * Handles a DiceCastMessage
     * <p>
     * If a new DiceCastMessage object is posted onto the EventBus,
     * this method is called.
     * It enables the endTurnButton.
     *
     * @param message The DiceCastMessage object seen on the EventBus
     *
     * @see de.uol.swp.common.game.message.DiceCastMessage
     * @since 2021-01-15
     */
    @Subscribe
    private void onDiceCastMessage(DiceCastMessage message) {
        setEndTurnButtonState(message.getUser());
    }

    /**
     * Method called when the EndTurnButton is pressed
     * <p>
     * If the EndTurnButton is pressed, this method requests the LobbyService
     * to end the current turn.
     *
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2021-1-15
     */
    public void onEndTurnButtonPressed() {
        lobbyService.endTurn(loggedInUser, lobbyName);
        lobbyService.updateInventory(lobbyName, loggedInUser);
    }

    /**
     * Handles a click on the LeaveLobby button
     * <p>
     * Method called when the leaveLobby button is pressed.
     * If the leaveLobby button is pressed this method requests
     * the lobby service to leave the lobby.
     *
     * @since 2020-12-14
     */
    @FXML
    private void onLeaveLobbyButtonPressed() {
        closeWindow();
    }

    /**
     * Handles LobbyUpdateEvents on the EventBus
     * <p>
     * If a new LobbyUpdateEvent is posted to the EventBus, this method checks
     * whether the lobbyName, loggedInUser, or readyUsers attributes of the current
     * LobbyPresenter are null. If they are, it sets these attributes to the
     * values found in the LobbyUpdateEvent or creates a new, empty instance.
     * Also makes sure that the lobby will be left gracefully should the window
     * be closed without using the Leave Lobby button.
     *
     * @param lobbyUpdateEvent the lobby update event
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.lobby.event.LobbyUpdateEvent
     * @since 2021-01-19
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
        if (this.readyUsers == null) {
            this.readyUsers = new HashSet<>();
        }
        this.window.setOnCloseRequest(event -> closeWindow());
    }

    /**
     * Handles a NextPlayerMessage
     * <p>
     * If a new NextPlayerMessage object is posted onto the EventBus,
     * this method is called.
     * It changes the text of a textField to state whose turn it is.
     *
     * @param message The NextPlayerMessage object seen on the EventBus
     */
    @Subscribe
    private void onNextPlayerMessage(NextPlayerMessage message) {
        setTextText(message.getActivePlayer());
        //In here to test the endTurnButton
        onDiceCastMessage(new DiceCastMessage(message.getLobby(), message.getActivePlayer()));
    }

    /**
     * Handles the click on the ReadyCheckBox
     * <p>
     * Method called when the Ready Checkbox is clicked. It checks whether the
     * CheckBox is selected or not and then posts a UserReadyRequest onto the
     * EventBus with the appropriate parameters.
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-19
     */
    @FXML
    private void onReadyCheckBoxClicked() {
        boolean isReady = readyCheckBox.isSelected();
        RequestMessage userReadyRequest = new UserReadyRequest(this.lobbyName, this.loggedInUser, isReady);
        eventBus.post(userReadyRequest);
    }

    /**
     * Handles leaving all Lobbies when a user logged out
     * <p>
     * If a new RemoveFromLobbiesResponse is posted onto the EventBus the
     * method leaveLobby in LobbyService is called for every Lobby the user
     * is in.
     *
     * @param response the RemoveFromLobbiesResponse seen on the EventBus
     *
     * @author Finn Haase
     * @author Aldin Dervisi
     * @see de.uol.swp.common.lobby.response.RemoveFromLobbiesResponse
     * @since 2021-01-28
     */
    @Subscribe
    private void onRemoveFromLobbiesResponse(RemoveFromLobbiesResponse response) {
        for (Map.Entry<String, Lobby> entry : response.getLobbiesWithUser().entrySet()) {
            lobbyService.leaveLobby(entry.getKey(), loggedInUser);
        }
    }

    /**
     * Handles a click on the StartSession Button
     * <p>
     * Method called when the StartSessionButton is pressed.
     * The Method posts a StartSessionRequest including the lobby name and the
     * logged in user onto the EventBus.
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-20
     */
    @FXML
    private void onStartSessionButtonPressed() {
        RequestMessage startSessionRequest = new StartSessionRequest(this.lobbyName, this.loggedInUser);
        eventBus.post(startSessionRequest);
    }

    /**
     * Handles a StartSessionMessage found on the EventBus
     * <p>
     * Sets the play field visible.
     *
     * @param startSessionMessage The StartSessionMessage found on the EventBus
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-21
     */
    @Subscribe
    private void onStartSessionMessage(StartSessionMessage startSessionMessage) {
        if (startSessionMessage.getName().equals(this.lobbyName)) {
            Platform.runLater(() -> {
                playField.setVisible(true);
                //This Line needs to be changed/ removed in the Future
                drawGameMap(new GameMapManagement(), gameMapCanvas);
                setTextText(startSessionMessage.getUser());
                //In here to test the endTurnButton.
                eventBus.post(new DiceCastMessage(startSessionMessage.getName(), startSessionMessage.getUser()));
                lobbyService.updateInventory(lobbyName, loggedInUser);
            });
        }
    }

    @Subscribe
    private void onUpdateInventoryResponse(UpdateInventoryResponse resp) {
        if (resp.getLobbyName().equals(this.lobbyName)) {
            if (resourceList == null) {
                resourceList = FXCollections.observableArrayList();
                inventoryView.setItems(resourceList);
            }
            resourceList.clear();
            for (Map.Entry<String, Integer> entry : resp.getResourceMap().entrySet()) {
                Pair<String, String> resource = new Pair<>(entry.getKey(), entry.getValue().toString());
                resourceList.add(resource);
            }
            for (Map.Entry<String, Boolean> entry : resp.getArmyAndRoadMap().entrySet()) {
                Pair<String, String> property = new Pair<>(entry.getKey(), entry.getValue() ? "Has" : "Not");
                resourceList.add(property);
            }
        }
    }

    /**
     * Handles new joined users
     * <p>
     * If a new UserJoinedLobbyMessage object is posted onto the EventBus, the name of the newly
     * joined user is appended to the user list in the lobby menu.
     * The state of the "Start Session" button is updated accordingly.
     * Furthermore, if the LOG-Level is set to DEBUG, the message "New user {@literal
     * <Username>} joined Lobby." is displayed in the log.
     *
     * @param message the UserJoinedLobbyMessage object seen on the EventBus
     *
     * @see de.uol.swp.common.lobby.message.UserJoinedLobbyMessage
     * @since 2020-11-22
     */
    @Subscribe
    private void onUserJoinedLobbyMessage(UserJoinedLobbyMessage message) {
        LOG.debug("New user " + message.getUser().getUsername() + " joined Lobby " + message.getName());
        Platform.runLater(() -> {
            if (lobbyMembers != null && loggedInUser != null && !loggedInUser.getUsername().equals(
                    message.getUser().getUsername()))
                lobbyMembers.add(new Pair<>(message.getUser().getUsername(), message.getUser().getUsername()));
            setStartSessionButtonState();
        });
    }

    /**
     * Handles users leaving a lobby
     * <p>
     * If a new UserLeftLobbyMessage object is posted onto the EventBus and
     * the leaving user was the lobby owner, the entire list of member is
     * requested from the server to ensure a new owner is decided. If the
     * leaving user wasn't the owner, their name is removed from the list
     * of members. If they were marked as ready, they are removed from the
     * Set of ready users. Afterwards, the "Start Session" button is set to
     * the appropriate state.
     * <p>
     * Furthermore, if the LOG-Level is set to DEBUG, the message "Owner/User
     * {@literal <Username>} left Lobby {@literal <Lobbyname>}" is displayed
     * in the log, depending on whether the owner or a normal user left.
     *
     * @param message The UserLeftLobbyMessage object seen on the EventBus
     *
     * @author Temmo Junkhoff
     * @see de.uol.swp.common.lobby.message.UserLeftLobbyMessage
     * @since 2021-01-20
     */
    @Subscribe
    private void onUserLeftLobbyMessage(UserLeftLobbyMessage message) {
        if (message.getUser().getUsername().equals(owner.getUsername())) {
            LOG.debug("Owner " + message.getUser().getUsername() + " left Lobby " + message.getName());
            lobbyService.retrieveAllLobbyMembers(lobbyName);
        } else {
            LOG.debug("User " + message.getUser().getUsername() + " left Lobby " + message.getName());
        }
        Platform.runLater(() -> {
            lobbyMembers.remove(findMember(message.getUser().getUsername()));
            readyUsers.remove(message.getUser());
            setStartSessionButtonState();
        });
    }

    /**
     * Handles the UserReadyMessage
     * <p>
     * If the UserReadyMessage belongs to this lobby, it calls the LobbyService
     * to retrieve all lobby members, which will also mark all ready users as
     * such.
     *
     * @param userReadyMessage The UserReadyMessage found on the EventBus
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-19
     */
    @Subscribe
    private void onUserReadyMessage(UserReadyMessage userReadyMessage) {
        if (userReadyMessage.getName().equals(this.lobbyName)) {
            lobbyService.retrieveAllLobbyMembers(this.lobbyName); // for updateUserList
        }
    }

    /**
     * Helper function that sets the disable state of the endTurnButton.
     * <p>
     * The button is only enabled to the active player when the
     * obligatory part of the turn is done.
     *
     * @author Alwin Bossert
     * @author Mario Fokken
     * @author Marvin Drees
     * @since 2021-01-23
     */
    private void setEndTurnButtonState(User player) {
        this.endTurn.setDisable(!super.loggedInUser.equals(player));
    }

    /**
     * Helper function that sets the Visible and Disable states of the "Start
     * Session" button.
     * <p>
     * The button is only ever visible to the lobby owner, and is only enabled
     * if there are 3 or more lobby members, and all members are marked as ready.
     *
     * @author Eric Vuong
     * @author Maximilian Lindner
     * @since 2021-01-20
     */
    private void setStartSessionButtonState() {
        if (super.loggedInUser.equals(this.owner)) {
            this.startSession.setVisible(true);
            this.startSession.setDisable(
                    this.readyUsers.size() < 3 || this.lobbyMembers.size() != this.readyUsers.size());
        } else {
            this.startSession.setDisable(true);
            this.startSession.setVisible(false);
        }
    }

    /**
     * Helper function that sets the text's text.
     * <p>
     * The text states whose turn it is.
     *
     * @author Alwin Bossert
     * @author Mario Fokken
     * @author Marvin Drees
     * @since 2021-01-23
     */
    private void setTextText(User player) {
        text.setText("It's " + player.getUsername() + "'s turn!");
    }

    /**
     * Updates the lobby's member list according to the list given
     * <p>
     * This method clears the entire member list and then adds the name of each user
     * in the list given to the lobby's member list.
     * If there is no member list, it creates one.
     * <p>
     * If a user is marked as ready in the readyUsers Set, their name is prepended
     * with a checkmark.
     * If the owner is found amongst the users, their username is appended with a
     * crown symbol.
     *
     * @param userLobbyList A list of User objects including all currently logged in
     *                      users
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore, it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.user.User
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
                if (readyUsers.contains(u)) {
                    username = "\u2713 " + username;
                }
                Pair<String, String> item = new Pair<>(u.getUsername(), u.getUsername().equals(
                        this.owner.getUsername()) ? username + " \uD83D\uDC51" : username);  //Leerzeile vor Krone hinzugefügt
                lobbyMembers.add(item);
            });
        });
    }
}

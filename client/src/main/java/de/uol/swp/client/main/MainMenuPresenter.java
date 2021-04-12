package de.uol.swp.client.main;

import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.AbstractPresenterWithChat;
import de.uol.swp.client.ChangeAccountDetails.event.ShowChangeAccountDetailsViewEvent;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.lobby.event.CloseLobbiesViewEvent;
import de.uol.swp.client.lobby.event.ShowLobbyViewEvent;
import de.uol.swp.common.game.message.GameCreatedMessage;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.response.*;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.GetOldSessionsRequest;
import de.uol.swp.common.user.response.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Manages the main menu
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.AbstractPresenter
 * @see de.uol.swp.client.AbstractPresenterWithChat
 * @since 2019-08-29
 */
@SuppressWarnings("UnstableApiUsage")
public class MainMenuPresenter extends AbstractPresenterWithChat {

    public static final String fxml = "/fxml/MainMenuView.fxml";
    public static final int MIN_HEIGHT = 550;
    public static final int MIN_WIDTH = 820;
    private static final Logger LOG = LogManager.getLogger(MainMenuPresenter.class);
    private static final CloseLobbiesViewEvent closeLobbiesViewEvent = new CloseLobbiesViewEvent();
    private static final ShowLoginViewEvent showLoginViewMessage = new ShowLoginViewEvent();

    @FXML
    private Label randomLobbyState;
    @FXML
    private ListView<Pair<String, String>> lobbyView;
    @FXML
    private ListView<String> usersView;

    private ObservableList<Pair<String, String>> lobbies;
    private ObservableList<String> users;
    private Window window;

    /**
     * Constructor
     * <p>
     * This constructor calls the init method of the AbstractPresenterWithChat
     * to set the appropriate logger.
     *
     * @since 2021-01-02
     */
    public MainMenuPresenter() {
        super.init(LogManager.getLogger(MainMenuPresenter.class));
    }

    @Override
    @FXML
    public void initialize() {
        super.initialize();
        lobbyView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Pair<String, String> item, boolean empty) {
                Platform.runLater(() -> {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : item.getValue());
                });
            }
        });
    }

    /**
     * Helper function to log out the user
     * <p>
     * Makes sure the chat related variables are reset.
     *
     * @author Temmo Junkhoff
     * @since 2021-01-06
     */
    private void logout() {
        lobbyService.removeFromAllLobbies();
        userService.logout(userService.getLoggedInUser());
        resetChatVars();
    }

    /**
     * Handles an AllLobbiesMessage found on the EventBus
     * <p>
     * If a new AllLobbiesMessage object is posted to the EventBus, this method
     * calls the {@code updateLobbyList()} method to update the list of lobbies
     * displayed in the Main Menu.
     *
     * @param msg The AllLobbiesMessage found on the EventBus
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @author Phillip-André Suhr
     * @since 2021-03-01
     */
    @Subscribe
    private void onAllLobbiesMessage(AllLobbiesMessage msg) {
        if (userService.getLoggedInUser() == null) return;
        LOG.debug("Received AllLobbiesMessage");
        updateLobbyList(msg.getLobbies());
        randomLobbyState.setVisible(false);
    }

    /**
     * Handles a new list of lobbies
     * <p>
     * If a new AllLobbiesResponse object is posted to the EventBus, the names
     * of all currently existing lobbies are put into the lobby list in the main menu.
     *
     * @param rsp The AllLobbiesResponse object seen on the EventBus
     *
     * @see de.uol.swp.common.lobby.response.AllLobbiesResponse
     * @since 2020-11-29
     */
    @Subscribe
    private void onAllLobbiesResponse(AllLobbiesResponse rsp) {
        LOG.debug("Received AllLobbiesResponse");
        updateLobbyList(rsp.getLobbies());
        randomLobbyState.setVisible(false);
    }

    /**
     * Handles a new list of users
     * <p>
     * If a new AllOnlineUsersResponse object is posted onto the EventBus, the names
     * of all currently logged in users are put onto the UserList in the main menu.
     *
     * @param rsp The AllOnlineUsersResponse object seen on the EventBus
     *
     * @see de.uol.swp.common.user.response.AllOnlineUsersResponse
     * @since 2019-08-29
     */
    @Subscribe
    private void onAllOnlineUsersResponse(AllOnlineUsersResponse rsp) {
        LOG.debug("Received AllOnlineUsersResponse");
        updateUsersList(rsp.getUsers());
    }

    /**
     * Handles a AllowedAmountOfPlayersMessage found on the EventBus
     * <p>
     * If a AllowedAmountOfPlayersMessage, a lobby has changed a lobby-setting.
     * It calls the retrieveAllLobbies method of the LobbyService to update
     * the lobby list.
     *
     * @param msg AllowedAmountOfPlayersMessage found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Aldin Dervisi
     * @see de.uol.swp.common.lobby.message.AllowedAmountOfPlayersChangedMessage
     * @since 2021-03-14
     */
    @Subscribe
    private void onAllowedAmountOfPlayersMessage(AllowedAmountOfPlayersChangedMessage msg) {
        if (userService.getLoggedInUser() == null) return;
        LOG.debug("Received AllowedAmountOfPlayersMessage");
        lobbyService.retrieveAllLobbies();
    }

    /**
     * Method called when the ChangeAccountDetailsButton is pressed
     * <p>
     * This method is called when the ChangeAccountDetailsButton is pressed.
     * It calls the checkUserInLobby method of the LobbyService to check if
     * the user is in a lobby.
     *
     * @author Eric Vuong
     * @author Alwin Bossert
     * @see de.uol.swp.client.ChangeAccountDetails.event.ShowChangeAccountDetailsViewEvent
     * @see de.uol.swp.client.SceneManager
     * @since 2021-03-16
     */
    @FXML
    private void onChangeAccountDetailsButtonPressed() {
        lobbyService.checkUserInLobby();
    }

    /**
     * Handles a CheckUserInLobbyResponse found on the EventBus
     * <p>
     * If a new CheckUserInLobbyResponse object is found on the EventBus, this method
     * gets called. If the user is not in a lobby, it posts a new ShowChangeAccountDetailsViewEvent
     * onto the EventBus. Otherwise it posts a LobbyErrorEvent.
     *
     * @param rsp The CheckUserInLobbyResponse object found on the EventBus
     *
     * @author Alwin Bossert
     * @author Finn Haase
     * @see de.uol.swp.common.user.response.CheckUserInLobbyResponse
     * @since 2021-04-09
     */
    @Subscribe
    private void onCheckUserInLobbyResponse(CheckUserInLobbyResponse rsp) {
        LOG.debug("Received a CheckUserInLobbyResponse");
        if (rsp.getIsInLobby()) {
            lobbyService.showLobbyError(resourceBundle.getString("lobby.error.in.lobby"));
        } else {
            eventBus.post(new ShowChangeAccountDetailsViewEvent());
        }
    }

    /**
     * Method called when the CreateLobbyButton is pressed
     * <p>
     * If the CreateLobbyButton is pressed, this method requests the LobbyService
     * to create a new lobby with the selected maximum amount of players.
     * This lobby will get a unique name and registers the user as its creator.
     *
     * @author Mario Fokken
     * @author Marvin Drees
     * @author Maximilian Lindner
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2021-03-17
     */
    @FXML
    private void onCreateLobbyButtonPressed() {
        //give the lobby a default name
        String name = String.format(resourceBundle.getString("lobby.window.defaulttitle"),
                                    userService.getLoggedInUser().getUsername());

        //create Dialogue, disallow any use of § in the name (used for command parsing)
        UnaryOperator<TextFormatter.Change> filter = (s) ->
                !s.getControlNewText().startsWith("§") && !s.getControlNewText().contains("§") ? s : null;

        TextInputDialog dialogue = new TextInputDialog();
        dialogue.setTitle(resourceBundle.getString("lobby.dialog.title"));
        dialogue.setHeaderText(resourceBundle.getString("lobby.dialog.header"));
        Label lbl = new Label(resourceBundle.getString("lobby.dialog.content"));
        TextField lobbyName = new TextField(name);
        lobbyName.setTextFormatter(new TextFormatter<>(filter));
        HBox box1 = new HBox(10, lbl, lobbyName);
        ToggleGroup grp = new ToggleGroup();
        RadioButton threePlayerButton = new RadioButton(resourceBundle.getString("lobby.radio.threeplayers"));
        RadioButton fourPlayerButton = new RadioButton(resourceBundle.getString("lobby.radio.fourplayers"));
        fourPlayerButton.setSelected(true);
        threePlayerButton.setToggleGroup(grp);
        fourPlayerButton.setToggleGroup(grp);
        HBox box2 = new HBox(10, threePlayerButton, fourPlayerButton);
        VBox box = new VBox(10, box1, box2);
        dialogue.getDialogPane().setContent(box);
        //dialogue.setContentText(resourceBundle.getString("lobby.dialog.content"));

        ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(resourceBundle.getString("button.cancel"),
                                           ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogue.getDialogPane().getButtonTypes().setAll(confirm, cancel);

        //if 'OK' is pressed the lobby will be created. Otherwise, it won't
        Optional<String> result = dialogue.showAndWait();
        int maxPlayers;
        if (threePlayerButton.isSelected()) maxPlayers = 3;
        else maxPlayers = 4;
        result.ifPresent(s -> lobbyService.createNewLobby(lobbyName.getText(), maxPlayers));
    }

    /**
     * Handles a CreateLobbyResponse found on the EventBus
     * <p>
     * If a new CreateLobbyResponse object is found on the EventBus, this method
     * posts a new ShowLobbyViewEvent onto the EventBus the SceneManager is
     * subscribed to. Then it calls the LobbyService to retrieve
     * all members of that new lobby enabling the lobby window to
     * display all members from the beginning.
     *
     * @param rsp The CreateLobbyResponse object found on the EventBus
     *
     * @see de.uol.swp.common.lobby.response.CreateLobbyResponse
     * @see de.uol.swp.client.lobby.event.ShowLobbyViewEvent
     * @see de.uol.swp.client.lobby.LobbyService#retrieveAllLobbyMembers(String)
     * @since 2020-12-20
     */
    @Subscribe
    private void onCreateLobbyResponse(CreateLobbyResponse rsp) {
        LOG.debug("Received CreateLobbyResponse");
        Platform.runLater(() -> {
            eventBus.post(new ShowLobbyViewEvent(rsp.getLobbyName()));
            lobbyService.refreshLobbyPresenterFields(rsp.getLobby());
        });
    }

    /**
     * Method called when the DeleteUserButton is pressed
     * <p>
     * This method is called when the DeleteUserButton is pressed. It first
     * calls the UserService to log the user out, resets the variables used
     * for storing the chat history, and then posts an instance of the
     * ShowLoginViewEvent to the EventBus the SceneManager is subscribed to,
     * and finally calls the UserService to drop the user.
     *
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.AbstractPresenterWithChat#resetChatVars()
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2020-11-20
     */
    @FXML
    private void onDeleteButtonPressed() {
        userService.logout(userService.getLoggedInUser());
        resetChatVars();
        eventBus.post(showLoginViewMessage);
        userService.dropUser(userService.getLoggedInUser());
    }

    /**
     * Handles a GameCreatedMessage found on the EventBus
     * <p>
     * If a GameCreatedMessage is found on the EventBus, this method calls on the
     * LobbyService to get an updated list of all lobbies, so the "in Game" and "full"
     * statuses are displayed correctly.
     *
     * @param msg The GameCreatedMessage found on the EventBus
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @author Phillip-André Suhr
     * @since 2021-03-01
     */
    @Subscribe
    private void onGameCreatedMessage(GameCreatedMessage msg) {
        if (userService.getLoggedInUser() == null) return;
        LOG.debug("Received GameCreatedMessage");
        lobbyService.retrieveAllLobbies();
    }

    /**
     * Method called when the JoinLobbyButton is pressed
     * <p>
     * If the JoinLobbyButton is pressed, this method requests the LobbyService
     * to join a specified lobby. If there is no existing lobby or the user didnt choose one,
     * nothing will happen.
     *
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2020-11-29
     */
    @FXML
    private void onJoinLobbyButtonPressed() {
        lobbyView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        if (lobbyView.getSelectionModel().isEmpty()) {
            lobbyService.showLobbyError(resourceBundle.getString("lobby.error.invalidlobby"));
        } else {
            String lobbyName = lobbyView.getSelectionModel().getSelectedItem().getKey();
            lobbyService.joinLobby(lobbyName);
        }
    }

    /**
     * Handles a JoinLobbyResponse found on the EventBus
     * <p>
     * If a new JoinLobbyResponse object is found on the EventBus, this method
     * posts a new ShowLobbyViewEvent onto the EventBus the SceneManager is
     * subscribed to, and then calls the LobbyService to retrieve
     * all members of that new lobby in order for the lobby window to be
     * able to display all members from the beginning.
     *
     * @param rsp The JoinLobbyResponse object found on the EventBus
     *
     * @see de.uol.swp.common.lobby.response.JoinLobbyResponse
     * @see de.uol.swp.client.lobby.event.ShowLobbyViewEvent
     * @see de.uol.swp.client.lobby.LobbyService#retrieveAllLobbyMembers(String)
     * @since 2020-12-20
     */
    @Subscribe
    private void onJoinLobbyResponse(JoinLobbyResponse rsp) {
        LOG.debug("Received JoinLobbyResponse");
        Platform.runLater(() -> {
            eventBus.post(new ShowLobbyViewEvent(rsp.getLobbyName()));
            lobbyService.refreshLobbyPresenterFields(rsp.getLobby());
        });
    }

    /**
     * Method called when the JoinRandomLobbyButton is pressed
     * <p>
     * If the JoinRandomLobbyButton is pressed, this method requests the LobbyService
     * to join a random lobby. If there is no existing lobby or there is no fitting one,
     * nothing will happen.
     *
     * @author Finn Haase
     * @author Sven Ahrens
     * @see de.uol.swp.client.lobby.LobbyService
     * @since 2021-04-08
     */
    @FXML
    private void onJoinRandomLobbyButtonPressed() {
        lobbyService.joinRandomLobby();
    }

    /**
     * Handles a JoinRandomLobbyFailedResponse found on the EventBus
     * <p>
     * If a new JoinRandomLobbyFailedResponse object is found on the EventBus,
     * this method sets the state of the randomLobbyState label to true.
     *
     * @author Finn Haase
     * @author Sven Ahrens
     * @since 2021-04-08
     */
    @Subscribe
    private void onJoinRandomLobbyFailedResponse(JoinRandomLobbyFailedResponse rsp) {
        randomLobbyState.setVisible(true);
    }

    /**
     * Handles a KillOldClientResponse found on the EventBus
     * <p>
     * If a new KillOldClientResponse object is found on the EventBus, this
     * method removes the user from all lobbies and resets the users chat vars.
     * After that it posts a new showLoginViewMessage on the bus,
     * so the old client gets reset to the login screen. The final step is
     * to post a CloseLobbiesViewEvent on the bus so the remaining lobby
     * windows get closed as well.
     *
     * @param rsp TheKillOldClientResponse object fount on the EventBus
     *
     * @author Eric Vuong
     * @author Marvin Drees
     * @see de.uol.swp.common.user.response.KillOldClientResponse
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @see de.uol.swp.client.lobby.event.CloseLobbiesViewEvent
     * @since 2021-03-03
     */
    @Subscribe
    private void onKillOldClientResponse(KillOldClientResponse rsp) {
        resetChatVars();
        eventBus.post(showLoginViewMessage);
        Platform.runLater(() -> eventBus.post(closeLobbiesViewEvent));
    }

    /**
     * Adds a newly created lobby to LobbyList
     * <p>
     * If a new LobbyCreatedMessage object is posted onto the EventBus, the name
     * of the newly created lobby is put onto the LobbyList in the main menu.
     * It also calls the LobbyService to retrieve all lobbies from the server
     * so the SceneManager can properly keep track of the lobby scenes.
     *
     * @param msg the LobbyCreatedMessage object seen on the EventBus
     *
     * @author Temmo Junkhoff
     * @see de.uol.swp.common.lobby.message.LobbyCreatedMessage
     * @see de.uol.swp.client.SceneManager
     * @since 2020-12-17
     */
    @Subscribe
    private void onLobbyCreatedMessage(LobbyCreatedMessage msg) {
        if (userService.getLoggedInUser() == null) return;
        LOG.debug("Received LobbyCreatedMessage");
        lobbyService.retrieveAllLobbies();
    }

    /**
     * Removes a deleted lobby from LobbyList
     * <p>
     * If a new LobbyDeletedMessage object is posted to the EventBus, the name
     * of the deleted lobby is removed from the LobbyList in the main menu.
     *
     * @param msg The LobbyDeletedMessage object seen on the EventBus
     *
     * @author Temmo Junkhoff
     * @see de.uol.swp.common.lobby.message.LobbyDeletedMessage
     * @since 2020-12-17
     */
    @Subscribe
    private void onLobbyDeletedMessage(LobbyDeletedMessage msg) {
        if (userService.getLoggedInUser() == null) return;
        LOG.debug("Received LobbyDeletedMessage");
        lobbyService.retrieveAllLobbies();
    }

    /**
     * Handles a successful login
     * <p>
     * If a LoginSuccessfulResponse is posted onto the EventBus, the list of
     * the currently logged in users and the list of lobbies is requested,
     * as well as a set amount of history for the global chat.
     * Makes sure that the user is logged out gracefully, should the window be
     * closed without using the Logout button. Closing the window also clears
     * the EventBus to avoid NullPointerExceptions.
     *
     * @param rsp The LoginSuccessfulResponse object seen on the EventBus
     *
     * @see de.uol.swp.common.user.response.LoginSuccessfulResponse
     * @since 2021-01-07
     */
    @Subscribe
    private void onLoginSuccessfulResponse(LoginSuccessfulResponse rsp) {
        LOG.debug("Received LoginSuccessfulResponse");
        eventBus.post(new GetOldSessionsRequest(rsp.getUser()));
        userService.retrieveAllUsers();
        lobbyService.retrieveAllLobbies();
        chatService.askLatestMessages(10);

        if (this.window == null) {
            this.window = this.usersView.getScene().getWindow();
        }
        try {
            this.window.setOnCloseRequest(event -> {
                logout();
                ((Stage) event.getSource()).close();
                clearEventBus();
            });
        } catch (Exception ignored) {
        }
    }

    /**
     * Method called when the LogoutButton is pressed
     * <p>
     * This method is called when the LogoutButton is pressed. It calls the
     * logout(user) method of the UserService to log out the user, resets the
     * variables used for storing the chat history, calls the removeFromAllLobbies
     * method of the LobbyService, and then posts an
     * instance of the ShowLoginViewEvent and CloseLobbiesViewEvent to the
     * EventBus the SceneManager is subscribed to.
     *
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.AbstractPresenterWithChat#resetChatVars()
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @see de.uol.swp.client.lobby.event.CloseLobbiesViewEvent
     * @see de.uol.swp.client.SceneManager
     * @see de.uol.swp.client.user.UserService
     * @since 2020-11-02
     */
    @FXML
    private void onLogoutButtonPressed() {
        logout();
        eventBus.post(showLoginViewMessage);
        eventBus.post(closeLobbiesViewEvent);
    }

    /**
     * Handles newly logged in users
     * <p>
     * If a new UserLoggedInMessage object is posted onto the EventBus, the name of the newly
     * logged in user is appended to the UserList in the main menu.
     * Furthermore, if the LOG-Level is set to DEBUG, the message "New user {@literal
     * <Username>} logged in." is displayed in the log.
     *
     * @param msg The UserLoggedInMessage object seen on the EventBus
     *
     * @see de.uol.swp.common.user.message.UserLoggedInMessage
     * @since 2019-08-29
     */
    @Subscribe
    private void onUserLoggedInMessage(UserLoggedInMessage msg) {
        if (userService.getLoggedInUser() == null) return;
        LOG.debug("Received UserLoggedInMessage");
        LOG.debug("---- New user " + msg.getUsername() + " logged in");
        Platform.runLater(() -> {
            if (users != null && !userService.getLoggedInUser().getUsername().equals(msg.getUsername()))
                users.add(msg.getUsername());
        });
    }

    /**
     * Handles newly logged out users
     * <p>
     * If a new UserLoggedOutMessage object is posted onto the EventBus, the name of the newly
     * logged out user is removed from the UserList in the main menu.
     * Furthermore, if the LOG-Level is set to DEBUG, the message "User {@literal
     * <Username>} logged out." is displayed in the log.
     *
     * @param msg The UserLoggedOutMessage object seen on the EventBus
     *
     * @see de.uol.swp.common.user.message.UserLoggedOutMessage
     * @since 2019-08-29
     */
    @Subscribe
    private void onUserLoggedOutMessage(UserLoggedOutMessage msg) {
        if (userService.getLoggedInUser() == null) return;
        LOG.debug("Received UserLoggedOutMessage");
        LOG.debug("---- User " + msg.getUsername() + " logged out");
        Platform.runLater(() -> users.remove(msg.getUsername()));
    }

    /**
     * Updates the main menu's LobbyList according to the list given
     * <p>
     * This method clears the entire lobby list and then adds the name of each lobby
     * in the list given to the main menu's LobbyList. If there is no LobbyList,
     * this creates one.
     *
     * @param lobbyList A list of LobbyDTO objects including all currently existing
     *                  lobbies
     *
     * @implNote The code inside this Method has to run in the JavaFX-application
     * thread. Therefore, it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.lobby.dto.LobbyDTO
     * @since 2020-11-29
     */
    private void updateLobbyList(List<Lobby> lobbyList) {
        Platform.runLater(() -> {
            if (lobbies == null) {
                lobbies = FXCollections.observableArrayList();
                lobbyView.setItems(lobbies);
            }
            lobbies.clear();
            for (Lobby l : lobbyList) {
                String s = l.getName() + " (" + l.getUserOrDummies().size() + "/" + l.getMaxPlayers() + ")";
                if (l.isInGame()) s = String.format(resourceBundle.getString("mainmenu.lobbylist.ingame"), s);
                else if (l.getUserOrDummies().size() == l.getMaxPlayers())
                    s = String.format(resourceBundle.getString("mainmenu.lobbylist.full"), s);
                lobbies.add(new Pair<>(l.getName(), s));
            }
        });
    }

    /**
     * Updates the main menu's user list according to the list given
     * <p>
     * This method clears the entire UserList and then adds the name of each user
     * in the list given to the main menus user list. If there is no UserList,
     * this creates one.
     *
     * @param userList A list of UserDTO objects including all currently logged in
     *                 users
     *
     * @implNote The code inside this method has to run in the JavaFX-application
     * thread. Therefore, it is crucial not to remove the {@code Platform.runLater()}
     * @see de.uol.swp.common.user.UserDTO
     * @since 2019-08-29
     */
    private void updateUsersList(List<User> userList) {
        Platform.runLater(() -> {
            if (users == null) {
                users = FXCollections.observableArrayList();
                usersView.setItems(users);
            }
            users.clear();
            userList.forEach(u -> users.add(u.getUsername()));
        });
    }
}

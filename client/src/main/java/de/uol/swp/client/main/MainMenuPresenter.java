package de.uol.swp.client.main;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import de.uol.swp.client.AbstractPresenterWithChat;
import de.uol.swp.client.SetAcceleratorsEvent;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.changeAccountDetails.event.ShowChangeAccountDetailsViewEvent;
import de.uol.swp.client.lobby.event.CloseLobbiesViewEvent;
import de.uol.swp.client.lobby.event.ShowLobbyViewEvent;
import de.uol.swp.client.rules.event.ShowRulesOverviewViewEvent;
import de.uol.swp.common.game.message.GameCreatedMessage;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.message.*;
import de.uol.swp.common.lobby.request.JoinLobbyWithPasswordConfirmationRequest;
import de.uol.swp.common.lobby.response.*;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.request.GetOldSessionsRequest;
import de.uol.swp.common.user.response.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
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

    @Inject
    @Named("styleSheet")
    private static String styleSheet;

    @FXML
    private Label randomLobbyState;
    @FXML
    private ListView<Pair<LobbyName, String>> lobbyView;
    @FXML
    private ListView<String> usersView;

    private ObservableList<Pair<LobbyName, String>> lobbies;
    private ObservableList<String> users;

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
            protected void updateItem(Pair<LobbyName, String> item, boolean empty) {
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
        // called from logout button or onUserDeletionSuccessfulResponse; both warrant reset of 'Remember Me'
        userService.logout(true);
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
     * @see de.uol.swp.client.changeAccountDetails.event.ShowChangeAccountDetailsViewEvent
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
        LOG.debug("Received CheckUserInLobbyResponse");
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
        UnaryOperator<TextFormatter.Change> filter = s ->
                s.getControlNewText().matches("[ A-Za-z0-9_',-]+") || s.isDeleted() ? s : null;

        TextInputDialog dialogue = new TextInputDialog();
        dialogue.setTitle(resourceBundle.getString("lobby.dialog.title"));
        dialogue.setHeaderText(resourceBundle.getString("lobby.dialog.header"));
        Label lbl = new Label(resourceBundle.getString("lobby.dialog.content"));
        Label lbl1 = new Label(resourceBundle.getString("lobby.dialog.password"));
        TextField lobbyName = new TextField(name);
        lobbyName.setTextFormatter(new TextFormatter<>(filter));
        HBox box = new HBox(10, lbl, lobbyName);
        CheckBox lobbyPasswordCheckBox = new CheckBox();
        PasswordField lobbyPassword = new PasswordField();
        HBox box1 = new HBox(10, lobbyPasswordCheckBox, lbl1, lobbyPassword);
        VBox vBox = new VBox(10, box, box1);
        lobbyPassword.disableProperty().bind(Bindings.createBooleanBinding(() -> !lobbyPasswordCheckBox.isSelected(),
                                                                           lobbyPasswordCheckBox.selectedProperty()));
        dialogue.getDialogPane().setContent(vBox);
        ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(resourceBundle.getString("button.cancel"),
                                           ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogue.getDialogPane().getButtonTypes().setAll(confirm, cancel);
        dialogue.getDialogPane().lookupButton(confirm).disableProperty().bind(Bindings.createBooleanBinding(
                () -> lobbyName.getText().isBlank() || !lobbyName.getText().matches("[ A-Za-z0-9_',-]+"),
                lobbyName.textProperty()));
        dialogue.getDialogPane().getStylesheets().add(styleSheet);
        //if 'OK' is pressed the lobby will be created. Otherwise, it won't
        Optional<String> result = dialogue.showAndWait();
        String lobbyPasswordHash = lobbyPassword.getText();
        if (!Strings.isNullOrEmpty(lobbyPassword.getText())) {
            lobbyPasswordHash = userService.hash(lobbyPassword.getText());
        }
        String finalLobbyPasswordHash = lobbyPasswordHash;
        result.ifPresent(s -> lobbyService.createNewLobby(new LobbyName(lobbyName.getText()), finalLobbyPasswordHash));
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
     * @see de.uol.swp.client.lobby.LobbyService#retrieveAllLobbyMembers(de.uol.swp.common.lobby.LobbyName)
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
     * Handles a CreateLobbyWithPasswordResponse found on the EventBus
     * <p>
     * If a new CreateLobbyWithPasswordResponse object is found on the EventBus, this method
     * posts a new ShowLobbyViewEvent onto the EventBus the SceneManager is
     * subscribed to. Then it calls the LobbyService to retrieve
     * all members of that new lobby enabling the lobby window to
     * display all members from the beginning.
     *
     * @param rsp The CreateLobbyWithPasswordResponse object found on the EventBus
     *
     * @see de.uol.swp.common.lobby.response.CreateLobbyWithPasswordResponse
     * @see de.uol.swp.client.lobby.event.ShowLobbyViewEvent
     * @see de.uol.swp.client.lobby.LobbyService#retrieveAllLobbyMembers(de.uol.swp.common.lobby.LobbyName)
     * @since 2021-04-22
     */
    @Subscribe
    private void onCreateLobbyWithPasswordResponse(CreateLobbyWithPasswordResponse rsp) {
        LOG.debug("Received CreateLobbyWithPasswordResponse");
        Platform.runLater(() -> {
            eventBus.post(new ShowLobbyViewEvent(rsp.getLobbyName()));
            lobbyService.refreshLobbyPresenterFields(rsp.getLobby());
        });
    }

    /**
     * Method called when the DeleteUserButton is pressed
     * <p>
     * This method is called when the DeleteUserButton is pressed. It first asks
     * the user to confirm that they want to delete the Account. It calls on the
     * UserService to drop the user if and only if the user has a password and
     * clicked the checkbox.
     *
     * @author Timo Gerken
     * @since 2021-04-19
     */
    @FXML
    private void onDeleteButtonPressed() {
        TextInputDialog dialogue = new TextInputDialog();
        dialogue.setTitle(resourceBundle.getString("mainmenu.settings.deleteaccount.title"));
        dialogue.setHeaderText(resourceBundle.getString("mainmenu.settings.deleteaccount.header"));
        Label lbl = new Label(resourceBundle.getString("mainmenu.settings.deleteaccount.content"));
        PasswordField confirmPasswordField = new PasswordField();
        CheckBox userDeletionConfirmCheckBox = new CheckBox(
                resourceBundle.getString("mainmenu.settings.deleteaccount.confirm"));
        HBox hbox = new HBox(10, lbl, confirmPasswordField);
        VBox box = new VBox(10, hbox, userDeletionConfirmCheckBox);
        dialogue.getDialogPane().setContent(box);
        ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"), ButtonBar.ButtonData.OK_DONE);
        ButtonType cancel = new ButtonType(resourceBundle.getString("button.cancel"),
                                           ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogue.getDialogPane().getButtonTypes().setAll(confirm, cancel);
        dialogue.getDialogPane().lookupButton(confirm).disableProperty().bind(Bindings.createBooleanBinding(
                () -> !userDeletionConfirmCheckBox.isSelected() || confirmPasswordField.getText().isBlank(),
                userDeletionConfirmCheckBox.selectedProperty(), confirmPasswordField.textProperty()));
        dialogue.getDialogPane().getStylesheets().add(styleSheet);
        Optional<String> result = dialogue.showAndWait();
        result.ifPresent(s -> userService
                .dropUser(userService.getLoggedInUser(), userService.hash(confirmPasswordField.getText())));
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
            LobbyName lobbyName = lobbyView.getSelectionModel().getSelectedItem().getKey();
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
     * @see de.uol.swp.client.lobby.LobbyService#retrieveAllLobbyMembers(de.uol.swp.common.lobby.LobbyName)
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
     * Method called when a JoinLobbyWithPasswordResponse is found on the EventBus
     * <p>
     * If a JoinLobbyWithPasswordResponse is found on the EventBus,
     * this method sends a JoinLobbyWithPasswordConfirmationRequest to the server
     * with the confirmation of the lobby password
     *
     * @author Alwin Bossert
     * @since 2021-04-26
     */
    @Subscribe
    private void onJoinLobbyWithPasswordResponse(JoinLobbyWithPasswordResponse response) {
        LOG.debug("Received a JoinLobbyWithPasswordResponse for Lobby {}", response.getLobby());
        Platform.runLater(() -> {
            TextInputDialog dialogue = new TextInputDialog();
            dialogue.setTitle(resourceBundle.getString("lobby.dialog.password.title"));
            Label confirmPasswordLabel = new Label(resourceBundle.getString("lobby.dialog.password.confirmation"));
            PasswordField lobbyPasswordField = new PasswordField();
            HBox box3 = new HBox(10, confirmPasswordLabel, lobbyPasswordField);
            VBox box = new VBox(10, box3);
            dialogue.getDialogPane().setContent(box);
            ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"),
                                                ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType(resourceBundle.getString("button.cancel"),
                                               ButtonBar.ButtonData.CANCEL_CLOSE);
            dialogue.getDialogPane().getButtonTypes().setAll(confirm, cancel);

            //if 'OK' is pressed a JoinLobbyWithPasswordConfirmationRequest is send. Otherwise, it won't
            Optional<String> result = dialogue.showAndWait();
            String lobbyPassword = lobbyPasswordField.getText();
            if (!Strings.isNullOrEmpty(lobbyPasswordField.getText())) {
                lobbyPassword = userService.hash(lobbyPasswordField.getText());
            }
            String finalLobbyPassword = lobbyPassword;
            result.ifPresent(s -> eventBus.post(new JoinLobbyWithPasswordConfirmationRequest(response.getLobbyName(),
                                                                                             userService
                                                                                                     .getLoggedInUser(),
                                                                                             finalLobbyPassword)));
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
     * Handles a click on the Show Rules Overview menu item
     * <p>
     * Method called when the Show Rules Overview menu item is clicked.
     * It posts a ShowRulesOverviewViewEvent onto the EventBus.
     *
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.rules.event.ShowRulesOverviewViewEvent
     * @since 2021-05-02
     */
    @FXML
    private void onRulesMenuClicked() {
        eventBus.post(new ShowRulesOverviewViewEvent());
    }

    /**
     * Handles a SetAcceleratorEvent found on the EventBus
     * <p>
     * This method sets the accelerators for the MainMenuPresenter, namely
     * <ul>
     *     <li> CTRL/META + N = Create Lobby button
     *     <li> CTRL/META + J = Join Lobby button
     *     <li> CTRL/META + C = Open Change Account Details window
     *     <li> CTRL/META + L = Logout button
     *     <li> CTRL/META + D = Delete Account
     *     <li> F2            = Open Rules menu
     *
     * @param event The SetAcceleratorEvent found on the EventBus
     *
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.SetAcceleratorsEvent
     * @since 2021-05-20
     */
    @Subscribe
    private void onSetAcceleratorsEvent(SetAcceleratorsEvent event) {
        LOG.debug("Received SetAcceleratorsEvent");
        Map<KeyCombination, Runnable> accelerators = new HashMap<>();
        accelerators.put(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN), // CTRL/META + N
                         this::onCreateLobbyButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.J, KeyCombination.SHORTCUT_DOWN), // CTRL/META + J
                         this::onJoinLobbyButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN), // CTRL/META + C
                         this::onChangeAccountDetailsButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.L, KeyCombination.SHORTCUT_DOWN), // CTRL/META + L
                         this::onLogoutButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.D, KeyCombination.SHORTCUT_DOWN), // CTRL/META + D
                         this::onDeleteButtonPressed);
        accelerators.put(new KeyCodeCombination(KeyCode.F2), // F2 for Rules
                         this::onRulesMenuClicked);
        usersView.getScene().getAccelerators().putAll(accelerators);
    }

    /**
     * Handles a UserDeletionSuccessfulResponse found on the EventBus
     * <p>
     * This method logs the currently logged in user out and returns them to the Login Screen.
     * It also shows a Dialog Window to the user telling them the Account was deleted.
     *
     * @param rsp The UserDeletionSuccessfulResponse found on the EventBus
     *
     * @author Timo Gerken
     * @since 2021-04-19
     */
    @Subscribe
    private void onUserDeletionSuccessfulResponse(UserDeletionSuccessfulResponse rsp) {
        LOG.info("User deletion successful");
        String username = userService.getLoggedInUser().getUsername();
        eventBus.post(showLoginViewMessage);
        logout();
        ButtonType ok = new ButtonType(resourceBundle.getString("button.confirm"), ButtonBar.ButtonData.OK_DONE);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION,
                                    String.format(resourceBundle.getString("mainmenu.settings.deleteaccount.success"),
                                                  username), ok);
            alert.setTitle(resourceBundle.getString("information.title"));
            alert.setHeaderText(resourceBundle.getString("information.header"));
            alert.getDialogPane().getStylesheets().add(styleSheet);
            alert.show();
        });
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
        LOG.debug("---- New user {} logged in", msg.getUsername());
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
        LOG.debug("---- User {} logged out", msg.getUsername());
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
     * @since 2020-11-29
     */
    private void updateLobbyList(List<ISimpleLobby> lobbyList) {
        Platform.runLater(() -> {
            if (lobbies == null) {
                lobbies = FXCollections.observableArrayList();
                lobbyView.setItems(lobbies);
            }
            lobbies.clear();
            for (ISimpleLobby l : lobbyList) {
                String s = l.getName() + " (" + l.getUserOrDummies().size() + "/" + l.getMaxPlayers() + ")";
                if (l.isInGame()) s = String.format(resourceBundle.getString("mainmenu.lobbylist.ingame"), s);
                else if (l.getUserOrDummies().size() == l.getMaxPlayers())
                    s = String.format(resourceBundle.getString("mainmenu.lobbylist.full"), s);
                else if (l.hasPassword())
                    s = String.format(resourceBundle.getString("mainmenu.lobbylist.haspassword"), s);
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

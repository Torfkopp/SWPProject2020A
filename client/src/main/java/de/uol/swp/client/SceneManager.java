package de.uol.swp.client;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.name.Named;
import de.uol.swp.client.ChangeAccountDetails.ChangeAccountDetailsPresenter;
import de.uol.swp.client.ChangeAccountDetails.event.ChangeAccountDetailsCanceledEvent;
import de.uol.swp.client.ChangeAccountDetails.event.ChangeAccountDetailsErrorEvent;
import de.uol.swp.client.ChangeAccountDetails.event.ShowChangeAccountDetailsViewEvent;
import de.uol.swp.client.auth.LoginPresenter;
import de.uol.swp.client.auth.events.RetryLoginEvent;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.devmenu.DevMenuPresenter;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.lobby.RobberTaxPresenter;
import de.uol.swp.client.lobby.event.*;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.main.events.ClientDisconnectedFromServerEvent;
import de.uol.swp.client.register.RegistrationPresenter;
import de.uol.swp.client.register.event.RegistrationCanceledEvent;
import de.uol.swp.client.register.event.RegistrationErrorEvent;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import de.uol.swp.client.rules.RulesOverviewPresenter;
import de.uol.swp.client.rules.event.ResetRulesOverviewEvent;
import de.uol.swp.client.rules.event.ShowRulesOverviewViewEvent;
import de.uol.swp.client.trade.TradeWithBankPresenter;
import de.uol.swp.client.trade.TradeWithUserAcceptPresenter;
import de.uol.swp.client.trade.TradeWithUserPresenter;
import de.uol.swp.client.trade.event.*;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.devmenu.response.OpenDevMenuResponse;
import de.uol.swp.common.game.response.TradeWithUserCancelResponse;
import de.uol.swp.common.lobby.response.AllLobbiesResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.NukeUsersSessionsRequest;
import de.uol.swp.common.user.response.NukedUsersSessionsResponse;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Class that manages which window/scene is currently shown
 *
 * @author Marco Grawunder
 * @since 2019-09-03
 */
@SuppressWarnings("UnstableApiUsage")
public class SceneManager {

    private static final Logger LOG = LogManager.getLogger(SceneManager.class);

    @Inject
    private static Injector injector;
    @Inject
    private static ResourceBundle resourceBundle;
    @Inject
    @Named("styleSheet")
    private static String styleSheet;

    private final Stage primaryStage;
    private final Map<String, Stage> tradingStages = new HashMap<>();
    private final Map<String, Stage> tradingResponseStages = new HashMap<>();
    private final Map<String, Stage> robberTaxStages = new HashMap<>();
    private final Map<String, Scene> lobbyScenes = new HashMap<>();
    private final List<Stage> lobbyStages = new ArrayList<>();
    private final EventBus eventBus;

    @Inject
    private IUserService userService;

    private Scene loginScene;
    private String lastTitle;
    private Scene registrationScene;
    private Scene mainScene;
    private Scene lastScene = null;
    private Scene currentScene = null;
    private Scene changeAccountDetailsScene;
    private Scene rulesScene;
    private boolean devMenuIsOpen;
    private boolean rulesOverviewIsOpen;

    /**
     * Constructor
     *
     * @param eventBus     The EventBus
     * @param primaryStage The primary Stage
     */
    @Inject
    public SceneManager(EventBus eventBus, @Assisted Stage primaryStage) {
        eventBus.register(this);
        this.eventBus = eventBus;
        this.primaryStage = primaryStage;
        initViews();
    }

    /**
     * Closes all Lobbies
     *
     * @author Finn Haase
     * @author Aldin Dervisi
     * @since 2021-01-28
     */
    public void closeLobbies() {
        for (Stage lobbyStage : lobbyStages) {
            lobbyStage.close();
        }
        lobbyStages.clear();
    }

    /**
     * Method used to close down the client
     *
     * @author Aldin Dervisi
     * @author Marvin Drees
     * @since 2021-03-25
     */
    public void closeMainScreen() {
        primaryStage.close();
    }

    /**
     * Shows the ChangeAccountDetailsScreen
     * <p>
     * Sets the scene's UserData to the current user.
     * Switches the current Scene to the ChangeAccountDetailsScreen
     * and sets the window's title to "Change AccountDetails"
     *
     * @author Eric Vuong
     * @author Mario Fokken
     * @since 2020-12-19
     */
    public void showChangeAccountDetailsScreen() {
        showScene(changeAccountDetailsScene, resourceBundle.getString("changeaccdetails.window.title"),
                  ChangeAccountDetailsPresenter.MIN_WIDTH, ChangeAccountDetailsPresenter.MIN_HEIGHT);
    }

    /**
     * Shows an error message inside an error alert
     *
     * @param message The type of error to be shown
     * @param e       The error message
     *
     * @author Mario Fokken
     * @author Marvin Drees
     * @since 2021-03-12
     */
    public void showError(String message, String e) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resourceBundle.getString("error.title"));
            alert.setHeaderText(resourceBundle.getString("error.header"));
            String context = internationaliseServerMessage(e);
            alert.setContentText(message + context);
            ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"),
                                                ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(confirm);
            alert.getDialogPane().getStylesheets().add(styleSheet);
            alert.showAndWait();
        });
    }

    /**
     * Shows an error message inside an error alert
     *
     * @param e The error message
     *
     * @since 2019-09-03
     */
    public void showError(String e) {
        showError(resourceBundle.getString("error.generic") + '\n', e);
    }

    /**
     * Method to open a popup which allows to log an old session out
     * <p>
     * This method allows logging an old session out by posting
     * a NukeUsersSessionsRequest on the EventBus once the
     * confirmation button is pressed on the opened popup.
     *
     * @param user The user that is already logged in.
     *
     * @author Eric Vuong
     * @author Marvin Drees
     * @since 2021-03-03
     */
    public void showLogOldSessionOutScreen(User user) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, resourceBundle.getString("logoldsessionout.error"));
            alert.setTitle(resourceBundle.getString("confirmation.title"));
            alert.setHeaderText(resourceBundle.getString("confirmation.header"));
            ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"),
                                                ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType(resourceBundle.getString("button.cancel"),
                                               ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(confirm, cancel);
            alert.getDialogPane().getStylesheets().add(styleSheet);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == confirm) {
                eventBus.post(new NukeUsersSessionsRequest(user));
            }
        });
    }

    /**
     * Shows the login error alert
     * <p>
     * Opens an ErrorAlert popup saying "Error logging in to server"
     *
     * @since 2019-09-03
     */
    public void showLoginErrorScreen() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("login.error"));
            ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"),
                                                ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(confirm);
            alert.getDialogPane().getStylesheets().add(styleSheet);
            alert.showAndWait();
            showLoginScreen();
        });
    }

    /**
     * Shows the login screen
     * <p>
     * Switches the current Scene to the loginScene
     * and sets the window's title of to "Login"
     *
     * @since 2019-09-03
     */
    public void showLoginScreen() {
        showScene(loginScene, resourceBundle.getString("login.window.title"), LoginPresenter.MIN_WIDTH,
                  LoginPresenter.MIN_HEIGHT);
    }

    /**
     * Shows the main menu
     * <p>
     * Switches the current scene to the mainScene and sets the window's title
     * to "Welcome " and the current user's username
     *
     * @since 2019-09-03
     */
    public void showMainScreen(User currentUser) {
        showScene(mainScene,
                  String.format(resourceBundle.getString("mainmenu.window.title"), currentUser.getUsername()),
                  MainMenuPresenter.MIN_WIDTH, MainMenuPresenter.MIN_HEIGHT);
        primaryStage.setOnCloseRequest(event -> {
            closeLobbies();
            closeMainScreen();
        });
    }

    /**
     * Shows the registration screen
     * <p>
     * Switches the current Scene to the registrationScene
     * and sets the window's title to "Registration"
     *
     * @since 2019-09-03
     */
    public void showRegistrationScreen() {
        showScene(registrationScene, resourceBundle.getString("register.window.title"), RegistrationPresenter.MIN_WIDTH,
                  RegistrationPresenter.MIN_HEIGHT);
    }

    /**
     * Shows a server error message inside an error alert
     *
     * @param e The error message
     *
     * @since 2019-09-03
     */
    public void showServerError(String e) {
        showError(resourceBundle.getString("error.server") + '\n', e);
    }

    /**
     * Shows a server error message inside an error alert
     * <p>
     * This method can check for certain Throwables by taking the thrown
     * Throwable as an argument.
     *
     * @param e     The Throwable that was thrown
     * @param cause The cause of the Throwable being thrown
     *
     * @author Phillip-André Suhr
     * @since 2021-04-26
     */
    public void showServerError(Throwable e, String cause) {
        if (e instanceof IOException) {
            //so users don't have any access to settings and the like, even though the LogoutRequest won't go through
            userService.logout(userService.getLoggedInUser());
            showLoginScreen();
            cause = resourceBundle.getString("error.server.disrupted");
        }
        showServerError(cause);
    }

    /**
     * Method used to display a custom error for the
     * connection timeout.
     *
     * @author Marvin Drees
     * @since 2021-03-26
     */
    public void showTimeoutErrorScreen() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("error.context.disconnected"));
            alert.setHeaderText(resourceBundle.getString("error.header.disconnected"));
            ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"),
                                                ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(confirm);
            alert.getDialogPane().getStylesheets().add(styleSheet);
            alert.showAndWait();
        });
    }

    /**
     * Initialises the ChangeAccountDetailsView
     * <p>
     * If the ChangeAccountDetailsScene is null, it gets set to a new scene containing the
     * pane showing the ChangeAccountDetailsView as specified by the ChangeAccountDetailsView
     * FXML file.
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.ChangeAccountDetails.ChangeAccountDetailsPresenter
     * @since 2020-12-19
     */
    private void initChangeAccountDetailsView() {
        if (changeAccountDetailsScene == null) {
            Parent rootPane = initPresenter(ChangeAccountDetailsPresenter.fxml);
            changeAccountDetailsScene = new Scene(rootPane, 400, 200);
            changeAccountDetailsScene.getStylesheets().add(styleSheet);
        }
    }

    /**
     * Initialises the login view
     * <p>
     * If the loginScene is null, it gets set to a new scene containing the
     * pane showing the login view as specified by the LoginView FXML file.
     *
     * @see de.uol.swp.client.auth.LoginPresenter
     * @since 2019-09-03
     */
    private void initLoginView() {
        if (loginScene == null) {
            Parent rootPane = initPresenter(LoginPresenter.fxml);
            loginScene = new Scene(rootPane, 400, 200);
            loginScene.getStylesheets().add(styleSheet);
        }
    }

    /**
     * Initialises the main menu view
     * <p>
     * If the mainScene is null, it gets set to a new scene containing the
     * pane showing the main menu view as specified by the MainMenuView
     * FXML file.
     *
     * @see de.uol.swp.client.main.MainMenuPresenter
     * @since 2019-09-03
     */
    private void initMainView() {
        if (mainScene == null) {
            Parent rootPane = initPresenter(MainMenuPresenter.fxml);
            mainScene = new Scene(rootPane, 800, 600);
            mainScene.getStylesheets().add(styleSheet);
        }
    }

    /**
     * Subroutine creating parent panes from FXML files
     * <p>
     * This Method tries to create a parent pane from the FXML file specified by
     * the URL String given to it. If the LOG-Level is set to Debug or higher,
     * "Loading " is written to the LOG.
     * If it fails to load the view, a RuntimeException is thrown.
     *
     * @param fxmlFile FXML file to load the view from
     *
     * @return View loaded from FXML or null
     *
     * @since 2019-09-03
     */
    private Parent initPresenter(String fxmlFile) {
        Parent rootPane;
        FXMLLoader loader = injector.getInstance(FXMLLoader.class);
        try {
            URL url = getClass().getResource(fxmlFile);
            LOG.debug("Loading FXML-File {}", url);
            loader.setLocation(url);
            rootPane = loader.load();
        } catch (IOException e) {
            throw new RuntimeException("Could not load View!" + e.getMessage(), e);
        }
        return rootPane;
    }

    /**
     * Initialises the registration view
     * <p>
     * If the registrationScene is null, it gets set to a new scene containing the
     * pane showing the registration view as specified by the RegistrationView
     * FXML file.
     *
     * @see de.uol.swp.client.register.RegistrationPresenter
     * @since 2019-09-03
     */
    private void initRegistrationView() {
        if (registrationScene == null) {
            Parent rootPane = initPresenter(RegistrationPresenter.fxml);
            registrationScene = new Scene(rootPane, 400, 200);
            registrationScene.getStylesheets().add(styleSheet);
        }
    }

    /**
     * Initialises the Rules Overview View
     * <p>
     * If the rulesScene is null, this method sets it to a new Scene showing
     * the Rules Overview View as specified by the RulesOverviewView FXML file.
     *
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.rules.RulesOverviewPresenter
     * @since 2021-04-22
     */
    private void initRulesOverviewView() {
        if (rulesScene == null) {
            Parent rootPane = initPresenter(RulesOverviewPresenter.fxml);
            rulesScene = new Scene(rootPane, 400, 600);
            rulesScene.getStylesheets().add(styleSheet);
        }
    }

    /**
     * Subroutine to initialise all views
     * <p>
     * This is a subroutine of the constructor to initialise all views
     *
     * @since 2019-09-03
     */
    private void initViews() {
        initLoginView();
        initMainView();
        initRegistrationView();
        initRulesOverviewView();
        initChangeAccountDetailsView();
    }

    /**
     * Internationalises a Message coming from the server
     *
     * @param e The original exception message
     *
     * @return The internationalised message
     *
     * @author Mario Fokken
     * @author Marvin Drees
     * @since 2021-03-12
     */
    private String internationaliseServerMessage(String e) {
        String context = e;
        switch (e) {
            //Found in ChatService
            case "This lobby doesn't allow the use of commands!":
                context = resourceBundle.getString("error.context.commandsforbidden");
                break;
            //Found in LobbyService
            case "Game session started already!":
                context = resourceBundle.getString("error.context.sessionstarted");
                break;
            case "You're already in this lobby!":
                context = resourceBundle.getString("error.context.alreadyin");
                break;
            case "This lobby is full!":
                context = resourceBundle.getString("error.context.full");
                break;
            case "This lobby does not exist!":
                context = resourceBundle.getString("error.context.nonexistant");
                break;
            //Found in GameService
            case "Can not kick while a game is ongoing":
                context = resourceBundle.getString("error.context.ongoing");
                break;
            //Found in ServerHandler
            case "Authorisation required. Client not logged in!":
                context = resourceBundle.getString("error.context.authneeded");
                break;
            //Found in UserManagement
            case "Username already used!":
            case "Username already taken":
                context = resourceBundle.getString("error.context.nameused");
                break;
            case "Username unknown!":
                context = resourceBundle.getString("error.context.unknown");
                break;
            case "User unknown!":
                context = resourceBundle.getString("error.context.unknownuser");
                break;
            //Found in UserService
            case "Old Passwords are not equal":
                context = resourceBundle.getString("error.context.oldpw");
                break;
            case "Old Password was not correct":
                context = resourceBundle.getString("error.context.oldpwincorrect");
                break;
        }
        //found in UserManagement
        if (e.contains("Cannot auth user "))
            context = String.format(resourceBundle.getString("error.context.cannotauth"), e.substring(17));
        //found in UserService
        if (e.contains("Cannot delete user ")) {
            context = String.format(resourceBundle.getString("error.context.cannotdelete"),
                                    e.substring(e.indexOf('[') + 1, e.lastIndexOf(']')),
                                    resourceBundle.getString("error.context.unknown"));
        }
        if (e.contains("User deletion unsuccessful for user ")) {
            context = String.format(resourceBundle.getString("error.context.cannotdelete"),
                                    e.substring(e.indexOf('[') + 1, e.lastIndexOf(']')),
                                    resourceBundle.getString("error.context.wrongpw"));
        }
        if (e.contains("Cannot create user ")) {
            context = String.format(resourceBundle.getString("error.context.cannotcreate"),
                                    e.substring(e.indexOf('[') + 2 - 1, e.lastIndexOf(']')),
                                    resourceBundle.getString("error.context.nameused"));
        }
        if (e.contains("Cannot change Password of ")) {
            context = String.format(resourceBundle.getString("error.context.cannotchangepw"),
                                    e.substring(e.indexOf('[') + 3 - 2, e.lastIndexOf(']')),
                                    resourceBundle.getString("error.context.unknown"));
        }
        //found in LobbyManagement
        if (e.contains("Lobby") && e.contains(" already exists!")) {
            context = String.format(resourceBundle.getString("error.context.lobby.alreadyused"),
                                    e.substring(e.indexOf('[') + 4 - 3, e.lastIndexOf(']')));
        }
        if (e.contains("Lobby") && e.contains(" not found!")) {
            context = String.format(resourceBundle.getString("error.context.lobby.notfound"),
                                    e.substring(e.indexOf('[') + 5 - 4, e.lastIndexOf(']')));
        }
        //found in GameManagement
        if (e.contains("Game") && e.contains(" already exists!")) {
            context = String.format(resourceBundle.getString("error.context.game.alreadyexists"),
                                    e.substring(e.indexOf('[') + 6 - 5, e.lastIndexOf(']')));
        }
        if (e.contains("Game") && e.contains(" not found!")) {
            context = String.format(resourceBundle.getString("error.context.game.notfound"),
                                    e.substring(e.indexOf('[') + 7 - 6, e.lastIndexOf(']')));
        }
        return context;
    }

    /**
     * Handles an incoming LobbyListMessage
     * <p>
     * If a LobbyListMessage is detected, the lobbyScenes map
     * is updated to know the same lobbies as the server
     *
     * @param rsp The LobbyListMessage detected on the EventBus
     *
     * @see de.uol.swp.common.lobby.response.AllLobbiesResponse
     * @since 2020-12-12
     */
    @Subscribe
    private void onAllLobbiesResponse(AllLobbiesResponse rsp) {
        LOG.debug("Received AllLobbiesResponse");
        for (String name : rsp.getLobbyNames()) {
            if (!lobbyScenes.containsKey(name)) lobbyScenes.put(name, null); //do not overwrite existing lobbyScene
        }
    }

    /**
     * Handles the ChangeAccountDetailsCanceledEvent detected on the EventBus
     * <p>
     * If a ChangeAccountDetailsCanceledEvent is detected on the EventBus, this method gets
     * called. It calls a method to show the main screen.
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.ChangeAccountDetails.event.ChangeAccountDetailsCanceledEvent
     * @since 2020-12-19
     */
    @Subscribe
    private void onChangeAccountDetailsCanceledEvent(ChangeAccountDetailsCanceledEvent event) {
        showScene(lastScene, lastTitle, MainMenuPresenter.MIN_WIDTH, MainMenuPresenter.MIN_HEIGHT);
    }

    /**
     * Handles the ChangeAccountDetailsErrorEvent detected on the EventBus
     * <p>
     * If a ChangeAccountDetailsErrorEvent is detected on the EventBus, this method gets
     * called. It shows the error message of the event in a error alert.
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.ChangeAccountDetails.event.ChangeAccountDetailsErrorEvent
     * @since 2020-12-19
     */
    @Subscribe
    private void onChangeAccountDetailsErrorEvent(ChangeAccountDetailsErrorEvent event) {
        showError(event.getMessage());
    }

    /**
     * Method used to close the client and show an error.
     * <p>
     * This method is called when a ClientDisconnectedFromServerEvent
     * is found on the EventBus. It shows an error indicating that the
     * connection timed out and closes the client.
     *
     * @param msg ClientDisconnectedFromServerEvent found on the EventBus
     *
     * @author Aldin Dervisi
     * @author Marvin Drees
     * @since 2021-03-25
     */
    @Subscribe
    private void onClientDisconnectedFromServer(ClientDisconnectedFromServerEvent msg) {
        LOG.debug("Client disconnected from server");
        showTimeoutErrorScreen();
        Platform.runLater(this::closeMainScreen);
    }

    /**
     * Handles the CloseLobbiesViewEvent detected on the EventBus
     * <p>
     * If a CloseLobbiesEvent is detected on the EventBus, this method gets called.
     * It calls a method to close all lobby screens.
     *
     * @param event The CloseLobbiesViewEvent detected on the EventBus
     *
     * @author Finn Haase
     * @see de.uol.swp.client.lobby.event.CloseLobbiesViewEvent
     * @since 2021-01-28
     */
    @Subscribe
    private void onCloseLobbiesViewEvent(CloseLobbiesViewEvent event) {
        closeLobbies();
    }

    /**
     * Handles a CloseRobberTaxViewEvent detected on the EventBus.
     * <p>
     * It then proceeds to close the robberTax window.
     *
     * @param event The CloseRobberTaxViewEvent found on the EventBus
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @since 2021-04-08
     */
    @Subscribe
    private void onCloseRobberTaxViewEvent(CloseRobberTaxViewEvent event) {
        LOG.debug("Received CloseRobberTaxViewEvent");
        String lobby = event.getLobbyName();
        if (robberTaxStages.containsKey(lobby)) {
            robberTaxStages.get(lobby).close();
            robberTaxStages.remove(lobby);
        }
    }

    /**
     * Handles the CloseTradeResponseEvent detected on the EventBus
     * <p>
     * If a CloseTradeResponseEvent is detected on the EventBus, this method gets
     * called. If there is a trading response stage in the according lobby, it gets closed.
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @author Aldin Dervisi
     * @see de.uol.swp.client.trade.event.CloseTradeResponseEvent
     * @since 2021-03-19
     */
    @Subscribe
    private void onCloseTradeResponseEvent(CloseTradeResponseEvent event) {
        String lobbyName = event.getLobbyName();
        if (tradingResponseStages.containsKey(lobbyName)) {
            Platform.runLater(() -> {
                tradingResponseStages.get(lobbyName).close();
                tradingResponseStages.remove(lobbyName);
            });
        }
    }

    /**
     * Handles the LobbyErrorEvent detected on the EventBus
     * <p>
     * If a LobbyErrorEvent is detected on the EventBus, this method gets
     * called. It shows the error message of the event in a error alert.
     *
     * @param event The LobbyErrorEvent detected on the EventBus
     *
     * @see de.uol.swp.client.lobby.event.LobbyErrorEvent
     * @since 2020-12-18
     */
    @Subscribe
    private void onLobbyErrorEvent(LobbyErrorEvent event) {
        showError(event.getMessage());
    }

    /**
     * Handles the NukeUsersSessionsResponse detected on the EventBus
     * <p>
     * If this method is called, it means all sessions belonging to a
     * user have been nuked, therefore it posts a RetryLoginEvent
     * on the EventBus to create a new session for the user.
     *
     * @param rsp The NukeUsersSessionsResponse detected on the EventBus
     *
     * @author Eric Vuong
     * @author Marvin Drees
     * @see de.uol.swp.common.user.response.NukedUsersSessionsResponse
     * @since 2021-03-03
     */
    @Subscribe
    private void onNukedUsersSessionsResponse(NukedUsersSessionsResponse rsp) {
        LOG.debug("Received NukedUsersSessionsResponse");
        eventBus.post(new RetryLoginEvent());
    }

    /**
     * Handles the OpenDevMenuResponse detected on the EventBus
     * <p>
     * If an OpenDevMenuResponse is detected on the EventBus, this method gets
     * called. It opens the Developer Menu in a new window.
     *
     * @param rsp The OpenDevMenuResponse detected on the EventBus
     *
     * @author Temmo Junkhoff
     * @author Phillip-André Suhr
     * @see de.uol.swp.common.devmenu.response.OpenDevMenuResponse
     * @since 2021-02-22
     */
    @Subscribe
    private void onOpenDevMenuResponse(OpenDevMenuResponse rsp) {
        LOG.debug("Received OpenDevMenuResponse");
        if (devMenuIsOpen) return;
        Platform.runLater(() -> {
            devMenuIsOpen = true;
            Stage devMenuStage = new Stage();
            devMenuStage.setTitle(resourceBundle.getString("devmenu.window.title"));
            devMenuStage.setHeight(DevMenuPresenter.MIN_HEIGHT);
            devMenuStage.setMinHeight(DevMenuPresenter.MIN_HEIGHT);
            devMenuStage.setWidth(DevMenuPresenter.MIN_WIDTH);
            devMenuStage.setMinWidth(DevMenuPresenter.MIN_WIDTH);
            Parent rootPane = initPresenter(DevMenuPresenter.fxml);
            Scene devMenuScene = new Scene(rootPane);
            devMenuScene.getStylesheets().add(styleSheet);
            devMenuStage.setScene(devMenuScene);
            devMenuStage.initOwner(primaryStage);
            devMenuStage.setX(primaryStage.getX() + 100);
            devMenuStage.setY(primaryStage.getY());
            devMenuStage.show();
            devMenuStage.setOnCloseRequest(event -> devMenuIsOpen = false);
        });
    }

    /**
     * Handles the RegistrationCanceledEvent detected on the EventBus
     * <p>
     * If a RegistrationCanceledEvent is detected on the EventBus, this method gets
     * called. It calls a method to show the screen shown before registration.
     *
     * @param event The RegistrationCanceledEvent detected on the EventBus
     *
     * @see de.uol.swp.client.register.event.RegistrationCanceledEvent
     * @since 2019-09-03
     */
    @Subscribe
    private void onRegistrationCanceledEvent(RegistrationCanceledEvent event) {
        showScene(lastScene, lastTitle, LoginPresenter.MIN_WIDTH, LoginPresenter.MIN_HEIGHT);
    }

    /**
     * Handles the RegistrationErrorEvent detected on the EventBus
     * <p>
     * If a RegistrationErrorEvent is detected on the EventBus, this method gets
     * called. It shows the error message of the event in a error alert.
     *
     * @param event The RegistrationErrorEvent detected on the EventBus
     *
     * @see de.uol.swp.client.register.event.RegistrationErrorEvent
     * @since 2019-09-03
     */
    @Subscribe
    private void onRegistrationErrorEvent(RegistrationErrorEvent event) {
        showError(event.getMessage());
    }

    /**
     * Handles the ShowChangeAccountDetailsViewEvent detected on the EventBus
     * <p>
     * If a ShowChangeAccountDetailsViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the Change Account Details
     * screen.
     * If the user wants to close this window, the user gets redirected to the Main Menu.
     *
     * @param event The ShowChangeAccountDetailsViewEvent detected on the EventBus
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.ChangeAccountDetails.event.ShowChangeAccountDetailsViewEvent
     * @since 2020-12-19
     */
    @Subscribe
    private void onShowChangeAccountDetailsViewEvent(ShowChangeAccountDetailsViewEvent event) {
        showChangeAccountDetailsScreen();
        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            showMainScreen(userService.getLoggedInUser());
        });
    }

    /**
     * Handles the ShowLobbyViewEvent detected on the EventBus
     * <p>
     * If a ShowLobbyViewEvent is detected on the EventBus, this method gets
     * called. It opens the lobby in a new window.
     *
     * @param event The ShowLobbyViewEvent detected on the EventBus
     *
     * @see de.uol.swp.client.lobby.event.ShowLobbyViewEvent
     * @since 2020-11-21
     */
    @Subscribe
    private void onShowLobbyViewEvent(ShowLobbyViewEvent event) {
        //gets the lobby's name
        String lobbyName = event.getName();
        //New window (Stage)
        Stage lobbyStage = new Stage();
        lobbyStage.setTitle(lobbyName);
        lobbyStage.setHeight(LobbyPresenter.MIN_HEIGHT_PRE_GAME);
        lobbyStage.setMinHeight(LobbyPresenter.MIN_HEIGHT_PRE_GAME);
        lobbyStage.setWidth(LobbyPresenter.MIN_WIDTH_PRE_GAME);
        lobbyStage.setMinWidth(LobbyPresenter.MIN_WIDTH_PRE_GAME);
        //Initialises a new lobbyScene
        Parent rootPane = initPresenter(LobbyPresenter.fxml);
        Scene lobbyScene = new Scene(rootPane);
        lobbyScene.getStylesheets().add(styleSheet);
        lobbyScenes.put(lobbyName, lobbyScene);
        //Sets the stage to the newly created scene
        lobbyStage.setScene(lobbyScenes.get(lobbyName));
        //Specifies the modality for new window
        lobbyStage.initModality(Modality.NONE);
        //Specifies the owner Window (parent) for new window
        lobbyStage.initOwner(primaryStage);
        //Set position of second window, related to primary window
        lobbyStage.setX(primaryStage.getX() + 100);
        lobbyStage.setY(10);
        //Shows the window
        lobbyStage.show();
        lobbyStages.add(lobbyStage);
    }

    /**
     * Handles the ShowLoginViewEvent detected on the EventBus
     * <p>
     * If a ShowLoginViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the login screen.
     *
     * @param event The ShowLoginViewEvent detected on the EventBus
     *
     * @see de.uol.swp.client.auth.events.ShowLoginViewEvent
     * @since 2019-09-03
     */
    @Subscribe
    private void onShowLoginViewEvent(ShowLoginViewEvent event) {
        showLoginScreen();
    }

    /**
     * Handles the ShowRegistrationViewEvent detected on the EventBus
     * <p>
     * If a ShowRegistrationViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the registration
     * screen.
     *
     * @param event The ShowRegistrationViewEvent detected on the EventBus
     *
     * @see de.uol.swp.client.register.event.ShowRegistrationViewEvent
     * @since 2019-09-03
     */
    @Subscribe
    private void onShowRegistrationViewEvent(ShowRegistrationViewEvent event) {
        showRegistrationScreen();
    }

    /**
     * Handles the ShowRobberTaxViewEvent detected on the EventBus
     * <p>
     * If a ShowRobberTaxViewEvent is detected on the EventBus, this method gets
     * called. It opens the window to choose which resources to give up on and
     * a ShowRobberTaxUpdateEvent is posted onto the EventBus
     *
     * @param event The ShowRobberTaxViewEvent found on the EventBus
     *
     * @author Mario Fokken
     * @author Timo Gerken
     * @see de.uol.swp.client.lobby.event.ShowRobberTaxViewEvent
     * @see de.uol.swp.client.lobby.event.ShowRobberTaxUpdateEvent
     * @since 2021-04-08
     */
    @Subscribe
    private void onShowRobberTaxViewEvent(ShowRobberTaxViewEvent event) {
        LOG.debug("Received ShowRobberTaxViewEvent");
        String lobbyName = event.getLobbyName();
        Platform.runLater(() -> {
            Stage robberTaxStage = new Stage();
            robberTaxStages.put(event.getLobbyName(), robberTaxStage);
            robberTaxStage.setTitle(resourceBundle.getString("game.robber.tax.title"));
            robberTaxStage.setHeight(RobberTaxPresenter.MIN_HEIGHT);
            robberTaxStage.setMinHeight(RobberTaxPresenter.MIN_HEIGHT);
            robberTaxStage.setWidth(RobberTaxPresenter.MIN_WIDTH);
            robberTaxStage.setMinWidth(RobberTaxPresenter.MIN_WIDTH);
            Parent rootPane = initPresenter(RobberTaxPresenter.fxml);
            Scene robberTaxScene = new Scene(rootPane);
            robberTaxScene.getStylesheets().add(styleSheet);
            robberTaxStage.setScene(robberTaxScene);
            robberTaxStage.initModality(Modality.NONE);
            robberTaxStage.initOwner(primaryStage);
            robberTaxStage.initStyle(StageStyle.UNDECORATED);
            robberTaxStage.show();
            LOG.debug("Sending ShowRobberTaxUpdateEvent to Lobby {}", lobbyName);
            eventBus.post(
                    new ShowRobberTaxUpdateEvent(event.getLobbyName(), event.getTaxAmount(), event.getInventory()));
        });
    }

    /**
     * Handles the ShowRulesOverviewViewEvent detected on the EventBus
     * <p>
     * If a ShowRulesOverviewViewEvent is detected on the EventBus, this method
     * gets called. It opens the window showing short game rules explainers.
     *
     * @param event The ShowRulesOverviewViewEvent found on the EventBus
     *
     * @author Phillip-André Suhr
     * @since 2021-04-22
     */
    @Subscribe
    private void onShowRulesOverviewViewEvent(ShowRulesOverviewViewEvent event) {
        if (rulesOverviewIsOpen) return;
        Platform.runLater(() -> {
            Stage rulesStage = new Stage();
            rulesOverviewIsOpen = true;
            rulesStage.setTitle(resourceBundle.getString("rules.window.title"));
            rulesStage.setHeight(RulesOverviewPresenter.MIN_HEIGHT);
            rulesStage.setMinHeight(RulesOverviewPresenter.MIN_HEIGHT);
            rulesStage.setWidth(RulesOverviewPresenter.MIN_WIDTH);
            rulesStage.setMinWidth(RulesOverviewPresenter.MIN_WIDTH);
            rulesStage.setResizable(false);
            rulesStage.setScene(rulesScene);
            rulesStage.initOwner(primaryStage);
            rulesStage.show();
            rulesStage.toFront();
            rulesStage.setOnCloseRequest(windowEvent -> {
                rulesOverviewIsOpen = false;
                eventBus.post(new ResetRulesOverviewEvent());
            });
        });
    }

    /**
     * Handles the ShowTradeWithBankViewEvent detected on the EventBus
     * <p>
     * If a ShowTradeWithBankViewEvent is detected on the EventBus, this method gets
     * called. It opens the trading with the bank window in a new window and a
     * TradeUpdateEvent is sent onto the eventBus.
     *
     * @param event The ShowTradeWithBankViewEvent detected on the EventBus
     *
     * @see de.uol.swp.client.trade.event.ShowTradeWithBankViewEvent
     * @since 2021-02-20
     */
    @Subscribe
    private void onShowTradeWithBankViewEvent(ShowTradeWithBankViewEvent event) {
        //gets the lobby's name
        String lobbyName = event.getLobbyName();
        //New window (Stage)
        Stage bankStage = new Stage();
        bankStage.setTitle(resourceBundle.getString("game.trade.window.bank.title"));
        bankStage.setHeight(TradeWithBankPresenter.MIN_HEIGHT);
        bankStage.setMinHeight(TradeWithBankPresenter.MIN_HEIGHT);
        bankStage.setWidth(TradeWithBankPresenter.MIN_WIDTH);
        bankStage.setMinWidth(TradeWithBankPresenter.MIN_WIDTH);
        //Initialises a new lobbyScene
        Parent rootPane = initPresenter(TradeWithBankPresenter.fxml);
        Scene bankScene = new Scene(rootPane);
        bankScene.getStylesheets().add(styleSheet);
        bankStage.setScene(bankScene);
        tradingStages.put(lobbyName, bankStage);
        //Specifies the modality for new window
        bankStage.initModality(Modality.NONE);
        //Specifies the owner Window (parent) for new window
        bankStage.initOwner(primaryStage);
        //Shows the window
        bankStage.show();
        LOG.debug("Sending TradeUpdateEvent for the Lobby {}", lobbyName);
        eventBus.post(new TradeUpdateEvent(lobbyName));
    }

    /**
     * Handles the ShowTradeWithUserRespondViewEvent detected on the EventBus
     * <p>
     * If a ShowTradeWithUserRespondViewEvent is detected on the EventBus, this method gets
     * called. It opens the response window of a trade between 2 users in a new window and a
     * TradeWithUserResponseUpdateEvent is sent onto the EventBus.
     *
     * @param event The ShowTradeWithUserRespondViewEvent detected on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.client.trade.event.TradeWithUserResponseUpdateEvent
     * @see de.uol.swp.client.trade.event.ShowTradeWithUserRespondViewEvent
     * @since 2021-02-23
     */
    @Subscribe
    private void onShowTradeWithUserRespondViewEvent(ShowTradeWithUserRespondViewEvent event) {
        String lobbyName = event.getLobbyName();
        Platform.runLater(() -> {
            if (tradingStages.containsKey(lobbyName)) {
                tradingStages.get(lobbyName).close();
                tradingStages.remove(lobbyName);
            }
            Stage tradingResponseStage = new Stage();
            tradingResponseStage.setTitle(String.format(resourceBundle.getString("game.trade.window.receiving.title"),
                                                        event.getOfferingUser()));
            tradingResponseStage.setHeight(TradeWithUserAcceptPresenter.MIN_HEIGHT);
            tradingResponseStage.setMinHeight(TradeWithUserAcceptPresenter.MIN_HEIGHT);
            tradingResponseStage.setWidth(TradeWithUserAcceptPresenter.MIN_WIDTH);
            tradingResponseStage.setMinWidth(TradeWithUserAcceptPresenter.MIN_WIDTH);
            Parent rootPane = initPresenter(TradeWithUserAcceptPresenter.fxml);
            Scene tradeScene = new Scene(rootPane);
            tradeScene.getStylesheets().add(styleSheet);
            tradingResponseStage.setScene(tradeScene);
            tradingResponseStages.put(lobbyName, tradingResponseStage);
            tradingResponseStage.initModality(Modality.NONE);
            tradingResponseStage.initOwner(primaryStage);
            tradingResponseStage.show();
            LOG.debug("Sending TradeWithUserResponseUpdateEvent to Lobby {}", lobbyName);
            eventBus.post(new TradeWithUserResponseUpdateEvent(event.getRsp()));
        });
    }

    /**
     * Handles the ShowTradeWithUserViewEvent detected on the EventBus
     * <p>
     * If a ShowTradeWithUserViewEvent is detected on the EventBus, this method gets
     * called. It opens the trading with another user window in a new window and a
     * TradeWithUserUpdateEvent is sent onto the EventBus.
     *
     * @param event The TradeWithUserUpdateEvent detected on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.client.trade.event.TradeWithUserUpdateEvent
     * @see de.uol.swp.client.trade.event.ShowTradeWithUserViewEvent
     * @since 2021-02-23
     */
    @Subscribe
    private void onShowTradeWithUserViewEvent(ShowTradeWithUserViewEvent event) {
        String lobbyName = event.getLobbyName();
        Stage tradingStage = new Stage();
        tradingStage.setTitle(
                String.format(resourceBundle.getString("game.trade.window.offering.title"), event.getRespondingUser()));
        tradingStage.setHeight(TradeWithUserPresenter.MIN_HEIGHT);
        tradingStage.setMinHeight(TradeWithUserPresenter.MIN_HEIGHT);
        tradingStage.setWidth(TradeWithUserPresenter.MIN_WIDTH);
        tradingStage.setMinWidth(TradeWithUserPresenter.MIN_WIDTH);
        Parent rootPane = initPresenter(TradeWithUserPresenter.fxml);
        Scene tradeScene = new Scene(rootPane);
        tradeScene.getStylesheets().add(styleSheet);
        tradingStage.setScene(tradeScene);
        tradingStages.put(lobbyName, tradingStage);
        tradingStage.initModality(Modality.NONE);
        tradingStage.initOwner(primaryStage);
        tradingStage.show();
        eventBus.post(new TradeWithUserUpdateEvent(lobbyName));
        LOG.debug("Sending TradeWithUserUpdateEvent to Lobby {}", lobbyName);
    }

    /**
     * Handles the TradeCancelEvent detected on the EventBus
     * <p>
     * If a TradeCancelEvent is detected on the EventBus, this method gets
     * called. If there is a trading stage in the according lobby, it gets closed.
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.client.trade.event.TradeCancelEvent
     * @since 2021-02-23
     */
    @Subscribe
    private void onTradeCancelEvent(TradeCancelEvent event) {
        LOG.debug("Received TradeCancelEvent");
        String lobby = event.getLobbyName();
        Platform.runLater(() -> {
            if (tradingStages.containsKey(lobby)) {
                tradingStages.get(lobby).close();
                tradingStages.remove(lobby);
            }
        });
    }

    /**
     * Handles the TradeErrorEvent detected on the EventBus
     * <p>
     * If a TradeErrorEvent is detected on the EventBus, this method gets
     * called. It shows the error message of the event in an error alert.
     *
     * @param event The LobbyErrorEvent detected on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.client.trade.event.TradeErrorEvent
     * @since 2021-02-25
     */
    @Subscribe
    private void onTradeErrorEvent(TradeErrorEvent event) {
        showError(event.getMessage());
    }

    /**
     * Handles the TradeWithUserCancelResponse detected on the EventBus
     * <p>
     * If a TradeWithUserCancelResponse is detected on the EventBus, this method gets
     * called. If there is a responding trading stage in the according lobby, it gets closed.
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.common.game.response.TradeWithUserCancelResponse
     * @since 2021-02-28
     */
    @Subscribe
    private void onTradeWithUserCancelResponse(TradeWithUserCancelResponse rsp) {
        LOG.debug("Received TradeWithUserCancelResponse");
        String lobby = rsp.getLobbyName();
        if (tradingResponseStages.containsKey(lobby)) {
            Platform.runLater(() -> {
                tradingResponseStages.get(lobby).close();
                tradingResponseStages.remove(lobby);
            });
        }
    }

    /**
     * Switches the current scene and title to the given ones
     * <p>
     * The current scene and title are saved in the lastScene and lastTitle variables
     * before the new scene and title are set and shown.
     *
     * @param scene     New scene to show
     * @param title     New window title
     * @param minWidth  Minimum Width of the scene
     * @param minHeight Minimum Height of the scene
     *
     * @since 2019-09-03
     */
    private void showScene(final Scene scene, final String title, int minWidth, int minHeight) {
        this.lastScene = currentScene;
        this.lastTitle = primaryStage.getTitle();
        this.currentScene = scene;
        Platform.runLater(() -> {
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(minWidth);
            primaryStage.setMinHeight(minHeight);
            primaryStage.setWidth(minWidth);
            primaryStage.setHeight(minHeight);
            primaryStage.show();
        });
    }
}

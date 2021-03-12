package de.uol.swp.client;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.assistedinject.Assisted;
import de.uol.swp.client.ChangePassword.ChangePasswordPresenter;
import de.uol.swp.client.ChangePassword.event.ChangePasswordCanceledEvent;
import de.uol.swp.client.ChangePassword.event.ChangePasswordErrorEvent;
import de.uol.swp.client.ChangePassword.event.ShowChangePasswordViewEvent;
import de.uol.swp.client.auth.LoginPresenter;
import de.uol.swp.client.auth.events.RetryLoginEvent;
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
import de.uol.swp.client.devmenu.DevMenuPresenter;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.lobby.event.CloseLobbiesViewEvent;
import de.uol.swp.client.lobby.event.LobbyErrorEvent;
import de.uol.swp.client.lobby.event.ShowLobbyViewEvent;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.register.RegistrationPresenter;
import de.uol.swp.client.register.event.RegistrationCanceledEvent;
import de.uol.swp.client.register.event.RegistrationErrorEvent;
import de.uol.swp.client.register.event.ShowRegistrationViewEvent;
import de.uol.swp.client.trade.TradeWithBankPresenter;
import de.uol.swp.client.trade.TradeWithUserAcceptPresenter;
import de.uol.swp.client.trade.TradeWithUserPresenter;
import de.uol.swp.client.trade.event.*;
import de.uol.swp.common.devmenu.response.OpenDevMenuResponse;
import de.uol.swp.common.game.response.TradeWithUserCancelResponse;
import de.uol.swp.common.lobby.response.AllLobbiesResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.NukeUsersSessionsRequest;
import de.uol.swp.common.user.response.NukeUsersSessionsResponse;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    static final Logger LOG = LogManager.getLogger(SceneManager.class);
    static final String styleSheet = "css/swp.css";
    private static final int LOBBY_HEIGHT = 730;
    private static final int LOBBY_WIDTH = 685;
    private static final int DEVMENU_HEIGHT = 450;
    private static final int DEVMENU_WIDTH = 630;
    private static final int TRADING_HEIGHT = 600;
    private static final int TRADING_WIDTH = 600;
    private static final int LOGIN_HEIGHT = 220;
    private static final int LOGIN_WIDTH = 400;
    private static final int REGISTRATION_HEIGHT = 250;
    private static final int REGISTRATION_WIDTH = 410;
    private static final int MAINMENU_HEIGHT = 550;
    private static final int MAINMENU_WIDTH = 820;
    private static final int CHANGEPW_HEIGHT = 230;
    private static final int CHANGEPW_WIDTH = 395;
    private static final int RESPONSE_TRADING_WIDTH = 390;
    private static final int RESPONSE_TRADING_HEIGHT = 350;
    private static final int BANK_TRADING_HEIGHT = 420;
    private static final int BANK_TRADING_WIDTH = 600;

    private final ResourceBundle resourceBundle;
    private final Stage primaryStage;
    private final Map<String, Stage> tradingStages = new HashMap<>();
    private final Map<String, Stage> tradingResponseStages = new HashMap<>();
    private final Map<String, Scene> lobbyScenes = new HashMap<>();
    private final List<Stage> lobbyStages = new ArrayList<>();
    private final Injector injector;
    private final EventBus eventBus;
    private Scene loginScene;
    private String lastTitle;
    private Scene registrationScene;
    private Scene mainScene;
    private Scene lastScene = null;
    private Scene currentScene = null;
    private Scene ChangePasswordScene;
    private boolean devMenuIsOpen;

    /**
     * Constructor
     *
     * @param eventBus     The EventBus
     * @param injected     The Guice injector module
     * @param primaryStage The primary Stage
     */
    @Inject
    public SceneManager(EventBus eventBus, Injector injected, @Assisted Stage primaryStage) {
        eventBus.register(this);
        this.eventBus = eventBus;
        this.primaryStage = primaryStage;
        this.injector = injected;
        this.resourceBundle = this.injector.getInstance(ResourceBundle.class);
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
     * Shows the change password screen
     * <p>
     * Sets the scene's UserData to the current user.
     * Switches the current Scene to the ChangePasswordScene
     * and sets the window's title to "Change Password"
     *
     * @author Eric Vuong
     * @author Mario Fokken
     * @since 2020-12-19
     */
    public void showChangePasswordScreen(User user) {
        ChangePasswordScene.setUserData(user);
        showScene(ChangePasswordScene, resourceBundle.getString("changepw.window.title"), CHANGEPW_WIDTH,
                  CHANGEPW_HEIGHT);
    }

    /**
     * Shows an error message inside an error alert
     *
     * @param message The type of error to be shown
     * @param e       The error message
     *
     * @since 2019-09-03
     */
    public void showError(String message, String e) {
        Platform.runLater(() -> {
            String context = e;
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(resourceBundle.getString("error.title"));
            alert.setHeaderText(resourceBundle.getString("error.header"));
            // @formatter:off
            switch (e) {
                //Found in LobbyService
                case "Game session started already!":
                    context = resourceBundle.getString("error.context.sessionstarted");
                case "You're already in this lobby!":
                    context = resourceBundle.getString("error.context.alreadyin");
                case "This lobby is full!":
                    context = resourceBundle.getString("error.context.full");
                case "This lobby does not exist!":
                    context = resourceBundle.getString("error.context.noexistant");
                //Found in GameService
                case "Can not kick while a game is ongoing":
                    context = resourceBundle.getString("error.context.ongoing");
                //Found in ServerHandler
                case "Authorisation required. Client not logged in!":
                    context = resourceBundle.getString("error.context.authneeded");
                //Found in UserManagement
                case "Username already used!":
                    context = resourceBundle.getString("error.context.nameused");
                case "Username unknown!":
                    context = resourceBundle.getString("error.context.unknown");
            }
            //found in UserManagement
            if (e.contains("Cannot auth user "))
                context = resourceBundle.getString("error.context.cannotauth") + e.substring(16);
            //found in UserService
            if (e.contains("Cannot delete user ")) context =
                    resourceBundle.getString("error.context.cannotdelete") + " " +
                    e.substring(18);
            if (e.contains("Cannot create user ")) context =
                    resourceBundle.getString("error.context.cannotcreate") + " " +
                    e.substring(18);
            if (e.contains("Cannot change Password of ")) context =
                    resourceBundle.getString("error.context.cannotchangepw") + " " +
                    e.substring(26);
            //found in LobbyManagement
            if (e.contains(" already exists!")) context =
                    resourceBundle.getString("error.context.lobbyname") + " " +
                    e.substring(10, e.indexOf(" already exists!") - 17) + " " +
                    resourceBundle.getString("error.context.alreadyexists");
            if (e.contains(" not found!")) context =
                    resourceBundle.getString("error.context.lobbyname") + " " +
                    e.substring(10, e.indexOf(" not found!")) + " " +
                    resourceBundle.getString("error.context.notfound");
            //set context
            alert.setContentText(message + context);
            // @formatter:on
            ButtonType confirm = new ButtonType(resourceBundle.getString("button.confirm"),
                                                ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(confirm);
            alert.showAndWait();
            //TODO Registration/ ChangePW ExceptionMessages sind doppelt und nicht vernünftig
            //TODO REST FUNKTIONIERT AUCH SEMI-GUT
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
        showScene(loginScene, resourceBundle.getString("login.window.title"), LOGIN_WIDTH, LOGIN_HEIGHT);
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
                  MAINMENU_WIDTH, MAINMENU_HEIGHT);
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
        showScene(registrationScene, resourceBundle.getString("register.window.title"), REGISTRATION_WIDTH,
                  REGISTRATION_HEIGHT);
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
     * Initialises the Change Password view
     * <p>
     * If the ChangePasswordScene is null, it gets set to a new scene containing the
     * pane showing the Change Password view as specified by the ChangePasswordView
     * FXML file.
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.ChangePassword.ChangePasswordPresenter
     * @since 2020-12-19
     */
    private void initChangePasswordView() {
        if (ChangePasswordScene == null) {
            Parent rootPane = initPresenter(ChangePasswordPresenter.fxml);
            ChangePasswordScene = new Scene(rootPane, 400, 200);
            ChangePasswordScene.getStylesheets().add(styleSheet);
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
            LOG.debug("Loading FXML-File " + url);
            loader.setLocation(url);
            rootPane = loader.load();
        } catch (Exception e) {
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
        initChangePasswordView();
    }

    /**
     * Handles an incoming LobbyListMessage
     * <p>
     * If a LobbyListMessage is detected, the lobbyScenes map
     * is updated to know the same lobbies as the server
     *
     * @param allLobbiesResponse The LobbyListMessage detected on the EventBus
     *
     * @see de.uol.swp.common.lobby.response.AllLobbiesResponse
     * @since 2020-12-12
     */
    @Subscribe
    private void onAllLobbiesResponse(AllLobbiesResponse allLobbiesResponse) {
        LOG.debug("Received AllLobbiesResponse");
        for (String name : allLobbiesResponse.getLobbyNames()) {
            if (!lobbyScenes.containsKey(name)) lobbyScenes.put(name, null); //do not overwrite existing lobbyScene
        }
    }

    /**
     * Handles the ChangePasswordCanceledEvent detected on the EventBus
     * <p>
     * If a ChangePasswordCanceledEvent is detected on the EventBus, this method gets
     * called. It calls a method to show the screen shown before Change Password screen.
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.ChangePassword.event.ChangePasswordCanceledEvent
     * @since 2020-12-19
     */
    @Subscribe
    private void onChangePasswordCanceledEvent(ChangePasswordCanceledEvent event) {
        showScene(lastScene, lastTitle, MAINMENU_WIDTH, MAINMENU_HEIGHT);
    }

    /**
     * Handles the ChangePasswordErrorEvent detected on the EventBus
     * <p>
     * If a ChangePasswordErrorEvent is detected on the EventBus, this method gets
     * called. It shows the error message of the event in a error alert.
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.ChangePassword.event.ChangePasswordErrorEvent
     * @since 2020-12-19
     */
    @Subscribe
    private void onChangePasswordErrorEvent(ChangePasswordErrorEvent event) {
        showError(event.getMessage());
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
     * Handles a CloseTradeWithUserResponseEvent found on the EventBus
     * <p>
     * If a CloseTradeWithUserResponseEvent is detected on the EventBus, this method gets called.
     * Its closes the tradingResponseStage according to the lobbyName.
     *
     * @param event CloseTradeWithUserResponseEvent found on the EventBus
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.client.trade.event.CloseTradeWithUserResponseEvent
     * @since 2021-02-25
     */
    @Subscribe
    private void onCloseTradeWithUserResponseEvent(CloseTradeWithUserResponseEvent event) {
        LOG.debug("Received CloseTradeWithUserResponseEvent");
        String lobby = event.getLobbyName();
        if (tradingResponseStages.containsKey(lobby)) {
            tradingResponseStages.get(lobby).close();
            tradingResponseStages.remove(lobby);
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
     * @see de.uol.swp.common.user.response.NukeUsersSessionsResponse
     * @since 2021-03-03
     */
    @Subscribe
    private void onNukeUsersSessionsResponse(NukeUsersSessionsResponse rsp) {
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
            devMenuStage.setTitle("Developer Access Board");
            devMenuStage.setHeight(DEVMENU_HEIGHT);
            devMenuStage.setMinHeight(DEVMENU_HEIGHT);
            devMenuStage.setWidth(DEVMENU_WIDTH);
            devMenuStage.setMinWidth(DEVMENU_WIDTH);
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
        showScene(lastScene, lastTitle, LOGIN_WIDTH, LOGIN_HEIGHT);
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
     * Handles the ShowChangePasswordViewEvent detected on the EventBus
     * <p>
     * If a ShowChangePasswordViewEvent is detected on the EventBus, this method gets
     * called. It calls a method to switch the current screen to the Change Password
     * screen.
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.ChangePassword.event.ShowChangePasswordViewEvent
     * @since 2020-12-19
     */
    @Subscribe
    private void onShowChangePasswordViewEvent(ShowChangePasswordViewEvent event) {
        showChangePasswordScreen(event.getUser());
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
        lobbyStage.setHeight(LOBBY_HEIGHT);
        lobbyStage.setMinHeight(LOBBY_HEIGHT);
        lobbyStage.setWidth(LOBBY_WIDTH);
        lobbyStage.setMinWidth(LOBBY_WIDTH);
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
     * Handles the ShowTradeWithBankViewEvent detected on the EventBus
     * <p>
     * If a ShowTradeWithBankViewEvent is detected on the EventBus, this method gets
     * called. It opens the trading with the bank window in a new window and a
     * TradeUpdateEvent is sent onto teh eventBus.
     *
     * @param event The ShowTradeWithBankViewEvent detected on the EventBus
     *
     * @see de.uol.swp.client.trade.event.ShowTradeWithBankViewEvent
     * @since 2021-02-20
     */
    @Subscribe
    private void onShowTradeWithBankViewEvent(ShowTradeWithBankViewEvent event) {
        //gets the lobby's name
        User user = event.getUser();
        String lobbyName = event.getLobbyName();
        //New window (Stage)
        Stage bankStage = new Stage();
        bankStage.setTitle(resourceBundle.getString("game.trade.window.bank.title"));
        bankStage.setHeight(BANK_TRADING_HEIGHT);
        bankStage.setMinHeight(BANK_TRADING_HEIGHT);
        bankStage.setWidth(BANK_TRADING_WIDTH);
        bankStage.setMinWidth(BANK_TRADING_WIDTH);
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
        LOG.debug("Sending a TradeUpdateEvent for the lobby " + lobbyName);
        eventBus.post(new TradeUpdateEvent(lobbyName, user));
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
            Stage tradingResponseStage = new Stage();
            tradingResponseStage.setTitle(String.format(resourceBundle.getString("game.trade.window.receiving.title"),
                                                        event.getOfferingUser()));
            tradingResponseStage.setHeight(RESPONSE_TRADING_HEIGHT);
            tradingResponseStage.setMinHeight(RESPONSE_TRADING_HEIGHT);
            tradingResponseStage.setWidth(RESPONSE_TRADING_WIDTH);
            tradingResponseStage.setMinWidth(RESPONSE_TRADING_WIDTH);
            Parent rootPane = initPresenter(TradeWithUserAcceptPresenter.fxml);
            Scene tradeScene = new Scene(rootPane);
            tradeScene.getStylesheets().add(styleSheet);
            tradingResponseStage.setScene(tradeScene);
            tradingResponseStages.put(lobbyName, tradingResponseStage);
            System.out.println("Scene gesetzt");
            System.out.println(tradingResponseStages.get(lobbyName));
            System.out.println(tradingResponseStages.size());
            tradingResponseStage.initModality(Modality.NONE);
            tradingResponseStage.initOwner(primaryStage);
            tradingResponseStage.show();
            LOG.debug("Sending a TradeWithUserResponseUpdateEvent to lobby " + lobbyName);
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
        User offeringUser = event.getOfferingUser();
        Stage tradingStage = new Stage();
        tradingStage.setTitle(String.format(resourceBundle.getString("game.trade.window.offering.title"),
                                            event.getRespondingUserName()));
        tradingStage.setHeight(TRADING_HEIGHT);
        tradingStage.setMinHeight(TRADING_HEIGHT);
        tradingStage.setWidth(TRADING_WIDTH);
        tradingStage.setMinWidth(TRADING_WIDTH);
        Parent rootPane = initPresenter(TradeWithUserPresenter.fxml);
        Scene tradeScene = new Scene(rootPane);
        tradeScene.getStylesheets().add(styleSheet);
        tradingStage.setScene(tradeScene);
        tradingStages.put(lobbyName, tradingStage);
        tradingStage.initModality(Modality.NONE);
        tradingStage.initOwner(primaryStage);
        tradingStage.show();
        eventBus.post(new TradeWithUserUpdateEvent(lobbyName, offeringUser));
        LOG.debug("Sending a TradeWithUserUpdateEvent to lobby " + lobbyName);
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
     * Handles the TradeWithBankCancelEvent detected on the EventBus
     * <p>
     * If a TradeWithBankCancelEvent is detected on the EventBus, this method gets
     * called. If there is a trading stage in the according lobby, it gets closed.
     *
     * @author Maximilian Lindner
     * @author Alwin Bossert
     * @see de.uol.swp.client.trade.event.TradeWithBankCancelEvent
     * @since 2021-02-20
     */
    @Subscribe
    private void onTradeWithBankCancelEvent(TradeWithBankCancelEvent event) {
        LOG.debug("Received TradeWithBankCancelEvent");
        String lobby = event.getLobbyName();
        if (tradingStages.containsKey(lobby)) {
            tradingStages.get(lobby).close();
            tradingStages.remove(lobby);
        }
    }

    /**
     * Handles the TradeWithUserCancelEvent detected on the EventBus
     * <p>
     * If a TradeWithUserCancelEvent is detected on the EventBus, this method gets
     * called. If there is a trading stage in the according lobby, it gets closed.
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @see de.uol.swp.client.trade.event.TradeWithUserCancelEvent
     * @since 2021-02-23
     */
    @Subscribe
    private void onTradeWithUserCancelEvent(TradeWithUserCancelEvent event) {
        LOG.debug("Received TradeWithUserCancelEvent");
        String lobby = event.getLobbyName();
        if (tradingStages.containsKey(lobby)) {
            tradingStages.get(lobby).close();
            tradingStages.remove(lobby);
        }
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
        LOG.debug("Received a TradeWithUserCancelResponse");
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

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
import de.uol.swp.client.auth.events.ShowLoginViewEvent;
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
import de.uol.swp.client.trade.event.ShowTradeWithBankViewEvent;
import de.uol.swp.client.trade.event.TradeUpdateEvent;
import de.uol.swp.client.trade.event.TradeWithBankCancelEvent;
import de.uol.swp.common.lobby.response.AllLobbiesResponse;
import de.uol.swp.common.user.User;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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
    private static final int TRADING_HEIGHT = 600;
    private static final int TRADING_WIDTH = 600;

    private final ResourceBundle resourceBundle;
    private final Stage primaryStage;
    private final Map<String, Scene> lobbyScenes = new HashMap<>();
    private final Map<String, Stage> tradingStage = new HashMap<>();
    private final List<Stage> lobbyStages = new ArrayList<>();
    private final Injector injector;
    private Scene loginScene;
    private String lastTitle;
    private Scene registrationScene;
    private Scene mainScene;
    private Scene lastScene = null;
    private Scene currentScene = null;
    private Scene ChangePasswordScene;
    private Scene tradeWithBankScene;
    private EventBus eventBus;

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
        LOG.debug("Received AllLobiesReponse");
        for (String name : allLobbiesResponse.getLobbyNames()) {
            lobbyScenes.put(name, null);
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
        showScene(lastScene, lastTitle);
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
        showScene(lastScene, lastTitle);
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
        Stage lobbyStage = new Stage();
        lobbyStage.setTitle("Trade of " + user.getUsername());
        lobbyStage.setHeight(TRADING_HEIGHT);
        lobbyStage.setMinHeight(TRADING_HEIGHT);
        lobbyStage.setWidth(TRADING_WIDTH);
        lobbyStage.setMinWidth(TRADING_WIDTH);
        //Initialises a new lobbyScene
        Parent rootPane = initPresenter(TradeWithBankPresenter.fxml);
        Scene lobbyScene = new Scene(rootPane);
        lobbyScene.getStylesheets().add(styleSheet);
        lobbyStage.setScene(lobbyScene);
        tradingStage.put(lobbyName, lobbyStage);
        //Specifies the modality for new window
        lobbyStage.initModality(Modality.NONE);
        //Specifies the owner Window (parent) for new window
        lobbyStage.initOwner(primaryStage);
        //Shows the window
        lobbyStage.show();
        LOG.debug("Sending a TradeUpdateEvent for the lobby " + lobbyName);
        eventBus.post(new TradeUpdateEvent(lobbyName, user));
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
    private void onTradeWithUserCancelEvent(TradeWithBankCancelEvent event) {
        LOG.debug("Received TradeWithUserCancelEvent");
        String lobby = event.getLobbyName();
        if (tradingStage.containsKey(lobby)) {
            tradingStage.get(lobby).close();
            tradingStage.remove(lobby);
        }
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
        showScene(ChangePasswordScene, resourceBundle.getString("changepw.window.title"));
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
            Alert a = new Alert(Alert.AlertType.ERROR, message + e);
            a.showAndWait();
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
     * Shows the login error alert
     * <p>
     * Opens an ErrorAlert popup saying "Error logging in to server"
     *
     * @since 2019-09-03
     */
    public void showLoginErrorScreen() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("login.error"));
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
        System.out.println(lobbyStages.toString());
        showScene(loginScene, resourceBundle.getString("login.window.title"));
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
                  String.format(resourceBundle.getString("mainmenu.window.title"), currentUser.getUsername()));
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
        showScene(registrationScene, resourceBundle.getString("register.window.title"));
    }

    /**
     * Switches the current scene and title to the given ones
     * <p>
     * The current scene and title are saved in the lastScene and lastTitle variables
     * before the new scene and title are set and shown.
     *
     * @param scene New scene to show
     * @param title New window title
     *
     * @since 2019-09-03
     */
    private void showScene(final Scene scene, final String title) {
        this.lastScene = currentScene;
        this.lastTitle = primaryStage.getTitle();
        this.currentScene = scene;
        Platform.runLater(() -> {
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        });
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
}

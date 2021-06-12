package de.uol.swp.client.scene;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import de.uol.swp.client.auth.LoginPresenter;
import de.uol.swp.client.changeAccountDetails.ChangeAccountDetailsPresenter;
import de.uol.swp.client.changeProperties.ChangePropertiesPresenter;
import de.uol.swp.client.devmenu.DevMenuPresenter;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.lobby.RobberTaxPresenter;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.register.RegistrationPresenter;
import de.uol.swp.client.rules.RulesOverviewPresenter;
import de.uol.swp.client.rules.event.ResetRulesOverviewEvent;
import de.uol.swp.client.sound.ISoundService;
import de.uol.swp.client.trade.TradeWithBankPresenter;
import de.uol.swp.client.trade.TradeWithUserAcceptPresenter;
import de.uol.swp.client.trade.TradeWithUserPresenter;
import de.uol.swp.client.util.ThreadManager;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserOrDummy;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.*;
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
@Singleton
public class SceneManager {

    private static final Logger LOG = LogManager.getLogger(SceneManager.class);

    @Inject
    private static Injector injector;

    private final Stage primaryStage;
    private final Map<LobbyName, Stage> tradingStages = new HashMap<>();
    private final Map<LobbyName, Stage> tradingResponseStages = new HashMap<>();
    private final Map<LobbyName, Stage> robberTaxStages = new HashMap<>();
    private final Map<LobbyName, Stage> lobbyStages = new HashMap<>();
    private final EventBus eventBus;
    private final ISoundService soundService;
    private final ResourceBundle resourceBundle;
    private final String styleSheet;

    private Scene loginScene;
    private Scene registrationScene;
    private Scene mainScene;
    private Scene changeAccountDetailsScene;
    private Scene changePropertiesScene;
    private Scene rulesScene;
    private boolean devMenuIsOpen;
    private boolean rulesOverviewIsOpen;

    /**
     * Constructor
     *
     * @param soundService   The SoundService this class should use.
     * @param eventBus       The EventBus this class should use.
     * @param primaryStage   The created PrimaryStage.
     * @param resourceBundle The used ResourceBundle.
     * @param styleSheet     The used StyleSheet.
     */
    @Inject
    public SceneManager(ISoundService soundService, EventBus eventBus, Stage primaryStage,
                        ResourceBundle resourceBundle, @Named("styleSheet") String styleSheet) {
        eventBus.register(this);
        this.soundService = soundService;
        this.eventBus = eventBus;
        this.primaryStage = primaryStage;
        this.resourceBundle = resourceBundle;
        this.styleSheet = styleSheet;
        initViews();
        System.err.println("this is scenemanager@" + hashCode());
    }

    void addPlaceholderScenesForNewLobbies(List<LobbyName> lobbies) {
        for (LobbyName name : lobbies) {
            if (!lobbyStages.containsKey(name)) lobbyStages.put(name, null); //do not overwrite existing lobbyScene
        }
    }

    void closeAcceptTradeWindow(LobbyName lobbyName) {
        if (!tradingResponseStages.containsKey(lobbyName)) {return;}
        Platform.runLater(() -> {
            Window window = tradingResponseStages.get(lobbyName);
            if (window != null) window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
            tradingResponseStages.remove(lobbyName);
        });
    }

    /**
     * Closes all Lobbies
     *
     * @author Finn Haase
     * @author Aldin Dervisi
     * @implNote The closing of the Stages is executed on the JavaFX Application Thread
     * @since 2021-01-28
     */
    void closeLobbies() {
        for (Map.Entry<LobbyName, Stage> entry : lobbyStages.entrySet()) {
            Platform.runLater(entry.getValue()::close);
        }
        lobbyStages.clear();
    }

    /**
     * Method used to close down the client
     *
     * @author Aldin Dervisi
     * @author Marvin Drees
     * @implNote The closing of the Main Screen is executed on the JavaFX Application Thread
     * @since 2021-03-25
     */
    void closeMainScreen() {
        Platform.runLater(primaryStage::close);
    }

    void closeRobberTaxWindow(LobbyName lobbyName) {
        if (robberTaxStages.containsKey(lobbyName)) {
            Platform.runLater(() -> {
                robberTaxStages.get(lobbyName).close();
                robberTaxStages.remove(lobbyName);
            });
        }
    }

    void closeTradeWindow(LobbyName lobbyName) {
        if (!tradingStages.containsKey(lobbyName)) return;
        Platform.runLater(() -> {
            tradingStages.get(lobbyName).close();
            tradingStages.remove(lobbyName);
        });
    }

    void showAcceptTradeWindow(LobbyName lobbyName, UserOrDummy offeringUser) {
        String title = String.format(resourceBundle.getString("game.trade.window.receiving.title"), offeringUser);
        makeAndShowStage(lobbyName, title, TradeWithUserAcceptPresenter.MIN_HEIGHT,
                         TradeWithUserAcceptPresenter.MIN_WIDTH, null, null, TradeWithUserAcceptPresenter.fxml,
                         tradingResponseStages);
    }

    void showBankTradeWindow(LobbyName lobbyName) {
        String title = resourceBundle.getString("game.trade.window.bank.title");
        makeAndShowStage(lobbyName, title, TradeWithBankPresenter.MIN_HEIGHT, TradeWithBankPresenter.MIN_WIDTH, null,
                         null, TradeWithBankPresenter.fxml, tradingStages);
    }

    /**
     * Shows the ChangeAccountDetailsScreen
     * <p>
     * Sets the scene's UserData to the current user.
     * Switches the current Scene to the ChangeAccountDetailsScreen
     * and sets the window's title to "Change AccountDetails"
     *
     * @param loggedInUser The currently logged in User
     *
     * @author Eric Vuong
     * @author Mario Fokken
     * @since 2020-12-19
     */
    void showChangeAccountDetailsScreen(User loggedInUser) {
        showScene(changeAccountDetailsScene, resourceBundle.getString("changeaccdetails.window.title"),
                  ChangeAccountDetailsPresenter.MIN_WIDTH, ChangeAccountDetailsPresenter.MIN_HEIGHT);
        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            showMainScreen(loggedInUser);
        });
    }

    /**
     * Shows the ChangePropertiesScreen
     * <p>
     * Sets the scene's UserData to the current user.
     * Switches the current Scene to the ChangePropertiesScreen
     * and sets the window's title to "Change Properties"
     *
     * @param loggedInUser The currently logged in USer
     *
     * @author Alwin Bossert
     * @since 2021-05-22
     */
    void showChangePropertiesScreen(User loggedInUser) {
        showScene(changePropertiesScene, resourceBundle.getString("changeproperties.window.title"),
                  ChangePropertiesPresenter.MIN_WIDTH, ChangePropertiesPresenter.MIN_HEIGHT);
        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            showMainScreen(loggedInUser);
        });
    }

    void showDevMenuWindow() {
        if (devMenuIsOpen) return;
        devMenuIsOpen = true;
        String title = resourceBundle.getString("devmenu.window.title");
        double xPos = primaryStage.getX() + 100;
        double yPos = primaryStage.getY();
        Platform.runLater(() -> {
            //IllegalStateException - thrown if this constructor is not called on the JavaFX Application Thread.
            Stage devMenuStage = new Stage();
            devMenuStage.setTitle(title);
            devMenuStage.setHeight(DevMenuPresenter.MIN_HEIGHT);
            devMenuStage.setMinHeight(DevMenuPresenter.MIN_HEIGHT);
            devMenuStage.setWidth(DevMenuPresenter.MIN_WIDTH);
            devMenuStage.setMinWidth(DevMenuPresenter.MIN_WIDTH);
            Parent rootPane = initPresenter(DevMenuPresenter.fxml);
            Scene devMenuScene = new Scene(rootPane);
            devMenuScene.getStylesheets().add(styleSheet);
            devMenuStage.setScene(devMenuScene);
            devMenuStage.initOwner(primaryStage);
            devMenuStage.setX(xPos);
            devMenuStage.setY(yPos);
            devMenuStage.show();
            devMenuStage.setOnCloseRequest(event -> devMenuIsOpen = false);
        });
    }

    /**
     * Shows an error message inside an error alert
     *
     * @param message The type of error to be shown
     * @param e       The error message
     *
     * @author Mario Fokken
     * @author Marvin Drees
     * @implNote The method contents are executed on the JavaFX Application Thread
     * @since 2021-03-12
     */
    void showError(String message, String e) {
        soundService.popup();
        String title = resourceBundle.getString("error.title");
        String headerText = resourceBundle.getString("error.header");
        String confirmText = resourceBundle.getString("button.confirm");
        String content = message + internationaliseServerMessage(e);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(headerText);
            alert.setContentText(content);
            ButtonType confirm = new ButtonType(confirmText, ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(confirm);
            alert.getDialogPane().getStylesheets().add(styleSheet);
            alert.showAndWait();
            soundService.button();
        });
    }

    /**
     * Shows an error message inside an error alert
     *
     * @param e The error message
     *
     * @since 2019-09-03
     */
    void showError(String e) {
        showError(resourceBundle.getString("error.generic") + '\n', e);
    }

    void showLobbyWindow(ISimpleLobby lobby) {
        LobbyName lobbyName = lobby.getName();
        double xPos = primaryStage.getX() - (0.5 * LobbyPresenter.MIN_WIDTH_PRE_GAME);
        double yPos = 10;
        makeAndShowStage(lobbyName, lobbyName.getLobbyName(), LobbyPresenter.MIN_HEIGHT_PRE_GAME,
                         LobbyPresenter.MIN_WIDTH_PRE_GAME, xPos, yPos, LobbyPresenter.fxml, lobbyStages);
        //Platform.runLater(() -> {
        //    Stage lobbyStage = new Stage();
        //    lobbyStage.setTitle(lobbyName.toString());
        //    lobbyStage.setHeight(LobbyPresenter.MIN_HEIGHT_PRE_GAME);
        //    lobbyStage.setMinHeight(LobbyPresenter.MIN_HEIGHT_PRE_GAME);
        //    lobbyStage.setWidth(LobbyPresenter.MIN_WIDTH_PRE_GAME);
        //    lobbyStage.setMinWidth(LobbyPresenter.MIN_WIDTH_PRE_GAME);
        //
        //    Parent rootPane = initPresenter(LobbyPresenter.fxml);
        //    Scene lobbyScene = new Scene(rootPane);
        //    lobbyScene.getStylesheets().add(styleSheet);
        //
        //    lobbyStage.setScene(lobbyScene);
        //
        //    lobbyStage.initModality(Modality.NONE);
        //
        //    lobbyStage.initOwner(primaryStage);
        //
        //    lobbyStage.setX(xPos);
        //    lobbyStage.setY(10);
        //
        //    lobbyStage.show();
        //    lobbyStages.put(lobbyName, lobbyStage);
        //});
    }

    /**
     * Shows the login error alert
     * <p>
     * Opens an ErrorAlert popup saying "Error logging in to server"
     *
     * @since 2019-09-03
     */
    void showLoginErrorScreen() {
        // Will we ever use this?
        soundService.popup();
        String contentText = resourceBundle.getString("login.error");
        String confirmText = resourceBundle.getString("button.confirm");
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, contentText);
            ButtonType confirm = new ButtonType(confirmText, ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(confirm);
            alert.getDialogPane().getStylesheets().add(styleSheet);
            alert.showAndWait();
            soundService.button();
        });
        showLoginScreen();
    }

    /**
     * Shows the login screen
     * <p>
     * Switches the current Scene to the loginScene
     * and sets the window's title of to "Login"
     *
     * @since 2019-09-03
     */
    void showLoginScreen() {
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
    void showMainScreen(User currentUser) {
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
    void showRegistrationScreen() {
        showScene(registrationScene, resourceBundle.getString("register.window.title"), RegistrationPresenter.MIN_WIDTH,
                  RegistrationPresenter.MIN_HEIGHT);
    }

    void showRobberTaxWindow(LobbyName lobbyName) {
        String title = resourceBundle.getString("game.robber.tax.title");
        makeAndShowStage(lobbyName, title, RobberTaxPresenter.MIN_HEIGHT, RobberTaxPresenter.MIN_WIDTH, null, null,
                         RobberTaxPresenter.fxml, robberTaxStages);
    }

    void showRulesWindow() {
        if (rulesOverviewIsOpen) return;
        String title = resourceBundle.getString("rules.window.title");
        Platform.runLater(() -> {
            Stage rulesStage = new Stage();
            rulesOverviewIsOpen = true;
            rulesStage.setTitle(title);
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
                ThreadManager.runNow(() -> eventBus.post(new ResetRulesOverviewEvent()));
            });
        });
    }

    /**
     * Method used to display a custom error for the
     * connection timeout.
     *
     * @author Marvin Drees
     * @since 2021-03-26
     */
    void showTimeoutErrorScreen() {
        soundService.popup();
        String contentText = resourceBundle.getString("error.context.disconnected");
        String headerText = resourceBundle.getString("error.header.disconnected");
        String confirmText = resourceBundle.getString("button.confirm");
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, contentText);
            alert.setHeaderText(headerText);
            ButtonType confirm = new ButtonType(confirmText, ButtonBar.ButtonData.OK_DONE);
            alert.getButtonTypes().setAll(confirm);
            alert.getDialogPane().getStylesheets().add(styleSheet);
            alert.showAndWait();
            soundService.button();
        });
    }

    void showUserTradeWindow(LobbyName lobbyName, UserOrDummy respondingUser) {
        String title = String.format(resourceBundle.getString("game.trade.window.offering.title"), respondingUser);
        makeAndShowStage(lobbyName, title, TradeWithUserPresenter.MIN_HEIGHT, TradeWithUserPresenter.MIN_WIDTH, null,
                         null, TradeWithUserPresenter.fxml, tradingStages);
    }

    /**
     * Initialises the ChangeAccountDetailsView
     * <p>
     * If the ChangeAccountDetailsScene is null, it gets set to a new scene containing the
     * pane showing the ChangeAccountDetailsView as specified by the ChangeAccountDetailsView
     * FXML file.
     *
     * @author Eric Vuong
     * @see de.uol.swp.client.changeAccountDetails.ChangeAccountDetailsPresenter
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
     * Initialises the ChangePropertiesView
     * <p>
     * If the ChangePropertiesScene is null, it gets set to a new scene containing the
     * pane showing the ChangePropertiesView as specified by the ChangePropertiesView
     * FXML file.
     *
     * @author Alwin Bossert
     * @see de.uol.swp.client.changeProperties.ChangePropertiesPresenter
     * @since 2021-05-22
     */
    private void initChangePropertiesView() {
        if (changePropertiesScene == null) {
            Parent rootPane = initPresenter(ChangePropertiesPresenter.fxml);
            changePropertiesScene = new Scene(rootPane, 400, 200);
            changePropertiesScene.getStylesheets().add(styleSheet);
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
        initChangePropertiesView();
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
        String substring;
        try {
            substring = e.substring(e.indexOf('[') + 1, e.lastIndexOf(']'));
        } catch (StringIndexOutOfBoundsException ignored) {
            substring = "";
        }
        if (e.contains("already logged in"))
            context = String.format(resourceBundle.getString("error.context.alreadyloggedin"), substring);
        //found in UserService
        if (e.contains("Cannot delete user ")) {
            context = String.format(resourceBundle.getString("error.context.cannotdelete"), substring,
                                    resourceBundle.getString("error.context.unknown"));
        }
        if (e.contains("User deletion unsuccessful for user ")) {
            context = String.format(resourceBundle.getString("error.context.cannotdelete"), substring,
                                    resourceBundle.getString("error.context.wrongpw"));
        }
        if (e.contains("Cannot create user ")) {
            context = String.format(resourceBundle.getString("error.context.cannotcreate"), substring,
                                    resourceBundle.getString("error.context.nameused"));
        }
        if (e.contains("Cannot change Password of ")) {
            context = String.format(resourceBundle.getString("error.context.cannotchangepw"), substring,
                                    resourceBundle.getString("error.context.unknown"));
        }
        //found in LobbyManagement
        if (e.contains("Lobby") && e.contains(" already exists!")) {
            context = String.format(resourceBundle.getString("error.context.lobby.alreadyused"), substring);
        }
        if (e.contains("Lobby") && e.contains(" not found!")) {
            context = String.format(resourceBundle.getString("error.context.lobby.notfound"), substring);
        }
        //found in GameManagement
        if (e.contains("Game") && e.contains(" already exists!")) {
            context = String.format(resourceBundle.getString("error.context.game.alreadyexists"), substring);
        }
        if (e.contains("Game") && e.contains(" not found!")) {
            context = String.format(resourceBundle.getString("error.context.game.notfound"), substring);
        }
        return context;
    }

    private void makeAndShowStage(LobbyName lobbyName, String title, int minHeight, int minWidth, Double x, Double y,
                                  String fxmlPath, Map<LobbyName, Stage> stageMap) {
        Platform.runLater(() -> {
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setHeight(minHeight);
            stage.setMinHeight(minHeight);
            stage.setWidth(minWidth);
            stage.setMinWidth(minWidth);

            Parent rootPane = initPresenter(fxmlPath);
            Scene scene = new Scene(rootPane);
            scene.getStylesheets().add(styleSheet);

            stage.setScene(scene);
            stage.initModality(Modality.NONE);
            stage.initOwner(primaryStage);

            if (x != null) stage.setX(x);
            if (y != null) stage.setY(y);

            if (Objects.equals(robberTaxStages, stageMap)) {
                stage.initStyle(StageStyle.UNDECORATED);
                Platform.setImplicitExit(false);
                stage.setOnCloseRequest(Event::consume);
            }
            stageMap.put(lobbyName, stage);
        });
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

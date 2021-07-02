package de.uol.swp.client.scene;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.auth.LoginPresenter;
import de.uol.swp.client.changeAccountDetails.ChangeAccountDetailsPresenter;
import de.uol.swp.client.changeSettings.ChangeGameSettingsPresenter;
import de.uol.swp.client.changeSettings.ChangeSettingsPresenter;
import de.uol.swp.client.devmenu.DevMenuPresenter;
import de.uol.swp.client.lobby.LobbyPresenter;
import de.uol.swp.client.lobby.RobberTaxPresenter;
import de.uol.swp.client.main.MainMenuPresenter;
import de.uol.swp.client.register.RegistrationPresenter;
import de.uol.swp.client.rules.RulesOverviewPresenter;
import de.uol.swp.client.rules.event.ResetRulesOverviewEvent;
import de.uol.swp.client.scene.event.SetAcceleratorsEvent;
import de.uol.swp.client.sound.ISoundService;
import de.uol.swp.client.specialisedUtil.LobbyStageMap;
import de.uol.swp.client.trade.*;
import de.uol.swp.client.trade.event.ResetTradeWithBankButtonEvent;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.request.NukeUsersSessionsRequest;
import de.uol.swp.common.util.ResourceManager;
import de.uol.swp.common.util.ThreadManager;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static de.uol.swp.client.scene.util.ErrorMessageI18nHelper.internationaliseServerMessage;
import static de.uol.swp.client.scene.util.PresenterAndStageHelper.*;

/**
 * Class that manages which window/scene is currently shown
 *
 * @author Marco Grawunder
 * @since 2019-09-03
 */
@SuppressWarnings("UnstableApiUsage")
public class SceneManager {

    private static final Logger LOG = LogManager.getLogger(SceneManager.class);

    private final EventBus eventBus;
    private final Stage primaryStage;
    private final ISoundService soundService;
    private final ITradeService tradeService;
    private final LobbyStageMap loadingDialogStages = new LobbyStageMap();
    private final LobbyStageMap tradingStages = new LobbyStageMap();
    private final LobbyStageMap tradingResponseStages = new LobbyStageMap();
    private final LobbyStageMap robberTaxStages = new LobbyStageMap();
    private final LobbyStageMap lobbyStages = new LobbyStageMap();

    private Scene loginScene;
    private Scene registrationScene;
    private Scene mainScene;
    private Scene changeAccountScene;
    private Scene changeGameSettingsScene;
    private Scene changeSettingsScene;
    private Scene rulesScene;
    private boolean devMenuIsOpen;
    private boolean rulesOverviewIsOpen;
    private boolean changeGameSettingsViewIsOpen;

    /**
     * Package-private Constructor
     *
     * @param soundService The ISoundService this class should use.
     * @param eventBus     The EventBus this class should use.
     * @param primaryStage The Primary Stage created by JavaFX.
     * @param tradeService The ITradeService this class should use.
     *
     * @implNote This Constructor is used by {@link de.uol.swp.client.scene.SceneManagerProvider}
     */
    @Inject
    SceneManager(ISoundService soundService, EventBus eventBus, Stage primaryStage, ITradeService tradeService) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.soundService = soundService;
        this.primaryStage = primaryStage;
        this.tradeService = tradeService;
        initViews();
        LOG.debug("SceneManager initialised");
    }

    /**
     * Closes the Accept Trade window associated with the provided LobbyName.
     *
     * @param lobbyName The LobbyName whose Accept Trade Offer window to close
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void closeAcceptTradeWindow(LobbyName lobbyName) {
        tradingResponseStages.close(lobbyName);
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
        for (LobbyName lobbyName : lobbyStages.keySet()) lobbyStages.close(lobbyName);
    }

    /**
     * Closes down the primary Stage
     *
     * @author Aldin Dervisi
     * @author Marvin Drees
     * @implNote The closing of the Main Screen is executed on the JavaFX Application Thread
     * @since 2021-03-25
     */
    void closeMainScreen() {
        Platform.setImplicitExit(true);
        Platform.runLater(primaryStage::close);
    }

    /**
     * Closes the Robber Tax window associated with the provided LobbyName.
     *
     * @param lobbyName The LobbyName whose Robber Tax window to close
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void closeRobberTaxWindow(LobbyName lobbyName) {
        // has to be handled like this because the window consumes all
        // WindowEvent.WINDOW_CLOSE_REQUESTs which are used in the LobbyStageMap class
        if (robberTaxStages.containsKey(lobbyName)) {
            Stage robberStage = robberTaxStages.get(lobbyName);
            Platform.runLater(robberStage::close);
            robberTaxStages.remove(lobbyName, robberStage);
        }
    }

    /**
     * Closes the User Trade window associated with the provided LobbyName.
     *
     * @param lobbyName The LobbyName whose User Trade window to close
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void closeTradeWindow(LobbyName lobbyName) {
        tradingStages.close(lobbyName);
    }

    /**
     * Opens a new Accept Trade Offer window associated with the provided LobbyName.
     * <p>
     * The offeringUser is used to set the title of the Accept Trade Offer window.
     *
     * @param lobbyName    The LobbyName with which to associate the new window
     * @param offeringUser The User offering the trade to be displayed
     * @param latch        The CountDownLatch used to signal the SceneService that
     *                     the window is ready
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showAcceptTradeWindow(LobbyName lobbyName, Actor offeringUser, CountDownLatch latch) {
        tradingStages.close(lobbyName);
        EventHandler<WindowEvent> onCloseRequestHandler = windowEvent -> {
            tradeService.resetOfferTradeButton(lobbyName, offeringUser);
            closeAcceptTradeWindow(lobbyName);
        };
        makeAndShowStage(primaryStage, TradeWithUserAcceptPresenter.fxml,
                         ResourceManager.get("game.trade.window.receiving.title", offeringUser),
                         TradeWithUserAcceptPresenter.MIN_HEIGHT, TradeWithUserAcceptPresenter.MIN_WIDTH, lobbyName,
                         tradingResponseStages, onCloseRequestHandler, false, latch);
    }

    /**
     * Opens a new Bank Trade window associated with the provided LobbyName.
     *
     * @param lobbyName The LobbyName with which to associate the new window
     * @param latch     The CountDownLatch used to signal the SceneService that
     *                  the window is ready
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showBankTradeWindow(LobbyName lobbyName, CountDownLatch latch) {
        EventHandler<WindowEvent> onCloseRequestHandler = windowEvent -> {
            closeTradeWindow(lobbyName);
            eventBus.post(new ResetTradeWithBankButtonEvent(lobbyName));
        };
        makeAndShowStage(primaryStage, TradeWithBankPresenter.fxml, ResourceManager.get("game.trade.window.bank.title"),
                         TradeWithBankPresenter.MIN_HEIGHT, TradeWithBankPresenter.MIN_WIDTH, lobbyName, tradingStages,
                         onCloseRequestHandler, false, latch);
    }

    /**
     * Displays the Change Account Details screen on the primary Stage
     *
     * @param loggedInUser The User wanting to edit their account details
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showChangeAccountDetailsScreen(User loggedInUser) {
        showSceneOnPrimaryStage(primaryStage, changeAccountScene, ResourceManager.get("changeaccdetails.window.title"),
                                ChangeAccountDetailsPresenter.MIN_WIDTH, ChangeAccountDetailsPresenter.MIN_HEIGHT);
        primaryStage.setOnCloseRequest(windowEvent -> {
            windowEvent.consume();
            showMainScreen(loggedInUser);
        });
    }

    /**
     * Displays the ChangeGameSettingsView
     *
     * @author Marvin Drees
     * @since 2021-06-28
     */
    void showChangeGameSettingsWindow() {
        if (changeGameSettingsViewIsOpen) return;
        changeGameSettingsViewIsOpen = true;
        makeAndShowStage(primaryStage, ChangeGameSettingsPresenter.fxml, ResourceManager.get(""),
                         ChangeSettingsPresenter.MIN_HEIGHT, ChangeSettingsPresenter.MIN_WIDTH,
                         primaryStage.getX() + 100, primaryStage.getY(), null, null,
                         windowEvent -> changeGameSettingsViewIsOpen = false, false, false, null);
    }

    /**
     * Displays the Change Settings screen on the primary Stage
     *
     * @param loggedInUser The User wanting to change their settings
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showChangeSettingsScreen(User loggedInUser) {
        showSceneOnPrimaryStage(primaryStage, changeSettingsScene, ResourceManager.get("changeproperties.window.title"),
                                ChangeSettingsPresenter.MIN_WIDTH, ChangeSettingsPresenter.MIN_HEIGHT);
        primaryStage.setOnCloseRequest(windowEvent -> showMainScreen(loggedInUser));
    }

    /**
     * Opens a new Developer Menu window
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showDevMenuWindow() {
        if (devMenuIsOpen) return;
        devMenuIsOpen = true;
        makeAndShowStage(primaryStage, DevMenuPresenter.fxml, ResourceManager.get("devmenu.window.title"),
                         DevMenuPresenter.MIN_HEIGHT, DevMenuPresenter.MIN_WIDTH, primaryStage.getX() + 100,
                         primaryStage.getY(), null, null, windowEvent -> devMenuIsOpen = false, false, false, null);
    }

    /**
     * Opens a generic error Alert dialogue window
     *
     * @param message The error message to display to the User
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showError(String message) {
        showError(ResourceManager.get("error.generic") + '\n', message);
    }

    /**
     * Opens an error Alert dialogue window
     *
     * @param title   The title of the Alert dialogue
     * @param message The error message to display to the User
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showError(String title, String message) {
        soundService.popup();
        showAlert(ResourceManager.get("error.title"), title + internationaliseServerMessage(message),
                  ResourceManager.get("error.header"), ResourceManager.get("button.confirm"), Alert.AlertType.ERROR);
    }

    /**
     * Opens a new "Loading Lobby" window for the provided LobbyName
     *
     * @param lobbyName The LobbyName to show a Loading Screen for
     *
     * @author Phillip-André Suhr
     * @since 2021-06-28
     */
    void showLoadingLobbyWindow(LobbyName lobbyName) {
        makeAndShowLoadingLobbyWindow(lobbyName, loadingDialogStages);
    }

    /**
     * Opens a new Lobby window with the provided LobbyName
     *
     * @param lobbyName The LobbyName of the Lobby to open a window for
     * @param latch     The CountDownLatch used to signal the SceneService that
     *                  the window is ready
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showLobbyWindow(LobbyName lobbyName, CountDownLatch latch) {
        EventHandler<WindowEvent> onCloseRequestHandler = windowEvent -> {
            windowEvent.consume();
            // call hide() to trigger onHidingRequest which controls the Lobby's exact shutdown procedure
            lobbyStages.get(lobbyName).hide();
            lobbyStages.close(lobbyName);
        };
        double xPos = Math.max(10, primaryStage.getX() - 0.5 * LobbyPresenter.MIN_WIDTH_IN_GAME);
        makeAndShowStage(primaryStage, LobbyPresenter.fxml, lobbyName.toString(), LobbyPresenter.MIN_HEIGHT_PRE_GAME,
                         LobbyPresenter.MIN_WIDTH_PRE_GAME, xPos, 10.0, lobbyName, lobbyStages, onCloseRequestHandler,
                         false, true, latch);
        try {
            //noinspection ResultOfMethodCallIgnored
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
        loadingDialogStages.close(lobbyName);
        if (lobbyStages.containsKey(lobbyName)) Platform.runLater(() -> lobbyStages.get(lobbyName).show());
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
     * @author Marvin Drees
     * @since 2021-06-29
     */
    void showLogOldSessionOutScreen(User user) {
        soundService.popup();
        Runnable AIDS = () -> {
            LOG.debug("Sending NukeUsersSessionsRequest");
            ThreadManager.runNow(() -> eventBus.post(new NukeUsersSessionsRequest(user)));
        };
        showAndGetConfirmation(ResourceManager.get("confirmation.title"), ResourceManager.get("logoldsessionout.error"),
                               ResourceManager.get("confirmation.header"), ResourceManager.get("button.confirm"),
                               ResourceManager.get("button.cancel"), Alert.AlertType.CONFIRMATION, AIDS);
    }

    /**
     * Displays the Login screen on the primary Stage
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showLoginScreen() {
        showSceneOnPrimaryStage(primaryStage, loginScene, ResourceManager.get("login.window.title"),
                                LoginPresenter.MIN_WIDTH, LoginPresenter.MIN_HEIGHT);
        primaryStage.setOnCloseRequest(windowEvent -> closeMainScreen());
    }

    /**
     * Displays the Main Menu screen on the primary Stage
     * <p>
     * The User is used to customise the window title.
     *
     * @param loggedInUser The logged in User
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showMainScreen(User loggedInUser) {
        showSceneOnPrimaryStage(primaryStage, mainScene, ResourceManager.get("mainmenu.window.title", loggedInUser),
                                MainMenuPresenter.MIN_WIDTH, MainMenuPresenter.MIN_HEIGHT);
        primaryStage.setOnCloseRequest(event -> {
            closeLobbies();
            closeMainScreen();
        });
    }

    /**
     * Displays the Registration screen on the primary Stage
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showRegistrationScreen() {
        showSceneOnPrimaryStage(primaryStage, registrationScene, ResourceManager.get("register.window.title"),
                                RegistrationPresenter.MIN_WIDTH, RegistrationPresenter.MIN_HEIGHT);
        primaryStage.setOnCloseRequest(windowEvent -> closeMainScreen());
    }

    /**
     * Opens a new Robber Tax window associated with the provided LobbyName
     *
     * @param lobbyName The LobbyName for which to open the window
     * @param latch     The CountDownLatch used to signal the SceneService that
     *                  the window is ready
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showRobberTaxWindow(LobbyName lobbyName, CountDownLatch latch) {
        makeAndShowStage(primaryStage, RobberTaxPresenter.fxml, ResourceManager.get("game.robber.tax.title"),
                         RobberTaxPresenter.MIN_HEIGHT, RobberTaxPresenter.MIN_WIDTH, lobbyName, robberTaxStages,
                         Event::consume, true, latch);
    }

    /**
     * Opens a new Rules Overview window
     *
     * @author Phillip-André Suhr
     * @since 2021-06-24
     */
    void showRulesWindow() {
        if (rulesOverviewIsOpen) return;
        rulesOverviewIsOpen = true;
        EventHandler<WindowEvent> onCloseRequestHandler = windowEvent -> {
            rulesOverviewIsOpen = false;
            ThreadManager.runNow(() -> eventBus.post(new ResetRulesOverviewEvent()));
        };
        showStageFromScene(primaryStage, ResourceManager.get("rules.window.title"), RulesOverviewPresenter.MIN_HEIGHT,
                           RulesOverviewPresenter.MIN_WIDTH, rulesScene, onCloseRequestHandler);
    }

    /**
     * Method used to display a custom error for the connection timeout.
     *
     * @author Marvin Drees
     * @since 2021-03-26
     */
    void showTimeoutErrorScreen() {
        soundService.popup();
        showAlert(ResourceManager.get("error.generic"), ResourceManager.get("error.context.disconnected"),
                  ResourceManager.get("error.header.disconnected"), ResourceManager.get("button.confirm"),
                  Alert.AlertType.ERROR);
    }

    /**
     * Opens a new User Trade window associated with the provided LobbyName
     * <p>
     * The respondingUser is the User to whom a Trade Offer is being made.
     *
     * @param lobbyName      The LobbyName for which to open the window
     * @param respondingUser The User to whom a Trade Offer is being made
     * @param latch          The CountDownLatch used to signal the SceneService that
     *                       the window is ready
     *
     * @author Maximilian Lindner
     * @author Finn Haase
     * @since 2021-02-23
     */
    void showUserTradeWindow(LobbyName lobbyName, Actor respondingUser, CountDownLatch latch) {
        String title = ResourceManager.get("game.trade.window.offering.title", respondingUser);
        EventHandler<WindowEvent> onCloseRequestHandler = windowEvent -> {
            closeTradeWindow(lobbyName);
            tradeService.cancelTrade(lobbyName, respondingUser);
        };
        makeAndShowStage(primaryStage, TradeWithUserPresenter.fxml, title, TradeWithUserPresenter.MIN_HEIGHT,
                         TradeWithUserPresenter.MIN_WIDTH, lobbyName, tradingStages, onCloseRequestHandler, false,
                         latch);
    }

    /**
     * Subroutine to initialise all views
     * <p>
     * This is a subroutine of the constructor to initialise all views
     */
    private void initViews() {
        if (loginScene == null) loginScene = initPresenter(LoginPresenter.fxml);
        if (mainScene == null) mainScene = initPresenter(MainMenuPresenter.fxml);
        if (registrationScene == null) registrationScene = initPresenter(RegistrationPresenter.fxml);
        if (rulesScene == null) rulesScene = initPresenter(RulesOverviewPresenter.fxml);
        if (changeAccountScene == null) changeAccountScene = initPresenter(ChangeAccountDetailsPresenter.fxml);
        if (changeSettingsScene == null) changeSettingsScene = initPresenter(ChangeSettingsPresenter.fxml);
        if (changeGameSettingsScene == null) changeGameSettingsScene = initPresenter(ChangeGameSettingsPresenter.fxml);
        eventBus.post(new SetAcceleratorsEvent());
    }
}
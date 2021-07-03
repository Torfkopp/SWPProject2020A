package de.uol.swp.client.scene;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.auth.event.RetryLoginEvent;
import de.uol.swp.client.lobby.ILobbyService;
import de.uol.swp.client.lobby.event.RobberTaxUpdateEvent;
import de.uol.swp.client.main.events.ClientDisconnectedFromServerEvent;
import de.uol.swp.client.trade.ITradeService;
import de.uol.swp.client.trade.event.*;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.devmenu.response.OpenDevMenuResponse;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.user.response.*;
import de.uol.swp.common.util.ResourceManager;
import de.uol.swp.common.util.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("UnstableApiUsage")
public class SceneService implements ISceneService {

    private static final Logger LOG = LogManager.getLogger(SceneService.class);
    private final EventBus eventBus;
    private final ILobbyService lobbyService;
    private final ITradeService tradeService;
    private final IUserService userService;
    private final SceneManager sceneManager;

    @Inject
    public SceneService(EventBus eventBus, IUserService userService, ILobbyService lobbyService,
                        SceneManager sceneManager, ITradeService tradeService) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.userService = userService;
        this.lobbyService = lobbyService;
        this.tradeService = tradeService;
        this.sceneManager = sceneManager;
        LOG.debug("SceneService initialised");
    }

    @Override
    public void closeAcceptTradeWindow(LobbyName lobbyName) {
        sceneManager.closeAcceptTradeWindow(lobbyName);
    }

    @Override
    public void closeAllLobbyWindows() {
        sceneManager.closeLobbies();
    }

    @Override
    public void closeBankTradeWindow(LobbyName lobbyName, boolean wasCanceled) {
        sceneManager.closeTradeWindow(lobbyName);
        if (wasCanceled) {
            LOG.debug("Sending ResetTradeWithBankButtonEvent");
            eventBus.post(new ResetTradeWithBankButtonEvent(lobbyName));
        }
    }

    @Override
    public void closeRobberTaxWindow(LobbyName lobbyName) {
        sceneManager.closeRobberTaxWindow(lobbyName);
    }

    @Override
    public void closeUserTradeWindow(LobbyName lobbyName) {
        sceneManager.closeTradeWindow(lobbyName);
    }

    @Override
    public void displayChangeAccountDetailsScreen() {
        sceneManager.showChangeAccountDetailsScreen(userService.getLoggedInUser());
    }

    @Override
    public void displayChangeSettingsScreen() {
        sceneManager.showChangeSettingsScreen(userService.getLoggedInUser());
    }

    @Override
    public void displayLoginScreen() {
        sceneManager.showLoginScreen();
    }

    @Override
    public void displayMainMenuScreen() {
        sceneManager.showMainScreen(userService.getLoggedInUser());
    }

    @Override
    public void displayRegistrationScreen() {
        sceneManager.showRegistrationScreen();
    }

    @Override
    public void openAcceptTradeWindow(LobbyName lobbyName, Actor offeringUser, TradeWithUserOfferResponse rsp) {
        CountDownLatch latch = new CountDownLatch(1);
        sceneManager.showAcceptTradeWindow(lobbyName, offeringUser, latch);
        ThreadManager.runNow(() -> {
            try {
                //noinspection ResultOfMethodCallIgnored
                latch.await(300, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {}
            LOG.debug("Sending TradeWithUserResponseUpdateEvent for Lobby {}", lobbyName);
            eventBus.post(new TradeWithUserResponseUpdateEvent(rsp));
        });
    }

    @Override
    public void openBankTradeWindow(LobbyName lobbyName) {
        CountDownLatch latch = new CountDownLatch(1);
        sceneManager.showBankTradeWindow(lobbyName, latch);
        ThreadManager.runNow(() -> {
            try {
                //noinspection ResultOfMethodCallIgnored
                latch.await(300, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {}
            LOG.debug("Sending TradeUpdateEvent for the Lobby {}", lobbyName);
            eventBus.post(new TradeUpdateEvent(lobbyName));
            tradeService.tradeWithBank(lobbyName);
        });
    }

    @Override
    public void openChangeGameSettingsWindow() {
        sceneManager.showChangeGameSettingsWindow();
    }

    @Override
    public void openLobbyWindow(ISimpleLobby lobby) {
        CountDownLatch latch = new CountDownLatch(1);
        sceneManager.showLoadingLobbyWindow(lobby.getName());
        sceneManager.showLobbyWindow(lobby.getName(), latch);
        try {
            //noinspection ResultOfMethodCallIgnored
            latch.await(2, TimeUnit.SECONDS);
        } catch (InterruptedException ignored) {}
        lobbyService.refreshLobbyPresenterFields(lobby);
    }

    @Override
    public void openRobberTaxWindow(LobbyName lobbyName, int taxAmount, ResourceList inventory) {
        CountDownLatch latch = new CountDownLatch(1);
        sceneManager.showRobberTaxWindow(lobbyName, latch);
        ThreadManager.runNow(() -> {
            try {
                //noinspection ResultOfMethodCallIgnored
                latch.await(300, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {}
            LOG.debug("Sending RobberTaxUpdateEvent to Lobby {}", lobbyName);
            eventBus.post(new RobberTaxUpdateEvent(lobbyName, taxAmount, inventory));
        });
    }

    @Override
    public void openRulesWindow() {
        sceneManager.showRulesWindow();
    }

    @Override
    public void openUserTradeWindow(LobbyName lobbyName, Actor respondingUser, boolean isCounterOffer) {
        CountDownLatch latch = new CountDownLatch(1);
        sceneManager.showUserTradeWindow(lobbyName, respondingUser, latch);
        ThreadManager.runNow(() -> {
            try {
                //noinspection ResultOfMethodCallIgnored
                latch.await(300, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ignored) {}
            LOG.debug("Sending TradeWithUserUpdateEvent to Lobby {}", lobbyName);
            eventBus.post(new TradeWithUserUpdateEvent(lobbyName));
            tradeService.tradeWithUser(lobbyName, respondingUser, isCounterOffer);
        });
    }

    @Override
    public void showError(String message) {
        sceneManager.showError(message);
    }

    @Override
    public void showServerError(String message) {
        sceneManager.showError(ResourceManager.get("error.server") + '\n', message);
    }

    @Override
    public void showServerError(Throwable e, String cause) {
        if (e instanceof IOException) {
            //so users don't have any access to settings and the like, even though the LogoutRequest won't go through
            userService.logout(false);
            displayLoginScreen();
            cause = ResourceManager.get("error.server.disrupted");
        }
        showServerError(cause);
    }

    /**
     * Handles an old session
     * <p>
     * If an AlreadyLoggedInResponse object is found on the EventBus this method
     * is called. If a client attempts to log in but the user is already
     * logged in elsewhere this method tells the SceneManager to open a popup
     * which prompts the user to log the old session out.
     *
     * @param rsp The AlreadyLoggedInResponse object detected on the EventBus
     *
     * @author Eric Vuong
     * @author Marvin Drees
     * @since 2021-03-03
     */
    @Subscribe
    private void onAlreadyLoggedInResponse(AlreadyLoggedInResponse rsp) {
        LOG.debug("Received AlreadyLoggedInResponse for User {}", rsp.getLoggedInUser());
        sceneManager.showLogOldSessionOutScreen(rsp.getLoggedInUser());
    }

    /**
     * Handles a successful account detail changing process
     * <p>
     * If an ChangeAccountDetailsSuccessfulResponse object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the MainScreen window.
     *
     * @param rsp The ChangeAccountDetailsSuccessfulResponse object detected on the EventBus
     *
     * @author Eric Vuong
     * @author Steven Luong
     * @since 2020-12-03
     */
    @Subscribe
    private void onChangeAccountDetailsSuccessfulResponse(ChangeAccountDetailsSuccessfulResponse rsp) {
        LOG.debug("Received ChangeAccountDetailsSuccessfulResponse");
        sceneManager.showMainScreen(rsp.getUser());
    }

    /**
     * Method used to close the client and show an error.
     * <p>
     * This method is called when a ClientDisconnectedFromServerEvent
     * is found on the EventBus. It shows an error indicating that the
     * connection timed out and closes the client.
     *
     * @param event ClientDisconnectedFromServerEvent found on the EventBus
     *
     * @author Aldin Dervisi
     * @author Marvin Drees
     * @since 2021-03-25
     */
    @Subscribe
    private void onClientDisconnectedFromServer(ClientDisconnectedFromServerEvent event) {
        LOG.debug("Received ClientDisconnectedFromServerEvent");
        sceneManager.showTimeoutErrorScreen();
        sceneManager.closeMainScreen();
    }

    /**
     * Handles a successful login
     * <p>
     * If an LoginSuccessfulResponse object is detected on the EventBus this
     * method is called. It tells the SceneManager to show the main menu, and sets
     * this clients user to the user found in the object. If the loglevel is set
     * to DEBUG or higher, "User logged in successfully " and the username of the
     * logged in user are written to the log.
     *
     * @param rsp The LoginSuccessfulResponse object detected on the EventBus
     *
     * @author Marco Grawunder
     * @since 2017-03-17
     */
    @Subscribe
    private void onLoginSuccessfulResponse(LoginSuccessfulResponse rsp) {
        LOG.debug("Received LoginSuccessfulResponse for User {}", rsp.getUser().getUsername());
        sceneManager.showMainScreen(rsp.getUser());
    }

    /**
     * Handles the NukedUsersSessionsResponse detected on the EventBus
     * <p>
     * If this method is called, it means all sessions belonging to a
     * user have been nuked, therefore it posts a RetryLoginEvent
     * on the EventBus to create a new session for the user.
     *
     * @param rsp The NukedUsersSessionsResponse detected on the EventBus
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
        sceneManager.showDevMenuWindow();
    }

    /**
     * Handles a successful registration
     * <p>
     * If a RegistrationSuccessfulResponse object is detected on the EventBus, this
     * method is called. It tells the SceneManager to show the login window. If
     * the loglevel is set to INFO or higher, "Registration Successful." is written
     * to the log.
     *
     * @param rsp The RegistrationSuccessfulResponse object detected on the EventBus
     *
     * @author Marco Grawunder
     * @since 2019-09-02
     */
    @Subscribe
    private void onRegistrationSuccessfulResponse(RegistrationSuccessfulResponse rsp) {
        LOG.debug("Received RegistrationSuccessfulResponse");
        displayLoginScreen();
    }
}

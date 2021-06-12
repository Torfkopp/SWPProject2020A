package de.uol.swp.client.scene;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.client.SetAcceleratorsEvent;
import de.uol.swp.client.lobby.ILobbyService;
import de.uol.swp.client.lobby.event.RobberTaxUpdateEvent;
import de.uol.swp.client.main.events.ClientDisconnectedFromServerEvent;
import de.uol.swp.client.trade.ITradeService;
import de.uol.swp.client.trade.event.TradeUpdateEvent;
import de.uol.swp.client.trade.event.TradeWithUserResponseUpdateEvent;
import de.uol.swp.client.trade.event.TradeWithUserUpdateEvent;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.client.util.ThreadManager;
import de.uol.swp.common.devmenu.response.OpenDevMenuResponse;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.response.AllLobbiesResponse;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.common.user.response.ChangeAccountDetailsSuccessfulResponse;
import de.uol.swp.common.user.response.LoginSuccessfulResponse;
import de.uol.swp.common.user.response.RegistrationSuccessfulResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ResourceBundle;

@SuppressWarnings("UnstableApiUsage")
public class SceneService implements ISceneService {

    private static final Logger LOG = LogManager.getLogger(SceneService.class);
    private final EventBus eventBus;
    private final ILobbyService lobbyService;
    private final ITradeService tradeService;
    private final IUserService userService;
    private final ResourceBundle resourceBundle;
    private final SceneManager sceneManager;

    @Inject
    public SceneService(EventBus eventBus, IUserService userService, ILobbyService lobbyService,
                        SceneManager sceneManager, ITradeService tradeService, ResourceBundle resourceBundle) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.userService = userService;
        this.lobbyService = lobbyService;
        this.tradeService = tradeService;
        this.sceneManager = sceneManager; // this somehow forces a new SceneManager even though it's a Singleton?
        this.resourceBundle = resourceBundle;
        eventBus.post(new SetAcceleratorsEvent());
        LOG.debug("SceneService initialised");
    }

    @Override
    public void closeAcceptTradeWindow(LobbyName lobbyName) {
        sceneManager.closeAcceptTradeWindow(lobbyName);
    }

    @Override
    public void closeBankTradeWindow(LobbyName lobbyName) {
        sceneManager.closeTradeWindow(lobbyName);
    }

    @Override
    public void closeLobbyWindows() {
        sceneManager.closeLobbies();
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
    public void showAcceptTradeWindow(LobbyName lobbyName, UserOrDummy offeringUser, TradeWithUserOfferResponse rsp) {
        sceneManager.showAcceptTradeWindow(lobbyName, offeringUser);
        ThreadManager.runNow(() -> eventBus.post(new TradeWithUserResponseUpdateEvent(rsp)));
    }

    @Override
    public void showBankTradeWindow(LobbyName lobbyName) {
        sceneManager.showBankTradeWindow(lobbyName);
        LOG.debug("Sending TradeUpdateEvent for the Lobby {}", lobbyName);
        eventBus.post(new TradeUpdateEvent(lobbyName));
        tradeService.tradeWithBank(lobbyName);
    }

    @Override
    public void showChangeAccountDetailsScreen() {
        sceneManager.showChangeAccountDetailsScreen(userService.getLoggedInUser());
    }

    @Override
    public void showChangePropertiesScreen() {
        sceneManager.showChangePropertiesScreen(userService.getLoggedInUser());
    }

    @Override
    public void showError(String message) {
        sceneManager.showError(message);
    }

    @Override
    public void showLobbyWindow(ISimpleLobby lobby) {
        sceneManager.showLobbyWindow(lobby);
        lobbyService.refreshLobbyPresenterFields(lobby);
    }

    @Override
    public void showLoginScreen() {
        sceneManager.showLoginScreen();
    }

    @Override
    public void showMainMenuScreen() {
        sceneManager.showMainScreen(userService.getLoggedInUser());
    }

    @Override
    public void showRegistrationScreen() {
        sceneManager.showRegistrationScreen();
    }

    @Override
    public void showRobberTaxWindow(LobbyName lobbyName, Integer taxAmount, ResourceList inventory) {
        sceneManager.showRobberTaxWindow(lobbyName);
        ThreadManager.runNow(() -> eventBus.post(new RobberTaxUpdateEvent(lobbyName, taxAmount, inventory)));
    }

    @Override
    public void showRulesWindow() {
        sceneManager.showRulesWindow();
    }

    @Override
    public void showServerError(String message) {
        sceneManager.showError(resourceBundle.getString("error.server") + '\n', message);
    }

    @Override
    public void showServerError(Throwable e, String cause) {
        if (e instanceof IOException) {
            //so users don't have any access to settings and the like, even though the LogoutRequest won't go through
            userService.logout(false);
            showLoginScreen();
            cause = resourceBundle.getString("error.server.disrupted");
        }
        showServerError(cause);
    }

    @Override
    public void showUserTradeWindow(LobbyName lobbyName, UserOrDummy respondingUser, boolean isCounterOffer) {
        sceneManager.showUserTradeWindow(lobbyName, respondingUser);
        ThreadManager.runNow(() -> {
            eventBus.post(new TradeWithUserUpdateEvent(lobbyName));
            tradeService.tradeWithUser(lobbyName, respondingUser, isCounterOffer);
        });
    }

    /**
     * Handles an incoming AllLobbiesResponse
     * <p>
     * If a AllLobbiesResponse is detected, the lobbyScenes map
     * is updated to know the same lobbies as the server
     *
     * @param rsp The AllLobbiesResponse detected on the EventBus
     *
     * @see de.uol.swp.common.lobby.response.AllLobbiesResponse
     * @since 2020-12-12
     */
    @Subscribe
    private void onAllLobbiesResponse(AllLobbiesResponse rsp) {
        LOG.debug("Received AllLobbiesResponse");
        sceneManager.addPlaceholderScenesForNewLobbies(rsp.getLobbyNames());
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
        LOG.debug("Account Details change was successful.");
        sceneManager.showMainScreen(rsp.getUser());
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
        LOG.debug("Registration was successful.");
        showLoginScreen();
    }
}

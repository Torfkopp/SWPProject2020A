package de.uol.swp.client.scene;

import com.google.inject.Inject;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.util.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An asynchronous wrapper for the ISceneService implementation
 * <p>
 * This class handles putting calls to an injected SceneService into
 * their own ThreadManager.runNow-Thread which is then executed away from the JavaFX
 * Application Thread, isolating non-UI calls onto their own threads.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.scene.ISceneService
 * @since 2021-06-25
 */
public class AsyncSceneService implements ISceneService {

    private static final Logger LOG = LogManager.getLogger(AsyncSceneService.class);
    private final SceneService syncSceneService;

    /**
     * Constructor
     *
     * @param syncSceneService The synchronous SceneService (injected)
     */
    @Inject
    public AsyncSceneService(SceneService syncSceneService) {
        this.syncSceneService = syncSceneService;
        LOG.debug("AsyncLobbyService initialised");
    }

    @Override
    public void closeAcceptTradeWindow(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncSceneService.closeAcceptTradeWindow(lobbyName));
    }

    @Override
    public void closeAllLobbyWindows() {
        ThreadManager.runNow(syncSceneService::closeAllLobbyWindows);
    }

    @Override
    public void closeBankTradeWindow(LobbyName lobbyName, boolean wasCanceled) {
        ThreadManager.runNow(() -> syncSceneService.closeBankTradeWindow(lobbyName, wasCanceled));
    }

    @Override
    public void closeRobberTaxWindow(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncSceneService.closeRobberTaxWindow(lobbyName));
    }

    @Override
    public void closeUserTradeWindow(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncSceneService.closeUserTradeWindow(lobbyName));
    }

    @Override
    public void displayChangeAccountDetailsScreen() {
        ThreadManager.runNow(syncSceneService::displayChangeAccountDetailsScreen);
    }

    @Override
    public void displayChangeSettingsScreen() {
        ThreadManager.runNow(syncSceneService::displayChangeSettingsScreen);
    }

    @Override
    public void displayLoginScreen() {
        ThreadManager.runNow(syncSceneService::displayLoginScreen);
    }

    @Override
    public void displayMainMenuScreen() {
        ThreadManager.runNow(syncSceneService::displayMainMenuScreen);
    }

    @Override
    public void displayRegistrationScreen() {
        ThreadManager.runNow(syncSceneService::displayRegistrationScreen);
    }

    @Override
    public void openAcceptTradeWindow(LobbyName lobbyName, Actor offeringUser, TradeWithUserOfferResponse rsp) {
        ThreadManager.runNow(() -> syncSceneService.openAcceptTradeWindow(lobbyName, offeringUser, rsp));
    }

    @Override
    public void openBankTradeWindow(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncSceneService.openBankTradeWindow(lobbyName));
    }

    @Override
    public void openChangeGameSettingsWindow() {
        ThreadManager.runNow(syncSceneService::openChangeGameSettingsWindow);
    }

    @Override
    public void openLobbyWindow(ISimpleLobby lobby) {
        ThreadManager.runNow(() -> syncSceneService.openLobbyWindow(lobby));
    }

    @Override
    public void openRobberTaxWindow(LobbyName lobbyName, int taxAmount, ResourceList inventory) {
        ThreadManager.runNow(() -> syncSceneService.openRobberTaxWindow(lobbyName, taxAmount, inventory));
    }

    @Override
    public void openRulesWindow() {
        ThreadManager.runNow(syncSceneService::openRulesWindow);
    }

    @Override
    public void openUserTradeWindow(LobbyName lobbyName, Actor respondingUser, boolean isCounterOffer) {
        ThreadManager.runNow(() -> syncSceneService.openUserTradeWindow(lobbyName, respondingUser, isCounterOffer));
    }

    @Override
    public void showError(String message) {
        ThreadManager.runNow(() -> syncSceneService.showError(message));
    }

    @Override
    public void showServerError(String message) {
        ThreadManager.runNow(() -> syncSceneService.showServerError(message));
    }

    @Override
    public void showServerError(Throwable e, String cause) {
        ThreadManager.runNow(() -> syncSceneService.showServerError(e, cause));
    }
}

package de.uol.swp.client.scene;

import com.google.inject.Inject;
import de.uol.swp.client.util.ThreadManager;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;

public class AsyncSceneService implements ISceneService {

    private final SceneService syncSceneService;

    @Inject
    public AsyncSceneService(SceneService syncSceneService) {
        this.syncSceneService = syncSceneService;
    }

    @Override
    public void closeAcceptTradeWindow(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncSceneService.closeAcceptTradeWindow(lobbyName));
    }

    @Override
    public void closeBankTradeWindow(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncSceneService.closeBankTradeWindow(lobbyName));
    }

    @Override
    public void closeLobbyWindows() {
        ThreadManager.runNow(syncSceneService::closeLobbyWindows);
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
    public void showAcceptTradeWindow(LobbyName lobbyName, UserOrDummy offeringUser, TradeWithUserOfferResponse rsp) {
        ThreadManager.runNow(() -> syncSceneService.showAcceptTradeWindow(lobbyName, offeringUser, rsp));
    }

    @Override
    public void showBankTradeWindow(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncSceneService.showBankTradeWindow(lobbyName));
    }

    @Override
    public void showChangeAccountDetailsScreen() {
        ThreadManager.runNow(syncSceneService::showChangeAccountDetailsScreen);
    }

    @Override
    public void showChangePropertiesScreen() {
        ThreadManager.runNow(syncSceneService::showChangePropertiesScreen);
    }

    @Override
    public void showError(String message) {
        ThreadManager.runNow(() -> syncSceneService.showError(message));
    }

    @Override
    public void showLobbyWindow(ISimpleLobby lobby) {
        ThreadManager.runNow(() -> syncSceneService.showLobbyWindow(lobby));
    }

    @Override
    public void showLoginScreen() {
        ThreadManager.runNow(syncSceneService::showLoginScreen);
    }

    @Override
    public void showMainMenuScreen() {
        ThreadManager.runNow(syncSceneService::showMainMenuScreen);
    }

    @Override
    public void showRegistrationScreen() {
        ThreadManager.runNow(syncSceneService::showRegistrationScreen);
    }

    @Override
    public void showRobberTaxWindow(LobbyName lobbyName, Integer taxAmount, ResourceList inventory) {
        ThreadManager.runNow(() -> syncSceneService.showRobberTaxWindow(lobbyName, taxAmount, inventory));
    }

    @Override
    public void showRulesWindow() {
        ThreadManager.runNow(syncSceneService::showRulesWindow);
    }

    @Override
    public void showServerError(String message) {
        ThreadManager.runNow(() -> syncSceneService.showServerError(message));
    }

    @Override
    public void showServerError(Throwable e, String cause) {
        ThreadManager.runNow(() -> syncSceneService.showServerError(e, cause));
    }

    @Override
    public void showUserTradeWindow(LobbyName lobbyName, UserOrDummy respondingUser, boolean isCounterOffer) {
        ThreadManager.runNow(() -> syncSceneService.showUserTradeWindow(lobbyName, respondingUser, isCounterOffer));
    }
}

package de.uol.swp.client.scene;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class AsyncSceneServiceTest {

    private static final long DURATION = 200L;
    private final LobbyName defaultLobby = mock(LobbyName.class);
    private final SceneService syncSceneService = mock(SceneService.class);
    private AsyncSceneService sceneService;

    @BeforeEach
    protected void setUp() {
        assertNotNull(syncSceneService);
        sceneService = new AsyncSceneService(syncSceneService);
    }

    @AfterEach
    protected void tearDown() {
        sceneService = null;
    }

    @Test
    void closeAcceptTradeWindow() {
        doNothing().when(syncSceneService).closeAcceptTradeWindow(isA(LobbyName.class));

        sceneService.closeAcceptTradeWindow(defaultLobby);

        verify(syncSceneService, after(DURATION)).closeAcceptTradeWindow(defaultLobby);
    }

    @Test
    void closeAllLobbyWindows() {
        doNothing().when(syncSceneService).closeAllLobbyWindows();

        sceneService.closeAllLobbyWindows();

        verify(syncSceneService, after(DURATION)).closeAllLobbyWindows();
    }

    @Test
    void closeBankTradeWindow() {
        doNothing().when(syncSceneService).closeBankTradeWindow(isA(LobbyName.class), isA(Boolean.class));

        sceneService.closeBankTradeWindow(defaultLobby, false);

        verify(syncSceneService, after(DURATION)).closeBankTradeWindow(defaultLobby, false);
    }

    @Test
    void closeRobberTaxWindow() {
        doNothing().when(syncSceneService).closeRobberTaxWindow(isA(LobbyName.class));

        sceneService.closeRobberTaxWindow(defaultLobby);

        verify(syncSceneService, after(DURATION)).closeRobberTaxWindow(defaultLobby);
    }

    @Test
    void closeUserTradeWindow() {
        doNothing().when(syncSceneService).closeUserTradeWindow(isA(LobbyName.class));

        sceneService.closeUserTradeWindow(defaultLobby);

        verify(syncSceneService, after(DURATION)).closeUserTradeWindow(defaultLobby);
    }

    @Test
    void displayChangeAccountDetailsScreen() {
        doNothing().when(syncSceneService).displayChangeAccountDetailsScreen();

        sceneService.displayChangeAccountDetailsScreen();

        verify(syncSceneService, after(DURATION)).displayChangeAccountDetailsScreen();
    }

    @Test
    void displayChangeSettingsScreen() {
        doNothing().when(syncSceneService).displayChangeSettingsScreen();

        sceneService.displayChangeSettingsScreen();

        verify(syncSceneService, after(DURATION)).displayChangeSettingsScreen();
    }

    @Test
    void displayLoginScreen() {
        doNothing().when(syncSceneService).displayLoginScreen();

        sceneService.displayLoginScreen();

        verify(syncSceneService, after(DURATION)).displayLoginScreen();
    }

    @Test
    void displayMainMenuScreen() {
        doNothing().when(syncSceneService).displayMainMenuScreen();

        sceneService.displayMainMenuScreen();

        verify(syncSceneService, after(DURATION)).displayMainMenuScreen();
    }

    @Test
    void displayRegistrationScreen() {
        doNothing().when(syncSceneService).displayRegistrationScreen();

        sceneService.displayRegistrationScreen();

        verify(syncSceneService, after(DURATION)).displayRegistrationScreen();
    }

    @Test
    void openAcceptTradeWindow() {
        Actor actor = mock(Actor.class);
        TradeWithUserOfferResponse response = mock(TradeWithUserOfferResponse.class);
        doNothing().when(syncSceneService).openAcceptTradeWindow(isA(LobbyName.class), isA(Actor.class),
                                                                 isA(TradeWithUserOfferResponse.class));

        sceneService.openAcceptTradeWindow(defaultLobby, actor, response);

        verify(syncSceneService, after(DURATION)).openAcceptTradeWindow(defaultLobby, actor, response);
    }

    @Test
    void openBankTradeWindow() {
        doNothing().when(syncSceneService).openBankTradeWindow(isA(LobbyName.class));

        sceneService.openBankTradeWindow(defaultLobby);

        verify(syncSceneService, after(DURATION)).openBankTradeWindow(defaultLobby);
    }

    @Test
    void openLobbyWindow() {
        ISimpleLobby lobby = mock(ISimpleLobby.class);
        doNothing().when(syncSceneService).openLobbyWindow(isA(ISimpleLobby.class));

        sceneService.openLobbyWindow(lobby);

        verify(syncSceneService, after(DURATION)).openLobbyWindow(lobby);
    }

    @Test
    void openRobberTaxWindow() {
        ResourceList inventory = mock(ResourceList.class);
        doNothing().when(syncSceneService)
                   .openRobberTaxWindow(isA(LobbyName.class), isA(Integer.class), isA(ResourceList.class));

        sceneService.openRobberTaxWindow(defaultLobby, 69, inventory);

        verify(syncSceneService, after(DURATION)).openRobberTaxWindow(defaultLobby, 69, inventory);
    }

    @Test
    void openRulesWindow() {
        doNothing().when(syncSceneService).openRulesWindow();

        sceneService.openRulesWindow();

        verify(syncSceneService, after(DURATION)).openRulesWindow();
    }

    @Test
    void openUserTradeWindow() {
        Actor actor = mock(Actor.class);
        doNothing().when(syncSceneService)
                   .openUserTradeWindow(isA(LobbyName.class), isA(Actor.class), isA(Boolean.class));

        sceneService.openUserTradeWindow(defaultLobby, actor, false);

        verify(syncSceneService, after(DURATION)).openUserTradeWindow(defaultLobby, actor, false);
    }

    @Test
    void showError() {
        String message = "error";
        doNothing().when(syncSceneService).showError(isA(String.class));

        sceneService.showError(message);

        verify(syncSceneService, after(DURATION)).showError(message);
    }

    @Test
    void showServerError() {
        String message = "server error";
        doNothing().when(syncSceneService).showServerError(isA(String.class));

        sceneService.showServerError(message);

        verify(syncSceneService, after(DURATION)).showServerError(message);
    }

    @Test
    void showServerError_WithThrowable() {
        String message = "server error";
        Throwable throwable = mock(Throwable.class);
        doNothing().when(syncSceneService).showServerError(isA(Throwable.class), isA(String.class));

        sceneService.showServerError(throwable, message);

        verify(syncSceneService, after(DURATION)).showServerError(throwable, message);
    }
}
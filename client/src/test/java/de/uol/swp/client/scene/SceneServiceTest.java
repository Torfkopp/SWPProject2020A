package de.uol.swp.client.scene;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
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
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.response.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
class SceneServiceTest {

    private static final long DURATION = 200L;
    private final CountDownLatch lock = new CountDownLatch(1);
    private final LobbyName defaultLobby = mock(LobbyName.class);
    private final EventBus eventBus = new EventBus();
    private final IUserService userService = mock(IUserService.class);
    private final ILobbyService lobbyService = mock(ILobbyService.class);
    private final ITradeService tradeService = mock(ITradeService.class);
    private final SceneManager sceneManager = mock(SceneManager.class);
    private SceneService sceneService;
    private Object event;

    @BeforeEach
    protected void setUp() {
        event = null;
        eventBus.register(this);
        sceneService = new SceneService(eventBus, userService, lobbyService, sceneManager, tradeService);
    }

    @AfterEach
    protected void tearDown() {
        eventBus.unregister(this);
        sceneService = null;
    }

    @Test
    void closeAcceptTradeWindow() {
        doNothing().when(sceneManager).closeAcceptTradeWindow(isA(LobbyName.class));

        sceneService.closeAcceptTradeWindow(defaultLobby);

        verify(sceneManager).closeAcceptTradeWindow(defaultLobby);
    }

    @Test
    void closeAllLobbyWindows() {
        doNothing().when(sceneManager).closeLobbies();

        sceneService.closeAllLobbyWindows();

        verify(sceneManager).closeLobbies();
    }

    @Test
    void closeBankTradeWindow() throws InterruptedException {
        doNothing().when(sceneManager).closeTradeWindow(isA(LobbyName.class));

        sceneService.closeBankTradeWindow(defaultLobby, false);

        verify(sceneManager).closeTradeWindow(defaultLobby);

        boolean value = lock.await(250, TimeUnit.MILLISECONDS);
        assertFalse(value);

        sceneService.closeBankTradeWindow(defaultLobby, true);

        verify(sceneManager, times(2)).closeTradeWindow(defaultLobby);

        value = lock.await(250, TimeUnit.MILLISECONDS);
        assertTrue(value);

        assertTrue(event instanceof ResetTradeWithBankButtonEvent);

        ResetTradeWithBankButtonEvent eve = (ResetTradeWithBankButtonEvent) event;
        assertEquals(defaultLobby, eve.getLobbyName());
    }

    @Test
    void closeRobberTaxWindow() {
        doNothing().when(sceneManager).closeRobberTaxWindow(isA(LobbyName.class));

        sceneService.closeRobberTaxWindow(defaultLobby);

        verify(sceneManager).closeRobberTaxWindow(defaultLobby);
    }

    @Test
    void closeUserTradeWindow() {
        doNothing().when(sceneManager).closeTradeWindow(isA(LobbyName.class));

        sceneService.closeUserTradeWindow(defaultLobby);

        verify(sceneManager).closeTradeWindow(defaultLobby);
    }

    @Test
    void displayChangeAccountDetailsScreen() {
        User user = mock(User.class);
        doReturn(user).when(userService).getLoggedInUser();
        doNothing().when(sceneManager).showChangeAccountDetailsScreen(isA(User.class));

        sceneService.displayChangeAccountDetailsScreen();

        verify(sceneManager).showChangeAccountDetailsScreen(user);
        verify(userService).getLoggedInUser();
    }

    @Test
    void displayChangeSettingsScreen() {
        User user = mock(User.class);
        doReturn(user).when(userService).getLoggedInUser();
        doNothing().when(sceneManager).showChangeSettingsScreen(isA(User.class));

        sceneService.displayChangeSettingsScreen();

        verify(sceneManager).showChangeSettingsScreen(user);
        verify(userService).getLoggedInUser();
    }

    @Test
    void displayLoginScreen() {
        doNothing().when(sceneManager).showLoginScreen();

        sceneService.displayLoginScreen();

        verify(sceneManager).showLoginScreen();
    }

    @Test
    void displayMainMenuScreen() {
        User user = mock(User.class);
        doReturn(user).when(userService).getLoggedInUser();
        doNothing().when(sceneManager).showMainScreen(isA(User.class));

        sceneService.displayMainMenuScreen();

        verify(sceneManager).showMainScreen(user);
    }

    @Test
    void displayRegistrationScreen() {
        doNothing().when(sceneManager).showRegistrationScreen();

        sceneService.displayRegistrationScreen();

        verify(sceneManager).showRegistrationScreen();
    }

    @Test
    void onAlreadyLoggedInResponse() {
        User user = mock(User.class);
        AlreadyLoggedInResponse response = mock(AlreadyLoggedInResponse.class);
        doReturn(user).when(response).getLoggedInUser();
        doNothing().when(sceneManager).showLogOldSessionOutScreen(isA(User.class));

        eventBus.post(response);

        verify(response, times(2)).getLoggedInUser();
        verify(sceneManager).showLogOldSessionOutScreen(user);
    }

    @Test
    void onChangeAccountDetailsResponse() {
        User user = mock(User.class);
        ChangeAccountDetailsSuccessfulResponse response = mock(ChangeAccountDetailsSuccessfulResponse.class);
        doReturn(user).when(response).getUser();
        doNothing().when(sceneManager).showMainScreen(isA(User.class));

        eventBus.post(response);

        verify(response).getUser();
        verify(sceneManager).showMainScreen(user);
    }

    @Test
    void onClientDisconnectedFromServer() {
        doNothing().when(sceneManager).showTimeoutErrorScreen();
        doNothing().when(sceneManager).closeMainScreen();
        ClientDisconnectedFromServerEvent eve = mock(ClientDisconnectedFromServerEvent.class);

        eventBus.post(eve);

        verify(sceneManager).showTimeoutErrorScreen();
        verify(sceneManager).closeMainScreen();
    }

    @Test
    void onLoginSuccessfulResponse() {
        User user = mock(User.class);
        LoginSuccessfulResponse response = mock(LoginSuccessfulResponse.class);
        doReturn(user).when(response).getUser();
        doNothing().when(sceneManager).showMainScreen(isA(User.class));

        eventBus.post(response);

        verify(sceneManager).showMainScreen(user);
    }

    @Test
    void onNukedUsersSessionsResponse() throws InterruptedException {
        NukedUsersSessionsResponse response = mock(NukedUsersSessionsResponse.class);
        eventBus.post(response);
        boolean value = lock.await(500, TimeUnit.MILLISECONDS);

        assertTrue(value);
        assertTrue(event instanceof RetryLoginEvent);
    }

    @Test
    void onOpenDevMenuResponse() {
        OpenDevMenuResponse response = mock(OpenDevMenuResponse.class);
        doNothing().when(sceneManager).showDevMenuWindow();

        eventBus.post(response);

        verify(sceneManager).showDevMenuWindow();
    }

    @Test
    void onRegistrationSuccessfulResponse() {
        RegistrationSuccessfulResponse response = mock(RegistrationSuccessfulResponse.class);
        doNothing().when(sceneManager).showLoginScreen();

        eventBus.post(response);

        verify(sceneManager).showLoginScreen();
    }

    @Test
    void openAcceptTradeWindow() throws InterruptedException {
        Actor actor = mock(Actor.class);
        TradeWithUserOfferResponse response = mock(TradeWithUserOfferResponse.class);
        doNothing().when(sceneManager)
                   .showAcceptTradeWindow(isA(LobbyName.class), isA(Actor.class), isA(CountDownLatch.class));

        sceneService.openAcceptTradeWindow(defaultLobby, actor, response);

        verify(sceneManager).showAcceptTradeWindow(eq(defaultLobby), eq(actor), isA(CountDownLatch.class));

        boolean value = lock.await(500, TimeUnit.MILLISECONDS);

        assertTrue(value);
        assertTrue(event instanceof TradeWithUserResponseUpdateEvent);

        TradeWithUserResponseUpdateEvent eve = (TradeWithUserResponseUpdateEvent) event;

        assertEquals(response, eve.getRsp());
    }

    @Test
    void openBankTradeWindow() throws InterruptedException {
        doNothing().when(sceneManager).showBankTradeWindow(isA(LobbyName.class), isA(CountDownLatch.class));
        doNothing().when(tradeService).tradeWithBank(isA(LobbyName.class));

        sceneService.openBankTradeWindow(defaultLobby);

        verify(sceneManager).showBankTradeWindow(eq(defaultLobby), isA(CountDownLatch.class));

        boolean value = lock.await(500, TimeUnit.MILLISECONDS);

        assertTrue(value);
        assertTrue(event instanceof TradeUpdateEvent);
        TradeUpdateEvent eve = (TradeUpdateEvent) event;

        assertEquals(defaultLobby, eve.getLobbyName());
        verify(tradeService, timeout(DURATION)).tradeWithBank(defaultLobby);
    }

    @Test
    void openLobbyWindow() throws InterruptedException {
        ISimpleLobby lobby = mock(ISimpleLobby.class);
        doReturn(defaultLobby).when(lobby).getName();
        doNothing().when(sceneManager).showLobbyWindow(isA(LobbyName.class), isA(CountDownLatch.class));

        sceneService.openLobbyWindow(lobby);

        verify(sceneManager).showLobbyWindow(eq(defaultLobby), isA(CountDownLatch.class));

        lock.await(350, TimeUnit.MILLISECONDS);

        verify(lobbyService).refreshLobbyPresenterFields(lobby);
    }

    @Test
    void openRobberTaxWindow() throws InterruptedException {
        ResourceList inventory = mock(ResourceList.class);
        doNothing().when(sceneManager).showRobberTaxWindow(isA(LobbyName.class), isA(CountDownLatch.class));

        sceneService.openRobberTaxWindow(defaultLobby, 4, inventory);

        verify(sceneManager).showRobberTaxWindow(eq(defaultLobby), isA(CountDownLatch.class));

        boolean value = lock.await(500, TimeUnit.MILLISECONDS);

        assertTrue(value);
        assertTrue(event instanceof RobberTaxUpdateEvent);
        RobberTaxUpdateEvent eve = (RobberTaxUpdateEvent) event;

        assertEquals(defaultLobby, eve.getLobbyName());
        assertEquals(4, eve.getTaxAmount());
    }

    @Test
    void openRulesWindow() {
        doNothing().when(sceneManager).showRulesWindow();

        sceneService.openRulesWindow();

        verify(sceneManager).showRulesWindow();
    }

    @Test
    void openUserTradeWindow() throws InterruptedException {
        Actor actor = mock(Actor.class);
        doNothing().when(sceneManager)
                   .showUserTradeWindow(isA(LobbyName.class), isA(Actor.class), isA(CountDownLatch.class));
        doNothing().when(tradeService).tradeWithUser(isA(LobbyName.class), isA(Actor.class), isA(Boolean.class));

        sceneService.openUserTradeWindow(defaultLobby, actor, false);

        verify(sceneManager).showUserTradeWindow(eq(defaultLobby), eq(actor), isA(CountDownLatch.class));

        boolean value = lock.await(500, TimeUnit.MILLISECONDS);

        assertTrue(value);
        assertTrue(event instanceof TradeWithUserUpdateEvent);

        TradeWithUserUpdateEvent eve = (TradeWithUserUpdateEvent) event;
        assertEquals(defaultLobby, eve.getLobbyName());

        verify(tradeService, timeout(DURATION)).tradeWithUser(defaultLobby, actor, false);
    }

    @Test
    void showError() {
        String message = "message";
        doNothing().when(sceneManager).showError(isA(String.class));

        sceneService.showError(message);

        verify(sceneManager).showError(message);
    }

    @Test
    void showServerError() {
        String message = "message";
        doNothing().when(sceneManager).showError(isA(String.class));

        sceneService.showServerError(message);

        verify(sceneManager).showError(isA(String.class), eq(message));
    }

    @Test
    void showServerError_WithThrowable() {
        Throwable throwable = mock(Throwable.class);
        String message = "message";
        doNothing().when(sceneManager).showError(isA(String.class), isA(String.class));

        sceneService.showServerError(throwable, message);

        verify(sceneManager).showError(isA(String.class), eq(message));

        Throwable ioException = mock(IOException.class);
        doNothing().when(userService).logout(isA(Boolean.class));
        doNothing().when(sceneManager).showLoginScreen();

        sceneService.showServerError(ioException, message);

        verify(userService).logout(false);
        verify(sceneManager).showLoginScreen();
        verify(sceneManager, times(2)).showError(isA(String.class), isA(String.class));
    }

    @Subscribe
    private void onDeadEvent(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }
}
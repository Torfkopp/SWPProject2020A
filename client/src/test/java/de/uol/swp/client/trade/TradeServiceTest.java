package de.uol.swp.client.trade;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.trade.event.*;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
class TradeServiceTest {

    private static final ResourceList defaultOffer = new ResourceList();
    private static final ResourceList defaultDemand = new ResourceList();
    private static final ResourceList defaultInventory = new ResourceList();
    private static final LobbyName defaultLobbyName = new LobbyName("Test lobby");
    private static final ResourceType defaultGainedResource = ResourceType.BRICK;
    private static final ResourceType defaultLostResource = ResourceType.GRAIN;
    private static final String defaultTradeError = "Test Trade Error";
    private static final User defaultUser = new UserDTO(42, "test", "test123", "test@test.test");
    private static final User secondUser = new UserDTO(69, "test2", "test2123", "test2@test.test");

    private static TradeWithUserOfferResponse defaultTradeOfferResp;

    private final EventBus eventBus = new EventBus();
    private final CountDownLatch lock = new CountDownLatch(1);

    private ITradeService tradeService;
    private IUserService userService;
    private Object event;
    private Object event2;

    @BeforeAll
    static void fillLists() {
        defaultOffer.increase(ResourceType.BRICK, 10);
        defaultOffer.increase(ResourceType.GRAIN, 10);
        defaultDemand.increase(ResourceType.LUMBER, 10);
        defaultDemand.increase(ResourceType.ORE, 10);
        for (ResourceType resource : ResourceType.values()) {
            defaultInventory.increase(resource, 10);
        }
        defaultTradeOfferResp = new TradeWithUserOfferResponse(defaultUser, defaultInventory, defaultOffer,
                                                               defaultDemand, defaultLobbyName);
    }

    @BeforeEach
    protected void setUp() {
        userService = new UserService(eventBus);
        userService.setLoggedInUser(defaultUser);
        tradeService = new TradeService(eventBus, userService);
        eventBus.register(this);
    }

    @AfterEach
    protected void tearDown() {
        event = null;
        tradeService = null;
        userService = null;
        eventBus.unregister(this);
    }

    @Test
    void acceptUserTrade() throws InterruptedException {
        tradeService.acceptUserTrade(defaultLobbyName, secondUser, defaultDemand, defaultOffer);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof AcceptUserTradeRequest);

        AcceptUserTradeRequest request = (AcceptUserTradeRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(secondUser, request.getOfferingUser());
        assertEquals(secondUser.getID(), request.getOfferingUser().getID());
        assertEquals(secondUser.getUsername(), request.getOfferingUser().getUsername());
        assertEquals(defaultUser, request.getRespondingUser());
        assertEquals(defaultUser.getID(), request.getRespondingUser().getID());
        assertEquals(defaultUser.getUsername(), request.getRespondingUser().getUsername());
        for (ResourceType resource : ResourceType.values()) {
            assertEquals(defaultDemand.getAmount(resource), request.getDemandedResources().getAmount(resource));
            assertEquals(defaultOffer.getAmount(resource), request.getOfferedResources().getAmount(resource));
        }
    }

    @Test
    void buyDevelopmentCard() throws InterruptedException {
        tradeService.buyDevelopmentCard(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof BuyDevelopmentCardRequest);

        BuyDevelopmentCardRequest request = (BuyDevelopmentCardRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
    }

    @Test
    void cancelTrade() throws InterruptedException {
        tradeService.cancelTrade(defaultLobbyName, secondUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof TradeWithUserCancelRequest);

        TradeWithUserCancelRequest request = (TradeWithUserCancelRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(secondUser, request.getRespondingUser());
        assertEquals(secondUser.getID(), request.getRespondingUser().getID());
        assertEquals(secondUser.getUsername(), request.getRespondingUser().getUsername());
    }

    @Test
    void closeBankTradeWindow() throws InterruptedException {
        tradeService.closeBankTradeWindow(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof TradeCancelEvent);

        TradeCancelEvent tradeCancelEvent = (TradeCancelEvent) event;

        assertEquals(defaultLobbyName, tradeCancelEvent.getLobbyName());

        assertTrue(event2 instanceof ResetTradeWithBankButtonEvent);

        ResetTradeWithBankButtonEvent resetTradeWithBankButtonEvent = (ResetTradeWithBankButtonEvent) event2;

        assertEquals(defaultLobbyName, resetTradeWithBankButtonEvent.getLobbyName());
    }

    @Test
    void closeTradeResponseWindow() throws InterruptedException {
        tradeService.closeTradeResponseWindow(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof CloseTradeResponseEvent);

        CloseTradeResponseEvent eve = (CloseTradeResponseEvent) event;

        assertEquals(defaultLobbyName, eve.getLobbyName());
    }

    @Test
    void closeUserTradeWindow() throws InterruptedException {
        tradeService.closeUserTradeWindow(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof TradeCancelEvent);

        TradeCancelEvent eve = (TradeCancelEvent) event;

        assertEquals(defaultLobbyName, eve.getLobbyName());
    }

    @Test
    void executeTradeWithBank() throws InterruptedException {
        tradeService.executeTradeWithBank(defaultLobbyName, defaultGainedResource, defaultLostResource);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof ExecuteTradeWithBankRequest);

        ExecuteTradeWithBankRequest request = (ExecuteTradeWithBankRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
        assertEquals(defaultGainedResource, request.getGetResource());
        assertEquals(defaultLostResource, request.getGiveResource());
    }

    @Test
    void offerTrade() throws InterruptedException {
        tradeService.offerTrade(defaultLobbyName, secondUser, defaultOffer, defaultDemand, false);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof OfferingTradeWithUserRequest);

        OfferingTradeWithUserRequest request = (OfferingTradeWithUserRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getOfferingUser());
        assertEquals(defaultUser.getID(), request.getOfferingUser().getID());
        assertEquals(defaultUser.getUsername(), request.getOfferingUser().getUsername());
        assertEquals(secondUser, request.getRespondingUser());
        assertEquals(secondUser.getID(), request.getRespondingUser().getID());
        assertEquals(secondUser.getUsername(), request.getRespondingUser().getUsername());
        assertEquals(defaultOffer, request.getOfferedResources());
        assertEquals(defaultDemand, request.getDemandedResources());
    }

    @Test
    void resetOfferTradeButton() throws InterruptedException {
        tradeService.resetOfferTradeButton(defaultLobbyName, defaultUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof ResetOfferTradeButtonRequest);

        ResetOfferTradeButtonRequest request = (ResetOfferTradeButtonRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getOfferingUser());
        assertEquals(defaultUser.getID(), request.getOfferingUser().getID());
        assertEquals(defaultUser.getUsername(), request.getOfferingUser().getUsername());
    }

    @Test
    void showBankTradeWindow() throws InterruptedException {
        tradeService.showBankTradeWindow(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof ShowTradeWithBankViewEvent);
        ShowTradeWithBankViewEvent eve = (ShowTradeWithBankViewEvent) event;

        assertEquals(defaultLobbyName, eve.getLobbyName());
    }

    @Test
    void showOfferWindow() throws InterruptedException {
        tradeService.showOfferWindow(defaultLobbyName, defaultUser, defaultTradeOfferResp);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof ShowTradeWithUserRespondViewEvent);

        ShowTradeWithUserRespondViewEvent eve = (ShowTradeWithUserRespondViewEvent) event;

        assertEquals(defaultLobbyName, eve.getLobbyName());
        assertEquals(defaultUser, eve.getOfferingUser());
        assertEquals(defaultUser.getID(), eve.getOfferingUser().getID());
        assertEquals(defaultUser.getUsername(), eve.getOfferingUser().getUsername());
        assertEquals(defaultTradeOfferResp, eve.getRsp());
        assertEquals(defaultTradeOfferResp.getOfferedResources(), eve.getRsp().getOfferedResources());
        assertEquals(defaultTradeOfferResp.getResourceList(), eve.getRsp().getResourceList());
        assertEquals(defaultTradeOfferResp.getDemandedResources(), eve.getRsp().getDemandedResources());
    }

    @Test
    void showTradeError() throws InterruptedException {
        tradeService.showTradeError(defaultTradeError);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof TradeErrorEvent);

        TradeErrorEvent eve = (TradeErrorEvent) event;

        assertEquals(defaultTradeError, eve.getMessage());
    }

    @Test
    void showUserTradeWindow() throws InterruptedException {
        tradeService.showUserTradeWindow(defaultLobbyName, secondUser, false);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof ShowTradeWithUserViewEvent);

        ShowTradeWithUserViewEvent eve = (ShowTradeWithUserViewEvent) event;

        assertEquals(defaultLobbyName, eve.getLobbyName());
        assertEquals(secondUser, eve.getRespondingUser());
        assertEquals(secondUser.getID(), eve.getRespondingUser().getID());
        assertEquals(secondUser.getUsername(), eve.getRespondingUser().getUsername());
    }

    @Test
    void tradeWithBank() throws InterruptedException {
        tradeService.tradeWithBank(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof TradeWithBankRequest);

        TradeWithBankRequest request = (TradeWithBankRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
    }

    @Test
    void tradeWithUser() throws InterruptedException {
        tradeService.tradeWithUser(defaultLobbyName, secondUser, false);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof TradeWithUserRequest);

        TradeWithUserRequest request = (TradeWithUserRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getActor());
        assertEquals(defaultUser.getID(), request.getActor().getID());
        assertEquals(defaultUser.getUsername(), request.getActor().getUsername());
        assertEquals(secondUser, request.getRespondingUser());
        assertEquals(secondUser.getID(), request.getRespondingUser().getID());
        assertEquals(secondUser.getUsername(), request.getRespondingUser().getUsername());
    }

    @Subscribe
    private void onDeadEvent(DeadEvent e) {
        if (this.event == null) {
            this.event = e.getEvent();
        } else {
            this.event2 = e.getEvent();
        }
        System.out.print(e.getEvent());
        lock.countDown();
    }
}
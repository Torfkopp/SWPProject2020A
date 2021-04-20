//package de.uol.swp.client.trade;
//
//import com.google.common.eventbus.DeadEvent;
//import com.google.common.eventbus.EventBus;
//import com.google.common.eventbus.Subscribe;
//import de.uol.swp.client.trade.event.*;
//import de.uol.swp.client.user.IUserService;
//import de.uol.swp.client.user.UserService;
//import de.uol.swp.common.game.request.*;
//import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
//import de.uol.swp.common.user.User;
//import de.uol.swp.common.user.UserDTO;
//import org.junit.jupiter.api.*;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@SuppressWarnings("UnstableApiUsage")
//class TradeServiceTest {
//
//    private static final Map<String, Integer> defaultOffer = new HashMap<>();
//    private static final Map<String, Integer> defaultDemand = new HashMap<>();
//    private static final Map<String, Integer> defaultInventoryMap = new HashMap<>();
//    private static final String defaultGainedResource = "game.resources.brick";
//    private static final String defaultLobbyName = "Test lobby";
//    private static final String defaultLostResource = "game.resources.grain";
//    private static final String defaultTradeError = "Test Trade Error";
//    private static final User defaultUser = new UserDTO(42, "test", "test123", "test@test.test");
//    private static final User secondUser = new UserDTO(69, "test2", "test2123", "test2@test.test");
//
//    private static TradeWithUserOfferResponse defaultTradeOfferResp;
//
//    private final EventBus eventBus = new EventBus();
//    private final CountDownLatch lock = new CountDownLatch(1);
//
//    private ITradeService tradeService;
//    private IUserService userService;
//    private Object event;
//
//    @BeforeAll
//    protected static void fillMaps() {
//        defaultOffer.put("game.resources.brick", 10);
//        defaultOffer.put("game.resources.grain", 10);
//        defaultDemand.put("game.resources.lumber", 10);
//        defaultDemand.put("game.resources.ore", 10);
//        defaultInventoryMap.put("game.resources.brick", 10);
//        defaultInventoryMap.put("game.resources.grain", 10);
//        defaultInventoryMap.put("game.resources.ore", 10);
//        defaultInventoryMap.put("game.resources.lumber", 10);
//        defaultInventoryMap.put("game.resources.wool", 10);
//        defaultTradeOfferResp = new TradeWithUserOfferResponse(defaultUser, secondUser, defaultInventoryMap,
//                                                               defaultOffer, defaultDemand, defaultLobbyName);
//    }
//
//    @BeforeEach
//    protected void setUp() {
//        userService = new UserService(eventBus);
//        userService.setLoggedInUser(defaultUser);
//        tradeService = new TradeService(eventBus, userService);
//        eventBus.register(this);
//    }
//
//    @AfterEach
//    protected void tearDown() {
//        event = null;
//        tradeService = null;
//        userService = null;
//        eventBus.unregister(this);
//    }
//
//    @Test
//    void acceptUserTrade() throws InterruptedException {
//        tradeService.acceptUserTrade(defaultLobbyName, secondUser, defaultDemand, defaultOffer);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof AcceptUserTradeRequest);
//
//        AcceptUserTradeRequest request = (AcceptUserTradeRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(secondUser, request.getOfferingUser());
//        assertEquals(secondUser.getID(), request.getOfferingUser().getID());
//        assertEquals(secondUser.getUsername(), request.getOfferingUser().getUsername());
//        assertEquals(defaultUser, request.getRespondingUser());
//        assertEquals(defaultUser.getID(), request.getRespondingUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getRespondingUser().getUsername());
//        assertEquals(defaultDemand, request.getRespondingResourceMap());
//        assertEquals(defaultDemand.keySet(), request.getRespondingResourceMap().keySet());
//        assertEquals(defaultDemand.values(), request.getRespondingResourceMap().values());
//        assertEquals(defaultOffer, request.getOfferingResourceMap());
//        assertEquals(defaultOffer.keySet(), request.getOfferingResourceMap().keySet());
//        assertEquals(defaultOffer.values(), request.getOfferingResourceMap().values());
//    }
//
//    @Test
//    void buyDevelopmentCard() throws InterruptedException {
//        tradeService.buyDevelopmentCard(defaultLobbyName);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof BuyDevelopmentCardRequest);
//
//        BuyDevelopmentCardRequest request = (BuyDevelopmentCardRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//    }
//
//    @Test
//    void cancelTrade() throws InterruptedException {
//        tradeService.cancelTrade(defaultLobbyName, secondUser);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof TradeWithUserCancelRequest);
//
//        TradeWithUserCancelRequest request = (TradeWithUserCancelRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(secondUser, request.getRespondingUser());
//        assertEquals(secondUser.getID(), request.getRespondingUser().getID());
//        assertEquals(secondUser.getUsername(), request.getRespondingUser().getUsername());
//    }
//
//    @Test
//    void closeTradeResponseWindow() throws InterruptedException {
//        tradeService.closeTradeResponseWindow(defaultLobbyName);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof CloseTradeResponseEvent);
//
//        CloseTradeResponseEvent eve = (CloseTradeResponseEvent) event;
//
//        assertEquals(defaultLobbyName, eve.getLobbyName());
//    }
//
//    @Test
//    void closeUserTradeWindow() throws InterruptedException {
//        tradeService.closeUserTradeWindow(defaultLobbyName);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof TradeCancelEvent);
//
//        TradeCancelEvent eve = (TradeCancelEvent) event;
//
//        assertEquals(defaultLobbyName, eve.getLobbyName());
//    }
//
//    @Test
//    void executeTradeWithBank() throws InterruptedException {
//        tradeService.executeTradeWithBank(defaultLobbyName, defaultGainedResource, defaultLostResource);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof ExecuteTradeWithBankRequest);
//
//        ExecuteTradeWithBankRequest request = (ExecuteTradeWithBankRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//        assertEquals(defaultGainedResource, request.getGetResource());
//        assertEquals(defaultLostResource, request.getGiveResource());
//    }
//
//    @Test
//    void offerTrade() throws InterruptedException {
//        tradeService.offerTrade(defaultLobbyName, secondUser, defaultOffer, defaultDemand);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof OfferingTradeWithUserRequest);
//
//        OfferingTradeWithUserRequest request = (OfferingTradeWithUserRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(defaultUser, request.getOfferingUser());
//        assertEquals(defaultUser.getID(), request.getOfferingUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getOfferingUser().getUsername());
//        assertEquals(secondUser, request.getRespondingUser());
//        assertEquals(secondUser.getID(), request.getRespondingUser().getID());
//        assertEquals(secondUser.getUsername(), request.getRespondingUser().getUsername());
//        assertEquals(defaultOffer, request.getOfferingResourceMap());
//        assertEquals(defaultOffer.keySet(), request.getOfferingResourceMap().keySet());
//        assertEquals(defaultOffer.values(), request.getOfferingResourceMap().values());
//        assertEquals(defaultDemand, request.getRespondingResourceMap());
//        assertEquals(defaultDemand.keySet(), request.getRespondingResourceMap().keySet());
//        assertEquals(defaultDemand.values(), request.getRespondingResourceMap().values());
//    }
//
//    @Test
//    void resetOfferTradeButton() throws InterruptedException {
//        tradeService.resetOfferTradeButton(defaultLobbyName, defaultUser);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof ResetOfferTradeButtonRequest);
//
//        ResetOfferTradeButtonRequest request = (ResetOfferTradeButtonRequest) event;
//
//        assertEquals(defaultLobbyName, request.getOriginLobby());
//        assertEquals(defaultUser, request.getOfferingUser());
//        assertEquals(defaultUser.getID(), request.getOfferingUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getOfferingUser().getUsername());
//    }
//
//    @Test
//    void showBankTradeWindow() throws InterruptedException {
//        tradeService.showBankTradeWindow(defaultLobbyName);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof ShowTradeWithBankViewEvent);
//
//        ShowTradeWithBankViewEvent eve = (ShowTradeWithBankViewEvent) event;
//
//        assertEquals(defaultLobbyName, eve.getLobbyName());
//    }
//
//    @Test
//    void showOfferWindow() throws InterruptedException {
//        tradeService.showOfferWindow(defaultLobbyName, defaultUser, defaultTradeOfferResp);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof ShowTradeWithUserRespondViewEvent);
//
//        ShowTradeWithUserRespondViewEvent eve = (ShowTradeWithUserRespondViewEvent) event;
//
//        assertEquals(defaultLobbyName, eve.getLobbyName());
//        assertEquals(defaultUser, eve.getOfferingUser());
//        assertEquals(defaultUser.getID(), eve.getOfferingUser().getID());
//        assertEquals(defaultUser.getUsername(), eve.getOfferingUser().getUsername());
//        assertEquals(defaultTradeOfferResp, eve.getRsp());
//        assertEquals(defaultTradeOfferResp.getOfferingResourceMap(), eve.getRsp().getOfferingResourceMap());
//        assertEquals(defaultTradeOfferResp.getOfferingResourceMap().keySet(),
//                     eve.getRsp().getOfferingResourceMap().keySet());
//        assertEquals(defaultTradeOfferResp.getOfferingResourceMap().values(),
//                     eve.getRsp().getOfferingResourceMap().values());
//        assertEquals(defaultTradeOfferResp.getOfferingUser(), eve.getRsp().getOfferingUser());
//        assertEquals(defaultTradeOfferResp.getOfferingUser().getID(), eve.getRsp().getOfferingUser().getID());
//        assertEquals(defaultTradeOfferResp.getOfferingUser().getUsername(),
//                     eve.getRsp().getOfferingUser().getUsername());
//        assertEquals(defaultTradeOfferResp.getResourceMap(), eve.getRsp().getResourceMap());
//        assertEquals(defaultTradeOfferResp.getResourceMap().keySet(), eve.getRsp().getResourceMap().keySet());
//        assertEquals(defaultTradeOfferResp.getResourceMap().values(), eve.getRsp().getResourceMap().values());
//        assertEquals(defaultTradeOfferResp.getRespondingResourceMap(), eve.getRsp().getRespondingResourceMap());
//        assertEquals(defaultTradeOfferResp.getRespondingResourceMap().keySet(),
//                     eve.getRsp().getRespondingResourceMap().keySet());
//        assertEquals(defaultTradeOfferResp.getRespondingResourceMap().values(),
//                     eve.getRsp().getRespondingResourceMap().values());
//        assertEquals(defaultTradeOfferResp.getRespondingUser(), eve.getRsp().getRespondingUser());
//        assertEquals(defaultTradeOfferResp.getRespondingUser().getID(), eve.getRsp().getRespondingUser().getID());
//        assertEquals(defaultTradeOfferResp.getRespondingUser().getUsername(),
//                     eve.getRsp().getRespondingUser().getUsername());
//    }
//
//    @Test
//    void showTradeError() throws InterruptedException {
//        tradeService.showTradeError(defaultTradeError);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof TradeErrorEvent);
//
//        TradeErrorEvent eve = (TradeErrorEvent) event;
//
//        assertEquals(defaultTradeError, eve.getMessage());
//    }
//
//    @Test
//    void showUserTradeWindow() throws InterruptedException {
//        tradeService.showUserTradeWindow(defaultLobbyName, secondUser);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof ShowTradeWithUserViewEvent);
//
//        ShowTradeWithUserViewEvent eve = (ShowTradeWithUserViewEvent) event;
//
//        assertEquals(defaultLobbyName, eve.getLobbyName());
//        assertEquals(secondUser, eve.getRespondingUser());
//        assertEquals(secondUser.getID(), eve.getRespondingUser().getID());
//        assertEquals(secondUser.getUsername(), eve.getRespondingUser().getUsername());
//    }
//
//    @Test
//    void tradeWithBank() throws InterruptedException {
//        tradeService.tradeWithBank(defaultLobbyName);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof TradeWithBankRequest);
//
//        TradeWithBankRequest request = (TradeWithBankRequest) event;
//
//        assertEquals(defaultLobbyName, request.getName());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//    }
//
//    @Test
//    void tradeWithUser() throws InterruptedException {
//        tradeService.tradeWithUser(defaultLobbyName, secondUser);
//
//        lock.await(250, TimeUnit.MILLISECONDS);
//
//        assertTrue(event instanceof TradeWithUserRequest);
//
//        TradeWithUserRequest request = (TradeWithUserRequest) event;
//
//        assertEquals(defaultLobbyName, request.getName());
//        assertEquals(defaultUser, request.getUser());
//        assertEquals(defaultUser.getID(), request.getUser().getID());
//        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
//        assertEquals(secondUser, request.getRespondingUser());
//        assertEquals(secondUser.getID(), request.getRespondingUser().getID());
//        assertEquals(secondUser.getUsername(), request.getRespondingUser().getUsername());
//    }
//
//    @Subscribe
//    private void onDeadEvent(DeadEvent e) {
//        this.event = e.getEvent();
//        System.out.print(e.getEvent());
//        lock.countDown();
//    }
//}
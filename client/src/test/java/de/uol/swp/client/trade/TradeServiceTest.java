package de.uol.swp.client.trade;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.client.trade.event.*;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.client.user.UserService;
import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.game.map.Resources;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("UnstableApiUsage")
class TradeServiceTest {

    private static final List<Map<String, Object>> defaultOffer = new ArrayList<>();
    private static final List<Map<String, Object>> defaultDemand = new ArrayList<>();
    private static final List<Map<String, Object>> defaultInventoryMap = new ArrayList<>();
    private static final Resources defaultGainedResource = Resources.BRICK;
    private static final String defaultLobbyName = "Test lobby";
    private static final Resources defaultLostResource = Resources.GRAIN;
    private static final String defaultTradeError = "Test Trade Error";
    private static final User defaultUser = new UserDTO(42, "test", "test123", "test@test.test");
    private static final User secondUser = new UserDTO(69, "test2", "test2123", "test2@test.test");

    private static TradeWithUserOfferResponse defaultTradeOfferResp;

    private final EventBus eventBus = new EventBus();
    private final CountDownLatch lock = new CountDownLatch(1);

    private ITradeService tradeService;
    private IUserService userService;
    private Object event;

    @BeforeAll
    protected static void fillMaps() {
        Map<String, Object> brickOffer = new HashMap<>();
        brickOffer.put("amount", 10);
        brickOffer.put("resource", new I18nWrapper("game.resources.brick"));
        brickOffer.put("enumType", Resources.BRICK);
        defaultOffer.add(brickOffer);
        Map<String, Object> grainOffer = new HashMap<>();
        grainOffer.put("amount", 10);
        grainOffer.put("resource", new I18nWrapper("game.resources.grain"));
        grainOffer.put("enumType", Resources.GRAIN);
        defaultOffer.add(grainOffer);
        Map<String, Object> lumberDemand = new HashMap<>();
        lumberDemand.put("amount", 10);
        lumberDemand.put("resource", new I18nWrapper("game.resources.lumber"));
        lumberDemand.put("enumType", Resources.LUMBER);
        defaultDemand.add(lumberDemand);
        Map<String, Object> oreDemand = new HashMap<>();
        oreDemand.put("amount", 10);
        oreDemand.put("resource", new I18nWrapper("game.resources.ore"));
        oreDemand.put("enumType", Resources.ORE);
        defaultDemand.add(oreDemand);
        defaultInventoryMap.add(Map.copyOf(brickOffer));
        defaultInventoryMap.add(Map.copyOf(grainOffer));
        defaultInventoryMap.add(Map.copyOf(oreDemand));
        defaultInventoryMap.add(Map.copyOf(lumberDemand));
        Map<String, Object> woolMap = new HashMap<>();
        woolMap.put("amount", 10);
        woolMap.put("resource", new I18nWrapper("game.resources.wool"));
        woolMap.put("enumType", Resources.WOOL);
        defaultInventoryMap.add(woolMap);
        defaultTradeOfferResp = new TradeWithUserOfferResponse(defaultUser, defaultInventoryMap, defaultOffer,
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
        assertEquals(defaultOffer, request.getOfferedResources());
        assertEquals(defaultOffer.get(0), request.getOfferedResources().get(0));
        assertEquals(defaultOffer.get(0).get("amount"), request.getOfferedResources().get(0).get("amount"));
        assertEquals(defaultOffer.get(0).get("resource"), request.getOfferedResources().get(0).get("resource"));
        assertEquals(defaultOffer.get(0).get("enumType"), request.getOfferedResources().get(0).get("enumType"));
        assertEquals(defaultOffer.get(1), request.getOfferedResources().get(1));
        assertEquals(defaultOffer.get(1).get("amount"), request.getOfferedResources().get(1).get("amount"));
        assertEquals(defaultOffer.get(1).get("resource"), request.getOfferedResources().get(1).get("resource"));
        assertEquals(defaultOffer.get(1).get("enumType"), request.getOfferedResources().get(1).get("enumType"));
        assertEquals(defaultDemand, request.getDemandedResources());
        assertEquals(defaultDemand.get(0), request.getDemandedResources().get(0));
        assertEquals(defaultDemand.get(0).get("amount"), request.getDemandedResources().get(0).get("amount"));
        assertEquals(defaultDemand.get(0).get("resource"), request.getDemandedResources().get(0).get("resource"));
        assertEquals(defaultDemand.get(0).get("enumType"), request.getDemandedResources().get(0).get("enumType"));
        assertEquals(defaultDemand.get(1), request.getDemandedResources().get(1));
        assertEquals(defaultDemand.get(1).get("amount"), request.getDemandedResources().get(1).get("amount"));
        assertEquals(defaultDemand.get(1).get("resource"), request.getDemandedResources().get(1).get("resource"));
        assertEquals(defaultDemand.get(1).get("enumType"), request.getDemandedResources().get(1).get("enumType"));
    }

    @Test
    void buyDevelopmentCard() throws InterruptedException {
        tradeService.buyDevelopmentCard(defaultLobbyName);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof BuyDevelopmentCardRequest);

        BuyDevelopmentCardRequest request = (BuyDevelopmentCardRequest) event;

        assertEquals(defaultLobbyName, request.getOriginLobby());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
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
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
        assertEquals(defaultGainedResource, request.getGetResource());
        assertEquals(defaultLostResource, request.getGiveResource());
    }

    @Test
    void offerTrade() throws InterruptedException {
        tradeService.offerTrade(defaultLobbyName, secondUser, defaultOffer, defaultDemand);

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
        assertEquals(defaultOffer.get(0), request.getOfferedResources().get(0));
        assertEquals(defaultOffer.get(0).get("amount"), request.getOfferedResources().get(0).get("amount"));
        assertEquals(defaultOffer.get(0).get("resource"), request.getOfferedResources().get(0).get("resource"));
        assertEquals(defaultOffer.get(0).get("enumType"), request.getOfferedResources().get(0).get("enumType"));
        assertEquals(defaultOffer.get(1), request.getOfferedResources().get(1));
        assertEquals(defaultOffer.get(1).get("amount"), request.getOfferedResources().get(1).get("amount"));
        assertEquals(defaultOffer.get(1).get("resource"), request.getOfferedResources().get(1).get("resource"));
        assertEquals(defaultOffer.get(1).get("enumType"), request.getOfferedResources().get(1).get("enumType"));
        assertEquals(defaultDemand, request.getDemandedResources());
        assertEquals(defaultDemand.get(0), request.getDemandedResources().get(0));
        assertEquals(defaultDemand.get(0).get("amount"), request.getDemandedResources().get(0).get("amount"));
        assertEquals(defaultDemand.get(0).get("resource"), request.getDemandedResources().get(0).get("resource"));
        assertEquals(defaultDemand.get(0).get("enumType"), request.getDemandedResources().get(0).get("enumType"));
        assertEquals(defaultDemand.get(1), request.getDemandedResources().get(1));
        assertEquals(defaultDemand.get(1).get("amount"), request.getDemandedResources().get(1).get("amount"));
        assertEquals(defaultDemand.get(1).get("resource"), request.getDemandedResources().get(1).get("resource"));
        assertEquals(defaultDemand.get(1).get("enumType"), request.getDemandedResources().get(1).get("enumType"));
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
        assertEquals(defaultTradeOfferResp.getOfferedResources().get(0), eve.getRsp().getOfferedResources().get(0));
        assertEquals(defaultTradeOfferResp.getOfferedResources().get(0).get("amount"),
                     eve.getRsp().getOfferedResources().get(0).get("amount"));
        assertEquals(defaultTradeOfferResp.getOfferedResources().get(0).get("resource"),
                     eve.getRsp().getOfferedResources().get(0).get("resource"));
        assertEquals(defaultTradeOfferResp.getOfferedResources().get(0).get("enumType"),
                     eve.getRsp().getOfferedResources().get(0).get("enumType"));
        assertEquals(defaultTradeOfferResp.getOfferedResources().get(1), eve.getRsp().getOfferedResources().get(1));
        assertEquals(defaultTradeOfferResp.getOfferedResources().get(1).get("amount"),
                     eve.getRsp().getOfferedResources().get(1).get("amount"));
        assertEquals(defaultTradeOfferResp.getOfferedResources().get(1).get("resource"),
                     eve.getRsp().getOfferedResources().get(1).get("resource"));
        assertEquals(defaultTradeOfferResp.getOfferedResources().get(1).get("enumType"),
                     eve.getRsp().getOfferedResources().get(1).get("enumType"));

        assertEquals(defaultTradeOfferResp.getResourceList(), eve.getRsp().getResourceList());

        for (int i = 0; i < Resources.values().length; i++) {
            assertEquals(defaultTradeOfferResp.getResourceList().get(i), eve.getRsp().getResourceList().get(i));
            assertEquals(defaultTradeOfferResp.getResourceList().get(i).get("amount"),
                         eve.getRsp().getResourceList().get(i).get("amount"));
            assertEquals(defaultTradeOfferResp.getResourceList().get(i).get("resource"),
                         eve.getRsp().getResourceList().get(i).get("resource"));
            assertEquals(defaultTradeOfferResp.getResourceList().get(i).get("enumType"),
                         eve.getRsp().getResourceList().get(i).get("enumType"));
        }

        assertEquals(defaultTradeOfferResp.getDemandedResources(), eve.getRsp().getDemandedResources());
        assertEquals(defaultTradeOfferResp.getDemandedResources().get(0), eve.getRsp().getDemandedResources().get(0));
        assertEquals(defaultTradeOfferResp.getDemandedResources().get(0).get("amount"),
                     eve.getRsp().getDemandedResources().get(0).get("amount"));
        assertEquals(defaultTradeOfferResp.getDemandedResources().get(0).get("resource"),
                     eve.getRsp().getDemandedResources().get(0).get("resource"));
        assertEquals(defaultTradeOfferResp.getDemandedResources().get(0).get("enumType"),
                     eve.getRsp().getDemandedResources().get(0).get("enumType"));
        assertEquals(defaultTradeOfferResp.getDemandedResources().get(1), eve.getRsp().getDemandedResources().get(1));
        assertEquals(defaultTradeOfferResp.getDemandedResources().get(1).get("amount"),
                     eve.getRsp().getDemandedResources().get(1).get("amount"));
        assertEquals(defaultTradeOfferResp.getDemandedResources().get(1).get("resource"),
                     eve.getRsp().getDemandedResources().get(1).get("resource"));
        assertEquals(defaultTradeOfferResp.getDemandedResources().get(1).get("enumType"),
                     eve.getRsp().getDemandedResources().get(1).get("enumType"));
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
        tradeService.showUserTradeWindow(defaultLobbyName, secondUser);

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
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
    }

    @Test
    void tradeWithUser() throws InterruptedException {
        tradeService.tradeWithUser(defaultLobbyName, secondUser);

        lock.await(250, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof TradeWithUserRequest);

        TradeWithUserRequest request = (TradeWithUserRequest) event;

        assertEquals(defaultLobbyName, request.getName());
        assertEquals(defaultUser, request.getUser());
        assertEquals(defaultUser.getID(), request.getUser().getID());
        assertEquals(defaultUser.getUsername(), request.getUser().getUsername());
        assertEquals(secondUser, request.getRespondingUser());
        assertEquals(secondUser.getID(), request.getRespondingUser().getID());
        assertEquals(secondUser.getUsername(), request.getRespondingUser().getUsername());
    }

    @Subscribe
    private void onDeadEvent(DeadEvent e) {
        this.event = e.getEvent();
        System.out.print(e.getEvent());
        lock.countDown();
    }
}
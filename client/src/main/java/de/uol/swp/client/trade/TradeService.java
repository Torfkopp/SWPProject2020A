package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.trade.event.*;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.UserOrDummy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

/**
 * The TradeService is responsible for posting requests and events regarding
 * trades between Users or a User and the Bank, like buying Development Cards
 * or offering a trade.
 *
 * @author Maximilian Lindner
 * @author Phillip-André Suhr
 * @since 2021-04-07
 */
@SuppressWarnings("UnstableApiUsage")
public class TradeService implements ITradeService {

    private static final Logger LOG = LogManager.getLogger(LobbyService.class);
    private final EventBus eventBus;
    private final IUserService userService;

    /**
     * Constructor
     *
     * @param eventBus The EventBus set in ClientModule
     *
     * @see de.uol.swp.client.di.ClientModule
     * @since 2021-04-07
     */
    @Inject
    public TradeService(EventBus eventBus, IUserService userService) {
        this.eventBus = eventBus;
        this.eventBus.register(this);
        this.userService = userService;
        LOG.debug("TradeService started");
    }

    @Override
    public void acceptUserTrade(String lobbyName, UserOrDummy offeringUser, Map<String, Integer> demandedResources,
                                Map<String, Integer> offeredResources) {
        LOG.debug("Sending AcceptUserTradeRequest");
        Message request = new AcceptUserTradeRequest(userService.getLoggedInUser(), offeringUser, lobbyName,
                                                     demandedResources, offeredResources);
        eventBus.post(request);
    }

    @Override
    public void buyDevelopmentCard(String lobbyName) {
        LOG.debug("Sending BuyDevelopmentCardRequest");
        Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(userService.getLoggedInUser(), lobbyName);
        eventBus.post(buyDevelopmentCardRequest);
    }

    @Override
    public void cancelTrade(String lobbyName, UserOrDummy respondingUser) {
        LOG.debug("Sending TradeWithUserCancelRequest");
        Message request = new TradeWithUserCancelRequest(lobbyName, respondingUser);
        eventBus.post(request);
    }

    @Override
    public void closeBankTradeWindow(String lobbyName) {
        LOG.debug("Sending TradeCancelEvent");
        eventBus.post(new TradeCancelEvent(lobbyName));
        LOG.debug("Sending ResetTradeWithBankButtonEvent");
        eventBus.post(new ResetTradeWithBankButtonEvent(lobbyName));
    }

    @Override
    public void closeTradeResponseWindow(String lobbyName) {
        LOG.debug("Sending CloseTradeResponseEvent");
        eventBus.post(new CloseTradeResponseEvent(lobbyName));
    }

    @Override
    public void closeUserTradeWindow(String lobbyName) {
        LOG.debug("Sending TradeCancelEvent");
        eventBus.post(new TradeCancelEvent(lobbyName));
    }

    @Override
    public void executeTradeWithBank(String lobbyName, String gainedResource, String lostResource) {
        LOG.debug("Sending ExecuteTradeWithBankRequest");
        Message request = new ExecuteTradeWithBankRequest(userService.getLoggedInUser(), lobbyName, gainedResource,
                                                          lostResource);
        eventBus.post(request);
    }

    @Override
    public void offerTrade(String lobbyName, UserOrDummy respondingUser, Map<String, Integer> offeredResources,
                           Map<String, Integer> demandedResources) {
        LOG.debug("Sending an OfferingTradeWithUserRequest");
        Message request = new OfferingTradeWithUserRequest(userService.getLoggedInUser(), respondingUser, lobbyName,
                                                           offeredResources, demandedResources);
        eventBus.post(request);
    }

    @Override
    public void resetOfferTradeButton(String lobbyName, UserOrDummy offeringUser) {
        LOG.debug("Sending ResetOfferTradeButtonRequest");
        Message request = new ResetOfferTradeButtonRequest(lobbyName, offeringUser);
        eventBus.post(request);
    }

    @Override
    public void showBankTradeWindow(String lobbyName) {
        LOG.debug("Sending ShowTradeWithBankViewEvent");
        eventBus.post(new ShowTradeWithBankViewEvent(lobbyName));
    }

    @Override
    public void showOfferWindow(String lobbyName, UserOrDummy offeringUser, TradeWithUserOfferResponse rsp) {
        LOG.debug("Sending ShowTradeWithUserRespondViewEvent");
        eventBus.post(new ShowTradeWithUserRespondViewEvent(rsp.getOfferingUser(), lobbyName, rsp));
    }

    @Override
    public void showTradeError(String message) {
        LOG.debug("Sending TradeErrorEvent");
        eventBus.post(new TradeErrorEvent(message));
    }

    @Override
    public void showUserTradeWindow(String lobbyName, UserOrDummy respondingUser) {
        LOG.debug("Sending ShowTradeWithUserViewEvent");
        eventBus.post(new ShowTradeWithUserViewEvent(lobbyName, respondingUser));
    }

    @Override
    public void tradeWithBank(String lobbyName) {
        LOG.debug("Sending TradeWithBankRequest");
        Message request = new TradeWithBankRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(request);
    }

    @Override
    public void tradeWithUser(String lobbyName, UserOrDummy respondingUser) {
        LOG.debug("Sending a TradeWithUserRequest");
        Message request = new TradeWithUserRequest(lobbyName, userService.getLoggedInUser(), respondingUser);
        eventBus.post(request);
    }
}

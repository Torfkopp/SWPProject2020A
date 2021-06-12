package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.UserOrDummy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger LOG = LogManager.getLogger(TradeService.class);
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
    public void acceptUserTrade(LobbyName lobbyName, UserOrDummy offeringUser, ResourceList demandedResources,
                                ResourceList offeredResources) {
        LOG.debug("Sending AcceptUserTradeRequest");
        Message request = new AcceptUserTradeRequest(userService.getLoggedInUser(), offeringUser, lobbyName,
                                                     demandedResources, offeredResources);
        eventBus.post(request);
    }

    @Override
    public void buyDevelopmentCard(LobbyName lobbyName) {
        LOG.debug("Sending BuyDevelopmentCardRequest");
        Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(userService.getLoggedInUser(), lobbyName);
        eventBus.post(buyDevelopmentCardRequest);
    }

    @Override
    public void cancelTrade(LobbyName lobbyName, UserOrDummy respondingUser) {
        LOG.debug("Sending TradeWithUserCancelRequest");
        Message request = new TradeWithUserCancelRequest(lobbyName, respondingUser);
        eventBus.post(request);
    }

    @Override
    public void executeTradeWithBank(LobbyName lobbyName, ResourceType gainedResource, ResourceType lostResource) {
        LOG.debug("Sending ExecuteTradeWithBankRequest");
        Message request = new ExecuteTradeWithBankRequest(userService.getLoggedInUser(), lobbyName, gainedResource,
                                                          lostResource);
        eventBus.post(request);
    }

    @Override
    public void offerTrade(LobbyName lobbyName, UserOrDummy respondingUser, ResourceList offeredResources,
                           ResourceList demandedResources, boolean counterOffer) {
        LOG.debug("Sending OfferingTradeWithUserRequest");
        Message request = new OfferingTradeWithUserRequest(userService.getLoggedInUser(), respondingUser, lobbyName,
                                                           offeredResources, demandedResources, counterOffer);
        eventBus.post(request);
    }

    @Override
    public void resetOfferTradeButton(LobbyName lobbyName, UserOrDummy offeringUser) {
        LOG.debug("Sending ResetOfferTradeButtonRequest");
        Message request = new ResetOfferTradeButtonRequest(lobbyName, offeringUser);
        eventBus.post(request);
    }

    @Override
    public void tradeWithBank(LobbyName lobbyName) {
        LOG.debug("Sending TradeWithBankRequest");
        Message request = new TradeWithBankRequest(lobbyName, userService.getLoggedInUser());
        eventBus.post(request);
    }

    @Override
    public void tradeWithUser(LobbyName lobbyName, UserOrDummy respondingUser, boolean counterOffer) {
        LOG.debug("Sending TradeWithUserRequest");
        Message request = new TradeWithUserRequest(lobbyName, userService.getLoggedInUser(), respondingUser,
                                                   counterOffer);
        eventBus.post(request);
    }
}

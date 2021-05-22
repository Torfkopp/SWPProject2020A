package de.uol.swp.client.trade;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import de.uol.swp.client.lobby.LobbyService;
import de.uol.swp.client.trade.event.*;
import de.uol.swp.client.user.IUserService;
import de.uol.swp.common.game.request.*;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.UserOrDummy;
import javafx.concurrent.Task;
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
    public void acceptUserTrade(LobbyName lobbyName, UserOrDummy offeringUser, ResourceList demandedResources,
                                ResourceList offeredResources) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending AcceptUserTradeRequest");
                Message request = new AcceptUserTradeRequest(userService.getLoggedInUser(), offeringUser, lobbyName,
                                                             demandedResources, offeredResources);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void buyDevelopmentCard(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending BuyDevelopmentCardRequest");
                Message buyDevelopmentCardRequest = new BuyDevelopmentCardRequest(userService.getLoggedInUser(),
                                                                                  lobbyName);
                eventBus.post(buyDevelopmentCardRequest);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void cancelTrade(LobbyName lobbyName, UserOrDummy respondingUser) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending TradeWithUserCancelRequest");
                Message request = new TradeWithUserCancelRequest(lobbyName, respondingUser);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void closeBankTradeWindow(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending TradeCancelEvent");
                eventBus.post(new TradeCancelEvent(lobbyName));
                LOG.debug("Sending ResetTradeWithBankButtonEvent");
                eventBus.post(new ResetTradeWithBankButtonEvent(lobbyName));
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void closeTradeResponseWindow(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending CloseTradeResponseEvent");
                eventBus.post(new CloseTradeResponseEvent(lobbyName));
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void closeUserTradeWindow(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending TradeCancelEvent");
                eventBus.post(new TradeCancelEvent(lobbyName));
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void executeTradeWithBank(LobbyName lobbyName, ResourceType gainedResource, ResourceType lostResource) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending ExecuteTradeWithBankRequest");
                Message request = new ExecuteTradeWithBankRequest(userService.getLoggedInUser(), lobbyName,
                                                                  gainedResource, lostResource);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void offerTrade(LobbyName lobbyName, UserOrDummy respondingUser, ResourceList offeredResources,
                           ResourceList demandedResources, boolean counterOffer) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending OfferingTradeWithUserRequest");
                Message request = new OfferingTradeWithUserRequest(userService.getLoggedInUser(), respondingUser,
                                                                   lobbyName, offeredResources, demandedResources,
                                                                   counterOffer);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void resetOfferTradeButton(LobbyName lobbyName, UserOrDummy offeringUser) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending ResetOfferTradeButtonRequest");
                Message request = new ResetOfferTradeButtonRequest(lobbyName, offeringUser);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void showBankTradeWindow(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending ShowTradeWithBankViewEvent");
                eventBus.post(new ShowTradeWithBankViewEvent(lobbyName));
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void showOfferWindow(LobbyName lobbyName, UserOrDummy offeringUser, TradeWithUserOfferResponse rsp) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending ShowTradeWithUserRespondViewEvent");
                eventBus.post(new ShowTradeWithUserRespondViewEvent(rsp.getOfferingUser(), lobbyName, rsp));
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void showTradeError(String message) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending TradeErrorEvent");
                eventBus.post(new TradeErrorEvent(message));
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void showUserTradeWindow(LobbyName lobbyName, UserOrDummy respondingUser) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending ShowTradeWithUserViewEvent");
                eventBus.post(new ShowTradeWithUserViewEvent(lobbyName, respondingUser));
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void tradeWithBank(LobbyName lobbyName) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending TradeWithBankRequest");
                Message request = new TradeWithBankRequest(lobbyName, userService.getLoggedInUser());
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }

    @Override
    public void tradeWithUser(LobbyName lobbyName, UserOrDummy respondingUser, boolean counterOffer) {
        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                LOG.debug("Sending TradeWithUserRequest");
                Message request = new TradeWithUserRequest(lobbyName, userService.getLoggedInUser(), respondingUser,
                                                           counterOffer);
                eventBus.post(request);
                return true;
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
}

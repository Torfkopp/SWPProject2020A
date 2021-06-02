package de.uol.swp.client.trade;

import com.google.inject.Inject;
import de.uol.swp.client.util.ThreadManager;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An asynchronous wrapper for the ITradeService implementation
 * <p>
 * This class handles putting calls to an injected ChatService into
 * their own Task-Thread which is then executed away from the JavaFX
 * Application Thread, isolating non-UI calls onto their own threads.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.trade.ITradeService
 * @since 2021-05-23
 */
public class AsyncTradeService implements ITradeService {

    private static final Logger LOG = LogManager.getLogger(AsyncTradeService.class);
    private final TradeService syncTradeService;

    @Inject
    public AsyncTradeService(TradeService syncTradeService) {
        this.syncTradeService = syncTradeService;
        LOG.debug("AsyncTradeService initialised");
    }

    @Override
    public void acceptUserTrade(LobbyName lobbyName, UserOrDummy offeringUser, ResourceList demandedResources,
                                ResourceList offeredResources) {
        ThreadManager.runNow(() -> syncTradeService
                .acceptUserTrade(lobbyName, offeringUser, demandedResources, offeredResources));
    }

    @Override
    public void buyDevelopmentCard(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncTradeService.buyDevelopmentCard(lobbyName));
    }

    @Override
    public void cancelTrade(LobbyName lobbyName, UserOrDummy respondingUser) {
        ThreadManager.runNow(() -> syncTradeService.cancelTrade(lobbyName, respondingUser));
    }

    @Override
    public void closeBankTradeWindow(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncTradeService.closeBankTradeWindow(lobbyName));
    }

    @Override
    public void closeTradeResponseWindow(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncTradeService.closeTradeResponseWindow(lobbyName));
    }

    @Override
    public void closeUserTradeWindow(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncTradeService.closeUserTradeWindow(lobbyName));
    }

    @Override
    public void executeTradeWithBank(LobbyName lobbyName, ResourceType gainedResource, ResourceType lostResource) {
        ThreadManager.runNow(() -> syncTradeService.executeTradeWithBank(lobbyName, gainedResource, lostResource));
    }

    @Override
    public void offerTrade(LobbyName lobbyName, UserOrDummy respondingUser, ResourceList offeredResources,
                           ResourceList demandedResources, boolean counterOffer) {
        ThreadManager.runNow(() -> syncTradeService
                .offerTrade(lobbyName, respondingUser, offeredResources, demandedResources, counterOffer));
    }

    @Override
    public void resetOfferTradeButton(LobbyName lobbyName, UserOrDummy offeringUser) {
        ThreadManager.runNow(() -> syncTradeService.resetOfferTradeButton(lobbyName, offeringUser));
    }

    @Override
    public void showBankTradeWindow(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncTradeService.showBankTradeWindow(lobbyName));
    }

    @Override
    public void showOfferWindow(LobbyName lobbyName, UserOrDummy offeringUser, TradeWithUserOfferResponse rsp) {
        ThreadManager.runNow(() -> syncTradeService.showOfferWindow(lobbyName, offeringUser, rsp));
    }

    @Override
    public void showTradeError(String message) {
        ThreadManager.runNow(() -> syncTradeService.showTradeError(message));
    }

    @Override
    public void showUserTradeWindow(LobbyName lobbyName, UserOrDummy respondingUser, boolean isCounterOffer) {
        ThreadManager.runNow(() -> syncTradeService.showUserTradeWindow(lobbyName, respondingUser, isCounterOffer));
    }

    @Override
    public void tradeWithBank(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncTradeService.tradeWithBank(lobbyName));
    }

    @Override
    public void tradeWithUser(LobbyName lobbyName, UserOrDummy respondingUser, boolean counterOffer) {
        ThreadManager.runNow(() -> syncTradeService.tradeWithUser(lobbyName, respondingUser, counterOffer));
    }
}

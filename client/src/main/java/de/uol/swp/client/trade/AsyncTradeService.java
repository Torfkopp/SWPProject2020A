package de.uol.swp.client.trade;

import com.google.inject.Inject;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import de.uol.swp.common.util.ThreadManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * An asynchronous wrapper for the ITradeService implementation
 * <p>
 * This class handles putting calls to an injected TradeService into
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

    /**
     * Constructor
     *
     * @param syncTradeService The synchronous TradeService (injected)
     */
    @Inject
    public AsyncTradeService(TradeService syncTradeService) {
        this.syncTradeService = syncTradeService;
        LOG.debug("AsyncTradeService initialised");
    }

    @Override
    public void acceptUserTrade(LobbyName lobbyName, Actor offeringUser, ResourceList demandedResources,
                                ResourceList offeredResources) {
        ThreadManager.runNow(() -> syncTradeService
                .acceptUserTrade(lobbyName, offeringUser, demandedResources, offeredResources));
    }

    @Override
    public void buyDevelopmentCard(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncTradeService.buyDevelopmentCard(lobbyName));
    }

    @Override
    public void cancelTrade(LobbyName lobbyName, Actor respondingUser) {
        ThreadManager.runNow(() -> syncTradeService.cancelTrade(lobbyName, respondingUser));
    }

    @Override
    public void executeTradeWithBank(LobbyName lobbyName, ResourceType gainedResource, ResourceType lostResource) {
        ThreadManager.runNow(() -> syncTradeService.executeTradeWithBank(lobbyName, gainedResource, lostResource));
    }

    @Override
    public void offerTrade(LobbyName lobbyName, Actor respondingUser, ResourceList offeredResources,
                           ResourceList demandedResources, boolean counterOffer) {
        ThreadManager.runNow(() -> syncTradeService
                .offerTrade(lobbyName, respondingUser, offeredResources, demandedResources, counterOffer));
    }

    @Override
    public void resetOfferTradeButton(LobbyName lobbyName, Actor offeringUser) {
        ThreadManager.runNow(() -> syncTradeService.resetOfferTradeButton(lobbyName, offeringUser));
    }

    @Override
    public void tradeWithBank(LobbyName lobbyName) {
        ThreadManager.runNow(() -> syncTradeService.tradeWithBank(lobbyName));
    }

    @Override
    public void tradeWithUser(LobbyName lobbyName, Actor respondingUser, boolean counterOffer) {
        ThreadManager.runNow(() -> syncTradeService.tradeWithUser(lobbyName, respondingUser, counterOffer));
    }
}

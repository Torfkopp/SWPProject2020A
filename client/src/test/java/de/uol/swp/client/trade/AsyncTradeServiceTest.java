package de.uol.swp.client.trade;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AsyncTradeServiceTest {

    private static final long DURATION = 500L;
    private final LobbyName defaultLobby = mock(LobbyName.class);
    private final Actor otherUser = mock(Actor.class);
    private final TradeService syncTradeService = mock(TradeService.class);
    private AsyncTradeService tradeService;

    @BeforeEach
    protected void setUp() {
        assertNotNull(syncTradeService);
        tradeService = new AsyncTradeService(syncTradeService);
    }

    @AfterEach
    protected void tearDown() {
        tradeService = null;
    }

    @Test
    void acceptUserTrade() {
        ResourceList offered = mock(ResourceList.class);
        ResourceList demanded = mock(ResourceList.class);
        doNothing().when(syncTradeService)
                   .acceptUserTrade(isA(LobbyName.class), isA(Actor.class), isA(ResourceList.class),
                                    isA(ResourceList.class));

        tradeService.acceptUserTrade(defaultLobby, otherUser, offered, demanded);

        verify(syncTradeService, timeout(DURATION)).acceptUserTrade(defaultLobby, otherUser, offered, demanded);
    }

    @Test
    void buyDevelopmentCard() {
        doNothing().when(syncTradeService).buyDevelopmentCard(isA(LobbyName.class));

        tradeService.buyDevelopmentCard(defaultLobby);

        verify(syncTradeService, timeout(DURATION)).buyDevelopmentCard(defaultLobby);
    }

    @Test
    void cancelTrade() {
        doNothing().when(syncTradeService).cancelTrade(isA(LobbyName.class), isA(Actor.class));

        tradeService.cancelTrade(defaultLobby, otherUser);

        verify(syncTradeService, timeout(DURATION)).cancelTrade(defaultLobby, otherUser);
    }

    @Test
    void executeTradeWithBank() {
        ResourceType giveResource = ResourceType.ORE;
        ResourceType loseResource = ResourceType.WOOL;
        doNothing().when(syncTradeService)
                   .executeTradeWithBank(isA(LobbyName.class), isA(ResourceType.class), isA(ResourceType.class));

        tradeService.executeTradeWithBank(defaultLobby, giveResource, loseResource);

        verify(syncTradeService, timeout(DURATION)).executeTradeWithBank(defaultLobby, giveResource, loseResource);
    }

    @Test
    void offerTrade() {
        ResourceList offered = mock(ResourceList.class);
        ResourceList demanded = mock(ResourceList.class);
        doNothing().when(syncTradeService)
                   .offerTrade(isA(LobbyName.class), isA(Actor.class), isA(ResourceList.class), isA(ResourceList.class),
                               isA(Boolean.class));

        tradeService.offerTrade(defaultLobby, otherUser, offered, demanded, false);

        verify(syncTradeService, timeout(DURATION)).offerTrade(defaultLobby, otherUser, offered, demanded, false);
    }

    @Test
    void resetOfferTradeButton() {
        doNothing().when(syncTradeService).resetOfferTradeButton(isA(LobbyName.class), isA(Actor.class));

        tradeService.resetOfferTradeButton(defaultLobby, otherUser);

        verify(syncTradeService, timeout(DURATION)).resetOfferTradeButton(defaultLobby, otherUser);
    }

    @Test
    void tradeWithBank() {
        doNothing().when(syncTradeService).tradeWithBank(isA(LobbyName.class));

        tradeService.tradeWithBank(defaultLobby);

        verify(syncTradeService, timeout(DURATION)).tradeWithBank(defaultLobby);
    }

    @Test
    void tradeWithUser() {
        doNothing().when(syncTradeService).tradeWithUser(isA(LobbyName.class), isA(Actor.class), isA(Boolean.class));

        tradeService.tradeWithUser(defaultLobby, otherUser, false);

        verify(syncTradeService, timeout(DURATION)).tradeWithUser(defaultLobby, otherUser, false);
    }
}
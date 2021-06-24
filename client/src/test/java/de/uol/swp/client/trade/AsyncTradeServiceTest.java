package de.uol.swp.client.trade;

import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.game.response.TradeWithUserOfferResponse;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class AsyncTradeServiceTest {

    private static final long DURATION = 200L;
    private final LobbyName defaultLobby = mock(LobbyName.class);
    private final Actor otherUser = mock(Actor.class);
    private final TradeService syncTradeService = mock(TradeService.class);
    AsyncTradeService tradeService;

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

        verify(syncTradeService, after(DURATION)).acceptUserTrade(defaultLobby, otherUser, offered, demanded);
    }

    @Test
    void buyDevelopmentCard() {
        doNothing().when(syncTradeService).buyDevelopmentCard(isA(LobbyName.class));

        tradeService.buyDevelopmentCard(defaultLobby);

        verify(syncTradeService, after(DURATION)).buyDevelopmentCard(defaultLobby);
    }

    @Test
    void cancelTrade() {
        doNothing().when(syncTradeService).cancelTrade(isA(LobbyName.class), isA(Actor.class));

        tradeService.cancelTrade(defaultLobby, otherUser);

        verify(syncTradeService, after(DURATION)).cancelTrade(defaultLobby, otherUser);
    }

    @Test
    void closeBankTradeWindow() {
        doNothing().when(syncTradeService).closeBankTradeWindow(isA(LobbyName.class));

        tradeService.closeBankTradeWindow(defaultLobby);

        verify(syncTradeService, after(DURATION)).closeBankTradeWindow(defaultLobby);
    }

    @Test
    void closeTradeResponseWindow() {
        doNothing().when(syncTradeService).closeTradeResponseWindow(isA(LobbyName.class));

        tradeService.closeTradeResponseWindow(defaultLobby);

        verify(syncTradeService, after(DURATION)).closeTradeResponseWindow(defaultLobby);
    }

    @Test
    void closeUserTradeWindow() {
        doNothing().when(syncTradeService).closeUserTradeWindow(isA(LobbyName.class));

        tradeService.closeUserTradeWindow(defaultLobby);

        verify(syncTradeService, after(DURATION)).closeUserTradeWindow(defaultLobby);
    }

    @Test
    void executeTradeWithBank() {
        ResourceType giveResource = ResourceType.ORE;
        ResourceType loseResource = ResourceType.WOOL;
        doNothing().when(syncTradeService)
                   .executeTradeWithBank(isA(LobbyName.class), isA(ResourceType.class), isA(ResourceType.class));

        tradeService.executeTradeWithBank(defaultLobby, giveResource, loseResource);

        verify(syncTradeService, after(DURATION)).executeTradeWithBank(defaultLobby, giveResource, loseResource);
    }

    @Test
    void offerTrade() {
        ResourceList offered = mock(ResourceList.class);
        ResourceList demanded = mock(ResourceList.class);
        doNothing().when(syncTradeService)
                   .offerTrade(isA(LobbyName.class), isA(Actor.class), isA(ResourceList.class), isA(ResourceList.class),
                               isA(Boolean.class));

        tradeService.offerTrade(defaultLobby, otherUser, offered, demanded, false);

        verify(syncTradeService, after(DURATION)).offerTrade(defaultLobby, otherUser, offered, demanded, false);
    }

    @Test
    void resetOfferTradeButton() {
        doNothing().when(syncTradeService).resetOfferTradeButton(isA(LobbyName.class), isA(Actor.class));

        tradeService.resetOfferTradeButton(defaultLobby, otherUser);

        verify(syncTradeService, after(DURATION)).resetOfferTradeButton(defaultLobby, otherUser);
    }

    @Test
    void showBankTradeWindow() {
        doNothing().when(syncTradeService).showBankTradeWindow(isA(LobbyName.class));

        tradeService.showBankTradeWindow(defaultLobby);

        verify(syncTradeService, after(DURATION)).showBankTradeWindow(defaultLobby);
    }

    @Test
    void showOfferWindow() {
        TradeWithUserOfferResponse response = mock(TradeWithUserOfferResponse.class);
        doNothing().when(syncTradeService)
                   .showOfferWindow(isA(LobbyName.class), isA(Actor.class), isA(TradeWithUserOfferResponse.class));

        tradeService.showOfferWindow(defaultLobby, otherUser, response);

        verify(syncTradeService, after(DURATION)).showOfferWindow(defaultLobby, otherUser, response);
    }

    @Test
    void showTradeError() {
        String message = "some trade error";
        doNothing().when(syncTradeService).showTradeError(isA(String.class));

        tradeService.showTradeError(message);

        verify(syncTradeService, after(DURATION)).showTradeError(message);
    }

    @Test
    void showUserTradeWindow() {
        doNothing().when(syncTradeService)
                   .showUserTradeWindow(isA(LobbyName.class), isA(Actor.class), isA(Boolean.class));

        tradeService.showUserTradeWindow(defaultLobby, otherUser, false);

        verify(syncTradeService, after(DURATION)).showUserTradeWindow(defaultLobby, otherUser, false);
    }

    @Test
    void tradeWithBank() {
        doNothing().when(syncTradeService).tradeWithBank(isA(LobbyName.class));

        tradeService.tradeWithBank(defaultLobby);

        verify(syncTradeService, after(DURATION)).tradeWithBank(defaultLobby);
    }

    @Test
    void tradeWithUser() {
        doNothing().when(syncTradeService).tradeWithUser(isA(LobbyName.class), isA(Actor.class), isA(Boolean.class));

        tradeService.tradeWithUser(defaultLobby, otherUser, false);

        verify(syncTradeService, after(DURATION)).tradeWithUser(defaultLobby, otherUser, false);
    }
}
package de.uol.swp.client.game;

import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class AsyncGameServiceTest {

    private static final long DURATION = 200L;
    private final LobbyName defaultLobby = mock(LobbyName.class);
    private final GameService syncGameService = mock(GameService.class);
    private AsyncGameService gameService;

    @BeforeEach
    protected void setUp() {
        assertNotNull(syncGameService);
        gameService = new AsyncGameService(syncGameService);
    }

    @AfterEach
    protected void tearDown() {
        gameService = null;
    }

    @Test
    void buildRequest() {
        MapPoint edgeMapPoint = MapPoint.EdgeMapPoint(MapPoint.HexMapPoint(1, 1), MapPoint.HexMapPoint(1, 2));
        doNothing().when(syncGameService).buildRequest(isA(LobbyName.class), isA(MapPoint.class));

        gameService.buildRequest(defaultLobby, edgeMapPoint);

        verify(syncGameService, after(DURATION)).buildRequest(defaultLobby, edgeMapPoint);
    }

    @Test
    void changeAutoRollState() {
        doNothing().when(syncGameService).changeAutoRollState(isA(LobbyName.class), isA(Boolean.class));

        gameService.changeAutoRollState(defaultLobby, true);

        verify(syncGameService, after(DURATION)).changeAutoRollState(defaultLobby, true);
    }

    @Test
    void endTurn() {
        doNothing().when(syncGameService).endTurn(isA(LobbyName.class));

        gameService.endTurn(defaultLobby);

        verify(syncGameService, after(DURATION)).endTurn(defaultLobby);
    }

    @Test
    void pauseGame() {
        doNothing().when(syncGameService).pauseGame(isA(LobbyName.class));

        gameService.pauseGame(defaultLobby);

        verify(syncGameService, after(DURATION)).pauseGame(defaultLobby);
    }

    @Test
    void playKnightCard() {
        doNothing().when(syncGameService).playKnightCard(isA(LobbyName.class));

        gameService.playKnightCard(defaultLobby);

        verify(syncGameService, after(DURATION)).playKnightCard(defaultLobby);
    }

    @Test
    void playMonopolyCard() {
        ResourceType resource = ResourceType.ORE;
        doNothing().when(syncGameService).playMonopolyCard(isA(LobbyName.class), isA(ResourceType.class));

        gameService.playMonopolyCard(defaultLobby, resource);

        verify(syncGameService, after(DURATION)).playMonopolyCard(defaultLobby, resource);
    }

    @Test
    void playRoadBuildingCard() {
        doNothing().when(syncGameService).playRoadBuildingCard(isA(LobbyName.class));

        gameService.playRoadBuildingCard(defaultLobby);

        verify(syncGameService, after(DURATION)).playRoadBuildingCard(defaultLobby);
    }

    @Test
    void playYearOfPlentyCard() {
        ResourceType resource1 = ResourceType.ORE;
        ResourceType resource2 = ResourceType.GRAIN;
        doNothing().when(syncGameService)
                   .playYearOfPlentyCard(isA(LobbyName.class), isA(ResourceType.class), isA(ResourceType.class));

        gameService.playYearOfPlentyCard(defaultLobby, resource1, resource2);

        verify(syncGameService, after(DURATION)).playYearOfPlentyCard(defaultLobby, resource1, resource2);
    }

    @Test
    void robberChooseVictim() {
        UserOrDummy userOrDummy = mock(UserOrDummy.class);
        doNothing().when(syncGameService).robberChooseVictim(isA(LobbyName.class), isA(UserOrDummy.class));

        gameService.robberChooseVictim(defaultLobby, userOrDummy);

        verify(syncGameService, after(DURATION)).robberChooseVictim(defaultLobby, userOrDummy);
    }

    @Test
    void robberNewPosition() {
        MapPoint newRobberPosition = mock(MapPoint.class);
        doNothing().when(syncGameService).robberNewPosition(isA(LobbyName.class), isA(MapPoint.class));

        gameService.robberNewPosition(defaultLobby, newRobberPosition);

        verify(syncGameService, after(DURATION)).robberNewPosition(defaultLobby, newRobberPosition);
    }

    @Test
    void rollDice() {
        doNothing().when(syncGameService).rollDice(isA(LobbyName.class));

        gameService.rollDice(defaultLobby);

        verify(syncGameService, after(DURATION)).rollDice(defaultLobby);
    }

    @Test
    void startSession() {
        doNothing().when(syncGameService).startSession(isA(LobbyName.class), isA(Integer.class));

        gameService.startSession(defaultLobby, 69);

        verify(syncGameService, after(DURATION)).startSession(defaultLobby, 69);
    }

    @Test
    void taxPayed() {
        ResourceList resources = mock(ResourceList.class);
        doNothing().when(syncGameService).taxPayed(defaultLobby, resources);

        gameService.taxPayed(defaultLobby, resources);

        verify(syncGameService, after(DURATION)).taxPayed(defaultLobby, resources);
    }

    @Test
    void updateGameMap() {
        doNothing().when(syncGameService).updateGameMap(isA(LobbyName.class));

        gameService.updateGameMap(defaultLobby);

        verify(syncGameService, after(DURATION)).updateGameMap(defaultLobby);
    }

    @Test
    void updateInventory() {
        doNothing().when(syncGameService).updateInventory(isA(LobbyName.class));

        gameService.updateInventory(defaultLobby);

        verify(syncGameService, after(DURATION)).updateInventory(defaultLobby);
    }
}
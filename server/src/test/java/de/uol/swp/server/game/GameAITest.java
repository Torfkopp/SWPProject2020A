package de.uol.swp.server.game;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.map.management.IIntersection;
import de.uol.swp.common.game.map.management.MapPoint;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.Inventory;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceList;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.*;
import de.uol.swp.server.game.map.GameMapManagement;
import de.uol.swp.server.game.map.IGameMapManagement;
import de.uol.swp.server.lobby.*;
import de.uol.swp.server.sessionmanagement.SessionManagement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the GameAI
 *
 * @author Mario Fokken
 * @since 2021-05-28
 */
class GameAITest {

    private final EventBus bus = new EventBus();
    private final ILobbyManagement lobbyManagement = new LobbyManagement();
    private final SessionManagement sessionManagement = new SessionManagement();
    private final LobbyService lobbyService = new LobbyService(lobbyManagement, sessionManagement, bus);

    private final User user = new UserDTO(0, "Rohan", "Kishibe", "Rohan@kishibe.jp");
    private final AI ai1 = new AIDTO("Rüdiger");
    private final AI ai2 = new AIDTO(AI.Difficulty.HARD);

    private final LobbyName lobbyName = new LobbyName("Lobby");
    private final ILobby lobby = new LobbyDTO(lobbyName, user, null);

    private IGameManagement gameManagement;
    private GameService gameService;
    private GameAI gameAI;
    private IGameMapManagement map;

    /**
     * Method to run before each test case
     * <p>
     * It instantiates new things, so that tests don't interfere with each other
     */
    @BeforeEach
    protected void setUp() {
        gameManagement = new GameManagement(lobbyManagement);
        gameService = new GameService(bus, gameManagement, lobbyManagement, lobbyService);
        gameAI = new GameAI(gameService, gameManagement, lobbyService);

        map = new GameMapManagement();
        map = map.createMapFromConfiguration(map.getBeginnerConfiguration());
        map.makeBeginnerSettlementsAndRoads(3);

        lobby.joinUser(ai1);
        lobby.joinUser(ai2);

        gameManagement.createGame(lobby, user, map, 0);
    }

    /**
     * Method to run after each test case
     * <p>
     * Sets the things to null
     */
    @AfterEach
    protected void tearDown() {
        gameService = null;
        gameManagement = null;
        gameAI = null;
        map = null;
    }

    /**
     * Tests the AI objects
     */
    @Test
    void aiTest() {
        assertNotEquals(ai1, ai2);

        assertFalse(ai1.getAiNames().contains(ai1.getUsername()));
        assertEquals("Man X", ai1.getUsername());
        assertTrue(ai2.getAiNames().contains(ai2.getUsername()));

        assertEquals(AI.Difficulty.EASY, ai1.getDifficulty());
        assertEquals(AI.Difficulty.HARD, ai2.getDifficulty());

        assertEquals("", ai1.writeMessage(AI.WriteType.START));
        assertNotEquals("", ai2.writeMessage(AI.WriteType.START));
    }

    /**
     * Tests the robberMovementAI method
     */
    @Test
    void robberMovementAITest() {
        MapPoint robber = MapPoint.HexMapPoint(3, 3);
        assertEquals(robber.getY(), map.getRobberPosition().getY());
        assertEquals(robber.getX(), map.getRobberPosition().getX());
        boolean isDifferent = false;
        //------- EASY
        // 1/19 change of getting the desert field randomly
        // (1/19)^5 = 4*10^-7, which should be improbable enough
        for (int i = 0; i <= 5; i++) {
            gameAI.robberMovementAI(ai1, lobbyName);
            if (robber.getY() != map.getRobberPosition().getY() || robber.getX() != map.getRobberPosition().getX()) {
                isDifferent = true;
                break;
            }
        }
        assertTrue(isDifferent);
        //-------
        map.moveRobber(robber);
        //------- HARD
        gameAI.robberMovementAI(ai2, lobbyName);
        MapPoint mp;
        MapPoint newPos = map.getRobberPosition();
        switch (gameManagement.getGame(lobbyName).getPlayer(ai2)) {
            case PLAYER_1:
                isDifferent = true;
                mp = MapPoint.HexMapPoint(5, 2);
                if (mp.getY() == newPos.getY() && mp.getX() == newPos.getX()) isDifferent = false;
                if (isDifferent) {
                    mp = MapPoint.HexMapPoint(3, 5);
                    if (mp.getY() == newPos.getY() && mp.getX() == newPos.getX()) isDifferent = false;
                }
                assertFalse(isDifferent);
                break;
            case PLAYER_2:
                mp = MapPoint.HexMapPoint(2, 2);
                assertEquals(mp.getY(), newPos.getY());
                assertEquals(mp.getX(), newPos.getX());
                break;
            case PLAYER_3:
                isDifferent = true;
                mp = MapPoint.HexMapPoint(5, 2);
                if (mp.getY() == newPos.getY() && mp.getX() == newPos.getX()) isDifferent = false;
                if (isDifferent) {
                    mp = MapPoint.HexMapPoint(4, 1);
                    if (mp.getY() == newPos.getY() && mp.getX() == newPos.getX()) isDifferent = false;
                }
                System.err.println(mp.getY() + " " + mp.getX());
                assertFalse(isDifferent);
                break;
            case PLAYER_4:
                assertEquals("How did this happen?", "We're smarter than this");
                break;
        }
    }

    /**
     * Tests the taxPayAI method
     */
    @Test
    void taxPayAITest() {
        Game game = gameManagement.getGame(lobbyName);
        //------ EASY
        Inventory inv = game.getInventory(ai1);
        for (ResourceType res : ResourceType.values()) inv.increase(res, 2);
        gameAI.taxPayAI(game, ai1);
        assertEquals(1, inv.get(ResourceType.BRICK));
        assertEquals(1, inv.get(ResourceType.GRAIN));
        assertEquals(1, inv.get(ResourceType.LUMBER));
        assertEquals(1, inv.get(ResourceType.ORE));
        assertEquals(1, inv.get(ResourceType.WOOL));
        //------ HARD
        inv = game.getInventory(ai2);
        for (ResourceType res : ResourceType.values()) inv.increase(res, 1);
        inv.increase(ResourceType.BRICK, 5);
        gameAI.taxPayAI(game, ai2);
        assertEquals(1, inv.get(ResourceType.BRICK));
        assertEquals(1, inv.get(ResourceType.GRAIN));
        assertEquals(1, inv.get(ResourceType.LUMBER));
        assertEquals(1, inv.get(ResourceType.ORE));
        assertEquals(1, inv.get(ResourceType.WOOL));

        inv.increase(ResourceType.BRICK, 4);
        inv.increase(ResourceType.GRAIN, 3);
        gameAI.taxPayAI(game, ai2);
        assertEquals(1, inv.get(ResourceType.BRICK));
        assertEquals(2, inv.get(ResourceType.GRAIN));
        assertEquals(1, inv.get(ResourceType.LUMBER));
        assertEquals(1, inv.get(ResourceType.ORE));
        assertEquals(1, inv.get(ResourceType.WOOL));
    }

    /**
     * Tests the tradeAcceptationAI method
     */
    @Test
    void tradeAcceptationAI() {
        ResourceList res1 = new ResourceList();
        res1.increase(ResourceType.BRICK, 4);
        ResourceList res2 = new ResourceList();
        //----- EASY
        assertTrue(gameAI.tradeAcceptationAI(ai1, lobbyName, res1, res2));
        assertFalse(gameAI.tradeAcceptationAI(ai1, lobbyName, res2, res1));
        //----- HARD
        assertTrue(gameAI.tradeAcceptationAI(ai2, lobbyName, res1, res2));
        assertFalse(gameAI.tradeAcceptationAI(ai2, lobbyName, res2, res1));
        res1.decrease(ResourceType.BRICK);
        assertTrue(gameAI.tradeAcceptationAI(ai2, lobbyName, res1, res2));
        assertFalse(gameAI.tradeAcceptationAI(ai2, lobbyName, res2, res1));
    }

    /**
     * Tests the turnAI method with an easy AI
     */
    @Test
    void turnAIEasyTest() {
        Game game = gameManagement.getGame(lobbyName);
        IGameMapManagement map = game.getMap();
        Player player = game.getPlayer(ai1);
        Inventory inv = game.getInventory(ai1);

        inv.increase(ResourceType.GRAIN, 2);
        inv.increase(ResourceType.ORE, 3);
        inv.increase(ResourceType.BRICK, 3);
        inv.increase(ResourceType.LUMBER, 3);
        inv.increase(ResourceType.WOOL, 0);

        gameAI.turnAI(game, ai1);

        boolean existsCity = false;
        for (MapPoint mp : map.getPlayerSettlementsAndCities().get(player)) {
            if (map.getIntersection(mp).getState() == IIntersection.IntersectionState.CITY) {
                existsCity = true;
                break;
            }
        }
        assertTrue(existsCity);
        assertEquals(0, inv.get(ResourceType.BRICK));
        assertEquals(0, inv.get(ResourceType.LUMBER));
        assertEquals(0, inv.get(ResourceType.GRAIN));
        assertEquals(0, inv.get(ResourceType.ORE));
        assertEquals(0, inv.get(ResourceType.WOOL));
        assertEquals(0, inv.getAmountOfDevelopmentCards());
    }

    /**
     * Tests the turnAI method with a hard AI
     */
    @Test
    void turnAIHardTest() {
        Game game = gameManagement.getGame(lobbyName);
        IGameMapManagement map = game.getMap();
        Player player = game.getPlayer(ai2);
        List<MapPoint> settlements = map.getPlayerSettlementsAndCities().get(player);
        Inventory inv = game.getInventory(ai2);
        inv.increase(DevelopmentCardType.KNIGHT_CARD);
        inv.increase(ResourceType.BRICK, 4);
        inv.increase(ResourceType.LUMBER, 4);
        gameAI.turnAI(game, ai2);
        assertEquals(1, inv.get(DevelopmentCardType.KNIGHT_CARD));
        assertTrue(inv.get(ResourceType.BRICK) < 4);
        assertTrue(inv.get(ResourceType.LUMBER) < 4);
    }
}

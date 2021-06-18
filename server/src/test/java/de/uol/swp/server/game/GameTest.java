package de.uol.swp.server.game;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.BankInventory;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.specialisedUtil.UserOrDummyColourMap;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.server.game.map.IGameMapManagement;
import de.uol.swp.server.lobby.ILobby;
import de.uol.swp.server.lobby.LobbyDTO;
import de.uol.swp.server.lobby.LobbyManagement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static de.uol.swp.common.game.map.management.MapPoint.*;
import static de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.developmentCard.DevelopmentCardType.*;
import static de.uol.swp.common.game.resourcesAndDevelopmentCardAndUniqueCards.resource.ResourceType.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the Game object
 *
 * @author Mario Fokken
 * @since 2021-02-04
 */
public class GameTest {

    static final User user = new UserDTO(42, "Jolyne", "IloveDaddyJoJo", "JolyneCujoh@jojo.us");
    static final User user2 = new UserDTO(69, "Johnny", "NailsGoSpin", "JohnnyJoestar@jojo.us");
    static final User user3 = new UserDTO(99, "Josuke", "4BallsBetterThan2", "HigashikataJosuke@jojo.jp");
    static final User user4 = new UserDTO(179, "Joseph", "SunOfABitch", "JosephJoestar@jojo.uk");
    static final ILobby lobby = new LobbyDTO(new LobbyName("Read the Manga"), user, "");
    static final LobbyName defaultLobbyName = new LobbyName("Read the Manga");
    static IGameMapManagement gameMap;
    static Game game;

    @BeforeEach
    protected void setUp() {
        gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gameMap.makeBeginnerSettlementsAndRoads(4);
        lobby.joinUser(user2);
        lobby.joinUser(user3);
        lobby.joinUser(user4);
        game = new Game(lobby, user, gameMap);
    }

    @AfterEach
    protected void tearDown() {
        gameMap = null;
        game = null;
        lobby.leaveUser(user2);
        lobby.leaveUser(user3);
        lobby.leaveUser(user4);
    }

    /**
     * Tests if the bankInventory gets created properly when a game is created
     * <p>
     * This test checks the content of the bank inventory.
     * <p>
     * This test fails if there are more or less Cards of a specific type than expected
     *
     * @author Maximilian Lindner
     * @author Alwin Bossert
     * @since 2021-02-23
     */
    @Test
    void bankInventoryCheck() {
        BankInventory bankInventory = new BankInventory();
        assertEquals(5, bankInventory.get(VICTORY_POINT_CARD));
        assertEquals(2, bankInventory.get(MONOPOLY_CARD));
        assertEquals(2, bankInventory.get(YEAR_OF_PLENTY_CARD));
        assertEquals(2, bankInventory.get(ROAD_BUILDING_CARD));
        assertEquals(14, bankInventory.get(KNIGHT_CARD));
    }

    @Test
    void calculateVictoryPointsTest() {
        Player player = Player.PLAYER_1;
        IGameMapManagement map = game.getMap();
        assertEquals(Player.PLAYER_1, player);
        // Player has 2 starting settlements
        assertEquals(2, game.calculateVictoryPoints(player));
        // build road to next intersection to be able to place a settlement
        map.placeRoad(player, EdgeMapPoint(HexMapPoint(1, 1), HexMapPoint(1, 2)));
        map.placeRoad(player, EdgeMapPoint(HexMapPoint(1, 1), HexMapPoint(0, 1)));
        try {
            map.placeSettlement(player, IntersectionMapPoint(0, 1));
        } catch (GameMapManagement.SettlementMightInterfereWithLongestRoadException ignored) {
        }
        // Player now has 3 settlements
        assertEquals(3, game.calculateVictoryPoints(player));
        map.upgradeSettlement(player, IntersectionMapPoint(0, 1));
        // Player has 2 settlements (1 VP), 1 city (2 VP) for 4 VP total
        assertEquals(4, game.calculateVictoryPoints(player));
        game.getInventory(player).set(VICTORY_POINT_CARD, 2);
        // Player has 2 settlements (1 VP), 1 city (2 VP), 2 victory point cards for 6 VP total
        assertEquals(6, game.calculateVictoryPoints(player));
        game.setPlayerWithLongestRoad(player);
        // Player has 2 settlements (1 VP), 1 city (2 VP), 2 victory point cards, Longest Road for 8 VP total
        assertEquals(8, game.calculateVictoryPoints(player));
        game.setPlayerWithLargestArmy(player);
        // Player has 2 settlements (1 VP), 1 city (2 VP), 2 victory point cards, Longest Road, Largest Army
        // for 10 VP total
        assertEquals(10, game.calculateVictoryPoints(player));
    }

    @Test
    void changePauseStatusTest() {
        game.changePauseStatus(user);

        assertEquals(1, game.getPausedMembers());

        game.changePauseStatus(user);

        assertEquals(0, game.getPausedMembers());
    }

    @Test
    void distributesResourceTest() {
        //Testing a hex
        game.distributeResources(6);
        assertEquals(1, game.getInventory(Player.PLAYER_1).get(BRICK));
        assertEquals(1, game.getInventory(Player.PLAYER_3).get(BRICK));
        assertEquals(1, game.getInventory(Player.PLAYER_2).get(GRAIN));
        //Testing another hex
        game.distributeResources(4);
        assertEquals(1, game.getInventory(Player.PLAYER_2).get(WOOL));
        assertEquals(1, game.getInventory(Player.PLAYER_4).get(GRAIN));
        assertEquals(2, game.getInventory(Player.PLAYER_2).get(GRAIN));
    }

    @Test
    void gameManagementTest() {
        IGameManagement gm = new GameManagement(new LobbyManagement());
        User user = new UserDTO(99, "", "", "");
        ILobby lobby = new LobbyDTO(defaultLobbyName, user);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gm.createGame(lobby, user, gameMap, 120);
        assertNotNull(gm.getGame(defaultLobbyName));
        Map<LobbyName, Game> map = gm.getGames();
        assertEquals(1, map.size());
        gm.dropGame(defaultLobbyName);
        map = gm.getGames();
        assertTrue(map.isEmpty());
    }

    @Test
    void gameTest() {
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        List<UserOrDummy> list = Arrays.asList(user, user2, user3, user4);
        UserOrDummy[] u = game.getPlayers();
        //order is random, so just check that everyone is somewhere in the list of users in the Game
        assertTrue(list.contains(u[0]));
        assertTrue(list.contains(u[1]));
        assertTrue(list.contains(u[2]));
        assertTrue(list.contains(u[3]));
        assertEquals(lobby, game.getLobby());
    }

    @Test
    void getActivePlayerTest() {
        assertEquals(user, game.getActivePlayer());
    }

    @Test
    void getBankInventoryTest() {
        BankInventory bankInventory = game.getBankInventory();
        assertEquals(14, bankInventory.get(KNIGHT_CARD));
        assertEquals(2, bankInventory.get(ROAD_BUILDING_CARD));
        assertEquals(2, bankInventory.get(YEAR_OF_PLENTY_CARD));
        assertEquals(2, bankInventory.get(MONOPOLY_CARD));
        assertEquals(5, bankInventory.get(VICTORY_POINT_CARD));
    }

    @Test
    void getUserColoursMapTest() {
        UserOrDummyColourMap userColoursMap = game.getUserColoursMap();
        // values are random, so just check all users have an associated Colour
        assertTrue(userColoursMap.containsKey(user));
        assertTrue(userColoursMap.containsKey(user2));
        assertTrue(userColoursMap.containsKey(user3));
        assertTrue(userColoursMap.containsKey(user4));
    }

    @Test
    void nextPlayerTest() {
        UserOrDummy[] players = game.getPlayers();
        assertEquals(players[1], game.nextPlayer());
        assertEquals(players[2], game.nextPlayer());
        assertEquals(players[3], game.nextPlayer());
        assertEquals(players[0], game.nextPlayer());
    }

    @Test
    void pauseGameTest() {
        assertFalse(game.isPausedByVoting());
        game.changePauseStatus(user);
        game.changePauseStatus(user2);
        game.changePauseStatus(user3);
        game.changePauseStatus(user4);

        game.updatePauseByVotingStatus();

        assertTrue(game.isPausedByVoting());

        game.changePauseStatus(user);
        game.changePauseStatus(user2);
        game.changePauseStatus(user3);
        game.changePauseStatus(user4);
        game.updatePauseByVotingStatus();

        assertFalse(game.isPausedByVoting());
    }

    @Test
    void rollDiceTest() {
        int[] dices;
        for (int i = 0; i < 69; i++) {
            dices = Game.rollDice();
            assertTrue(1 <= dices[0] && dices[0] <= 6);
            assertTrue(1 <= dices[1] && dices[1] <= 6);
        }
    }

    @Test
    void setBuildingAllowedTest() {
        assertFalse(game.isBuildingAllowed());

        game.setBuildingAllowed(true);

        assertTrue(game.isBuildingAllowed());
    }

    @Test
    void setRiceRolledAlreadyTest() {
        assertFalse(game.isDiceRolledAlready());

        game.setDiceRolledAlready(true);

        assertTrue(game.isDiceRolledAlready());
    }
}

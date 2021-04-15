package de.uol.swp.common.game;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.game.map.GameMapManagement;
import de.uol.swp.common.game.map.IGameMapManagement;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.uol.swp.common.game.map.MapPoint.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    static final Lobby lobby = new LobbyDTO(new LobbyName("Read the Manga"), user, true, 4, false, 60, false, false);
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
        BankInventory bankInventory = game.getBankInventory();

        assertEquals(5, bankInventory.get(DevelopmentCard.VICTORY_POINT_CARD));
        assertEquals(2, bankInventory.get(DevelopmentCard.MONOPOLY_CARD));
        assertEquals(2, bankInventory.get(DevelopmentCard.YEAR_OF_PLENTY_CARD));
        assertEquals(2, bankInventory.get(DevelopmentCard.ROAD_BUILDING_CARD));
        assertEquals(14, bankInventory.get(DevelopmentCard.KNIGHT_CARD));
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
        map.placeSettlement(player, IntersectionMapPoint(0, 1));
        // Player now has 3 settlements
        assertEquals(3, game.calculateVictoryPoints(player));
        map.upgradeSettlement(player, IntersectionMapPoint(0, 1));
        // Player has 2 settlements (1 VP), 1 city (2 VP) for 4 VP total
        assertEquals(4, game.calculateVictoryPoints(player));
        game.getInventory(player).increase(DevelopmentCard.VICTORY_POINT_CARD,2);
        // Player has 2 settlements (1 VP), 1 city (2 VP), 2 victory point cards for 6 VP total
        assertEquals(6, game.calculateVictoryPoints(player));
    }

    @Test
    void distributesResourceTest() {
        //Testing a hex
        game.distributeResources(6);
        assertEquals(1, game.getInventory(Player.PLAYER_1).get(Resource.BRICK));
        assertEquals(1, game.getInventory(Player.PLAYER_3).get(Resource.BRICK));
        assertEquals(1, game.getInventory(Player.PLAYER_2).get(Resource.GRAIN));
        //Testing another hex
        game.distributeResources(4);
        assertEquals(1, game.getInventory(Player.PLAYER_2).get(Resource.WOOL));
        assertEquals(1, game.getInventory(Player.PLAYER_4).get(Resource.GRAIN));
        assertEquals(2, game.getInventory(Player.PLAYER_2).get(Resource.GRAIN));
    }

    @Test
    void nextPlayerTest() {
        UserOrDummy[] players = game.getPlayers();
        //Tests if the players are in correct order
        //Ordered by ID
        assertEquals(user, players[0]);
        assertEquals(user2, players[1]);
        assertEquals(user3, players[2]);
        assertEquals(lobby, game.getLobby());
        //Since Jolyne made the lobby, she goes first
        assertEquals(user2, game.nextPlayer());
        assertEquals(user3, game.nextPlayer());
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
}

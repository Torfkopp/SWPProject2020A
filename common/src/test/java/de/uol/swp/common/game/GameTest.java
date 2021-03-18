package de.uol.swp.common.game;

import de.uol.swp.common.game.map.MapPoint;
import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    static final Lobby lobby = new LobbyDTO("Read the Manga", user, true, 4, false, 60, false, false);
    static Game game = new Game(lobby, user);

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
        String knightCard = "knightCard";
        String roadBuildingCard = "roadBuildingCard";
        String yearOfPlentyCard = "yearOfPlentyCard";
        String monopolyCard = "monopolyCard";
        String victoryPointCard = "victoryPointCard";
        int victoryPointCardAmount = 0;
        int monopolyCardAmount = 0;
        int yearOfPlentyCardAmount = 0;
        int roadBuildingCardAmount = 0;
        int knightCardAmount = 0;
        lobby.joinUser(user2);
        lobby.joinUser(user3);
        game = new Game(lobby, user);
        List<String> bankInventory = game.getBankInventory();
        for (String s : bankInventory) {
            if (s.equals(knightCard)) knightCardAmount++;
        }
        for (String s : bankInventory) {
            if (s.equals(yearOfPlentyCard)) yearOfPlentyCardAmount++;
        }
        for (String s : bankInventory) {
            if (s.equals(roadBuildingCard)) roadBuildingCardAmount++;
        }
        for (String s : bankInventory) {
            if (s.equals(monopolyCard)) monopolyCardAmount++;
        }
        for (String s : bankInventory) {
            if (s.equals(victoryPointCard)) victoryPointCardAmount++;
        }
        assertEquals(5, victoryPointCardAmount);
        assertEquals(2, monopolyCardAmount);
        assertEquals(2, yearOfPlentyCardAmount);
        assertEquals(2, roadBuildingCardAmount);
        assertEquals(14, knightCardAmount);
    }

    @Test
    @Disabled("This definitely works, trust me!")
    void calculateVictoryPointsTest() {
        game.getMap().createBeginnerMap();
        Player player = Player.PLAYER_1;
        assertEquals(1, game.getInventories().length);
        assertEquals(Player.PLAYER_1, player);
        //Player has nothing
        assertEquals(0, game.calculateVictoryPoints(player));
        game.getMap().placeSettlement(player, new MapPoint(0, 0));
        //Player has a settlement
        assertEquals(1, game.calculateVictoryPoints(player));
        game.getMap().upgradeSettlement(player, new MapPoint(0, 0));
        //Player has a city
        assertEquals(2, game.calculateVictoryPoints(player));
        game.getInventory(player).setVictoryPointCards(3);
        //Player has a city and 3 victory point cards
        assertEquals(5, game.calculateVictoryPoints(player));
        game.getInventory(player).setLongestRoad(true);
        //Player has a city, 3 victory point cards, and the longest road
        assertEquals(7, game.calculateVictoryPoints(player));
        game.getInventory(player).setLargestArmy(true);
        //Player has a city, 3 point cards, longest road, and the largest army
        assertEquals(9, game.calculateVictoryPoints(player));
        game.getMap().placeSettlement(player, new MapPoint(2, 5));
        //Player has a city, a settlement, 3 point cards, longest road, and the largest army
        assertEquals(10, game.calculateVictoryPoints(player));
    }

    @Test
    void nextPlayerTest() {
        lobby.joinUser(user2);
        lobby.joinUser(user3);
        game = new Game(lobby, user);
        User[] players = game.getPlayers();
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
            dices = game.rollDice();
            assertTrue(1 <= dices[0] && dices[0] <= 6);
            assertTrue(1 <= dices[1] && dices[1] <= 6);
        }
    }

    @Test
    void distributesResourceTest() {
        lobby.joinUser(user2);
        lobby.joinUser(user3);
        lobby.joinUser(user4);
        game = new Game(lobby, user);
        //Testing a hex
        game.distributeResources(6);
        assertEquals(1, game.getInventory(Player.PLAYER_1).getBrick());
        assertEquals(1, game.getInventory(Player.PLAYER_3).getBrick());
        assertEquals(1, game.getInventory(Player.PLAYER_2).getGrain());
        //Testing another hex
        game.distributeResources(4);
        assertEquals(1, game.getInventory(Player.PLAYER_2).getWool());
        assertEquals(1, game.getInventory(Player.PLAYER_4).getGrain());
        assertEquals(2, game.getInventory(Player.PLAYER_2).getGrain());
    }
}

package de.uol.swp.common.game;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the Game object
 *
 * @author Mario Fokken
 * @since 2021-02-04
 */
public class GameTest {

    static final User user = new UserDTO(42, "Jolyne", "IloveDaddyJoJo", "CujohJolyne@jojo.jp");
    static final User user2 = new UserDTO(69, "Johnny", "NailsGoSpin", "JoestarJohnny@jojo.jp");
    static final User user3 = new UserDTO(99, "Josuke", "4BallsBetterThan2", "HigashikataJosuke@jojo.jp");
    static final Lobby lobby = new LobbyDTO("Read the Manga", user);
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
        assertEquals(victoryPointCardAmount, 5);
        assertEquals(monopolyCardAmount, 2);
        assertEquals(yearOfPlentyCardAmount, 2);
        assertEquals(roadBuildingCardAmount, 2);
        assertEquals(knightCardAmount, 14);
    }

    @Test
    void calculateVictoryPointsTest() {
        Player player = Player.PLAYER_1;
        assertEquals(game.getInventories().length, 1);
        assertEquals(player, Player.PLAYER_1);
        //Player has nothing
        assertEquals(game.calculateVictoryPoints(player), 0);
        game.getMap().placeSettlement(player, 1);
        //Player has a settlement
        assertEquals(game.calculateVictoryPoints(player), 1);
        game.getMap().upgradeSettlement(player, 1);
        //Player has a city
        assertEquals(game.calculateVictoryPoints(player), 2);
        game.getInventory(player).setVictoryPointCards(3);
        //Player has a city and 3 victory point cards
        assertEquals(game.calculateVictoryPoints(player), 5);
        game.getInventory(player).setLongestRoad(true);
        //Player has a city, 3 victory point cards, and the longest road
        assertEquals(game.calculateVictoryPoints(player), 7);
        game.getInventory(player).setLargestArmy(true);
        //Player has a city, 3 point cards, longest road, and the largest army
        assertEquals(game.calculateVictoryPoints(player), 9);
        game.getMap().placeSettlement(player, 50);
        //Player has a city, a settlement, 3 point cards, longest road, and the largest army
        assertEquals(game.calculateVictoryPoints(player), 10);
    }

    @Test
    void nextPlayerTest() {
        lobby.joinUser(user2);
        lobby.joinUser(user3);
        game = new Game(lobby, user);
        User[] players = game.getPlayers();
        //Tests if the players are in correct order
        //Ordered by ID
        assertEquals(players[0], user);
        assertEquals(players[1], user2);
        assertEquals(players[2], user3);
        assertEquals(game.getLobby(), lobby);
        //Since Jolyne made the lobby, she goes first
        assertEquals(game.nextPlayer(), user2);
        assertEquals(game.nextPlayer(), user3);
        assertEquals(game.nextPlayer(), user);
    }
}

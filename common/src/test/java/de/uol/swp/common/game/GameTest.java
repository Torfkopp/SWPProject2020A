package de.uol.swp.common.game;

import de.uol.swp.common.game.map.Player;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the Game object
 *
 * @author Mario Fokken
 * @since 2021-02-04
 */
public class GameTest {

    static final User user = new UserDTO("Jolyne", "IloveDaddyJoJo", "CujohJolyne@jojo.jp");
    static final User user2 = new UserDTO("Johnny", "NailsGoSpin", "JoestarJohnny@jojo.jp");
    static final User user3 = new UserDTO("Josuke", "4BallsBetterThan2", "HigashikataJosuke@jojo.jp");
    static final Lobby lobby = new LobbyDTO("Read the Manga", user);
    static Game game = new Game(lobby, user);

    @Test
    void calcVicPointsTest() {
        Player player = Player.PLAYER_1;
        assertEquals(game.getInventories().length, 1);
        assertEquals(player, Player.PLAYER_1);
        //Player has nothing
        assertEquals(game.calcVicPoints(player), 0);
        game.getMap().placeSettlement(player, 1);
        //Player has a settlement
        assertEquals(game.calcVicPoints(player), 1);
        game.getMap().upgradeSettlement(player, 1);
        //Player has a city
        assertEquals(game.calcVicPoints(player), 2);
        game.getInventory(player).setVictoryPointCards(3);
        //Player has a city and 3 victory point cards
        assertEquals(game.calcVicPoints(player), 5);
        game.getInventory(player).setLongestRoad(true);
        //Player has a city, 3 victory point cards, and the longest road
        assertEquals(game.calcVicPoints(player), 7);
        game.getInventory(player).setLargestArmy(true);
        //Player has a city, 3 point cards, longest road, and the largest army
        assertEquals(game.calcVicPoints(player), 9);
        game.getMap().placeSettlement(player, 50);
        //Player has a city, a settlement, 3 point cards, longest road, and the largest army
        assertEquals(game.calcVicPoints(player), 10);
    }

    @Test
    void nextPlayerTest() {
        lobby.joinUser(user2);
        lobby.joinUser(user3);
        game = new Game(lobby, user);
        User[] players = game.getPlayers();
        //Tests if the players are in correct order
        //Alphabetical order (dunno why)
        assertEquals(players[0], user2);
        assertEquals(players[1], user);
        assertEquals(players[2], user3);
        assertEquals(game.getLobby(), lobby);
        //Since Jolyne made the lobby, she goes first
        assertEquals(game.nextPlayer(), user3);
        assertEquals(game.nextPlayer(), user2);
        assertEquals(game.nextPlayer(), user);
    }
}

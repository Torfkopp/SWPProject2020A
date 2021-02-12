package de.uol.swp.server.game;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests the Game object and GameManagement
 *
 * @author Alwin Bossert
 * @author Mario Fokken
 * @author Marvin Drees
 * @since 2021-01-24
 */
class GameTest {

    @Test
    void gameManagementTest() {
        IGameManagement gm = new GameManagement();
        User user = new UserDTO("", "", "");
        Lobby lobby = new LobbyDTO("testLobby", user);
        gm.createGame(lobby, user);
        assertNotNull(gm.getGame("testLobby"));
        Map<String, Game> map = gm.getGames();
        assertEquals(map.size(), 1);
        gm.dropGame("testLobby");
        map = gm.getGames();
        assertTrue(map.isEmpty());
    }

    @Test
    void gameTest() {
        User[] user = new User[3];
        user[0] = new UserDTO("Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO("Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO("Sylvester", "Stallone", "Sly@stall.com");
        Lobby lobby = new LobbyDTO("testlobby", user[0]);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        Game game = new Game(lobby, user[0]);
        //Lobby speichert Users alphabetisch. SMH mein Haupt
        User[] u = game.getPlayers();
        assertEquals(u[0], user[0]);
        assertEquals(u[1], user[1]);
        assertEquals(u[2], user[2]);
        assertEquals(game.getLobby(), lobby);
        assertEquals(game.nextPlayer(), user[1]);
        assertEquals(game.nextPlayer(), user[2]);
        assertEquals(game.nextPlayer(), user[0]);
    }
}
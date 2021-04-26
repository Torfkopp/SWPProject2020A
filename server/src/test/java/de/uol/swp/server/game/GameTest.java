package de.uol.swp.server.game;

import de.uol.swp.common.game.Game;
import de.uol.swp.common.game.map.management.GameMapManagement;
import de.uol.swp.common.game.map.management.IGameMapManagement;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.server.lobby.LobbyManagement;
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
        IGameManagement gm = new GameManagement(new LobbyManagement());
        User user = new UserDTO(99, "", "", "");
        Lobby lobby = new LobbyDTO("testLobby", user, "",false, false, 4, false, 60, true, true);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        gm.createGame(lobby, user, gameMap);
        assertNotNull(gm.getGame("testLobby"));
        Map<String, Game> map = gm.getGames();
        assertEquals(1, map.size());
        gm.dropGame("testLobby");
        map = gm.getGames();
        assertTrue(map.isEmpty());
    }

    @Test
    void gameTest() {
        User[] user = new User[3];
        user[0] = new UserDTO(0, "Chuck", "Norris", "chuck@norris.com");
        user[1] = new UserDTO(1, "Duck", "Morris", "duck@morris.com");
        user[2] = new UserDTO(2, "Sylvester", "Stallone", "Sly@stall.com");
        Lobby lobby = new LobbyDTO("testlobby", user[0], "",false, false, 4, false, 60, true, true);
        lobby.joinUser(user[1]);
        lobby.joinUser(user[2]);
        IGameMapManagement gameMap = new GameMapManagement();
        gameMap = gameMap.createMapFromConfiguration(gameMap.getBeginnerConfiguration());
        Game game = new Game(lobby, user[0], gameMap);
        //Lobby speichert Users alphabetisch. SMH mein Haupt
        UserOrDummy[] u = game.getPlayers();
        assertEquals(user[0], u[0]);
        assertEquals(user[1], u[1]);
        assertEquals(user[2], u[2]);
        assertEquals(lobby, game.getLobby());
        assertEquals(user[1], game.nextPlayer());
        assertEquals(user[2], game.nextPlayer());
        assertEquals(user[0], game.nextPlayer());
    }
}
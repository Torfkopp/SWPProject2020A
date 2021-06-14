package de.uol.swp.server.game;

import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.game.map.IGameMapManagement;
import de.uol.swp.server.lobby.ILobby;
import de.uol.swp.server.lobby.LobbyManagement;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GameManagementTest {

    private final LobbyName defaultLobbyName = new LobbyName("test");
    private final User defaultUser = new UserDTO(1, "tester", "testtestx3", "test@testing.uwu");
    private LobbyManagement lobbyManagement;
    private GameManagement gameManagement;

    @BeforeEach
    protected void setUp() {
        lobbyManagement = new LobbyManagement();
        lobbyManagement.createLobby(defaultLobbyName, defaultUser, "");
        gameManagement = new GameManagement(lobbyManagement);
    }

    @AfterEach
    protected void tearDown() {
        gameManagement = null;
        lobbyManagement = null;
    }

    @Test
    void createGame() {
        Optional<ILobby> lobbyOptional = lobbyManagement.getLobby(defaultLobbyName);
        assertTrue(lobbyOptional.isPresent());
        ILobby lobby = lobbyOptional.get();
        IGameMapManagement mapManagement = mock(IGameMapManagement.class);

        gameManagement.createGame(lobby, defaultUser, mapManagement, 120);

        assertTrue(lobbyManagement.getLobby(defaultLobbyName).isPresent());
        assertTrue(lobbyManagement.getLobby(defaultLobbyName).get().isInGame());

        assertThrows(IllegalArgumentException.class,
                     () -> gameManagement.createGame(lobby, defaultUser, mapManagement, 120));
    }

    @Test
    void dropGame() {
        Optional<ILobby> lobbyOptional = lobbyManagement.getLobby(defaultLobbyName);
        assertTrue(lobbyOptional.isPresent());
        ILobby lobby = lobbyOptional.get();
        IGameMapManagement mapManagement = mock(IGameMapManagement.class);
        gameManagement.createGame(lobby, defaultUser, mapManagement, 120);
        assertTrue(lobbyManagement.getLobby(defaultLobbyName).isPresent());
        assertTrue(lobbyManagement.getLobby(defaultLobbyName).get().isInGame());

        gameManagement.dropGame(defaultLobbyName);

        assertTrue(lobbyManagement.getLobby(defaultLobbyName).isPresent());
        assertFalse(lobbyManagement.getLobby(defaultLobbyName).get().isInGame());

        assertThrows(IllegalArgumentException.class, () -> gameManagement.dropGame(defaultLobbyName));
    }

    @Test
    void getGame() {
        Optional<ILobby> lobbyOptional = lobbyManagement.getLobby(defaultLobbyName);
        assertTrue(lobbyOptional.isPresent());
        ILobby lobby = lobbyOptional.get();
        IGameMapManagement mapManagement = mock(IGameMapManagement.class);
        gameManagement.createGame(lobby, defaultUser, mapManagement, 120);
        assertTrue(lobbyManagement.getLobby(defaultLobbyName).isPresent());
        assertTrue(lobbyManagement.getLobby(defaultLobbyName).get().isInGame());

        Game game = gameManagement.getGame(defaultLobbyName);

        assertEquals(defaultUser, game.getFirst());
        assertEquals(lobby, game.getLobby());

        assertNull(gameManagement.getGame(new LobbyName("unreal lobby")));
    }

    @Test
    void getGames() {
        LobbyName lobbyName = new LobbyName("Second Lobby");
        Map<LobbyName, Game> gameMap = new HashMap<>();

        Optional<ILobby> lobbyOptional = lobbyManagement.getLobby(defaultLobbyName);
        assertTrue(lobbyOptional.isPresent());
        ILobby lobby = lobbyOptional.get();
        IGameMapManagement mapManagement = mock(IGameMapManagement.class);
        gameManagement.createGame(lobby, defaultUser, mapManagement, 120);
        assertTrue(lobbyManagement.getLobby(defaultLobbyName).isPresent());
        assertTrue(lobbyManagement.getLobby(defaultLobbyName).get().isInGame());
        gameMap.put(defaultLobbyName, gameManagement.getGame(defaultLobbyName));

        lobbyManagement.createLobby(lobbyName, defaultUser, "");
        IGameMapManagement mapManagement1 = mock(IGameMapManagement.class);
        Optional<ILobby> lobbyOptional1 = lobbyManagement.getLobby(lobbyName);
        assertTrue(lobbyOptional1.isPresent());
        gameManagement.createGame(lobbyOptional1.get(), defaultUser, mapManagement1, 120);
        assertTrue(lobbyManagement.getLobby(lobbyName).isPresent());
        assertTrue(lobbyManagement.getLobby(lobbyName).get().isInGame());
        gameMap.put(lobbyName, gameManagement.getGame(lobbyName));

        Map<LobbyName, Game> returnedGameMap = gameManagement.getGames();

        assertIterableEquals(gameMap.entrySet(), returnedGameMap.entrySet());
    }
}
package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.lobby.request.LobbyJoinUserRequest;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class LobbyServiceTest {

    static final User user1 = new UserDTO("Chuck", "Norris", "chuck@norris.com");
    static final User user2 = new UserDTO("Danny", "DeVito", "danny@devito.com");
    static final User user3 = new UserDTO("Angy", "Merte", "Angy@Merte.com");
    static final User user4 = new UserDTO("User", "NummerVier", "User@NummerVier.com");
    static final User user5 = new UserDTO("Bruder", "WasGeht", "Bruder@WasGeht.com");

    static final Lobby lobbyToTest = new LobbyDTO("Testlobby", user1);
    static final Lobby lobbyWithSameName = new LobbyDTO("Testlobby", user2);

    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);
    final ILobbyManagement lobbyManagement = new LobbyManagement();
    final LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);

    @Test
    void createLobbyTest() {
        final Message request = new CreateLobbyRequest("Testlobby", user1);

        // The post will lead to a call of a LobbyService function
        bus.post(request);

        final Lobby createdLobby = lobbyManagement.getLobby(lobbyToTest.getName()).get();

        assertNotNull(createdLobby);
        assertEquals(createdLobby.getName(), lobbyToTest.getName());
        assertEquals(createdLobby.getOwner(), lobbyToTest.getOwner());
    }

    @Test
    void createSecondLobbyWithSameName() {
        final Message request1 = new CreateLobbyRequest("Testlobby", user1);
        final Message request2 = new CreateLobbyRequest("Testlobby", user2);

        bus.post(request1);
        bus.post(request2);

        final Lobby createdLobby = lobbyManagement.getLobby(lobbyToTest.getName()).get();

        assertNotNull(createdLobby);
        assertEquals(createdLobby.getName(), lobbyToTest.getName());
        assertEquals(createdLobby.getOwner(), lobbyToTest.getOwner());

        // old lobby should not be overwritten!
        assertNotEquals(createdLobby.getOwner(), lobbyWithSameName.getOwner());
    }

    @Test
    void userJoinLobbyRequest() {
        // Create a joinable lobby first
        final Message request0 = new CreateLobbyRequest("Testlobby", user1);
        // Create several join requests
        final Message request1 = new LobbyJoinUserRequest("Testlobby", user1);
        final Message request2 = new LobbyJoinUserRequest("Testlobby", user2);
        final Message request3 = new LobbyJoinUserRequest("Testlobby", user3);
        final Message request4 = new LobbyJoinUserRequest("Testlobby", user4);
        final Message request5 = new LobbyJoinUserRequest("Testlobby", user5);
        // post all requests
        bus.post(request0);
        bus.post(request1);
        bus.post(request2);
        bus.post(request3);
        bus.post(request4);
        bus.post(request5);

        final Lobby createdLobby = lobbyManagement.getLobby(lobbyToTest.getName()).get();

        // check if joinable lobby was created
        assertNotNull(createdLobby);
        // check if only 4 or less users are joined
        assertTrue(createdLobby.getUsers().size() <= 4);
        // check if every user joined except user5
        assertTrue(createdLobby.getUsers().contains(user1));
        assertTrue(createdLobby.getUsers().contains(user2));
        assertTrue(createdLobby.getUsers().contains(user3));
        assertTrue(createdLobby.getUsers().contains(user4));
        assertFalse(createdLobby.getUsers().contains(user5));
    }
}

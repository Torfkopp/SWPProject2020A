package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.lobby.request.LobbyJoinUserRequest;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class LobbyServiceTest {

    static final UserDTO user1 = new UserDTO("Chuck", "Norris", "chuck@norris.com");
    static final UserDTO user2 = new UserDTO("Danny", "DeVito", "danny@devito.com");
    static final UserDTO user3 = new UserDTO("Angy", "Merte", "Angy@Merte.com");
    static final UserDTO user4 = new UserDTO("User", "NummerVier", "User@NummerVier.com");
    static final UserDTO user5 = new UserDTO("Bruder", "WasGeht", "Bruder@WasGeht.com");

    static final Lobby lobbyToTest = new LobbyDTO("Testlobby", user1);
    static final Lobby lobbyWithSameName = new LobbyDTO("Testlobby", user2);

    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);
    final LobbyManagement lobbyManagement = new LobbyManagement();
    final LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);

    @Test
    void createLobbyTest() {
        final CreateLobbyRequest request = new CreateLobbyRequest("Testlobby", user1);

        // The post will lead to a call of a LobbyService function
        bus.post(request);

        final Lobby createdLobby = lobbyManagement.getLobby(lobbyToTest.getName()).get();

        assertNotNull(createdLobby);
        assertEquals(createdLobby.getName(), lobbyToTest.getName());
        assertEquals(createdLobby.getOwner(), lobbyToTest.getOwner());
    }

    @Test
    void createSecondLobbyWithSameName() {
        final CreateLobbyRequest request1 = new CreateLobbyRequest("Testlobby", user1);
        final CreateLobbyRequest request2 = new CreateLobbyRequest("Testlobby", user2);

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
        final CreateLobbyRequest request0 = new CreateLobbyRequest("Testlobby", user1);
        // Create several join requests
        final LobbyJoinUserRequest request1 = new LobbyJoinUserRequest("Testlobby", user1);
        final LobbyJoinUserRequest request2 = new LobbyJoinUserRequest("Testlobby", user2);
        final LobbyJoinUserRequest request3 = new LobbyJoinUserRequest("Testlobby", user3);
        final LobbyJoinUserRequest request4 = new LobbyJoinUserRequest("Testlobby", user4);
        final LobbyJoinUserRequest request5 = new LobbyJoinUserRequest("Testlobby", user5);
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

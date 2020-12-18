package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.Lobby;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class LobbyServiceTest {

    static final UserDTO creator1 = new UserDTO("Chuck", "Norris", "chuck@norris.com");
    static final UserDTO creator2 = new UserDTO("Danny", "DeVito", "danny@devito.com");
    static final Lobby lobbyToCreate = new LobbyDTO("Testlobby", creator1);
    static final Lobby lobbyWithSameName = new LobbyDTO("Testlobby", creator2);

    final EventBus bus = new EventBus();
    final UserManagement userManagement = new UserManagement(new MainMemoryBasedUserStore());
    final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);
    final LobbyManagement lobbyManagement = new LobbyManagement();
    final LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);

    @Test
    void createLobbyTest() {
        final CreateLobbyRequest request = new CreateLobbyRequest("Testlobby", creator1);

        // The post will lead to a call of a LobbyService function
        bus.post(request);

        final Lobby createdLobby = lobbyManagement.getLobby(lobbyToCreate.getName()).get();

        assertNotNull(createdLobby);
        assertEquals(createdLobby.getName(), lobbyToCreate.getName());
        assertEquals(createdLobby.getOwner(), lobbyToCreate.getOwner());
    }

    @Test
    void createSecondLobbyWithSameName() {
        final CreateLobbyRequest request = new CreateLobbyRequest("Testlobby", creator1);
        final CreateLobbyRequest request2 = new CreateLobbyRequest("Testlobby", creator2);

        bus.post(request);
        bus.post(request2);

        final Lobby createdLobby = lobbyManagement.getLobby(lobbyToCreate.getName()).get();

        assertNotNull(createdLobby);
        assertEquals(createdLobby.getName(), lobbyToCreate.getName());
        assertEquals(createdLobby.getOwner(), lobbyToCreate.getOwner());

        // old lobby should not be overwritten!
        assertNotEquals(createdLobby.getOwner(), lobbyWithSameName.getOwner());

    }

}
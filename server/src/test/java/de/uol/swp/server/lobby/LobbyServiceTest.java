package de.uol.swp.server.lobby;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.lobby.Lobby;
import de.uol.swp.common.lobby.dto.LobbyDTO;
import de.uol.swp.common.lobby.request.CreateLobbyRequest;
import de.uol.swp.common.lobby.request.JoinLobbyWithPasswordConfirmationRequest;
import de.uol.swp.common.lobby.request.LobbyJoinUserRequest;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.sessionmanagement.SessionManagement;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("UnstableApiUsage")
class LobbyServiceTest {

    static final User user1 = new UserDTO(1, "Chuck", "Norris", "chuck@norris.com");
    static final User user2 = new UserDTO(2, "Danny", "DeVito", "danny@devito.com");
    static final User user3 = new UserDTO(3, "Angy", "Merte", "Angy@Merte.com");
    static final User user4 = new UserDTO(4, "User", "NummerVier", "User@NummerVier.com");
    static final User user5 = new UserDTO(5, "Bruder", "WasGeht", "Bruder@WasGeht.com");

    static final Lobby lobbyToTest = new LobbyDTO("Testlobby", user1, "", false, false, 4, false, 60, false, false);
    static final Lobby lobbyToTestWithPassword = new LobbyDTO("TestLobbyWithPassword", user1, "123", false, true, 4,
                                                              false, 60, false, false);
    static final Lobby lobbyWithSameName = new LobbyDTO("Testlobby", user2, "", false, false, 4, false, 60, false,
                                                        false);

    final EventBus bus = new EventBus();
    final SessionManagement sessionManagement = new SessionManagement();
    final ILobbyManagement lobbyManagement = new LobbyManagement();
    final LobbyService lobbyService = new LobbyService(lobbyManagement, sessionManagement, bus);

    @Test
    void createLobbyTest() {
        final Message request = new CreateLobbyRequest("Testlobby", user1, 4, "");

        // The post will lead to a call of a LobbyService function
        bus.post(request);

        final Optional<Lobby> createdLobby = lobbyManagement.getLobby(lobbyToTest.getName());

        assertTrue(createdLobby.isPresent());
        assertEquals(lobbyToTest.getName(), createdLobby.get().getName());
        assertEquals(lobbyToTest.getOwner(), createdLobby.get().getOwner());
    }

    @Test
    void createLobbyWithPasswordRequest() {
        final Message request = new CreateLobbyRequest("TestLobbyWithPassword", user1, 3, "123");
        bus.post(request);
        final Optional<Lobby> createdLobby = lobbyManagement
                .getLobby(lobbyToTestWithPassword.getName(), lobbyToTestWithPassword.getPassword());
        // check if joinable lobby was created
        assertTrue(createdLobby.isPresent());
        // check if lobby has a password
        assertTrue(createdLobby.get().hasAPassword());
    }

    @Test
    void createSecondLobbyWithSameName() {
        final Message request1 = new CreateLobbyRequest("Testlobby", user1, 4, "");
        final Message request2 = new CreateLobbyRequest("Testlobby", user2, 4, "");

        bus.post(request1);
        bus.post(request2);

        final Optional<Lobby> createdLobby = lobbyManagement.getLobby(lobbyToTest.getName());

        assertTrue(createdLobby.isPresent());
        assertEquals(lobbyToTest.getName(), createdLobby.get().getName());
        assertEquals(lobbyToTest.getOwner(), createdLobby.get().getOwner());

        // old lobby should not be overwritten!
        assertNotEquals(lobbyWithSameName.getOwner(), createdLobby.get().getOwner());
    }

    @Test
    void joinLobbyWithPasswordRequest() {
        final Message request = new CreateLobbyRequest("TestLobbyWithPassword", user1, 4, "123");
        // Create several join requests
        final Message request1 = new JoinLobbyWithPasswordConfirmationRequest("TestLobbyWithPassword", user1, "123");
        final Message request2 = new JoinLobbyWithPasswordConfirmationRequest("TestLobbyWithPassword", user2, "123");
        final Message request3 = new JoinLobbyWithPasswordConfirmationRequest("TestLobbyWithPassword", user3, "1234");
        final Message request4 = new JoinLobbyWithPasswordConfirmationRequest("TestLobbyWithPassword", user4, "123");
        final Message request5 = new JoinLobbyWithPasswordConfirmationRequest("TestLobbyWithPassword", user5, "123");
        bus.post(request);
        bus.post(request1);
        bus.post(request2);
        bus.post(request3);
        bus.post(request4);
        bus.post(request5);
        final Optional<Lobby> createdLobby = lobbyManagement
                .getLobby(lobbyToTestWithPassword.getName(), lobbyToTest.getPassword());

        // check if joinable lobby was created
        assertTrue(createdLobby.isPresent());
        // check if lobby has a password
        assertTrue(createdLobby.get().hasAPassword());
        // check if only 4 users are joined
        assertTrue(createdLobby.get().getUserOrDummies().size() == 4);
        // check if every user joined except user5
        assertTrue(createdLobby.get().getUserOrDummies().contains(user1));
        assertTrue(createdLobby.get().getUserOrDummies().contains(user2));
        assertFalse(createdLobby.get().getUserOrDummies().contains(user3));
        assertTrue(createdLobby.get().getUserOrDummies().contains(user4));
        assertTrue(createdLobby.get().getUserOrDummies().contains(user5));
    }

    @Test
    void userJoinLobbyRequest() {
        // Create a joinable lobby first
        final Message request0 = new CreateLobbyRequest("Testlobby", user1, 4, "");
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

        final Optional<Lobby> createdLobby = lobbyManagement.getLobby(lobbyToTest.getName());

        // check if joinable lobby was created
        assertTrue(createdLobby.isPresent());
        // check if only 4 or less users are joined
        assertTrue(createdLobby.get().getUserOrDummies().size() <= 4);
        // check if every user joined except user5
        assertTrue(createdLobby.get().getUserOrDummies().contains(user1));
        assertTrue(createdLobby.get().getUserOrDummies().contains(user2));
        assertTrue(createdLobby.get().getUserOrDummies().contains(user3));
        assertTrue(createdLobby.get().getUserOrDummies().contains(user4));
        assertFalse(createdLobby.get().getUserOrDummies().contains(user5));
    }
}

package de.uol.swp.client.lobby;

import de.uol.swp.common.Colour;
import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.AI;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

class AsyncLobbyServiceTest {

    private static final long DURATION = 500L;
    private final LobbyName defaultLobby = mock(LobbyName.class);
    private final LobbyService syncLobbyService = mock(LobbyService.class);
    private AsyncLobbyService lobbyService;

    @BeforeEach
    protected void setUp() {
        assertNotNull(syncLobbyService);
        lobbyService = new AsyncLobbyService(syncLobbyService);
    }

    @AfterEach
    protected void tearDown() {
        lobbyService = null;
    }

    @Test
    void addAI() {
        AI ai = mock(AI.class);
        doNothing().when(syncLobbyService).addAI(isA(LobbyName.class), isA(AI.class));

        lobbyService.addAI(defaultLobby, ai);

        verify(syncLobbyService, timeout(DURATION)).addAI(defaultLobby, ai);
    }

    @Test
    void changeOwner() {
        Actor newOwner = mock(Actor.class);
        doNothing().when(syncLobbyService).changeOwner(isA(LobbyName.class), isA(Actor.class));

        lobbyService.changeOwner(defaultLobby, newOwner);

        verify(syncLobbyService, timeout(DURATION)).changeOwner(defaultLobby, newOwner);
    }

    @Test
    void checkUserInLobby() {
        doNothing().when(syncLobbyService).checkUserInLobby();

        lobbyService.checkUserInLobby();

        verify(syncLobbyService, timeout(DURATION)).checkUserInLobby();
    }

    @Test
    void createNewLobby() {
        String pass = "pass";
        doNothing().when(syncLobbyService).createNewLobby(isA(LobbyName.class), isA(String.class));

        lobbyService.createNewLobby(defaultLobby, pass);

        verify(syncLobbyService, timeout(DURATION)).createNewLobby(defaultLobby, pass);
    }

    @Test
    void joinLobby() {
        doNothing().when(syncLobbyService).joinLobby(isA(LobbyName.class));

        lobbyService.joinLobby(defaultLobby);

        verify(syncLobbyService, timeout(DURATION)).joinLobby(defaultLobby);
    }

    @Test
    void joinRandomLobby() {
        doNothing().when(syncLobbyService).joinRandomLobby();

        lobbyService.joinRandomLobby();

        verify(syncLobbyService, timeout(DURATION)).joinRandomLobby();
    }

    @Test
    void kickUser() {
        Actor otherUser = mock(Actor.class);
        doNothing().when(syncLobbyService).kickUser(isA(LobbyName.class), isA(Actor.class));

        lobbyService.kickUser(defaultLobby, otherUser);

        verify(syncLobbyService, timeout(DURATION)).kickUser(defaultLobby, otherUser);
    }

    @Test
    void leaveLobby() {
        doNothing().when(syncLobbyService).leaveLobby(isA(LobbyName.class));

        lobbyService.leaveLobby(defaultLobby);

        verify(syncLobbyService, timeout(DURATION)).leaveLobby(defaultLobby);
    }

    @Test
    void refreshLobbyPresenterFields() {
        ISimpleLobby lobby = mock(ISimpleLobby.class);
        doNothing().when(syncLobbyService).refreshLobbyPresenterFields(isA(ISimpleLobby.class));

        lobbyService.refreshLobbyPresenterFields(lobby);

        verify(syncLobbyService, timeout(DURATION)).refreshLobbyPresenterFields(lobby);
    }

    @Test
    void removeFromAllLobbies() {
        doNothing().when(syncLobbyService).removeFromAllLobbies();

        lobbyService.removeFromAllLobbies();

        verify(syncLobbyService, timeout(DURATION)).removeFromAllLobbies();
    }

    @Test
    void retrieveAllLobbies() {
        doNothing().when(syncLobbyService).retrieveAllLobbies();

        lobbyService.retrieveAllLobbies();

        verify(syncLobbyService, timeout(DURATION)).retrieveAllLobbies();
    }

    @Test
    void retrieveAllLobbyMembers() {
        doNothing().when(syncLobbyService).retrieveAllLobbyMembers(isA(LobbyName.class));

        lobbyService.retrieveAllLobbyMembers(defaultLobby);

        verify(syncLobbyService, timeout(DURATION)).retrieveAllLobbyMembers(defaultLobby);
    }

    @Test
    void returnToPreGameLobby() {
        doNothing().when(syncLobbyService).returnToPreGameLobby(isA(LobbyName.class));

        lobbyService.returnToPreGameLobby(defaultLobby);

        verify(syncLobbyService, timeout(DURATION)).returnToPreGameLobby(defaultLobby);
    }

    @Test
    void setColour() {
        Colour colour = mock(Colour.class);
        doNothing().when(syncLobbyService).setColour(isA(LobbyName.class), isA(Colour.class));

        lobbyService.setColour(defaultLobby, colour);

        verify(syncLobbyService, timeout(DURATION)).setColour(defaultLobby, colour);
    }

    @Test
    void updateLobbySettings() {
        doNothing().when(syncLobbyService)
                   .updateLobbySettings(isA(LobbyName.class), isA(Integer.class), isA(Boolean.class),
                                        isA(Integer.class), isA(Boolean.class), isA(Integer.class));

        lobbyService.updateLobbySettings(defaultLobby, 4, true, 120, true, 2);

        verify(syncLobbyService, timeout(DURATION)).updateLobbySettings(defaultLobby, 4, true, 120, true, 2);
    }

    @Test
    void userReady() {
        doNothing().when(syncLobbyService).userReady(isA(LobbyName.class), isA(Boolean.class));

        lobbyService.userReady(defaultLobby, true);

        verify(syncLobbyService, timeout(DURATION)).userReady(defaultLobby, true);
    }
}
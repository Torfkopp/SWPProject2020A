package de.uol.swp.client.lobby.event;

import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.SimpleLobby;
import de.uol.swp.common.specialisedUtil.UserOrDummySet;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test of the event used to show the Lobby View
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.lobby.event.ShowLobbyViewEvent
 * @since 2021-01-03
 */
class ShowLobbyViewEventTest {

    private static final LobbyName defaultLobbyName = new LobbyName("Test");
    private static final User owner = new UserDTO(1, "test", "test123", "test@test.test");
    private static final ISimpleLobby defaultLobby = new SimpleLobby(defaultLobbyName, false, owner, 4, 120, false,
                                                                     false, false, new UserOrDummySet(), new UserOrDummySet(), 2);

    /**
     * Test for the creation of ShowLobbyViewEvents
     * <p>
     * This test checks if the lobby window title gets set correctly during
     * the creation of a new event
     */
    @Test
    void createShowLobbyViewEventTest() {
        ShowLobbyViewEvent event = new ShowLobbyViewEvent(defaultLobby);

        assertEquals(defaultLobby, event.getLobby());
    }
}

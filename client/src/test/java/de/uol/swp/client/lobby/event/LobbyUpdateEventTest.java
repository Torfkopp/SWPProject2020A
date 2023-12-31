package de.uol.swp.client.lobby.event;

import de.uol.swp.common.lobby.ISimpleLobby;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.SimpleLobby;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the event used to communicate Lobby details to new
 * LobbyPresenter instances
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.lobby.event.LobbyUpdateEvent
 * @since 2021-01-03
 */
class LobbyUpdateEventTest {

    private static final User defaultUser = new UserDTO(1, "I'm tree", //
                                                        "I'm pretty sure I'm a tree", "tree@tree.test");
    private static final ISimpleLobby defaultLobby = new SimpleLobby(new LobbyName("Am I a lobby?"), false, //
                                                                     defaultUser, 3, //
                                                                     60, false, //
                                                                     false, false, //
                                                                     null, null, 2); //

    /**
     * Test for the creation of LobbyUpdateEvents
     * <p>
     * This checks if the lobby name and related User details are set
     * correctly during the creation of a new event
     */
    @Test
    void createLobbyUpdateEventTest() {
        LobbyUpdateEvent event = new LobbyUpdateEvent(defaultLobby);

        assertEquals(defaultLobby, event.getLobby());
        assertEquals(defaultLobby.getName(), event.getLobby().getName());
        assertEquals(defaultLobby.isInGame(), event.getLobby().isInGame());
        assertEquals(defaultLobby.getMaxPlayers(), event.getLobby().getMaxPlayers());
        assertEquals(defaultLobby.getMoveTime(), event.getLobby().getMoveTime());
        assertEquals(defaultLobby.isStartUpPhaseEnabled(), event.getLobby().isStartUpPhaseEnabled());
        assertEquals(defaultLobby.isRandomPlayFieldEnabled(), event.getLobby().isRandomPlayFieldEnabled());
    }
}

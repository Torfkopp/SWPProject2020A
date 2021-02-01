package de.uol.swp.client.lobby.event;

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

    private static final User defaultUser = new UserDTO("I'm tree", "I'm pretty sure I'm a tree", "tree@tree.test");

    /**
     * Test for the creation of LobbyUpdateEvents
     * <p>
     * This checks if the lobby name and related User details are set
     * correctly during the creation of a new event
     */
    @Test
    void createLobbyUpdateEventTest() {
        LobbyUpdateEvent event = new LobbyUpdateEvent("Am I a lobby?", defaultUser);

        assertEquals(event.getLobbyName(), "Am I a lobby?");
        assertEquals(event.getUser().getUsername(), defaultUser.getUsername());
        assertEquals(event.getUser().getPassword(), defaultUser.getPassword());
        assertEquals(event.getUser().getEMail(), defaultUser.getEMail());
    }
}

package de.uol.swp.client.lobby.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the event used to show the LobbyError alert
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.lobby.event.LobbyErrorEvent
 * @since 2021-01-03
 */
class LobbyErrorEventTest {

    /**
     * Test for the creation of LobbyErrorEvents
     * <p>
     * This test checks if the error message of the LobbyErrorEvents gets
     * set correctly during the creation of a new event
     */
    @Test
    void createLobbyErrorEventTest() {
        LobbyErrorEvent event = new LobbyErrorEvent("Test");

        assertEquals("Test", event.getMessage());
    }
}

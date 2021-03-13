package de.uol.swp.client.lobby.event;

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

    /**
     * Test for the creation of ShowLobbyViewEvents
     * <p>
     * This test checks if the lobby window title gets set correctly during
     * the creation of a new event
     */
    @Test
    void createShowLobbyViewEventTest() {
        ShowLobbyViewEvent event = new ShowLobbyViewEvent("Test");

        assertEquals("Test", event.getName());
    }
}

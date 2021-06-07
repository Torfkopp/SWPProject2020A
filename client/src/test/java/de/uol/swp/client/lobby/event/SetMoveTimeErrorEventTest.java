package de.uol.swp.client.lobby.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetMoveTimeErrorEventTest {

    @Test
    void getMessage() {
        String message = "some move time error";
        SetMoveTimeErrorEvent event = new SetMoveTimeErrorEvent(message);

        assertEquals(message, event.getMessage());
    }
}
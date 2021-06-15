package de.uol.swp.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LobbyExceptionMessageTest {

    @Test
    void testLobbyExceptionMessage() {
        String message = "some exception";
        LobbyExceptionMessage exceptionMessage = new LobbyExceptionMessage(message);

        assertEquals(message, exceptionMessage.getException());
    }
}
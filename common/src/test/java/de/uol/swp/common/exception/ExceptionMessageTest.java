package de.uol.swp.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionMessageTest {

    @Test
    void testExceptionMessage() {
        String message = "some message";
        ExceptionMessage exceptionMessage = new ExceptionMessage(message);

        assertEquals(message, exceptionMessage.toString());
        assertEquals(message, exceptionMessage.getException());
    }
}
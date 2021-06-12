package de.uol.swp.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegistrationExceptionMessageTest {

    @Test
    void testRegistrationExceptionMessage() {
        String message = "some exception";
        RegistrationExceptionMessage exceptionMessage = new RegistrationExceptionMessage(message);

        assertEquals(message, exceptionMessage.getException());
    }
}
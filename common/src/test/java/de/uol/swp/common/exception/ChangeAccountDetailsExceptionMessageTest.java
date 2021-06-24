package de.uol.swp.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ChangeAccountDetailsExceptionMessageTest {

    @Test
    void testChangeAccountDetailsExceptionMessage() {
        String message = "message";
        ChangeAccountDetailsExceptionMessage exceptionMessage = new ChangeAccountDetailsExceptionMessage(message);

        assertEquals(message, exceptionMessage.getException());
    }
}
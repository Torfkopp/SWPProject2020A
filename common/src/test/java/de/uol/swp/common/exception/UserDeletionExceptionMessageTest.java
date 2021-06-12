package de.uol.swp.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserDeletionExceptionMessageTest {

    @Test
    void testUserDeletionExceptionMessage() {
        String message = "some message";
        UserDeletionExceptionMessage exceptionMessage = new UserDeletionExceptionMessage(message);

        assertEquals(message, exceptionMessage.getException());
    }
}
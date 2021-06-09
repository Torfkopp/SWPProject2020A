package de.uol.swp.common.chat.request;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DeleteChatMessageRequestTest {

    private final User defaultUser = mock(User.class);
    private DeleteChatMessageRequest request;

    @BeforeEach
    protected void setUp() {
        request = new DeleteChatMessageRequest(1, defaultUser);
    }

    @AfterEach
    protected void tearDown() {
        request = null;
    }

    @Test
    void getId() {
        assertEquals(1, request.getId());
    }

    @Test
    void getRequestingUser() {
        assertEquals(defaultUser, request.getRequestingUser());
    }
}
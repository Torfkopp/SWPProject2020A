package de.uol.swp.common.chat.request;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class DeleteChatMessageRequestTest {

    @Test
    void testDeleteChatMessageRequest() {
        User defaultUser = mock(User.class);
        DeleteChatMessageRequest request = new DeleteChatMessageRequest(1, defaultUser);

        assertEquals(1, request.getId());
        assertEquals(defaultUser, request.getRequestingUser());
    }
}
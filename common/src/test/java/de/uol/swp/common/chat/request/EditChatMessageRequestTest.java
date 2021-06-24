package de.uol.swp.common.chat.request;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class EditChatMessageRequestTest {

    @Test
    void testEditChatMessageRequest() {
        User defaultUser = mock(User.class);
        String newContent = "new Content";
        EditChatMessageRequest request = new EditChatMessageRequest(1, newContent, defaultUser);

        assertEquals(1, request.getId());
        assertEquals(defaultUser, request.getRequestingUser());
        assertEquals(newContent, request.getContent());
    }
}
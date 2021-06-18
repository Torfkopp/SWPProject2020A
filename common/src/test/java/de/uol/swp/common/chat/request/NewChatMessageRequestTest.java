package de.uol.swp.common.chat.request;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class NewChatMessageRequestTest {

    private final User defaultUser = mock(User.class);
    private final String content = "content";
    private NewChatMessageRequest request;

    @BeforeEach
    protected void setUp() {
        request = new NewChatMessageRequest(defaultUser, content);
    }

    @AfterEach
    protected void tearDown() {
        request = null;
    }

    @Test
    void getAuthor() {
        assertEquals(defaultUser, request.getAuthor());
    }

    @Test
    void getContent() {
        assertEquals(content, request.getContent());
    }
}
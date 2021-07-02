package de.uol.swp.common.user.response;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class AlreadyLoggedInResponseTest {

    @Test
    void AlreadyLoggedInResponse() {
        User user = mock(User.class);

        AlreadyLoggedInResponse response = new AlreadyLoggedInResponse(user);

        assertEquals(user, response.getLoggedInUser());
    }
}
package de.uol.swp.common.user.response;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class NukedUsersSessionsResponseTest {

    @Test
    void NukedUsersSessionsResponse() {
        User user = mock(User.class);

        NukedUsersSessionsResponse response = new NukedUsersSessionsResponse(user);

        assertEquals(user, response.getUser());
    }
}
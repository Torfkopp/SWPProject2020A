package de.uol.swp.common.user.request;

import de.uol.swp.common.user.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;

class NukeUsersSessionsRequestTest {

    @Test
    void NukeUsersSessionsRequest() {
        User user = mock(User.class);

        NukeUsersSessionsRequest request = new NukeUsersSessionsRequest(user);

        assertEquals(user, request.getUser());
        assertFalse(request.authorisationNeeded());
    }
}
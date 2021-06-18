package de.uol.swp.server.game.event;

import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.user.UserOrDummy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ForwardToUserInternalRequestTest {

    @Test
    void testForwardToUserInternalRequest() {
        ResponseMessage responseMessage = mock(ResponseMessage.class);
        UserOrDummy userOrDummy = mock(UserOrDummy.class);
        ForwardToUserInternalRequest request = new ForwardToUserInternalRequest(userOrDummy, responseMessage);

        assertEquals(responseMessage, request.getResponseMessage());
        assertEquals(userOrDummy, request.getTargetUser());
    }
}
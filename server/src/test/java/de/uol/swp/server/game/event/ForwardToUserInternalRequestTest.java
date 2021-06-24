package de.uol.swp.server.game.event;

import de.uol.swp.common.message.ResponseMessage;
import de.uol.swp.common.user.Actor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class ForwardToUserInternalRequestTest {

    @Test
    void testForwardToUserInternalRequest() {
        ResponseMessage responseMessage = mock(ResponseMessage.class);
        Actor actor = mock(Actor.class);
        ForwardToUserInternalRequest request = new ForwardToUserInternalRequest(actor, responseMessage);

        assertEquals(responseMessage, request.getResponseMessage());
        assertEquals(actor, request.getTargetUser());
    }
}
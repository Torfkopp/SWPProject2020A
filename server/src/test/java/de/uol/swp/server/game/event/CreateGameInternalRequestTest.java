package de.uol.swp.server.game.event;

import de.uol.swp.common.user.Actor;
import de.uol.swp.server.lobby.ILobby;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class CreateGameInternalRequestTest {

    @Test
    void testCreateGameInternalRequest() {
        ILobby lobby = mock(ILobby.class);
        Actor actor = mock(Actor.class);
        CreateGameInternalRequest request = new CreateGameInternalRequest(lobby, actor, 120);

        assertEquals(lobby, request.getLobby());
        assertEquals(actor, request.getFirst());
        assertEquals(120, request.getMoveTime());
    }
}
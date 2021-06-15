package de.uol.swp.server.game.event;

import de.uol.swp.common.user.UserOrDummy;
import de.uol.swp.server.lobby.ILobby;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class CreateGameInternalRequestTest {

    @Test
    void testCreateGameInternalRequest() {
        ILobby lobby = mock(ILobby.class);
        UserOrDummy userOrDummy = mock(UserOrDummy.class);
        CreateGameInternalRequest request = new CreateGameInternalRequest(lobby, userOrDummy, 120);

        assertEquals(lobby, request.getLobby());
        assertEquals(userOrDummy, request.getFirst());
        assertEquals(120, request.getMoveTime());
    }
}
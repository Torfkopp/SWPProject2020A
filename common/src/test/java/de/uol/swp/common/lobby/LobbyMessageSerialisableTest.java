package de.uol.swp.common.lobby;

import de.uol.swp.common.SerialisationTestHelper;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LobbyMessageSerialisableTest {

    private static final UserDTO defaultUser = new UserDTO("marco", "marco", "marco@grawunder.de");

    @Test
    void testLobbyMessagesSerializable() {
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new CreateLobbyRequest("test", defaultUser),
                                                               CreateLobbyRequest.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new LobbyJoinUserRequest("test", defaultUser),
                                                               LobbyJoinUserRequest.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new LobbyLeaveUserRequest("test", defaultUser),
                                                               LobbyLeaveUserRequest.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new UserJoinedLobbyMessage("test", defaultUser),
                                                               UserJoinedLobbyMessage.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new UserLeftLobbyMessage("test", defaultUser),
                                                               UserLeftLobbyMessage.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(new RetrieveAllLobbiesRequest(),
                                                                              RetrieveAllLobbiesRequest.class));
    }
}

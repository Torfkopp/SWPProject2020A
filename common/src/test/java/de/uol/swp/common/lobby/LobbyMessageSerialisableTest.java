package de.uol.swp.common.lobby;

import de.uol.swp.common.LobbyName;
import de.uol.swp.common.SerialisationTestHelper;
import de.uol.swp.common.lobby.message.UserJoinedLobbyMessage;
import de.uol.swp.common.lobby.message.UserLeftLobbyMessage;
import de.uol.swp.common.lobby.request.*;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LobbyMessageSerialisableTest {

    private static final UserDTO defaultUser = new UserDTO(42, "marco", "marco", "marco@grawunder.de");
    private static final LobbyName defaultLobbyName = new LobbyName("test");

    @Test
    void testLobbyMessagesSerializable() {
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new CreateLobbyRequest(defaultLobbyName, defaultUser, 4),
                                                               CreateLobbyRequest.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new LobbyJoinUserRequest(defaultLobbyName, defaultUser),
                                                               LobbyJoinUserRequest.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new LobbyLeaveUserRequest(defaultLobbyName, defaultUser),
                                                               LobbyLeaveUserRequest.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new UserJoinedLobbyMessage(defaultLobbyName, defaultUser),
                                                               UserJoinedLobbyMessage.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new UserLeftLobbyMessage(defaultLobbyName, defaultUser),
                                                               UserLeftLobbyMessage.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(new RetrieveAllLobbiesRequest(),
                                                                              RetrieveAllLobbiesRequest.class));
    }
}

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
    private static final LobbyName defaultLobby = new LobbyName("test");

    @Test
    void testLobbyMessagesSerializable() {
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new CreateLobbyRequest(defaultLobby, defaultUser, ""),
                                                               CreateLobbyRequest.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new LobbyJoinUserRequest(defaultLobby, defaultUser),
                                                               LobbyJoinUserRequest.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new LobbyLeaveUserRequest(defaultLobby, defaultUser),
                                                               LobbyLeaveUserRequest.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new UserJoinedLobbyMessage(defaultLobby, defaultUser),
                                                               UserJoinedLobbyMessage.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new UserLeftLobbyMessage(defaultLobby, defaultUser),
                                                               UserLeftLobbyMessage.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(new RetrieveAllLobbiesRequest(),
                                                                              RetrieveAllLobbiesRequest.class));
    }
}

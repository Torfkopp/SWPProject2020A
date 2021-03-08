package de.uol.swp.common.chat;

import de.uol.swp.common.SerialisationTestHelper;
import de.uol.swp.common.chat.dto.ChatMessageDTO;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.request.*;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatMessageSerialisableTest {

    private static final User defaultUser = new UserDTO(42, "test", "test", "test@test.de");
    private static final Instant defaultTimestamp = Instant.ofEpochMilli(1608370913852L); // 2020-12-19-09:41:53.852
    private static final ChatMessageDTO defaultChatMessage = new ChatMessageDTO(1, defaultUser, defaultTimestamp,
                                                                                "test message content");
    private static final List<ChatMessage> defaultLatestChatMessageList = new LinkedList<>();
    private static final String defaultLobby = "I'm a lobby... full of TREES!!!";

    static {
        defaultLatestChatMessageList.add(defaultChatMessage);
    }

    @Test
    void testLobbyMessagesSerializable() {
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new CreatedChatMessageMessage(defaultChatMessage),
                                                               CreatedChatMessageMessage.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(new DeletedChatMessageMessage(42),
                                                                              DeletedChatMessageMessage.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new EditedChatMessageMessage(defaultChatMessage),
                                                               EditedChatMessageMessage.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(new AskLatestChatMessageRequest(37),
                                                                              AskLatestChatMessageRequest.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new DeleteChatMessageRequest(42, defaultUser),
                                                               DeleteChatMessageRequest.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(
                new EditChatMessageRequest(42, "I am content", defaultUser), EditChatMessageRequest.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(
                new NewChatMessageRequest(defaultUser, "I am content, too"), NewChatMessageRequest.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(
                new AskLatestChatMessageResponse(defaultLatestChatMessageList), AskLatestChatMessageResponse.class));

        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(
                new CreatedChatMessageMessage(defaultChatMessage, defaultLobby), CreatedChatMessageMessage.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new DeletedChatMessageMessage(42, defaultLobby),
                                                               DeletedChatMessageMessage.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(
                new EditedChatMessageMessage(defaultChatMessage, defaultLobby), EditedChatMessageMessage.class));
        assertTrue(SerialisationTestHelper
                           .checkSerialisableAndDeserialisable(new AskLatestChatMessageRequest(37, defaultLobby),
                                                               AskLatestChatMessageRequest.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(
                new DeleteChatMessageRequest(42, defaultUser, defaultLobby), DeleteChatMessageRequest.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(
                new EditChatMessageRequest(42, "I am content", defaultUser, defaultLobby),
                EditChatMessageRequest.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(
                new NewChatMessageRequest(defaultUser, "I am content, too", defaultLobby),
                NewChatMessageRequest.class));
        assertTrue(SerialisationTestHelper.checkSerialisableAndDeserialisable(
                new AskLatestChatMessageResponse(defaultLatestChatMessageList, defaultLobby),
                AskLatestChatMessageResponse.class));
    }
}

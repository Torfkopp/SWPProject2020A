package de.uol.swp.common.chat;

import de.uol.swp.common.SerializationTestHelper;
import de.uol.swp.common.chat.dto.ChatMessageDTO;
import de.uol.swp.common.chat.message.CreatedChatMessageMessage;
import de.uol.swp.common.chat.message.DeletedChatMessageMessage;
import de.uol.swp.common.chat.message.EditedChatMessageMessage;
import de.uol.swp.common.chat.request.AskLatestChatMessageRequest;
import de.uol.swp.common.chat.request.DeleteChatMessageRequest;
import de.uol.swp.common.chat.request.EditChatMessageRequest;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.chat.response.AskLatestChatMessageResponse;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatMessageSerializableTest {

    private static final UserDTO defaultUser = new UserDTO("test", "test", "test@test.de");
    private static final Instant defaultTimestamp = Instant.ofEpochMilli(1608370913852L); // 2020-12-19-09:41:53.852
    private static final ChatMessageDTO defaultChatMessage = new ChatMessageDTO(1, defaultUser, defaultTimestamp, "test message content");
    private static final List<ChatMessage> defaultLatestChatMessageList = new LinkedList<>();

    static {
        defaultLatestChatMessageList.add(defaultChatMessage);
    }

    @Test
    void testLobbyMessagesSerializable() {
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new CreatedChatMessageMessage(defaultChatMessage),
                CreatedChatMessageMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new DeletedChatMessageMessage(42),
                DeletedChatMessageMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new EditedChatMessageMessage(defaultChatMessage),
                EditedChatMessageMessage.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AskLatestChatMessageRequest(37),
                AskLatestChatMessageRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new DeleteChatMessageRequest(42),
                DeleteChatMessageRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new EditChatMessageRequest(42, "I am content"),
                EditChatMessageRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new NewChatMessageRequest(defaultUser, "I am content, too"),
                NewChatMessageRequest.class));
        assertTrue(SerializationTestHelper.checkSerializableAndDeserializable(new AskLatestChatMessageResponse(defaultLatestChatMessageList),
                AskLatestChatMessageResponse.class));
    }
}
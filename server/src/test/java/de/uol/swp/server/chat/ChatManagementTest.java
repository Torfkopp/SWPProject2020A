package de.uol.swp.server.chat;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.chat.store.ChatMessageStore;
import de.uol.swp.server.chat.store.MainMemoryBasedChatMessageStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test of the ChatManagement class that communicates with the ChatMessageStore
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.server.chat.IChatManagement
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.chat.ChatMessage
 * @see de.uol.swp.server.chat.store.ChatMessageStore
 * @since 2020-12-19
 */
class ChatManagementTest {
    private static final User defaultUser = new UserDTO("test", "test", "test@test.de");
    private static final String defaultContent = "I am intelligent content";
    private static final String secondContent = "I am new, more intelligent content";
    private static ChatManagement chatManagement;
    private static ChatMessageStore chatMessageStore;

    /**
     * Helper method run before each test case
     * <p>
     * This method instantiates a new ChatMessageStore and a new ChatManagement so that
     * one test's ChatMessage objects don't interfere with another test's
     *
     * @since 2020-12-19
     */
    @BeforeEach
    void setUp() {
        chatMessageStore = new MainMemoryBasedChatMessageStore();
        chatManagement = new ChatManagement(chatMessageStore);
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method resets the chatManagement and chatMessageStore variables to null
     *
     * @since 2020-12-19
     */
    @AfterEach
    void tearDown() {
        chatManagement = null;
        chatMessageStore = null;
    }

    /**
     * Test of the getLatestMessages routine
     * <p>
     * Tests if the list of ChatMessage objects returned by getLatestMessages
     * is equal to a local list of the created ChatMessages.
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is not equal to the local list of created ChatMessages.
     *
     * @since 2020-12-19
     */
    @Test
    void getLatestMessagesTest() {
        ChatMessage msg1 = chatMessageStore.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = chatMessageStore.createChatMessage(defaultUser, secondContent);
        List<ChatMessage> newMessages = new ArrayList<>();
        newMessages.add(msg1);
        newMessages.add(msg2);

        List<ChatMessage> list = chatManagement.getLatestMessages(2);

        assertEquals(newMessages, list);
    }

    /**
     * Test of the createChatMessage routine
     * <p>
     * Tests if a ChatMessage was created by checking if the return of getLatestMessages
     * contains the created ChatMessage object.
     * <p>
     * This test fails if the List of ChatMessage objects returned by getLatestMessages
     * is empty or if it doesn't contain the newly created ChatMessage.
     *
     * @since 2020-12-19
     */
    @Test
    void createChatMessageTest() {
        ChatMessage msg = chatManagement.createChatMessage(defaultUser, defaultContent);

        List<ChatMessage> list = chatMessageStore.getLatestMessages(1);
        assertFalse(list.isEmpty());

        assertTrue(list.contains(msg));
    }

    /**
     * Test of the updateChatMessage routine
     * <p>
     * Tests if a specified ChatMessage gets updated with the provided content
     * and no other ChatMessage gets changed when updateChatMessage is called.
     * <p>
     * This test fails if the List of ChatMessage objects returned by getLatestMessages
     * is empty or if one of the ChatMessages was inappropriately updated.
     *
     * @since 2020-12-19
     */
    @Test
    void updateChatMessageTest() {
        chatMessageStore.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = chatMessageStore.createChatMessage(defaultUser, defaultContent);
        chatManagement.updateChatMessage(msg2.getID(), secondContent);

        List<ChatMessage> list = chatMessageStore.getLatestMessages(2);
        assertFalse(list.isEmpty());

        assertEquals(list.get(0).getContent(), defaultContent);
        assertEquals(list.get(1).getContent(), secondContent);
    }

    /**
     * Test of the dropChatMessage routine
     * <p>
     * Tests if a specified ChatMessage gets deleted from the chatMessageStore
     * and no other ChatMessage gets deleted when dropChatMessage is called.
     * <p>
     * This test fails if the List of ChatMessage object returned by getLatestMessages
     * is empty, or if the list doesn't contain the created test ChatMessage, or if
     * after calling dropChatMessage, the List of ChatMessage returned by
     * getLatestMessages still contains the supposedly deleted ChatMessage.
     *
     * @since 2020-12-19
     */
    @Test
    void dropChatMessageTest() {
        ChatMessage msg = chatMessageStore.createChatMessage(defaultUser, defaultContent);
        List<ChatMessage> list = chatMessageStore.getLatestMessages(1);
        assertFalse(list.isEmpty());
        assertTrue(list.contains(msg));

        chatManagement.dropChatMessage(msg.getID());

        list = chatMessageStore.getLatestMessages(1);
        assertFalse(list.contains(msg));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when createChatMessage
     * is called with null as content.
     *
     * @since 2020-12-19
     */
    @Test
    void createChatMessageEmptyContent() {
        assertThrows(ChatManagementException.class, () -> chatManagement.createChatMessage(defaultUser, null));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when createChatMessage
     * is called with an empty content.
     *
     * @since 2020-12-19
     */
    @Test
    void createChatMessageEmptyStringContent() {
        assertThrows(ChatManagementException.class, () -> chatManagement.createChatMessage(defaultUser, ""));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when createChatMessage
     * is called with null as its author.
     *
     * @since 2020-12-19
     */
    @Test
    void createChatMessageEmptyAuthor() {
        assertThrows(ChatManagementException.class, () -> chatManagement.createChatMessage(null, defaultContent));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when updateChatMessage
     * is called with an ID not found in the chatMessageStore.
     *
     * @since 2020-12-19
     */
    @Test
    void updateChatMessageUnknownId() {
        assertThrows(ChatManagementException.class, () -> chatManagement.updateChatMessage(1, secondContent));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when dropChatMessage
     * is called with an ID not found in the chatMessageStore.
     *
     * @since 2020-12-19
     */
    @Test
    void deleteChatMessageUnknownId() {
        assertThrows(ChatManagementException.class, () -> chatManagement.dropChatMessage(42));
    }
}

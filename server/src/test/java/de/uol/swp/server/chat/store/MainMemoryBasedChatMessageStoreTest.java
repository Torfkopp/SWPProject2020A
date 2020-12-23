package de.uol.swp.server.chat.store;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test for the Main Memory based implementation of the ChatMessageStore interface
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @since 2020-12-19
 */
class MainMemoryBasedChatMessageStoreTest {

    private static final User defaultUser = new UserDTO("test", "test", "test@test.de");
    private static final User secondUser = new UserDTO("test2", "test", "test@test.de");
    private static final String defaultContent = "I am intelligent content";
    private static final String secondContent = "I am more intelligent content";
    private static final String thirdContent = "I am the most intelligent content";
    private ChatMessageStore store;

    /**
     * Helper method run before each test case
     * <p>
     * This method instantiates a new MainMemoryBasedChatMessageStore so that one test's
     * ChatMessage objects don't interfere with another test's.
     *
     * @since 2020-12-19
     */
    @BeforeEach
    void setUp() {
        store = new MainMemoryBasedChatMessageStore();
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method resets the store variable to null
     *
     * @since 2020-12-19
     */
    @AfterEach
    void tearDown() {
        store = null;
    }

    /**
     * Test of the findMessage routine
     * <p>
     * Tests if a created ChatMessage object can be found with its ID.
     * <p>
     * This test fails if no result is found, or if the result returned
     * is not the same ChatMessage that was created.
     *
     * @since 2020-12-19
     */
    @Test
    void findMessage() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        Optional<ChatMessage> result = store.findMessage(msg.getID());

        assertTrue(result.isPresent());
        assertEquals(msg, result.get());
    }

    /**
     * Test of the getLatestMessages routine
     * <p>
     * Tests if created ChatMessage objects are correctly returned by
     * getLatestMessages by comparing the returned List of ChatMessage
     * objects to a local list of the created ChatMessages.
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is empty, or if is longer than the requested amount,
     * or if it is not equal to the local list of the created ChatMessages.
     *
     * @since 2020-12-19
     */
    @Test
    void getLatestMessages() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent);
        ChatMessage msg3 = store.createChatMessage(defaultUser, thirdContent);
        List<ChatMessage> list1 = new ArrayList<>();
        list1.add(msg);
        list1.add(msg2);
        list1.add(msg3);

        List<ChatMessage> list2 = store.getLatestMessages(3);

        assertFalse(list2.isEmpty());
        assertTrue(list2.size() <= 3);
        assertEquals(list1, list2);
    }

    /**
     * Test for the createChatMessage routine
     * <p>
     * Tests if a ChatMessage with a given author and content was created.
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is empty, or doesn't contain the created ChatMessage,
     * or the author or content of the created ChatMessage are equal to the given
     * author and content.
     *
     * @since 2020-12-19
     */
    @Test
    void createChatMessage() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        List<ChatMessage> list = store.getLatestMessages(1);
        assertFalse(list.isEmpty());
        assertTrue(list.contains(msg));
        assertEquals(msg.getAuthor(), defaultUser);
        assertEquals(msg.getContent(), defaultContent);
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the contents of the ChatMessage with the specified ID are updated
     * <p>
     * This test fails if the contents of the specified ChatMessage are not changed to the given content
     * or any contents from other ChatMessages get changed.
     *
     * @since 2020-12-19
     */
    @Test
    void updateChatMessage() {
        store.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent);
        store.createChatMessage(defaultUser, thirdContent);

        store.updateChatMessage(msg2.getID(), defaultContent);

        List<ChatMessage> list = store.getLatestMessages(3);
        assertEquals(list.get(1).getContent(), defaultContent); // the edited ChatMessage
        assertEquals(list.get(0).getContent(), defaultContent);
        assertEquals(list.get(2).getContent(), thirdContent);
    }

    /**
     * Test for the removeChatMessage routine
     * <p>
     * Tests if the ChatMessage with the specified ID gets deleted from the chatMessageStore
     * when removeChatMessage is called.
     * <p>
     * This test fails when the created ChatMessage is still found.
     *
     * @since 2020-12-19
     */
    @Test
    void removeChatMessage() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        store.removeChatMessage(msg.getID());

        Optional<ChatMessage> result = store.findMessage(msg.getID());
        assertTrue(result.isEmpty());
    }

    /**
     * Test for the createChatMessage routine
     * <p>
     * Tests if the createChatMessage routine correctly throws an IllegalArgumentException.
     * <p>
     * This test fails if the IllegalArgumentException isn't thrown.
     *
     * @since 2020-12-19
     */
    @Test
    void createChatMessageEmptyAuthor() {
        assertThrows(IllegalArgumentException.class, () -> store.createChatMessage(null, defaultContent));
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the content of a created ChatMessage remains unchanged when
     * the updatedContent parameter is set to null.
     * <p>
     * This test fails if the ChatMessage wasn't found, or if the content of
     * the created ChatMessage isn't the same anymore.
     *
     * @since 2020-12-19
     */
    @Test
    void updateChatMessageEmptyContent() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        store.updateChatMessage(msg.getID(), null);

        Optional<ChatMessage> result = store.findMessage(msg.getID());
        assertTrue(result.isPresent());
        assertEquals(result.get().getContent(), defaultContent);
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the content of a created ChatMessage remains unchanged when
     * the updatedContent parameter is set to the empty String.
     * <p>
     * This test fails if the ChatMessage wasn't found, or if the content of
     * the created ChatMessage isn't the same anymore.
     *
     * @since 2020-12-19
     */
    @Test
    void updateChatMessageEmptyStringContent() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        store.updateChatMessage(msg.getID(), "");

        Optional<ChatMessage> result = store.findMessage(msg.getID());
        assertTrue(result.isPresent());
        assertEquals(result.get().getContent(), defaultContent);
    }
}
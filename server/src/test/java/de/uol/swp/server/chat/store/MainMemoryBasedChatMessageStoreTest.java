package de.uol.swp.server.chat.store;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.dto.ChatMessageDTO;
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
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.chat.ChatMessage
 * @see de.uol.swp.server.chat.store.ChatMessageStore
 * @since 2020-12-19
 */
class MainMemoryBasedChatMessageStoreTest {

    private static final User defaultUser = new UserDTO("test", "test", "test@test.de");
    private static final User secondUser = new UserDTO("test2", "test", "test@test.de");
    private static final String defaultContent = "I am intelligent content";
    private static final String secondContent = "I am more intelligent content";
    private static final String thirdContent = "I am the most intelligent content";
    private static final String defaultLobbyName = "Am I a lobby?";
    private static final ChatMessage msgNotInStore = new ChatMessageDTO(42, defaultUser, defaultContent);
    private ChatMessageStore store;

    /**
     * Helper method run before each test case
     * <p>
     * This method instantiates a new MainMemoryBasedChatMessageStore so that one test's
     * ChatMessage objects don't interfere with another test's.
     */
    @BeforeEach
    void setUp() {
        store = new MainMemoryBasedChatMessageStore();
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method resets the store variable to null
     */
    @AfterEach
    void tearDown() {
        store = null;
    }

    /**
     * Test of the findMessage routine
     * <p>
     * Tests if a created ChatMessage object can be found with its ID when
     * createChatMessage and findMessage are called without the originLobby
     * parameter.
     * <p>
     * This test fails if no result is found, or if the result returned
     * is not the same ChatMessage that was created.
     */
    @Test
    void findMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        Optional<ChatMessage> result = store.findMessage(msg.getID());

        assertTrue(result.isPresent());
        assertEquals(msg, result.get());
    }

    /**
     * Test of the findMessage routine
     * <p>
     * Tests if a ChatMessage that doesn't exist in the global chat store can
     * be found with its ID.
     * <p>
     * This test fails if a result is found.
     *
     * @since 2021-01-04
     */
    @Test
    void findMessageNotInGlobalStoreTest() {
        Optional<ChatMessage> result = store.findMessage(msgNotInStore.getID());

        assertTrue(result.isEmpty());
    }

    /**
     * Test of the findMessage routine
     * <p>
     * Tests if a ChatMessage that doesn't exist in a lobby's chat store can
     * be found with its ID.
     * <p>
     * This test fails if a result is found.
     *
     * @since 2021-01-04
     */
    @Test
    void findMessageNotInLobbyStoreTest() {
        Optional<ChatMessage> result = store.findMessage(msgNotInStore.getID(), defaultLobbyName);

        assertTrue(result.isEmpty());
    }

    /**
     * Test of the findMessage routine
     * <p>
     * Tests if a created ChatMessage object can be found with its ID when
     * createChatMessage and findMessage are called with the originLobby
     * parameter.
     * <p>
     * This test fails if no result is found, or if the result returned
     * is not the same ChatMessage that was created.
     *
     * @since 2021-01-04
     */
    @Test
    void findMessageWithOriginLobbyTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        Optional<ChatMessage> result = store.findMessage(msg.getID(), defaultLobbyName);

        assertTrue(result.isPresent());
        assertEquals(msg, result.get());
    }

    /**
     * Test of the findMessage routine
     * <p>
     * Tests if a created ChatMessage object can be found with its ID when
     * createChatMessage and findMessage are called with the originLobby
     * parameter set to null.
     * <p>
     * This test fails if no result is found, or if the result returned
     * is not the same ChatMessage that was created.
     *
     * @since 2021-01-04
     */
    @Test
    void findMessageWithOriginLobbyIsNullTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, null);

        Optional<ChatMessage> result = store.findMessage(msg.getID(), null);

        assertTrue(result.isPresent());
        assertEquals(msg, result.get());
    }

    /**
     * Test of the findMessage routine
     * <p>
     * Tests if a created ChatMessage object can be found with its ID when
     * createChatMessage was called without the originLobby parameter but
     * findMessage was called with the originLobby parameter set to null.
     * (both of these should point to the global chat store)
     * <p>
     * This test fails if no result is found, or if the result returned
     * is not the same ChatMessage that was created.
     *
     * @since 2021-01-04
     */
    @Test
    void findMessage_onNullOriginLobbyUseOtherFindMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        Optional<ChatMessage> result = store.findMessage(msg.getID(), null);

        assertTrue(result.isPresent());
        assertEquals(msg, result.get());
    }

    /**
     * Test of the findMessage routine
     * <p>
     * Tests if a created ChatMessage object can be found with its ID when
     * createChatMessage was called with the originLobby parameter set to null
     * but findMessage was called without the originLobby parameter.
     * <p>
     * This test fails if no result is found, or if the result returned
     * is not the same ChatMessage that was created.
     *
     * @since 2021-01-04
     */
    @Test
    void findMessage_CreateWithOriginLobbyNullFoundWithSingleParamFindMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, null);

        Optional<ChatMessage> result = store.findMessage(msg.getID());

        assertTrue(result.isPresent());
        assertEquals(msg, result.get());
    }

    /**
     * Test of the findMessage routine
     * <p>
     * Tests if a ChatMessage that was created in a lobby chat can be found in
     * the global chat store with its ID.
     * <p>
     * This test fails if a result is found.
     *
     * @since 2021-01-04
     */
    @Test
    void findMessage_CreateWithOriginLobbyButUseWrongFindMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        Optional<ChatMessage> result = store.findMessage(msg.getID());

        assertTrue(result.isEmpty());
    }

    /**
     * Test of the findMessage routine
     * <p>
     * Tests if ChatMessage that was created in the global chat store can be
     * found in a lobby's chat store with its ID.
     * <p>
     * This test fails is a result is found.
     *
     * @since 2021-01-04
     */
    @Test
    void findMessage_CreateWithoutOriginLobbyButProvideLobbyNameFindMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        Optional<ChatMessage> result = store.findMessage(msg.getID(), defaultLobbyName);

        assertTrue(result.isEmpty());
    }

    /**
     * Test of the findMessage routine
     * <p>
     * Tests if a ChatMessage that was created for one lobby's chat store can
     * be found in another lobby's chat store with its ID.
     * <p>
     * This test fails if a result is found.
     *
     * @since 2021-01-04
     */
    @Test
    void findMessage_CreateWithOriginLobbyButProvideWrongLobbyNameToFindMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        Optional<ChatMessage> result = store.findMessage(msg.getID(), "other lobby name");

        assertTrue(result.isEmpty());
    }

    /**
     * Test of the getLatestMessages routine
     * <p>
     * Tests if ChatMessage objects created in the global chat store are
     * correctly returned by getLatestMessages by comparing the returned
     * List of ChatMessage objects to a local list of the created ChatMessages.
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is empty, or if it is longer than the requested amount,
     * or if it is not equal to the local list of the created ChatMessages.
     */
    @Test
    void getLatestMessagesTest() {
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
     * Test of the getLatestMessages routine
     * <p>
     * Tests if an empty global chat store correctly returns an empty list if
     * it doesn't have any stored ChatMessages.
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages isn't empty.
     *
     * @since 2021-01-04
     */
    @Test
    void getLatestMessagesEmptyGlobalHistoryTest() {
        List<ChatMessage> list = store.getLatestMessages(3);

        assertTrue(list.isEmpty());
    }

    /**
     * Test of the getLatestMessages routine
     * <p>
     * Tests if an empty lobby chat store correctly returns an empty list if it
     * doesn't have any stored ChatMessages.
     * <p>
     * This test fails if the List of ChatMessage objects returns by
     * getLatestMessages isn't empty.
     *
     * @since 2021-01-04
     */
    @Test
    void getLatestMessageEmptyLobbyHistoryTest() {
        List<ChatMessage> list = store.getLatestMessages(3, defaultLobbyName);

        assertTrue(list.isEmpty());
    }

    /**
     * Test of the getLatestMessages routine
     * <p>
     * Tests if ChatMessage objects created in a lobby's chat store are
     * correctly returned by getLatestMessages when calling it with the
     * originLobby parameter by comparing the returned List of ChatMessage
     * objects to a local list of the created ChatMessages.
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is empty, or if it is longer than the requested amount,
     * or if it is not equal to the local list of the created ChatMessages.
     *
     * @since 2021-01-04
     */
    @Test
    void getLatestMessagesWithOriginLobbyTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent, defaultLobbyName);
        ChatMessage msg3 = store.createChatMessage(defaultUser, thirdContent, defaultLobbyName);
        List<ChatMessage> list1 = new ArrayList<>();
        list1.add(msg);
        list1.add(msg2);
        list1.add(msg3);

        List<ChatMessage> list2 = store.getLatestMessages(3, defaultLobbyName);

        assertFalse(list2.isEmpty());
        assertTrue(list2.size() <= 3);
        assertEquals(list1, list2);
    }

    /**
     * Test of the getLatestMessages routine
     * <p>
     * Tests if ChatMessage objects created in the global chat store are
     * correctly returned by getLatestMessages when calling createChatMessages
     * and getLatestMessages with the originLobby parameter set to null.
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is empty, or if it is longer than the requested amount,
     * of if it is not equal to the local list of the created ChatMessages.
     *
     * @since 2021-01-04
     */
    @Test
    void getLatestMessagesWithOriginLobbyIsNullTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, null);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent, null);
        ChatMessage msg3 = store.createChatMessage(defaultUser, thirdContent, null);
        List<ChatMessage> list1 = new ArrayList<>();
        list1.add(msg);
        list1.add(msg2);
        list1.add(msg3);

        List<ChatMessage> list2 = store.getLatestMessages(3, null);

        assertFalse(list2.isEmpty());
        assertTrue(list2.size() <= 3);
        assertEquals(list1, list2);
    }

    /**
     * Test of the getLatestMessages routine
     * <p>
     * Tests if ChatMessage objects created without the originLobby parameter
     * are correctly returned when calling getLatestMessages with the
     * originLobby parameter set to null.
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is empty, or if it is longer than the requested amount,
     * or if it is not equal to the local list of the created ChatMessages.
     *
     * @since 2021-01-04
     */
    @Test
    void getLatestMessages_onNullOriginLobbyUseOtherGetLatestMessages() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent);
        ChatMessage msg3 = store.createChatMessage(defaultUser, thirdContent);
        List<ChatMessage> list1 = new ArrayList<>();
        list1.add(msg);
        list1.add(msg2);
        list1.add(msg3);

        List<ChatMessage> list2 = store.getLatestMessages(3, null);

        assertFalse(list2.isEmpty());
        assertTrue(list2.size() <= 3);
        assertEquals(list1, list2);
    }

    /**
     * Test of the getLatestMessages routine
     * <p>
     * Tests if ChatMessage objects created with the originLobby parameter set
     * to null are correctly returned when calling getLatestMessages without
     * the originLobby parameter.
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is empty, or if it is longer than the requested amount,
     * or if it is not equal to the local list of the created ChatMessages.
     *
     * @since 2021-01-04
     */
    @Test
    void getLatestMessages_CreateWithOriginLobbyNullFoundWithSingleParamGetLatestMessagesTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, null);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent, null);
        ChatMessage msg3 = store.createChatMessage(defaultUser, thirdContent, null);
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
     * Test of the getLatestMessages routine
     * <p>
     * Tests if ChatMessage objects created with the originLobby parameter
     * are not returned when calling getLatestMessages without the originLobby
     * parameter (as that points to the global chat store).
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is not empty, or if it is equal to the local list of
     * the created ChatMessages.
     *
     * @since 2021-01-04
     */
    @Test
    void getLatestMessages_CreateWithOriginLobbyButUseWrongGetLatestMessagesTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent, defaultLobbyName);
        ChatMessage msg3 = store.createChatMessage(defaultUser, thirdContent, defaultLobbyName);
        List<ChatMessage> list1 = new ArrayList<>();
        list1.add(msg);
        list1.add(msg2);
        list1.add(msg3);

        List<ChatMessage> list2 = store.getLatestMessages(3);

        assertTrue(list2.isEmpty());
        assertNotEquals(list1, list2);
    }

    /**
     * Test of the getLatestMessages routine
     */
    @Test
    void getLatestMessages_CreateWithoutOriginLobbyButProvideLobbyNameGetLatestMessagesTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent);
        ChatMessage msg3 = store.createChatMessage(defaultUser, thirdContent);
        List<ChatMessage> list1 = new ArrayList<>();
        list1.add(msg);
        list1.add(msg2);
        list1.add(msg3);

        List<ChatMessage> list2 = store.getLatestMessages(3, defaultLobbyName);

        assertTrue(list2.isEmpty());
        assertNotEquals(list1, list2);
    }

    /**
     * Test of the getLatestMessages routine
     */
    @Test
    void getLatestMessages_CreateWithOriginLobbyButProvideWrongLobbyNameToGetLatestMessagesTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent, defaultLobbyName);
        ChatMessage msg3 = store.createChatMessage(defaultUser, thirdContent, defaultLobbyName);
        List<ChatMessage> list1 = new ArrayList<>();
        list1.add(msg);
        list1.add(msg2);
        list1.add(msg3);

        List<ChatMessage> list2 = store.getLatestMessages(3, "other lobby name");

        assertTrue(list2.isEmpty());
        assertNotEquals(list1, list2);
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
     */
    @Test
    void createChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        List<ChatMessage> list = store.getLatestMessages(1);
        assertFalse(list.isEmpty());
        assertTrue(list.contains(msg));
        assertEquals(msg.getAuthor(), defaultUser);
        assertEquals(msg.getContent(), defaultContent);
    }

    /**
     * Create chat message with origin lobby test.
     */
    @Test
    void createChatMessageWithOriginLobbyTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        List<ChatMessage> list = store.getLatestMessages(1, defaultLobbyName);
        assertFalse(list.isEmpty());
        assertTrue(list.contains(msg));
        assertEquals(msg.getAuthor(), defaultUser);
        assertEquals(msg.getContent(), defaultContent);
    }

    /**
     * Create chat message with origin lobby is null test.
     */
    @Test
    void createChatMessageWithOriginLobbyIsNullTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, null);

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
     */
    @Test
    void updateChatMessageTest() {
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
     * Update chat message with origin lobby test.
     */
    @Test
    void updateChatMessageWithOriginLobbyTest() {
        store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent, defaultLobbyName);
        store.createChatMessage(defaultUser, thirdContent, defaultLobbyName);

        store.updateChatMessage(msg2.getID(), defaultContent, defaultLobbyName);

        List<ChatMessage> list = store.getLatestMessages(3, defaultLobbyName);
        assertEquals(list.get(1).getContent(), defaultContent); // the edited ChatMessage
        assertEquals(list.get(0).getContent(), defaultContent);
        assertEquals(list.get(2).getContent(), thirdContent);
    }

    /**
     * Update chat message with origin lobby is null test.
     */
    @Test
    void updateChatMessageWithOriginLobbyIsNullTest() {
        store.createChatMessage(defaultUser, defaultContent, null);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent, null);
        store.createChatMessage(defaultUser, thirdContent, null);

        store.updateChatMessage(msg2.getID(), defaultContent, null);

        List<ChatMessage> list = store.getLatestMessages(3, null);
        assertEquals(list.get(1).getContent(), defaultContent); // the edited ChatMessage
        assertEquals(list.get(0).getContent(), defaultContent);
        assertEquals(list.get(2).getContent(), thirdContent);
    }

    /**
     * Update chat message on null origin lobby use other update chat message test.
     */
    @Test
    void updateChatMessage_onNullOriginLobbyUseOtherUpdateChatMessageTest() {
        store.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent);
        store.createChatMessage(defaultUser, thirdContent);

        store.updateChatMessage(msg2.getID(), defaultContent, null);

        List<ChatMessage> list = store.getLatestMessages(3);
        assertEquals(list.get(1).getContent(), defaultContent); // the edited ChatMessage
        assertEquals(list.get(0).getContent(), defaultContent);
        assertEquals(list.get(2).getContent(), thirdContent);
    }

    /**
     * Update chat message create with origin lobby null found with single param update chat message test.
     */
    @Test
    void updateChatMessage_CreateWithOriginLobbyNullFoundWithSingleParamUpdateChatMessageTest() {
        store.createChatMessage(defaultUser, defaultContent, null);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent, null);
        store.createChatMessage(defaultUser, thirdContent, null);

        store.updateChatMessage(msg2.getID(), defaultContent);

        List<ChatMessage> list = store.getLatestMessages(3);
        assertEquals(list.get(1).getContent(), defaultContent); // the edited ChatMessage
        assertEquals(list.get(0).getContent(), defaultContent);
        assertEquals(list.get(2).getContent(), thirdContent);
    }

    /**
     * Update chat message create with origin lobby but use wrong update chat message test.
     */
    @Test
    void updateChatMessage_CreateWithOriginLobbyButUseWrongUpdateChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        assertThrows(IllegalArgumentException.class, () -> store.updateChatMessage(msg.getID(), secondContent));
    }

    /**
     * Update chat message create without origin lobby but provide lobby name update chat message test.
     */
    @Test
    void updateChatMessage_CreateWithoutOriginLobbyButProvideLobbyNameUpdateChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        assertThrows(IllegalArgumentException.class, () -> store.updateChatMessage(msg.getID(), secondContent, defaultLobbyName));
    }

    /**
     * Update chat message create with origin lobby but provide wrong lobby name to update chat message test.
     */
    @Test
    void updateChatMessage_CreateWithOriginLobbyButProvideWrongLobbyNameToUpdateChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        assertThrows(IllegalArgumentException.class, () -> store.updateChatMessage(msg.getID(), secondContent, "other lobby name"));
    }

    /**
     * Test for the removeChatMessage routine
     * <p>
     * Tests if the ChatMessage with the specified ID gets deleted from the chatMessageStore
     * when removeChatMessage is called.
     * <p>
     * This test fails when the created ChatMessage is still found.
     */
    @Test
    void removeChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);
        Optional<ChatMessage> result = store.findMessage(msg.getID());
        assertFalse(result.isEmpty());

        store.removeChatMessage(msg.getID());

        Optional<ChatMessage> result1 = store.findMessage(msg.getID());
        assertTrue(result1.isEmpty());
    }

    /**
     * Remove chat message with origin lobby test.
     */
    @Test
    void removeChatMessageWithOriginLobbyTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        Optional<ChatMessage> result = store.findMessage(msg.getID(), defaultLobbyName);
        assertFalse(result.isEmpty());

        store.removeChatMessage(msg.getID(), defaultLobbyName);

        Optional<ChatMessage> result1 = store.findMessage(msg.getID(), defaultLobbyName);
        assertTrue(result1.isEmpty());
    }

    /**
     * Remove chat message with origin lobby is null test.
     */
    @Test
    void removeChatMessageWithOriginLobbyIsNullTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, null);
        Optional<ChatMessage> result = store.findMessage(msg.getID());
        assertFalse(result.isEmpty());

        store.removeChatMessage(msg.getID(), null);

        Optional<ChatMessage> result1 = store.findMessage(msg.getID(), null);
        assertTrue(result1.isEmpty());
    }

    /**
     * Remove chat message create with origin lobby null found with single param remove chat message.
     */
    @Test
    void removeChatMessage_CreateWithOriginLobbyNullFoundWithSingleParamRemoveChatMessage() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, null);
        Optional<ChatMessage> result = store.findMessage(msg.getID());
        assertFalse(result.isEmpty());

        store.removeChatMessage(msg.getID());

        Optional<ChatMessage> result1 = store.findMessage(msg.getID());
        assertTrue(result1.isEmpty());
    }

    /**
     * Remove chat message create without origin lobby but provide lobby name remove chat message test.
     */
    @Test
    void removeChatMessage_CreateWithoutOriginLobbyButProvideLobbyNameRemoveChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);
        Optional<ChatMessage> result = store.findMessage(msg.getID());
        assertFalse(result.isEmpty());

        store.removeChatMessage(msg.getID(), defaultLobbyName);

        Optional<ChatMessage> result1 = store.findMessage(msg.getID());
        assertFalse(result1.isEmpty());
        assertEquals(msg, result1.get());
    }

    /**
     * Remove chat message create with origin lobby but provide wrong lobby name to remove chat message test.
     */
    @Test
    void removeChatMessage_CreateWithOriginLobbyButProvideWrongLobbyNameToRemoveChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        Optional<ChatMessage> result = store.findMessage(msg.getID(), defaultLobbyName);
        assertFalse(result.isEmpty());

        store.removeChatMessage(msg.getID(), "other lobby name");

        Optional<ChatMessage> result1 = store.findMessage(msg.getID(), defaultLobbyName);
        assertFalse(result1.isEmpty());
        assertEquals(msg, result1.get());
    }

    /**
     * Test for the createChatMessage routine
     * <p>
     * Tests if the createChatMessage routine correctly throws an IllegalArgumentException.
     * <p>
     * This test fails if the IllegalArgumentException isn't thrown.
     */
    @Test
    void createChatMessageEmptyAuthorTest() {
        assertThrows(IllegalArgumentException.class, () -> store.createChatMessage(null, defaultContent));
    }

    /**
     * Create chat message with origin lobby empty author test.
     */
    @Test
    void createChatMessageWithOriginLobbyEmptyAuthorTest() {
        assertThrows(IllegalArgumentException.class, () -> store.createChatMessage(null, defaultContent, defaultLobbyName));
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the content of a created ChatMessage remains unchanged when
     * the updatedContent parameter is set to null.
     * <p>
     * This test fails if the ChatMessage wasn't found, or if the content of
     * the created ChatMessage isn't the same anymore.
     */
    @Test
    void updateChatMessageEmptyContentTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        store.updateChatMessage(msg.getID(), null);

        Optional<ChatMessage> result = store.findMessage(msg.getID());
        assertTrue(result.isPresent());
        assertEquals(result.get().getContent(), defaultContent);
    }

    /**
     * Update chat message with origin lobby empty content test.
     */
    @Test
    void updateChatMessageWithOriginLobbyEmptyContentTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        store.updateChatMessage(msg.getID(), null, defaultLobbyName);

        Optional<ChatMessage> result = store.findMessage(msg.getID(), defaultLobbyName);
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
     */
    @Test
    void updateChatMessageEmptyStringContentTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        store.updateChatMessage(msg.getID(), "");

        Optional<ChatMessage> result = store.findMessage(msg.getID());
        assertTrue(result.isPresent());
        assertEquals(result.get().getContent(), defaultContent);
    }

    /**
     * Update chat message with origin lobby empty string content test.
     */
    @Test
    void updateChatMessageWithOriginLobbyEmptyStringContentTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        store.updateChatMessage(msg.getID(), "", defaultLobbyName);

        Optional<ChatMessage> result = store.findMessage(msg.getID(), defaultLobbyName);
        assertTrue(result.isPresent());
        assertEquals(result.get().getContent(), defaultContent);
    }
}

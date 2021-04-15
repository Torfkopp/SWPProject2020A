package de.uol.swp.server.chat.store;

import de.uol.swp.common.LobbyName;
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

    private static final User defaultUser = new UserDTO(1, "test", "test", "test@test.de");
    private static final User secondUser = new UserDTO(2, "test2", "test", "test@test.de");
    private static final String defaultContent = "I am intelligent content";
    private static final String secondContent = "I am more intelligent content";
    private static final String thirdContent = "I am the most intelligent content";
    private static final LobbyName defaultLobbyName = new LobbyName("Am I a lobby?");
    private static final ChatMessage msgNotInStore = new ChatMessageDTO(42, defaultUser, defaultContent);
    private ChatMessageStore store;

    /**
     * Helper method run before each test case
     * <p>
     * This method instantiates a new MainMemoryBasedChatMessageStore so that one test's
     * ChatMessage objects don't interfere with another test's.
     */
    @BeforeEach
    protected void setUp() {
        store = new MainMemoryBasedChatMessageStore();
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method resets the store variable to null
     */
    @AfterEach
    protected void tearDown() {
        store = null;
    }

    /**
     * Test for the createChatMessage routine
     * <p>
     * Tests if the createChatMessage routine correctly throws an
     * IllegalArgumentException when called with the author parameter set to
     * null.
     * <p>
     * This test fails if the IllegalArgumentException isn't thrown.
     */
    @Test
    void createChatMessageEmptyAuthorTest() {
        assertThrows(IllegalArgumentException.class, () -> store.createChatMessage(null, defaultContent));
    }

    /**
     * Test for the createChatMessage routine
     * <p>
     * Tests if a ChatMessage with a given Author and Content was created in
     * the global chat store when calling createChatMessage without the
     * originLobby parameter.
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is empty, or doesn't contain the created ChatMessage,
     * or the Author or Content of the created ChatMessage are equal to the given
     * Author and Content.
     */
    @Test
    void createChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        List<ChatMessage> list = store.getLatestMessages(1);
        assertFalse(list.isEmpty());
        assertTrue(list.contains(msg));
        assertEquals(defaultUser, msg.getAuthor());
        assertEquals(defaultContent, msg.getContent());
    }

    /**
     * Test for the createChatMessage routine
     * <p>
     * Tests if the createChatMessage routine correctly throws an
     * IllegalArgumentException when called with a lobby name and the author
     * parameter set to null.
     * <p>
     * This test fails if the IllegalArgumentException isn't thrown.
     *
     * @since 2021-01-04
     */
    @Test
    void createChatMessageWithOriginLobbyEmptyAuthorTest() {
        assertThrows(IllegalArgumentException.class,
                     () -> store.createChatMessage(null, defaultContent, defaultLobbyName));
    }

    /**
     * Test for the createChatMessage routine
     * <p>
     * Tests if a ChatMessage with a given Author and Content was created in
     * the global chat store when calling createChatMessage with the
     * originLobby parameter set to null.
     * <p>
     * This test fails if he List of ChatMessage objects returned by
     * getLatestMessages is empty, or doesn't contain the created ChatMessage,
     * or the Author or Content of the created ChatMessage are equal to the
     * given Author and Content.
     *
     * @since 2021-01-04
     */
    @Test
    void createChatMessageWithOriginLobbyIsNullTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, null);

        List<ChatMessage> list = store.getLatestMessages(1);
        assertFalse(list.isEmpty());
        assertTrue(list.contains(msg));
        assertEquals(defaultUser, msg.getAuthor());
        assertEquals(defaultContent, msg.getContent());
    }

    /**
     * Test for the createChatMessage routine
     * <p>
     * Tests if a ChatMessage with a given Author and Content was created in a
     * lobby's chat store when calling createChatMessage with the originLobby
     * parameter.
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is empty, or doesn't contain the created ChatMessage,
     * or the Author or Content of the created ChatMessage are equal to the given
     * Author and Content.
     *
     * @since 2021-01-04
     */
    @Test
    void createChatMessageWithOriginLobbyTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        List<ChatMessage> list = store.getLatestMessages(1, defaultLobbyName);
        assertFalse(list.isEmpty());
        assertTrue(list.contains(msg));
        assertEquals(defaultUser, msg.getAuthor());
        assertEquals(defaultContent, msg.getContent());
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

        Optional<ChatMessage> result = store.findMessage(msg.getID(), new LobbyName("other lobby name"));

        assertTrue(result.isEmpty());
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
     * Tests if ChatMessage objects created with the originLobby parameter are
     * not returned when calling getLatestMessages with a different lobby's name
     * as the originLobby parameter.
     * <p>
     * This test fails  if the List of ChatMessage objects returned by
     * getLatestMessages is not empty, or if it is equal to the local list of
     * the created ChatMessages.
     *
     * @since 2021-01-04
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

        List<ChatMessage> list2 = store.getLatestMessages(3, new LobbyName("other lobby name"));

        assertTrue(list2.isEmpty());
        assertNotEquals(list1, list2);
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
     * Tests if ChatMessage objects created without the originLobby parameter
     * are not returned when calling getLatestMessages with the originLobby
     * parameter set to a lobby name (as the messages are in the global chat store
     * instead).
     * <p>
     * This test fails if the List of ChatMessage objects returned by
     * getLatestMessages is not empty, or if it is equal to the local list of
     * the created ChatMessages.
     *
     * @since 2021-01-04
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
     * Test for the removeChatMessage routine
     * <p>
     * Tests if a ChatMessage with the specified ID in the global chat store
     * gets deleted from the global chat store when removeChatMessage is called
     * without the originLobby parameter.
     * <p>
     * This test fails if the created ChatMessage is still found.
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
     * Test for the removeChatMessage routine
     * <p>
     * Tests if a ChatMessage with the specified ID in the global chat store,
     * created with the originLobby parameter set to null, is removed from the
     * global chat store when removeChatMessage is called with the originLobby
     * parameter set to null.
     * <p>
     * This test fails if the created ChatMessage is still found in the lobby's
     * chat store.
     *
     * @since 2021-01-04
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
     * Test for the removeChatMessage routine
     * <p>
     * Tests if a ChatMessage with the specified ID in a lobby's chat store
     * gets deleted from the lobby's chat store when removeChatMessage is
     * called with the originLobby parameter.
     * <p>
     * This test fails if the created ChatMessage is still found in the lobby's
     * chat store.
     *
     * @since 2021-01-04
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
     * Test for the removeChatMessage routine
     * <p>
     * Tests if a ChatMessage with the specified ID in a lobby's chat store is
     * not removed from the lobby's chat store when removeChatMessage is called
     * with the originLobby parameter set to a different lobby's name.
     * <p>
     * This test fails if the created ChatMessage is not found by findMessage,
     * or if the ChatMessage found by findMessage is not equal to the created
     * one.
     *
     * @since 2021-01-04
     */
    @Test
    void removeChatMessage_CreateWithOriginLobbyButProvideWrongLobbyNameToRemoveChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        Optional<ChatMessage> result = store.findMessage(msg.getID(), defaultLobbyName);
        assertFalse(result.isEmpty());

        store.removeChatMessage(msg.getID(), new LobbyName("other lobby name"));

        Optional<ChatMessage> result1 = store.findMessage(msg.getID(), defaultLobbyName);
        assertFalse(result1.isEmpty());
        assertEquals(msg, result1.get());
    }

    /**
     * Test for the removeChatMessage routine
     * <p>
     * Tests if a ChatMessage with the specified ID in the global chat store,
     * created with the originLobby parameter set to null, is removed from the
     * global chat store when removeChatMessage is called without the
     * originLobby parameter.
     * <p>
     * This test fails if the created ChatMessage is still found in the global
     * chat store.
     *
     * @since 2021-01-04
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
     * Test for the removeChatMessage routine
     * <p>
     * Tests if a ChatMessage with the specified ID in the global chat store is
     * not removed from the global chat store when removeChatMessage is called
     * with the originLobby parameter.
     * <p>
     * This test fails if the created ChatMessage is not found by findMessage,
     * or if the ChatMessage found by findMessage is not equal to the created
     * one.
     *
     * @since 2021-01-04
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
     * Test of the removeLobbyHistory routine
     * <p>
     * Tests if a lobby's Chat History is properly removed when calling
     * removeLobbyHistory.
     * <p>
     * This test fails if the list of ChatMessage object returned by getLatestMessages
     * is empty before removeLobbyHistory was called, or if the List of ChatMessage
     * returned by getLatestMessages is not empty for the lobby whose Chat
     * History was removed by removeLobbyHistory.
     *
     * @author Phillip-André Suhr
     * @author Sven Ahrens
     * @since 2021-01-16
     */
    @Test
    void removeLobbyHistoryTest() {
        store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        List<ChatMessage> list = store.getLatestMessages(1, defaultLobbyName);
        assertFalse(list.isEmpty());

        store.removeLobbyHistory(defaultLobbyName);

        List<ChatMessage> list2 = store.getLatestMessages(1, defaultLobbyName);
        assertTrue(list2.isEmpty());
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the content of a created ChatMessage in the global chat store
     * remains unchanged when the updatedContent parameter is set to null.
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
        assertEquals(defaultContent, result.get().getContent());
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the content of a created ChatMessage in the global chat store
     * remains unchanged when the updatedContent parameter is set to the empty
     * String.
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
        assertEquals(defaultContent, result.get().getContent());
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the contents of the ChatMessage with the specified ID in the
     * global chat store are updated when calling updateChatMessage without the
     * originLobby parameter.
     * <p>
     * This test fails if the contents of the specified ChatMessage are not
     * changed to the given content, or if any contents from other ChatMessages get
     * changed.
     */
    @Test
    void updateChatMessageTest() {
        store.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent);
        store.createChatMessage(defaultUser, thirdContent);

        store.updateChatMessage(msg2.getID(), defaultContent);

        List<ChatMessage> list = store.getLatestMessages(3);
        assertEquals(defaultContent, list.get(1).getContent()); // the edited ChatMessage
        assertEquals(defaultContent, list.get(0).getContent());
        assertEquals(thirdContent, list.get(2).getContent());
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the content of a created ChatMessage in a lobby's chat store
     * remains when the updatedContent parameter is set to null.
     * <p>
     * This test fails if the ChatMessage wasn't found, or if the content of
     * the created ChatMessage isn't the same anymore.
     *
     * @since 2021-01-04
     */
    @Test
    void updateChatMessageWithOriginLobbyEmptyContentTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        store.updateChatMessage(msg.getID(), null, defaultLobbyName);

        Optional<ChatMessage> result = store.findMessage(msg.getID(), defaultLobbyName);
        assertTrue(result.isPresent());
        assertEquals(defaultContent, result.get().getContent());
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the content of a created ChatMessage in a lobby's chat store
     * remains unchanged when the updatedContent parameter is set to the empty
     * String.
     * <p>
     * This test fails if the ChatMessage wasn't found, or if the content of
     * the created ChatMessage isn't the same anymore.
     *
     * @since 2021-01-04
     */
    @Test
    void updateChatMessageWithOriginLobbyEmptyStringContentTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        store.updateChatMessage(msg.getID(), "", defaultLobbyName);

        Optional<ChatMessage> result = store.findMessage(msg.getID(), defaultLobbyName);
        assertTrue(result.isPresent());
        assertEquals(defaultContent, result.get().getContent());
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the contents of the ChatMessage with the specified ID in the
     * global chat store are updated when calling updateChatMessage with the
     * originLobby parameter set to null.
     * <p>
     * This test fails if the contents of the specified ChatMessage are not
     * changed, or if any contents from other ChatMessages get changed.
     *
     * @since 2021-01-04
     */
    @Test
    void updateChatMessageWithOriginLobbyIsNullTest() {
        store.createChatMessage(defaultUser, defaultContent, null);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent, null);
        store.createChatMessage(defaultUser, thirdContent, null);

        store.updateChatMessage(msg2.getID(), defaultContent, null);

        List<ChatMessage> list = store.getLatestMessages(3, null);
        assertEquals(defaultContent, list.get(1).getContent()); // the edited ChatMessage
        assertEquals(defaultContent, list.get(0).getContent());
        assertEquals(thirdContent, list.get(2).getContent());
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the contents of the ChatMessage with the specified ID in a
     * lobby's chat store are updated when calling updateChatMessage with the
     * originLobby parameter.
     * <p>
     * This test fails if the contents of the specified ChatMessage are not
     * changed to the given content, or if any contents from other ChatMessages
     * get changed.
     *
     * @since 2021-01-04
     */
    @Test
    void updateChatMessageWithOriginLobbyTest() {
        store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent, defaultLobbyName);
        store.createChatMessage(defaultUser, thirdContent, defaultLobbyName);

        store.updateChatMessage(msg2.getID(), defaultContent, defaultLobbyName);

        List<ChatMessage> list = store.getLatestMessages(3, defaultLobbyName);
        assertEquals(defaultContent, list.get(1).getContent()); // the edited ChatMessage
        assertEquals(defaultContent, list.get(0).getContent());
        assertEquals(thirdContent, list.get(2).getContent());
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if updating a ChatMessage with a specified ID in a lobby's chat
     * store by calling updateChatMessage with the originLobby parameter set to
     * a different lobby name throws an IllegalArgumentException.
     * <p>
     * This test fails if the IllegalArgumentException is not thrown.
     *
     * @since 2021-01-04
     */
    @Test
    void updateChatMessage_CreateWithOriginLobbyButProvideWrongLobbyNameToUpdateChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        assertThrows(IllegalArgumentException.class,
                     () -> store.updateChatMessage(msg.getID(), secondContent, new LobbyName("other lobby name")));
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if updating a ChatMessage with a specified ID in a lobby's chat
     * store by calling updateChatMessage without the originLobby parameter
     * causes an IllegalArgumentException to be thrown.
     * <p>
     * This test fails if the IllegalArgumentException is not thrown.
     *
     * @since 2021-01-04
     */
    @Test
    void updateChatMessage_CreateWithOriginLobbyButUseWrongUpdateChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        assertThrows(IllegalArgumentException.class, () -> store.updateChatMessage(msg.getID(), secondContent));
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the contents of the ChatMessage with the specified ID in the
     * global chat store, created with the originLobby parameter set to null,
     * are updated when calling updateChatMessage without the originLobby
     * parameter.
     * <p>
     * This test fails if the contents of the specified ChatMessage are not
     * changed, or if any contents from other ChatMessages get changed.
     *
     * @since 2021-01-04
     */
    @Test
    void updateChatMessage_CreateWithOriginLobbyNullFoundWithSingleParamUpdateChatMessageTest() {
        store.createChatMessage(defaultUser, defaultContent, null);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent, null);
        store.createChatMessage(defaultUser, thirdContent, null);

        store.updateChatMessage(msg2.getID(), defaultContent);

        List<ChatMessage> list = store.getLatestMessages(3);
        assertEquals(defaultContent, list.get(1).getContent()); // the edited ChatMessage
        assertEquals(defaultContent, list.get(0).getContent());
        assertEquals(thirdContent, list.get(2).getContent());
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if updating a ChatMessage with a specified ID in the global chat
     * store by calling updateChatMessage with the originLobby parameter set to
     * a lobby name throws an IllegalArgumentException.
     * <p>
     * This test fails if the IllegalArgumentException is not thrown.
     *
     * @since 2021-01-04
     */
    @Test
    void updateChatMessage_CreateWithoutOriginLobbyButProvideLobbyNameUpdateChatMessageTest() {
        ChatMessage msg = store.createChatMessage(defaultUser, defaultContent);

        assertThrows(IllegalArgumentException.class,
                     () -> store.updateChatMessage(msg.getID(), secondContent, defaultLobbyName));
    }

    /**
     * Test for the updateChatMessage routine
     * <p>
     * Tests if the contents of the ChatMessage with the specified ID in the
     * global chat store, created without the originLobby parameter, are
     * updated when calling updateChatMessage with the originLobby parameter
     * set to null.
     * <p>
     * This test fails if the contents of the specified ChatMessage are not
     * changed, or if any contents from other ChatMessages get changed.
     *
     * @since 2021-01-04
     */
    @Test
    void updateChatMessage_onNullOriginLobbyUseOtherUpdateChatMessageTest() {
        store.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = store.createChatMessage(secondUser, secondContent);
        store.createChatMessage(defaultUser, thirdContent);

        store.updateChatMessage(msg2.getID(), defaultContent, null);

        List<ChatMessage> list = store.getLatestMessages(3);
        assertEquals(defaultContent, list.get(1).getContent()); // the edited ChatMessage
        assertEquals(defaultContent, list.get(0).getContent());
        assertEquals(thirdContent, list.get(2).getContent());
    }
}

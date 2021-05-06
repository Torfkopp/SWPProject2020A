package de.uol.swp.server.chat;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.chat.store.IChatMessageStore;
import de.uol.swp.server.chat.store.MainMemoryBasedChatMessageStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test of the ChatManagement class that communicates with the ChatMessageStore
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.server.chat.IChatManagement
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.chat.ChatMessage
 * @see de.uol.swp.server.chat.store.IChatMessageStore
 * @since 2020-12-19
 */
class ChatManagementTest {

    private static final User defaultUser = new UserDTO(1, "test", "test", "test@test.de");
    private static final String defaultContent = "I am intelligent content";
    private static final String secondContent = "I am new, more intelligent content";
    private static final LobbyName defaultLobbyName = new LobbyName("I might be a lobby, or I might not be");
    private static final LobbyName secondLobbyName = new LobbyName("I don't think I'm a lobby");
    private static ChatManagement chatManagement;
    private static IChatMessageStore chatMessageStore;

    /**
     * Helper method run before each test case
     * <p>
     * This method instantiates a new ChatMessageStore and a new ChatManagement so that
     * one test's ChatMessage objects don't interfere with another test's
     */
    @BeforeEach
    protected void setUp() {
        chatMessageStore = new MainMemoryBasedChatMessageStore();
        chatManagement = new ChatManagement(chatMessageStore);
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method resets the chatManagement and chatMessageStore variables to null
     */
    @AfterEach
    protected void tearDown() {
        chatManagement = null;
        chatMessageStore = null;
    }

    /**
     * Test of the createChatMessage routine
     * <p>
     * Tests if a ChatMessage was created by checking if the return of getLatestMessages for defaultLobby
     * contains the created ChatMessage object.
     * <p>
     * This test fails if the lists of ChatMessage objects returned by getLatestMessages for the MainMenu or secondLobby
     * aren't empty or if the list of ChatMessage objects returned by getLatestMessages for defaultLobby is empty
     * or it doesn't contain the newly created ChatMessage.
     *
     * @since 2021-01-03
     */
    @Test
    void createChatMessageForLobbyTest() {
        ChatMessage msg = chatManagement.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        List<ChatMessage> listFirstLobby = chatManagement.getLatestMessages(1, defaultLobbyName);
        List<ChatMessage> listSecondLobby = chatManagement.getLatestMessages(1, secondLobbyName);
        List<ChatMessage> listMainMenu = chatManagement.getLatestMessages(1);

        assertFalse(listFirstLobby.isEmpty());
        assertTrue(listSecondLobby.isEmpty());
        assertTrue(listMainMenu.isEmpty());

        assertTrue(listFirstLobby.contains(msg));
        assertTrue(true);
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when createChatMessage
     * for defaultLobby is called with null as author.
     *
     * @since 2021-01-04
     */
    @Test
    void createChatMessageForLobbyWithEmptyAuthorTest() {
        assertThrows(ChatManagementException.class,
                     () -> chatManagement.createChatMessage(null, defaultContent, defaultLobbyName));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when createChatMessage for defaultLobby
     * is called with null as content.
     *
     * @since 2021-01-04
     */
    @Test
    void createChatMessageForLobbyWithEmptyContentTest() {
        assertThrows(ChatManagementException.class,
                     () -> chatManagement.createChatMessage(defaultUser, null, defaultLobbyName));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when createChatMessage
     * for defaultLobby is called with an empty content.
     *
     * @since 2021-01-04
     */
    @Test
    void createChatMessageForLobbyWithEmptyStringContentTest() {
        assertThrows(ChatManagementException.class,
                     () -> chatManagement.createChatMessage(defaultUser, "", defaultLobbyName));
    }

    /**
     * Test of the createChatMessage routine
     * <p>
     * Tests if a ChatMessage was created by checking if the return of getLatestMessages for MainMenu
     * contains the created ChatMessage object.
     * <p>
     * This test fails if the lists of ChatMessage objects returned by getLatestMessages for defaultLobby or secondLobby
     * aren't empty, or if the list of ChatMessage objects returned by getLatestMessages for the MainMenu is empty.
     * or it doesn't contain the newly created ChatMessage.
     *
     * @since 2021-01-03
     */
    @Test
    void createChatMessageTest() {
        ChatMessage msg = chatManagement.createChatMessage(defaultUser, defaultContent);

        List<ChatMessage> listFirstLobby = chatManagement.getLatestMessages(1, defaultLobbyName);
        List<ChatMessage> listSecondLobby = chatManagement.getLatestMessages(1, secondLobbyName);
        List<ChatMessage> listMainMenu = chatManagement.getLatestMessages(1);

        assertTrue(listFirstLobby.isEmpty());
        assertTrue(listSecondLobby.isEmpty());
        assertFalse(listMainMenu.isEmpty());

        assertTrue(listMainMenu.contains(msg));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when createChatMessage
     * is called with null as its author.
     */
    @Test
    void createChatMessageWithEmptyAuthorTest() {
        assertThrows(ChatManagementException.class, () -> chatManagement.createChatMessage(null, defaultContent));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when createChatMessage
     * is called with null as content.
     */
    @Test
    void createChatMessageWithEmptyContentTest() {
        assertThrows(ChatManagementException.class, () -> chatManagement.createChatMessage(defaultUser, null));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when createChatMessage
     * is called with an empty content.
     */
    @Test
    void createChatMessageWithEmptyStringContentTest() {
        assertThrows(ChatManagementException.class, () -> chatManagement.createChatMessage(defaultUser, ""));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when dropChatMessage
     * for defaultLobby is called with an ID not found in the chatMessageStore.
     *
     * @since 2021-01-04
     */
    @Test
    void deleteChatMessageForLobbyWithUnknownIdTest() {
        assertThrows(ChatManagementException.class, () -> chatManagement.dropChatMessage(42, defaultLobbyName));
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when dropChatMessage
     * is called with an ID not found in the chatMessageStore.
     */
    @Test
    void deleteChatMessageWithUnknownIdTest() {
        assertThrows(ChatManagementException.class, () -> chatManagement.dropChatMessage(42));
    }

    /**
     * Test of the dropChatMessage routine
     * <p>
     * Tests if a specified ChatMessage in a lobby gets deleted from the chatMessageStore
     * and if no other ChatMessage gets deleted when dropChatMessage is called.
     * <p>
     * This test fails if the list of ChatMessage object returned by getLatestMessages for defaultLobby
     * is empty, or if the list doesn't contain the created test ChatMessage, or if
     * after calling dropChatMessage, the List of ChatMessage returned by
     * getLatestMessages still contains the supposedly deleted ChatMessage.
     *
     * @since 2021-01-04
     */
    @Test
    void dropChatMessageForLobbyTest() {
        ChatMessage msg = chatMessageStore.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        List<ChatMessage> list = chatMessageStore.getLatestMessages(1, defaultLobbyName);
        assertFalse(list.isEmpty());
        assertTrue(list.contains(msg));

        chatManagement.dropChatMessage(msg.getID(), defaultLobbyName);

        list = chatMessageStore.getLatestMessages(1, defaultLobbyName);
        assertFalse(list.contains(msg));
    }

    /**
     * Test of the dropChatMessage routine
     * <p>
     * Tests if a specified ChatMessage gets deleted from the chatMessageStore
     * and no other ChatMessage gets deleted when dropChatMessage is called.
     * <p>
     * This test fails if the list of ChatMessage object returned by getLatestMessages
     * is empty, or if the list doesn't contain the created test ChatMessage, or if
     * after calling dropChatMessage, the List of ChatMessage returned by
     * getLatestMessages still contains the supposedly deleted ChatMessage.
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
     * Test of the dropLobbyHistory routine
     * <p>
     * Tests if a lobby's entire Chat History gets properly dropped from the
     * chatMessageStore.
     * <p>
     * This test fails if the list of ChatMessage object returned by
     * getLatestMessages for defaultLobby is empty before calling dropLobbyHistory,
     * or if the list of ChatMessage objects returned by getLatestMessages for defaultLobby
     * is not empty after calling dropLobbyHistory.
     *
     * @author Phillip-André Suhr
     * @author Sven Ahrens
     * @since 2021-01-16
     */
    @Test
    void dropLobbyHistoryTest() {
        chatMessageStore.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        List<ChatMessage> list = chatMessageStore.getLatestMessages(1, defaultLobbyName);
        assertFalse(list.isEmpty());

        chatMessageStore.removeLobbyHistory(defaultLobbyName);

        List<ChatMessage> list1 = chatMessageStore.getLatestMessages(1, defaultLobbyName);
        assertTrue(list1.isEmpty());
    }

    /**
     * Test of the findChatMessage routine
     * <p>
     * Tests if a specified ChatMessage in a specified lobby will be found by
     * the findChatMessage routine.
     * <p>
     * This test fails if the Optional returned by findChatMessage is empty,
     * or if any of the attributes of the found ChatMessage differ from the
     * parameters provided on creation.
     *
     * @author Maximilian Lindner
     * @author Phillip-André Suhr
     * @since 2021-02-06
     */
    @Test
    void findChatMessageInLobbyTest() {
        ChatMessage chatMessage = chatMessageStore.createChatMessage(defaultUser, defaultContent, defaultLobbyName);

        Optional<ChatMessage> foundMessage = chatManagement.findChatMessage(chatMessage.getID(), defaultLobbyName);

        assertTrue(foundMessage.isPresent());
        assertEquals(chatMessage.getID(), foundMessage.get().getID());
        assertEquals(defaultUser, foundMessage.get().getAuthor());
        assertEquals(defaultContent, foundMessage.get().getContent());
    }

    /**
     * Test of the findChatMessage routine
     * <p>
     * Tests if a specified ChatMessage will be found by the findChatMessage
     * routine.
     * <p>
     * This test fails if the Optional returned by findChatMessage is empty,
     * or if any of the attributes of the found ChatMessage differ from the
     * parameters provided on creation.
     *
     * @author Maximilian Lindner
     * @author Phillip-André Suhr
     * @since 2021-02-06
     */
    @Test
    void findChatMessageTest() {
        ChatMessage chatMessage = chatMessageStore.createChatMessage(defaultUser, defaultContent);

        Optional<ChatMessage> foundMessage = chatManagement.findChatMessage(chatMessage.getID());

        assertTrue(foundMessage.isPresent());
        assertEquals(chatMessage.getID(), foundMessage.get().getID());
        assertEquals(defaultUser, foundMessage.get().getAuthor());
        assertEquals(defaultContent, foundMessage.get().getContent());
    }

    /**
     * Test of the getLatestMessages routine
     * <p>
     * Tests if the local list of the created ChatMessages
     * is equal to a list of ChatMessage objects returned by getLatestMessages for defaultLobby
     * and not equal to lists of ChatMessage objects returned by getLatestMessages for the MainMenu and secondLobby.
     * <p>
     * This test fails if the list created ChatMessages is equal to the list of ChatMessage objects returned by
     * getLatestMessages for the MainMenu or secondLobby, or if it is not equal to the list of ChatMessage objects
     * returned by getLatestMessage for defaultLobby.
     *
     * @since 2021-01-03
     */
    @Test
    void getLatestMessagesForLobbyTest() {
        ChatMessage msg1 = chatMessageStore.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        ChatMessage msg2 = chatMessageStore.createChatMessage(defaultUser, secondContent, defaultLobbyName);
        List<ChatMessage> newMessages = new ArrayList<>();
        newMessages.add(msg1);
        newMessages.add(msg2);

        List<ChatMessage> listFirstLobby = chatManagement.getLatestMessages(2, defaultLobbyName);
        List<ChatMessage> listSecondLobby = chatManagement.getLatestMessages(2, secondLobbyName);
        List<ChatMessage> listMainMenu = chatManagement.getLatestMessages(2);

        assertEquals(newMessages, listFirstLobby);
        assertNotEquals(newMessages, listSecondLobby);
        assertNotEquals(newMessages, listMainMenu);
    }

    /**
     * Test of the getLatestMessages routine
     * <p>
     * Tests if the local list of the created ChatMessages
     * is equal to a list of ChatMessage objects returned by getLatestMessages for the MainMenu
     * and not equal to lists of ChatMessage objects returned by getLatestMessages for defaultLobby and secondLobby.
     * <p>
     * This test fails if the created list of ChatMessages is equal to the list of ChatMessage objects returned by
     * getLatestMessages for the defaultLobby or the second lobby, or if it is not equal to the list of ChatMessage objects
     * returned by getLatestMessage for the MainMenuChat.
     *
     * @since 2021-01-03
     */
    @Test
    void getLatestMessagesTest() {
        ChatMessage msg1 = chatMessageStore.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = chatMessageStore.createChatMessage(defaultUser, secondContent);
        List<ChatMessage> newMessages = new ArrayList<>();
        newMessages.add(msg1);
        newMessages.add(msg2);

        List<ChatMessage> listFirstLobby = chatManagement.getLatestMessages(2, defaultLobbyName);
        List<ChatMessage> listSecondLobby = chatManagement.getLatestMessages(2, secondLobbyName);
        List<ChatMessage> listMainMenu = chatManagement.getLatestMessages(2);

        assertNotEquals(newMessages, listFirstLobby);
        assertNotEquals(newMessages, listSecondLobby);
        assertEquals(newMessages, listMainMenu);
    }

    /**
     * Test of the updateChatMessage routine
     * <p>
     * Tests if a specified ChatMessage in a lobby gets updated with the provided content
     * and no other ChatMessage gets changed when updateChatMessage is called.
     * <p>
     * This test fails if the list of ChatMessage objects returned by getLatestMessages for defaultLobby
     * is empty, or if one of the ChatMessages was inappropriately updated.
     *
     * @since 2021-01-04
     */
    @Test
    void updateChatMessageForLobbyTest() {
        chatMessageStore.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        ChatMessage msg2 = chatMessageStore.createChatMessage(defaultUser, defaultContent, defaultLobbyName);
        chatManagement.updateChatMessage(msg2.getID(), secondContent, defaultLobbyName);

        List<ChatMessage> listFirstLobby = chatManagement.getLatestMessages(2, defaultLobbyName);

        assertFalse(listFirstLobby.isEmpty());

        assertEquals(defaultContent, listFirstLobby.get(0).getContent());
        assertEquals(secondContent, listFirstLobby.get(1).getContent());
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when updateChatMessage
     * for defaultLobby is called with an ID not found in the chatMessageStore.
     *
     * @since 2021-01-04
     */
    @Test
    void updateChatMessageForLobbyWithUnknownIdTest() {
        assertThrows(ChatManagementException.class,
                     () -> chatManagement.updateChatMessage(1, secondContent, defaultLobbyName));
    }

    /**
     * Test of the updateChatMessage routine
     * <p>
     * Tests if a specified ChatMessage gets updated with the provided content
     * and no other ChatMessage gets changed when updateChatMessage is called.
     * <p>
     * This test fails if the List of ChatMessage objects returned by getLatestMessages
     * is empty or if one of the ChatMessages was inappropriately updated.
     */
    @Test
    void updateChatMessageTest() {
        chatMessageStore.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = chatMessageStore.createChatMessage(defaultUser, defaultContent);
        chatManagement.updateChatMessage(msg2.getID(), secondContent);

        List<ChatMessage> listMainMenu = chatManagement.getLatestMessages(2);

        assertFalse(listMainMenu.isEmpty());

        assertEquals(defaultContent, listMainMenu.get(0).getContent());
        assertEquals(secondContent, listMainMenu.get(1).getContent());
    }

    /**
     * Tests if the ChatManagement throws a ChatManagementException when updateChatMessage
     * is called with an ID not found in the chatMessageStore.
     */
    @Test
    void updateChatMessageWithUnknownIdTest() {
        assertThrows(ChatManagementException.class, () -> chatManagement.updateChatMessage(1, secondContent));
    }
}

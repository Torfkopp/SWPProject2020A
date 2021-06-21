package de.uol.swp.server.chat;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.request.DeleteChatMessageRequest;
import de.uol.swp.common.chat.request.EditChatMessageRequest;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.lobby.LobbyName;
import de.uol.swp.common.lobby.message.LobbyDeletedMessage;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.chat.store.MainMemoryBasedChatMessageStore;
import de.uol.swp.server.lobby.ILobbyManagement;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.sessionmanagement.SessionManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test of the class used to handle the requests sent by the client regarding the chat
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.chat.ChatMessage
 * @see de.uol.swp.server.chat.IChatManagement
 * @since 2020-12-19
 */
@SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
class ChatServiceTest {

    private static final String defaultContent = "I am intelligent content";
    private static final String secondContent = "I am new, even more intelligent content";
    private static final User defaultUser = new UserDTO(1, "test", "test", "test@test.de");
    private static final User secondUser = new UserDTO(2, "test2", "test2", "test2@test.de");
    private static final LobbyName defaultLobby = new LobbyName("I am an intelligent lobby");

    final EventBus bus = new EventBus();
    final CountDownLatch lock = new CountDownLatch(1);
    private final UserStore userStore = new MainMemoryBasedUserStore();
    private final ILobbyManagement lobbyManagement = new LobbyManagement();
    private final SessionManagement sessionManagement = new SessionManagement();
    private final LobbyService lobbyService = new LobbyService(lobbyManagement, sessionManagement, bus);
    private ChatManagement chatManagement;
    private ChatService chatService;

    /**
     * Helper method run before each test case
     * <p>
     * This method instantiates a new ChatManagement and a new ChatService so that
     * one test's ChatMessage objects don't interfere with another test's
     */
    @BeforeEach
    protected void setUp() {
        chatManagement = new ChatManagement(new MainMemoryBasedChatMessageStore());
        boolean commandsAllowed = false;
        CommandChatService commandChatService = null;
        chatService = new ChatService(bus, chatManagement, lobbyManagement, null, lobbyService, false);
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method resets the chatManagement and chatService variables to null
     */
    @AfterEach
    protected void tearDown() {
        chatManagement = null;
        chatService = null;
    }

    /**
     * Tests if the ChatService properly handles the DeleteChatMessageRequest.
     * <p>
     * A new DeleteChatMessageRequest with a lobby name is posted onto the bus and it is checked
     * if only the ChatMessage with the specified ID was deleted.
     * <p>
     * This test fails when the list of ChatMessages returned by the chatManagement is
     * empty or when the requested ChatMessage still exists.
     *
     * @throws java.lang.InterruptedException Thrown by lock.await()
     * @since 2021-01-04
     */
    @Test
    void deleteChatMessageInLobbyTest() throws InterruptedException {
        ChatMessage msg1 = chatManagement.createChatMessage(defaultUser, defaultContent, defaultLobby);
        ChatMessage msg2 = chatManagement.createChatMessage(defaultUser, secondContent, defaultLobby);
        final Message req = new DeleteChatMessageRequest(msg2.getID(), defaultUser, defaultLobby);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(2, defaultLobby);
        assertEquals(1, latestMessages.size());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(msg1.getID(), latestMessage.getID());
        assertEquals(defaultContent, latestMessage.getContent());
    }

    /**
     * Tests if the ChatService properly handles the DeleteChatMessageRequest.
     * <p>
     * A new DeleteChatMessageRequest with a different User than the
     * ChatMessage author is posted onto the EventBus and it is checked
     * that the ChatMessage was not deleted in a specified lobby.
     * <p>
     * This test fails when the list of ChatMessages returned by the
     * chatManagement is not of length 1, or when any of the attributes of the
     * ChatMessage differ from the parameters provided on creation.
     *
     * @throws java.lang.InterruptedException the interrupted exception
     * @author Maximilian Lindner
     * @author Phillip-André Suhr
     * @since 2021-02-06
     */
    @Test
    void deleteChatMessageInLobbyWrongUserTest() throws InterruptedException {
        ChatMessage msg1 = chatManagement.createChatMessage(defaultUser, defaultContent, defaultLobby);
        final Message req = new DeleteChatMessageRequest(msg1.getID(), secondUser, defaultLobby);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1, defaultLobby);
        assertEquals(1, latestMessages.size());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(msg1.getID(), latestMessage.getID());
        assertEquals(defaultContent, latestMessage.getContent());
    }

    /**
     * Tests if the ChatService properly handles the DeleteChatMessageRequest.
     * <p>
     * A new DeleteChatMessageRequest is posted onto the EventBus and it is checked
     * if only the ChatMessage with the specified ID was deleted.
     * <p>
     * This test fails when the list of ChatMessages returned by the chatManagement is
     * empty or when the requested ChatMessage still exists.
     *
     * @throws java.lang.InterruptedException Thrown by lock.await()
     */
    @Test
    void deleteChatMessageTest() throws InterruptedException {
        ChatMessage msg1 = chatManagement.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = chatManagement.createChatMessage(defaultUser, secondContent);
        final Message req = new DeleteChatMessageRequest(msg2.getID(), defaultUser);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(2);
        assertEquals(1, latestMessages.size());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(msg1.getID(), latestMessage.getID());
        assertEquals(defaultContent, latestMessage.getContent());
    }

    /**
     * Tests if the ChatService properly handles the DeleteChatMessageRequest.
     * <p>
     * A new DeleteChatMessageRequest with a different User than the
     * ChatMessage author is posted onto the EventBus and it is checked
     * that the ChatMessage was not deleted.
     * <p>
     * This test fails when the list of ChatMessages returned by the
     * chatManagement is not of length 1, or when any of the attributes of the
     * ChatMessage differ from the parameters provided on creation.
     *
     * @throws InterruptedException the interrupted exception
     * @author Maximilian Lindner
     * @author Phillip-André Suhr
     * @since 2021-02-06
     */
    @Test
    void deleteChatMessageWrongUserTest() throws InterruptedException {
        ChatMessage msg1 = chatManagement.createChatMessage(defaultUser, defaultContent);
        final Message req = new DeleteChatMessageRequest(msg1.getID(), secondUser);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1);
        assertEquals(1, latestMessages.size());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(msg1.getID(), latestMessage.getID());
        assertEquals(defaultContent, latestMessage.getContent());
    }

    /**
     * Tests if the ChatService properly handles the EditChatMessageRequest.
     * <p>
     * An EditChatMessageRequest with a lobby name is posted onto the EventBus and it is checked
     * if the requested ChatMessage was edited
     * <p>
     * This test fails when the list of ChatMessages returned by the chatManagement is
     * empty or when the requested ChatMessage wasn't edited.
     *
     * @throws java.lang.InterruptedException Thrown by lock.await()
     * @since 2021-01-04
     */
    @Test
    void editChatMessageInLobbyTest() throws InterruptedException {
        ChatMessage msg = chatManagement.createChatMessage(defaultUser, defaultContent, defaultLobby);
        final Message req = new EditChatMessageRequest(msg.getID(), secondContent, defaultUser, defaultLobby);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1, defaultLobby);
        assertFalse(latestMessages.isEmpty());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(msg.getID(), latestMessage.getID());
        assertEquals(secondContent, latestMessage.getContent());
    }

    /**
     * Tests if the ChatService properly handles the DeleteChatMessageRequest.
     * <p>
     * A new EditChatMessageRequest with a different User than the
     * ChatMessage author is posted onto the EventBus and it is checked
     * that the ChatMessage was not edited in a specified lobby.
     * <p>
     * This test fails when the list of ChatMessages returned by the
     * chatManagement is not of length 1, or when any of the attributes of the
     * ChatMessage differ from the parameters provided on creation.
     *
     * @throws java.lang.InterruptedException the interrupted exception
     * @author Maximilian Lindner
     * @author Phillip-André Suhr
     * @since 2021-02-06
     */
    @Test
    void editChatMessageInLobbyWrongUserTest() throws InterruptedException {
        ChatMessage msg1 = chatManagement.createChatMessage(defaultUser, defaultContent, defaultLobby);
        final Message req = new EditChatMessageRequest(msg1.getID(), secondContent, secondUser, defaultLobby);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1, defaultLobby);
        assertEquals(1, latestMessages.size());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(msg1.getID(), latestMessage.getID());
        assertEquals(defaultContent, latestMessage.getContent());
    }

    /**
     * Tests if the ChatService properly handles the EditChatMessageRequest.
     * <p>
     * An EditChatMessageRequest is posted onto the EventBus. It is checked
     * if the requested ChatMessage was edited.
     * <p>
     * This test fails when the list of ChatMessages returned by the chatManagement is
     * empty or when the requested ChatMessage wasn't edited.
     *
     * @throws java.lang.InterruptedException Thrown by lock.await()
     */
    @Test
    void editChatMessageTest() throws InterruptedException {
        ChatMessage msg = chatManagement.createChatMessage(defaultUser, defaultContent);
        final Message req = new EditChatMessageRequest(msg.getID(), secondContent, defaultUser);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1);
        assertFalse(latestMessages.isEmpty());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(msg.getID(), latestMessage.getID());
        assertEquals(secondContent, latestMessage.getContent());
    }

    /**
     * Tests if the ChatService properly handles the DeleteChatMessageRequest.
     * <p>
     * A new EditChatMessageRequest with a different User than the
     * ChatMessage author is posted onto the EventBus and it is checked
     * that the ChatMessage was not edited.
     * <p>
     * This test fails when the list of ChatMessages returned by the
     * chatManagement is not of length 1, or when any of the attributes of the
     * ChatMessage differ from the parameters provided on creation.
     *
     * @throws java.lang.InterruptedException the interrupted exception
     * @author Maximilian Lindner
     * @author Phillip-André Suhr
     * @since 2021-02-06
     */
    @Test
    void editChatMessageWrongUserTest() throws InterruptedException {
        ChatMessage msg1 = chatManagement.createChatMessage(defaultUser, defaultContent);
        final Message req = new EditChatMessageRequest(msg1.getID(), secondContent, secondUser);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1);
        assertEquals(1, latestMessages.size());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(msg1.getID(), latestMessage.getID());
        assertEquals(defaultContent, latestMessage.getContent());
    }

    /**
     * Tests if the ChatService properly handles the NewChatMessageRequest.
     * <p>
     * A NewChatMessageRequest with a lobby name is posted on the bus and it is checked if the
     * requested ChatMessage was created
     * <p>
     * This test fails when the list of ChatMessages returned by the chatManagement is
     * empty or when the requested ChatMessage wasn't created with the requested attributes.
     *
     * @throws java.lang.InterruptedException Thrown by lock.await()
     * @since 2021-01-04
     */
    @Test
    void newChatMessageInLobbyTest() throws InterruptedException {
        final Message req = new NewChatMessageRequest(defaultUser, defaultContent, defaultLobby);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1, defaultLobby);
        assertFalse(latestMessages.isEmpty());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(defaultUser, latestMessage.getAuthor());
        assertEquals(defaultContent, latestMessage.getContent());
    }

    /**
     * Tests if the ChatService properly handles the NewChatMessageRequest.
     * <p>
     * A NewChatMessageRequest is posted onto the EventBus. It is checked if the
     * requested ChatMessage was created
     * <p>
     * This test fails when the list of ChatMessages returned by the chatManagement is
     * empty or when the requested ChatMessage wasn't created with the requested attributes.
     *
     * @throws java.lang.InterruptedException Thrown by lock.await()
     */
    @Test
    void newChatMessageTest() throws InterruptedException {
        final Message req = new NewChatMessageRequest(defaultUser, defaultContent);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1);
        assertFalse(latestMessages.isEmpty());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(defaultUser, latestMessage.getAuthor());
        assertEquals(defaultContent, latestMessage.getContent());
    }

    /**
     * Tests if the ChatService properly handles the LobbyDeletedMessage.
     * <p>
     * A new LobbyDeletedMessage with a lobby name is posted onto the EventBus
     * and it is checked if the Chat History of the specified lobby was dropped
     * entirely.
     * <p>
     * This test fails if the list of ChatMessage objects returned by the
     * chatManagement is empty before the LobbyDeletedMessage was posted, or
     * if it is empty after the LobbyDeletedMessage was posted.
     *
     * @throws java.lang.InterruptedException Thrown by lock.await()
     * @author Phillip-André Suhr
     * @author Sven Ahrens
     * @since 2021-01-16
     */
    @Test
    void onLobbyDeletedMessageTest() throws InterruptedException {
        chatManagement.createChatMessage(defaultUser, defaultContent, defaultLobby);
        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1, defaultLobby);
        assertFalse(latestMessages.isEmpty());
        final Message msg = new LobbyDeletedMessage(defaultLobby);
        bus.post(msg);

        lock.await(250, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages1 = chatManagement.getLatestMessages(1, defaultLobby);
        assertTrue(latestMessages1.isEmpty());
    }
}

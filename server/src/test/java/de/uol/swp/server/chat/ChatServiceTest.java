package de.uol.swp.server.chat;

import com.google.common.eventbus.EventBus;
import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.chat.request.DeleteChatMessageRequest;
import de.uol.swp.common.chat.request.EditChatMessageRequest;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.message.Message;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import de.uol.swp.server.chat.store.MainMemoryBasedChatMessageStore;
import de.uol.swp.server.lobby.LobbyManagement;
import de.uol.swp.server.lobby.LobbyService;
import de.uol.swp.server.usermanagement.AuthenticationService;
import de.uol.swp.server.usermanagement.ServerUserService;
import de.uol.swp.server.usermanagement.UserManagement;
import de.uol.swp.server.usermanagement.store.MainMemoryBasedUserStore;
import de.uol.swp.server.usermanagement.store.UserStore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
@SuppressWarnings("UnstableApiUsage")
class ChatServiceTest {

    private static final String defaultContent = "I am intelligent content";
    private static final String secondContent = "I am new, even more intelligent content";
    private static final User defaultUser = new UserDTO("test", "test", "test@test.de");
    private static final String defaultLobby = "I am an intelligent lobby";

    final EventBus bus = new EventBus();
    final CountDownLatch lock = new CountDownLatch(1);
    private final UserStore userStore = new MainMemoryBasedUserStore();
    private final UserManagement userManagement = new UserManagement(userStore);
    private final LobbyManagement lobbyManagement = new LobbyManagement();
    private final AuthenticationService authenticationService = new AuthenticationService(bus, userManagement);
    private final LobbyService lobbyService = new LobbyService(lobbyManagement, authenticationService, bus);
    private ChatManagement chatManagement;
    private ChatService chatService;

    /**
     * Helper method run before each test case
     * <p>
     * This method instantiates a new ChatManagement and a new ChatService so that
     * one test's ChatMessage objects don't interfere with another test's
     */
    @BeforeEach
    void setUp() {
        chatManagement = new ChatManagement(new MainMemoryBasedChatMessageStore());
        chatService = new ChatService(bus, chatManagement, lobbyService);
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method resets the chatManagement and chatService variables to null
     */
    @AfterEach
    void tearDown() {
        chatManagement = null;
        chatService = null;
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
     * @throws java.lang.InterruptedException The interrupted exception
     */
    @Test
    void newChatMessageTest() throws InterruptedException {
        final Message req = new NewChatMessageRequest(defaultUser, defaultContent);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1);
        assertFalse(latestMessages.isEmpty());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(latestMessage.getAuthor(), defaultUser);
        assertEquals(latestMessage.getContent(), defaultContent);
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
     * @throws java.lang.InterruptedException the interrupted exception
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
        assertEquals(latestMessage.getAuthor(), defaultUser);
        assertEquals(latestMessage.getContent(), defaultContent);
    }

    /**
     * Tests if the ChatService properly handles the EditChatMessageRequest.
     * <p>
     * An EditChatMessageRequest is posted onto the EventBus. It is checked
     * if the requested ChatMessage was edited
     * <p>
     * This test fails when the list of ChatMessages returned by the chatManagement is
     * empty or when the requested ChatMessage wasn't edited.
     *
     * @throws java.lang.InterruptedException Interrupted exception
     */
    @Test
    void editChatMessageTest() throws InterruptedException {
        ChatMessage msg = chatManagement.createChatMessage(defaultUser, defaultContent);
        final Message req = new EditChatMessageRequest(msg.getID(), secondContent);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1);
        assertFalse(latestMessages.isEmpty());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(latestMessage.getID(), msg.getID());
        assertEquals(latestMessage.getContent(), secondContent);
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
     * @throws java.lang.InterruptedException interrupted exception
     * @since 2021-01-04
     */
    @Test
    void editChatMessageInLobbyTest() throws InterruptedException {
        ChatMessage msg = chatManagement.createChatMessage(defaultUser, defaultContent, defaultLobby);
        final Message req = new EditChatMessageRequest(msg.getID(), secondContent, defaultLobby);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(1, defaultLobby);
        assertFalse(latestMessages.isEmpty());

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(latestMessage.getID(), msg.getID());
        assertEquals(latestMessage.getContent(), secondContent);
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
     * @throws java.lang.InterruptedException the interrupted exception
     */
    @Test
    void deleteChatMessageTest() throws InterruptedException {
        ChatMessage msg1 = chatManagement.createChatMessage(defaultUser, defaultContent);
        ChatMessage msg2 = chatManagement.createChatMessage(defaultUser, secondContent);
        final Message req = new DeleteChatMessageRequest(msg2.getID());
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(2);
        assertEquals(latestMessages.size(), 1);

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(latestMessage.getID(), msg1.getID());
        assertEquals(latestMessage.getContent(), defaultContent);
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
     * @throws java.lang.InterruptedException the interrupted exception
     * @since 2021-01-04
     */
    @Test
    void deleteChatMessageInLobbyTest() throws InterruptedException {
        ChatMessage msg1 = chatManagement.createChatMessage(defaultUser, defaultContent, defaultLobby);
        ChatMessage msg2 = chatManagement.createChatMessage(defaultUser, secondContent, defaultLobby);
        final Message req = new DeleteChatMessageRequest(msg2.getID(), defaultLobby);
        bus.post(req);

        lock.await(75, TimeUnit.MILLISECONDS);

        List<ChatMessage> latestMessages = chatManagement.getLatestMessages(2, defaultLobby);
        assertEquals(latestMessages.size(), 1);

        ChatMessage latestMessage = latestMessages.get(0);
        assertEquals(latestMessage.getID(), msg1.getID());
        assertEquals(latestMessage.getContent(), defaultContent);
    }
}

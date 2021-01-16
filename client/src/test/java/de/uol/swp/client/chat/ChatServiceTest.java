package de.uol.swp.client.chat;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.chat.request.AskLatestChatMessageRequest;
import de.uol.swp.common.chat.request.DeleteChatMessageRequest;
import de.uol.swp.common.chat.request.EditChatMessageRequest;
import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is a test of the class used to hide the communication details regarding ChatMessages
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.client.chat.ChatService
 * @since 2020-12-19
 */
@SuppressWarnings("UnstableApiUsage")
class ChatServiceTest {

    private static final String defaultContent = "I am intelligent content";
    private static final int defaultId = 42;
    private static final int defaultAmount = 37;
    private static final User defaultUser = new UserDTO("test", "test", "test@test.de");
    private static final String defaultLobby = "I am an intelligent lobby";
    final EventBus bus = new EventBus();
    final CountDownLatch lock = new CountDownLatch(1);
    Object event;

    /**
     * Handles DeadEvents detected on the EventBus
     * <p>
     * If a DeadEvent is detected, the event variable of this class gets updated
     * to its event, and its event is printed to the console output.
     *
     * @param e The DeadEvent detected on the EventBus
     */
    @Subscribe
    void handle(DeadEvent e) {
        this.event = e.getEvent();
        System.out.println(e.getEvent());
        lock.countDown();
    }

    /**
     * Helper method run before each test case
     * <p>
     * This method resets the variable event to null and registers the object of
     * this class to the EventBus.
     */
    @BeforeEach
    void registerBus() {
        event = null;
        bus.register(this);
    }

    /**
     * Helper method run after each test case
     * <p>
     * This method only unregisters the object of this class from the EventBus.
     */
    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    /**
     * Test for the newMessage routine
     * <p>
     * Test if the ChatService creates a NewChatMessageRequest with the given
     * user and content, and posts it onto the EventBus when newMessage is called
     * without an originLobby parameter.
     * <p>
     * This test fails if the NewChatMessageRequest says it originated from a lobby,
     * or if its originLobby attribute is not null, or if the author or content
     * aren't equal to what was sent originally.
     *
     * @throws java.lang.InterruptedException Thrown by lock.await()
     */
    @Test
    void newMessageTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.newMessage(defaultUser, defaultContent);

        lock.await(500, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof NewChatMessageRequest);

        NewChatMessageRequest chatMessageRequest = (NewChatMessageRequest) event;

        assertFalse(chatMessageRequest.isFromLobby());
        assertNull(chatMessageRequest.getOriginLobby());
        assertEquals(chatMessageRequest.getAuthor(), defaultUser);
        assertEquals(chatMessageRequest.getContent(), defaultContent);
    }

    /**
     * Test for the newMessage routine
     * <p>
     * Test if the ChatService creates a NewChatMessageRequest with the given
     * user and content and posts it onto the EventBus when newMessage is called
     * with the originLobby parameter.
     * <p>
     * This test fails if the NewChatMessageRequest says it did not originate
     * from a lobby, or if the author, or content, or originLobby attributes
     * aren't equal to what was sent originally.
     *
     * @throws java.lang.InterruptedException Thrown by lock.await()
     * @since 2021-01-03
     */
    @Test
    void newMessageWithOriginLobbyTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.newMessage(defaultUser, defaultContent, defaultLobby);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof NewChatMessageRequest);

        NewChatMessageRequest chatMessageRequest = (NewChatMessageRequest) event;

        assertTrue(chatMessageRequest.isFromLobby());
        assertEquals(chatMessageRequest.getOriginLobby(), defaultLobby);
        assertEquals(chatMessageRequest.getAuthor(), defaultUser);
        assertEquals(chatMessageRequest.getContent(), defaultContent);
    }

    /**
     * Test for the newMessage routine
     * <p>
     * Test if the ChatService creates a NewChatMessageRequest with the given
     * User and Content and posts it onto the EventBus when newMessage is called
     * with the originLobby parameter set to null.
     * <p>
     * This test fails if the NewChatMessageRequest says it originated from a lobby,
     * or if its originLobby attribute is not null, or if the author or content
     * aren't equal to what was sent originally.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-01-03
     */
    @Test
    void newMessageWithOriginLobbyIsNullTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.newMessage(defaultUser, defaultContent, null);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof NewChatMessageRequest);

        NewChatMessageRequest chatMessageRequest = (NewChatMessageRequest) event;

        assertFalse(chatMessageRequest.isFromLobby());
        assertNull(chatMessageRequest.getOriginLobby());
        assertEquals(chatMessageRequest.getAuthor(), defaultUser);
        assertEquals(chatMessageRequest.getContent(), defaultContent);
    }

    /**
     * Test for the deleteMessage routine
     * <p>
     * Tests if the ChatService creates a DeleteChatMessageRequest with
     * the given ID, and posts it onto the EventBus when deleteMessage is called
     * without an originLobby parameter.
     * <p>
     * This test fails if the DeleteChatMessageRequest says it originated from a
     * lobby, or if its originLobby attribute is not null, or if the ID doesn't
     * equal the ID that was sent originally.
     *
     * @throws java.lang.InterruptedException Thrown by lock.await()
     */
    @Test
    void deleteMessageTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.deleteMessage(defaultId);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof DeleteChatMessageRequest);

        DeleteChatMessageRequest chatMessageRequest = (DeleteChatMessageRequest) event;

        assertFalse(chatMessageRequest.isFromLobby());
        assertNull(chatMessageRequest.getOriginLobby());
        assertEquals(chatMessageRequest.getId(), defaultId);
    }

    /**
     * Test for the deleteMessage routine
     * <p>
     * Tests if the ChatService creates a DeleteChatMessageRequest
     * with the given ID and posts it onto the EventBus when deleteMessage is called
     * with the originLobby parameter.
     * <p>
     * This test fails if the DeleteChatMessageRequest says it did not originate
     * from a lobby, or if the ID or originLobby aren't equal to what was
     * sent originally.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-01-03
     */
    @Test
    void deleteMessageWithOriginLobbyTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.deleteMessage(defaultId, defaultLobby);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof DeleteChatMessageRequest);

        DeleteChatMessageRequest chatMessageRequest = (DeleteChatMessageRequest) event;

        assertTrue(chatMessageRequest.isFromLobby());
        assertEquals(chatMessageRequest.getOriginLobby(), defaultLobby);
        assertEquals(chatMessageRequest.getId(), defaultId);
    }

    /**
     * Test for the deleteMessage routine
     * <p>
     * Tests if the ChatService creates a DeleteChatMessageRequest
     * with the given ID and posts it onto the EventBus when deleteMessage is called
     * with the originLobby parameter set to null.
     * <p>
     * This test fails if the DeleteChatMessageRequest says it originated from a
     * lobby, or if its originLobby attribute is not null, or if the ID doesn't
     * equal the ID that was sent originally.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-01-03
     */
    @Test
    void deleteMessageWithOriginLobbyIsNullTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.deleteMessage(defaultId, null);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof DeleteChatMessageRequest);

        DeleteChatMessageRequest chatMessageRequest = (DeleteChatMessageRequest) event;

        assertFalse(chatMessageRequest.isFromLobby());
        assertNull(chatMessageRequest.getOriginLobby());
        assertEquals(chatMessageRequest.getId(), defaultId);
    }

    /**
     * Test for editMessage routine
     * <p>
     * Tests if the ChatService creates a EditChatMessageRequest with
     * the given ID and content, and posts it onto the EventBus when editMessage
     * is called without an originLobby parameter.
     * <p>
     * This test fails if the EditChatMessageRequest says it originated from a lobby,
     * or if its originLobby attribute is not null, or if the ID or content aren't
     * equal to what was sent originally.
     *
     * @throws InterruptedException thrown by lock.await()
     */
    @Test
    void editMessageTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.editMessage(defaultId, defaultContent);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof EditChatMessageRequest);

        EditChatMessageRequest chatMessageRequest = (EditChatMessageRequest) event;

        assertFalse(chatMessageRequest.isFromLobby());
        assertNull(chatMessageRequest.getOriginLobby());
        assertEquals(chatMessageRequest.getId(), defaultId);
        assertEquals(chatMessageRequest.getContent(), defaultContent);
    }

    /**
     * Test for editMessage routine
     * <p>
     * Tests if the ChatService creates a EditChatMessageRequest with the given
     * ID and content and posts it onto the EventBus when editMessage is called
     * with the originLobby parameter.
     * <p>
     * This test fails if the EditMessageRequest says it did not originate from
     * a lobby, or if the originLobby, ID, or content aren't equal to what was
     * sent originally.
     *
     * @throws InterruptedException thrown by lock.await()
     * @since 2021-01-03
     */
    @Test
    void editMessageWithOriginLobbyTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.editMessage(defaultId, defaultContent, defaultLobby);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof EditChatMessageRequest);

        EditChatMessageRequest chatMessageRequest = (EditChatMessageRequest) event;

        assertTrue(chatMessageRequest.isFromLobby());
        assertEquals(chatMessageRequest.getOriginLobby(), defaultLobby);
        assertEquals(chatMessageRequest.getId(), defaultId);
        assertEquals(chatMessageRequest.getContent(), defaultContent);
    }

    /**
     * Test for editMessage routine
     * <p>
     * Tests if the ChatService creates a EditChatMessageRequest with the given
     * ID and content and posts it onto the EventBus when editMessage is called
     * with the originLobby parameter set to null.
     * <p>
     * This test fails if the EditMessageRequest says it originated from a lobby,
     * or if its originLobby attribute is not null, or if the ID or content aren't
     * equal to what was sent originally.
     *
     * @throws InterruptedException thrown by lock.await()
     * @since 2021-01-03
     */
    @Test
    void editMessageWithOriginLobbyIsNullTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.editMessage(defaultId, defaultContent);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof EditChatMessageRequest);

        EditChatMessageRequest chatMessageRequest = (EditChatMessageRequest) event;

        assertFalse(chatMessageRequest.isFromLobby());
        assertNull(chatMessageRequest.getOriginLobby());
        assertEquals(chatMessageRequest.getId(), defaultId);
        assertEquals(chatMessageRequest.getContent(), defaultContent);
    }

    /**
     * Test for the askLatestMessages routine
     * <p>
     * Tests if the ChatService creates an AskLatestChatMessageRequest with
     * the given amount, and posts it onto the EventBus when askLatestMessages is
     * called without an originLobby parameter.
     * <p>
     * This test fails if the AskLatestMessagesRequest says it originated from a
     * lobby, or if its originLobby attribute is not null, or if the amount isn't
     * equal to what was sent originally.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     */
    @Test
    void askLatestMessagesTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.askLatestMessages(defaultAmount);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof AskLatestChatMessageRequest);

        AskLatestChatMessageRequest chatMessageRequest = (AskLatestChatMessageRequest) event;

        assertFalse(chatMessageRequest.isFromLobby());
        assertNull(chatMessageRequest.getOriginLobby());
        assertEquals(chatMessageRequest.getAmount(), defaultAmount);
    }

    /**
     * Test for the askLatestMessages routine
     * <p>
     * Tests if the ChatService creates an AskLatestChatMessageRequest
     * with the given amount and posts it onto the EventBus when askLatestMessages is called
     * with the originLobby parameter.
     * <p>
     * This test fails if the AskLatestMessagesRequest says it did not originate from a lobby,
     * or if the originLobby, the author, or content aren't equal to what was sent originally.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-01-03
     */
    @Test
    void askLatestMessagesWithOriginLobbyTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.askLatestMessages(defaultAmount, defaultLobby);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof AskLatestChatMessageRequest);

        AskLatestChatMessageRequest chatMessageRequest = (AskLatestChatMessageRequest) event;

        assertTrue(chatMessageRequest.isFromLobby());
        assertEquals(chatMessageRequest.getOriginLobby(), defaultLobby);
        assertEquals(chatMessageRequest.getAmount(), defaultAmount);
    }

    /**
     * Test for the askLatestMessages routine
     * <p>
     * Tests if the ChatService creates an AskLatestChatMessageRequest
     * with the given amount and posts it to the EventBus when askLatestMessages is called
     * with the originLobby parameter set to null.
     * <p>
     * This test fails if the AskLatestMessagesRequest says it originated from a lobby,
     * or if its originLobby attribute is not null, or if the amount isn't equal to what
     * was sent originally.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2021-01-03
     */
    @Test
    void askLatestMessagesWithOriginLobbyIsNullTest() throws InterruptedException {
        IChatService chatService = new ChatService(bus);
        chatService.askLatestMessages(defaultAmount, null);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof AskLatestChatMessageRequest);

        AskLatestChatMessageRequest chatMessageRequest = (AskLatestChatMessageRequest) event;

        assertFalse(chatMessageRequest.isFromLobby());
        assertNull(chatMessageRequest.getOriginLobby());
        assertEquals(chatMessageRequest.getAmount(), defaultAmount);
    }
}

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
     * @since 2020-12-19
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
     *
     * @since 2020-12-19
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
     *
     * @since 2020-12-19
     */
    @AfterEach
    void deregisterBus() {
        bus.unregister(this);
    }

    /**
     * Test for the newMessage routine
     * <p>
     * Test if the ChatService creates a NewChatMessageRequest with
     * the given User and Content, and posts it onto the EventBus when newMessage is called.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2020-12-19
     */
    @Test
    void newMessageTest() throws InterruptedException {
        ChatService chatService = new ChatService(bus);
        chatService.newMessage(defaultUser, defaultContent);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof NewChatMessageRequest);

        NewChatMessageRequest chatMessageRequest = (NewChatMessageRequest) event;

        assertEquals(chatMessageRequest.getAuthor(), defaultUser);
        assertEquals(chatMessageRequest.getContent(), defaultContent);
    }

    /**
     * Test for the deleteMessage routine
     * <p>
     * Tests if the ChatService creates a DeleteChatMessageRequest with
     * the given ID, and posts it onto the EventBus when deleteMessage is called.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2020-12-19
     */
    @Test
    void deleteMessageTest() throws InterruptedException {
        ChatService chatService = new ChatService(bus);
        chatService.deleteMessage(defaultId);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof DeleteChatMessageRequest);

        DeleteChatMessageRequest chatMessageRequest = (DeleteChatMessageRequest) event;

        assertEquals(chatMessageRequest.getId(), defaultId);
    }

    /**
     * Test for editMessage routine
     * <p>
     * Tests if the ChatService creates a EditChatMessageRequest with
     * the given ID and content, and posts it onto the EventBus when editMessage is called.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2020-12-19
     */
    @Test
    void editMessageTest() throws InterruptedException {
        ChatService chatService = new ChatService(bus);
        chatService.editMessage(defaultId, defaultContent);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof EditChatMessageRequest);

        EditChatMessageRequest chatMessageRequest = (EditChatMessageRequest) event;

        assertEquals(chatMessageRequest.getId(), defaultId);
        assertEquals(chatMessageRequest.getContent(), defaultContent);
    }

    /**
     * Test for the askLatestMessages routine
     * <p>
     * Tests if the ChatService creates an AskLatestChatMessageRequest with
     * the given amount, and posts it onto the EventBus when askLatestMessages is called.
     *
     * @throws java.lang.InterruptedException thrown by lock.await()
     * @since 2020-12-19
     */
    @Test
    void askLatestMessagesTest() throws InterruptedException {
        ChatService chatService = new ChatService(bus);
        chatService.askLatestMessages(defaultAmount);

        lock.await(1000, TimeUnit.MILLISECONDS);

        assertTrue(event instanceof AskLatestChatMessageRequest);

        AskLatestChatMessageRequest chatMessageRequest = (AskLatestChatMessageRequest) event;
        assertEquals(chatMessageRequest.getAmount(), defaultAmount);
    }
}

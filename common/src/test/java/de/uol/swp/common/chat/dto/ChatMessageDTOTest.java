package de.uol.swp.common.chat.dto;

import de.uol.swp.common.chat.ChatMessage;
import de.uol.swp.common.user.User;
import de.uol.swp.common.user.UserDTO;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the ChatMessageDTO
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.user.User
 * @see de.uol.swp.common.user.UserDTO
 * @see de.uol.swp.common.chat.ChatMessage
 * @see de.uol.swp.common.chat.dto.ChatMessageDTO
 * @see java.time.Instant
 * @since 2020-12-19
 */
class ChatMessageDTOTest {

    public static final int defaultID = 1;
    private static final String defaultContent = "I am intelligent content";
    private static final User defaultUser = new UserDTO(42, "test", "test", "test@test.de");
    private static final Instant defaultTimestamp = Instant.ofEpochMilli(1608370913852L); // 2020-12-19-09:41:53.852
    private static final ChatMessage defaultMessage = new ChatMessageDTO(defaultID, defaultUser, defaultTimestamp,
                                                                         defaultContent);

    /**
     * Test of compareTo function. Tests that two ChatMessageDTO objects instantiated
     * with different IDs don't compare as equal.
     * <p>
     * This test fails if the compareTo function returns that both of them are equal
     *
     * @since 2020-12-19
     */
    @Test
    void compareToDifferentIds() {
        ChatMessage chatMessage = new ChatMessageDTO(42, defaultUser, defaultTimestamp, defaultContent);
        assertNotEquals(chatMessage.compareTo(defaultMessage), 0);
    }

    /**
     * Test of compareTo function. Tests that two ChatMessageDTO instantiated with the same ID compare as equal.
     * <p>
     * This test fails if the compareTo function returns that they are not equal
     *
     * @since 2020-12-19
     */
    @Test
    void compareToSameIds() {
        ChatMessage chatMessage = new ChatMessageDTO(defaultID, defaultUser, defaultTimestamp, defaultContent);
        assertEquals(chatMessage.compareTo(defaultMessage), 0);
    }

    /**
     * Tests instantiation of the ChatMessageDTO constructor with the edited attribute
     * set to true.
     * <p>
     * This test fails if any of the attributes of the new object are different from
     * the parameters passed to the constructor.
     *
     * @since 2020-12-19
     */
    @Test
    void createMessageWithEdited() {
        ChatMessage chatMessage = new ChatMessageDTO(defaultID, defaultUser, defaultTimestamp, defaultContent, true);
        assertEquals(chatMessage.getID(), defaultID);
        assertEquals(chatMessage.getAuthor(), defaultUser);
        assertEquals(chatMessage.getTimestamp(), defaultTimestamp);
        assertEquals(chatMessage.getContent(), defaultContent);
        assertTrue(chatMessage.isEdited());
    }

    /**
     * Tests if the default ChatMessageDTO constructor fails on passing a null object for the timestamp parameter.
     * <p>
     * This test fails if the ChatMessageDTO constructor allows a null object as its author.
     *
     * @since 2020-12-19
     */
    @Test
    void createMessageWithEmptyAuthor() {
        assertThrows(IllegalArgumentException.class,
                     () -> new ChatMessageDTO(defaultID, null, defaultTimestamp, defaultContent));
    }

    /**
     * Tests if the default ChatMessageDTO constructor fails on passing a null object for the content parameter
     * <p>
     * This test fails if the ChatMessageDTO constructor allows a null object as its content
     *
     * @since 2020-12-19
     */
    @Test
    void createMessageWithEmptyContent() {
        assertThrows(IllegalArgumentException.class,
                     () -> new ChatMessageDTO(defaultID, defaultUser, defaultTimestamp, null));
    }

    /**
     * Tests if the default ChatMessageDTO constructor fails on passing an empty String for the content parameter
     * <p>
     * This test fails if the ChatMessageDTO constructor allows an empty string as its content
     *
     * @since 2020-12-19
     */
    @Test
    void createMessageWithEmptyStringContent() {
        assertThrows(IllegalArgumentException.class,
                     () -> new ChatMessageDTO(defaultID, defaultUser, defaultTimestamp, ""));
    }

    /**
     * Tests if the default ChatMessageDTO constructor fails on passing a null object for the timestamp parameter
     * <p>
     * This test fails if the ChatMessageDTO constructor allows a null object as its content
     *
     * @since 2020-12-19
     */
    @Test
    void createMessageWithEmptyTimestamp() {
        assertThrows(IllegalArgumentException.class,
                     () -> new ChatMessageDTO(defaultID, defaultUser, null, defaultContent));
    }

    /**
     * Tests instantiation of the ChatMessageDTO constructor with a timestamp,
     * and checks all attributes to be equal to the parameters.
     * <p>
     * This test fails if any of the attributes of the new object are different
     * from the parameters passed to the constructor
     *
     * @since 2020-12-19
     */
    @Test
    void createMessageWithTimestamp() {
        ChatMessage chatMessage = new ChatMessageDTO(defaultID, defaultUser, defaultTimestamp, defaultContent);
        assertEquals(chatMessage.getID(), defaultID);
        assertEquals(chatMessage.getAuthor(), defaultUser);
        assertEquals(chatMessage.getTimestamp(), defaultTimestamp);
        assertEquals(chatMessage.getContent(), defaultContent);
        assertFalse(chatMessage.isEdited());
    }

    /**
     * Tests instantiation of the ChatMessageDTO constructor without a timestamp,
     * and checks all attributes to be equal to the parameters.
     * <p>
     * This test fails if any of the attributes of the new object are different
     * from the parameters passed to the constructor
     *
     * @since 2020-12-19
     */
    @Test
    void createMessageWithoutTimestamp() {
        ChatMessage chatMessage = new ChatMessageDTO(defaultID, defaultUser, defaultContent);
        assertEquals(chatMessage.getID(), defaultID);
        assertEquals(chatMessage.getAuthor(), defaultUser);
        assertEquals(chatMessage.getContent(), defaultContent);
        assertFalse(chatMessage.isEdited());
    }

    /**
     * Tests if the timestamp-less ChatMessageDTO constructor fails on passing a null object for the author parameter
     * <p>
     * This test fails if the ChatMessageDTO constructor allows a null object as its author
     *
     * @since 2020-12-19
     */
    @Test
    void createMessageWithoutTimestampWithEmptyAuthor() {
        assertThrows(IllegalArgumentException.class, () -> new ChatMessageDTO(defaultID, null, defaultContent));
    }

    /**
     * Tests if the timestamp-less ChatMessageDTO constructor fails on passing a null object for the content parameter
     * <p>
     * This test fails if the ChatMessageDTO constructor allows a null object as its content
     *
     * @since 2020-12-19
     */
    @Test
    void createMessageWithoutTimestampWithEmptyContent() {
        assertThrows(IllegalArgumentException.class, () -> new ChatMessageDTO(defaultID, defaultUser, null));
    }

    /**
     * Tests if the timestamp-less ChatMessageDTO constructor fails on passing an empty String for the content parameter
     * <p>
     * This test fails if the ChatMessageDTO constructor allows an empty string as its content
     *
     * @since 2020-12-19
     */
    @Test
    void createMessageWithoutTimestampWithEmptyStringContent() {
        assertThrows(IllegalArgumentException.class, () -> new ChatMessageDTO(defaultID, defaultUser, ""));
    }

    /**
     * Tests if a ChatMessageDTO created with the copy constructor is equal to the original ChatMessageDTO.
     * <p>
     * This test fails if the two are not equal
     *
     * @since 2020-12-19
     */
    @Test
    void createWithCopyConstructor() {
        ChatMessage copiedMessage = ChatMessageDTO.create(defaultMessage);
        assertEquals(defaultMessage, copiedMessage);
    }

    /**
     * Tests if a ChatMessageDTO created with the copy constructor can be edited
     * without any changes occurring in the original ChatMessageDTO.
     * <p>
     * This test fails when the content of the copied ChatMessage wasn't changed,
     * the two objects are considered equal, or when the edited attribute was not set.
     *
     * @since 2020-12-19
     */
    @Test
    void createWithCopyConstructorAndEditContent() {
        final String newContent = "I am new, even more intelligent content";
        ChatMessage copiedMessage = ChatMessageDTO.create(defaultMessage);
        copiedMessage.setContent(newContent);
        assertEquals(copiedMessage.getContent(), newContent);
        assertNotEquals(defaultMessage, copiedMessage);
        assertTrue(copiedMessage.isEdited());
    }

    /**
     * Tests if setting the content with setContent to null doesn't change the content.
     * <p>
     * This test fails if the content of the ChatMessage has changed
     *
     * @since 2020-12-19
     */
    @Test
    void createWithCopyConstructorAndSetContentToNull() {
        ChatMessage copiedMessage = ChatMessageDTO.create(defaultMessage);
        copiedMessage.setContent(null);
        assertEquals(copiedMessage.getContent(), defaultContent);
    }

    /**
     * Tests if two ChatMessageDTO objects instantiated with different IDs are not equal.
     * <p>
     * This test fails if they are considered equal
     *
     * @since 2020-12-19
     */
    @Test
    void messageNotEqualDifferentIds() {
        ChatMessage chatMessage = new ChatMessageDTO(42, defaultUser, defaultTimestamp, defaultContent);
        assertNotEquals(chatMessage, defaultMessage);
    }
}

package de.uol.swp.client.ChangeAccountDetails.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the event used to show the ChangePasswordError alert
 *
 * @author Eric Vuong
 * @author Steven Luong
 * @see ChangeAccountDetailsErrorEvent
 * @since 2020-12-03
 */
public class ChangeAccountDetailsErrorEventTest {

    /**
     * Test for the creation of ChangePasswordErrorEvents
     * <p>
     * This test checks if the error message of the ChangePasswordErrorEvent gets
     * set correctly during the creation of a new event
     *
     * @since 2020-12-03
     */
    @Test
    void createChangePasswordErrorEvent() {
        ChangeAccountDetailsErrorEvent event = new ChangeAccountDetailsErrorEvent("Test");

        assertEquals(event.getMessage(), "Test");
    }
}

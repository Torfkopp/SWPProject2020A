package de.uol.swp.client.ChangeAccountDetails.event;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test for the event used to show the ChangeAccountDetailsError alert
 *
 * @author Eric Vuong
 * @author Steven Luong
 * @see de.uol.swp.client.ChangeAccountDetails.event.ChangeAccountDetailsErrorEvent
 * @since 2020-12-03
 */
public class ChangeAccountDetailsErrorEventTest {

    /**
     * Test for the creation of ChangeAccountDetailsErrorEvent
     * <p>
     * This test checks if the error message of the ChangeAccountDetailsErrorEvent gets
     * set correctly during the creation of a new event
     *
     * @since 2020-12-03
     */
    @Test
    void createChangePasswordErrorEvent() {
        ChangeAccountDetailsErrorEvent event = new ChangeAccountDetailsErrorEvent("Test");

        assertEquals("Test", event.getMessage());
    }
}

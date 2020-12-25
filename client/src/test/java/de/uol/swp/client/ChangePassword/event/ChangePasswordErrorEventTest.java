package de.uol.swp.client.ChangePassword.event;

import de.uol.swp.client.ChangePassword.event.ChangePasswordErrorEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Test for the event used to show the ChangePasswordError alert
 *
 * @author Eric Vuong
 * @author Steven Luong
 * @see de.uol.swp.client.ChangePassword.event.ChangePasswordErrorEvent
 * @since 2020-12-03
 *
 */
public class ChangePasswordErrorEventTest {

    /**
     * Test for the creation of ChangePasswordErrorEvents
     *
     * This test checks if the error message of the ChangePasswordErrorEvent gets
     * set correctly during the creation of a new event
     *
     * @since 2020-12-03
     */
    @Test
    void createChangePasswordErrorEvent() {
        ChangePasswordErrorEvent event = new ChangePasswordErrorEvent("Test");

        assertEquals(event.getMessage(), "Test");
    }

}

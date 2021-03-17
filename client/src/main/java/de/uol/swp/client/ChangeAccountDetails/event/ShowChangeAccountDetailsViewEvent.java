package de.uol.swp.client.ChangeAccountDetails.event;

import de.uol.swp.common.user.User;

/**
 * Event used to show the ChangeAccountDetails window
 * <p>
 * In order to show the ChangeAccountDetails window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 * It contains the user object of the user changing the account details.
 *
 * @author Eric Vuong
 * @see de.uol.swp.client.SceneManager
 * @since 2020-11-25
 */
public class ShowChangeAccountDetailsViewEvent {

    private final User user;

    /**
     * Constructor
     *
     * @param user The user changing his account details
     *
     * @author Mario Fokken
     * since 2020-12-16
     */
    public ShowChangeAccountDetailsViewEvent(User user) {
        this.user = user;
    }

    /**
     * Gets the user
     *
     * @return user object
     *
     * @author Mario Fokken
     * @since 2020-12-16
     */
    public User getUser() {
        return user;
    }
}

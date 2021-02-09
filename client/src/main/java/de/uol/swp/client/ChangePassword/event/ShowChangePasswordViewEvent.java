package de.uol.swp.client.ChangePassword.event;

import de.uol.swp.common.user.User;

/**
 * Event used to show the ChangePassword window
 * <p>
 * In order to show the ChangePassword window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 * It contains the user object of the user changing the password.
 *
 * @author Eric Vuong
 * @see de.uol.swp.client.SceneManager
 * @since 2020-11-25
 */
public class ShowChangePasswordViewEvent {

    private final User user;

    /**
     * Constructor
     *
     * @param user The user changing his password
     *
     * @author Mario Fokken
     * since 2020-12-16
     */
    public ShowChangePasswordViewEvent(User user) {
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

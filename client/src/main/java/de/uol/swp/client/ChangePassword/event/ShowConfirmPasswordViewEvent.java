package de.uol.swp.client.ChangePassword.event;

import de.uol.swp.common.user.User;

/**
 * Event used to show the ConfirmPassword window
 * <p>
 * In order to show the ConfirmPassword window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 * It contains the user object of the user confirming the password.
 *
 * @author Eric Vuong
 * @author Alwin Bossert
 * @see de.uol.swp.client.SceneManager
 * @since 2021-03-16
 */
public class ShowConfirmPasswordViewEvent {

    private final User user;

    /**
     * Constructor
     *
     * @param user The user confirming his password
     *
     * @author Alwin Bossert
     * @author Eric Vuong
     * since 2021-03-16
     */
    public ShowConfirmPasswordViewEvent(User user) {
        this.user = user;
    }

    /**
     * Gets the user
     *
     * @return user object
     *
     * @author Alwin Bossert
     * @author Eric Vuong
     * @since 2021-03-16
     */
    public User getUser() {
        return user;
    }
}

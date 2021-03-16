package de.uol.swp.client.ChangePassword.event;

/**
 * Event used to show the window shown before the password change
 * <p>
 * In order to show the previous window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Alwin Bossert
 * @author Eric Vuong
 * @see de.uol.swp.client.SceneManager
 * @since 2021-03-16
 */
public class ConfirmPasswordCanceledEvent {}

package de.uol.swp.client.changeSettings.event;

/**
 * Event used to show the window shown before the settings change
 * <p>
 * In order to show the previous window using this event, post an instance of it
 * onto the EventBus the SceneManager is subscribed to.
 *
 * @author Alwin Bossert
 * @see de.uol.swp.client.SceneManager
 * @since 2021-05-21
 */
public class ChangeSettingsSuccessfulEvent {}
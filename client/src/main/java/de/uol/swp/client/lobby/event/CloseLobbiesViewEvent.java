package de.uol.swp.client.lobby.event;

/**
 * Event used to trigger the closing of all lobby windows
 * <p>
 * In order to close all the client's lobby windows using this event, post an
 * instance of it onto the EventBus the SceneManager is subscribed to.
 *
 * @author Aldin Dervisi
 * @author Finn Haase
 * @see de.uol.swp.client.SceneManager
 * @since 2021-01-28
 */
public class CloseLobbiesViewEvent {}

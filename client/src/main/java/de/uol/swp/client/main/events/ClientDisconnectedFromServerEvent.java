package de.uol.swp.client.main.events;

/**
 * An event inside the client to make it close itself
 * <p>
 * This event gets triggered when when there is no connection to
 * the server anymore. It causes the corresponding client to
 * shut itself down so a user can reconnect later.
 *
 * @author Aldin Dervisi
 * @author Marvin Drees
 * @since 2021-03-25
 */
public class ClientDisconnectedFromServerEvent {}

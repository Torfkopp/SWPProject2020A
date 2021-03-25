package de.uol.swp.client.main.events;

/**
 * A response from server to client to make it fall back to the login screen
 * <p>
 * This response is sent to the client whose session get logged out
 * by a different client so it no longer remains logged in.
 *
 * @author Marvin Drees
 * @author Eric Vuong
 * @since 2021-03-02
 */
public class ClientDisconnectedFromServerEvent {}

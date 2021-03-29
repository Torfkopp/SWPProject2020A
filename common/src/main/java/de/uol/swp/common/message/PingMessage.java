package de.uol.swp.common.message;

/**
 * Generic ping sent from the server to all clients
 * if no other communication takes places in order to assure
 * that no client got disconnected.
 *
 * @author Aldin Dervisi
 * @author Marvin Drees
 * @since 2021-03-18
 */
public class PingMessage extends AbstractResponseMessage {}

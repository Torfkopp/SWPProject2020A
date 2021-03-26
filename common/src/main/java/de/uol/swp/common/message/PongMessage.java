package de.uol.swp.common.message;

/**
 * This message gets sent from a client back to the
 * server if it received a PingMessage previously in order
 * to verify, that it's still connected to the server
 *
 * @author Aldin Dervisi
 * @author Marvin Drees
 * @see de.uol.swp.common.message.PingMessage
 * @since 2021-03-18
 */
public class PongMessage extends AbstractRequestMessage {}

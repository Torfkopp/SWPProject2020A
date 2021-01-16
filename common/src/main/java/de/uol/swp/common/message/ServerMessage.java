package de.uol.swp.common.message;

import de.uol.swp.common.user.Session;

import java.util.List;

/**
 * A message from server to a number of clients
 * that is not necessary a response to a request (aka server push)
 * // ^ Can't change it; he's speaking the language of the gods! -Mario
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.Message
 * @see de.uol.swp.common.message.Message
 * @since 2019-08-07
 */
public interface ServerMessage extends Message {

    /**
     * Gets the receiver list
     *
     * @return List of session objects defining the receivers
     * @since 2019-10-08
     */
    List<Session> getReceiver();

    /**
     * Sets the receivers of this message
     *
     * @param receiver List of session objects defining the receivers
     * @since 2019-10-08
     */
    void setReceiver(List<Session> receiver);
}

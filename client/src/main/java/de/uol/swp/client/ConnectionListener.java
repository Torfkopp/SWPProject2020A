package de.uol.swp.client;

import io.netty.channel.Channel;

/**
 * This interface is the base for creating a new ConnectionListener.
 * <p>
 * This class forces the child classes to have the functions connectionEstablished
 * and exceptionOccurred in order to call them through a loop to reduce unnecessary
 * code repetition.
 *
 * @author Marco Grawunder
 * @see de.uol.swp.client.ClientApp
 * @see de.uol.swp.client.ClientConnection
 * @since 2017-03-17
 */
public interface ConnectionListener {

    /**
     * Is called when the connection to the server has been established
     *
     * @param channel The netty channel the connection is established on
     *
     * @see de.uol.swp.client.ClientConnection
     */
    void connectionEstablished(Channel channel);

    /**
     * If the server sends an exception, this method is called
     *
     * @param cause The cause which led to sending the exception
     *
     * @see de.uol.swp.client.ClientConnection
     */
    void exceptionOccurred(String cause);

    /**
     * If the server sends an exception, this method is called
     * <p>
     * This method also accepts the Throwable that was thrown as
     * an argument.
     *
     * @param e     The Throwable that occurred
     * @param cause The cause which led to sending the exception
     *
     * @author Phillip-André Suhr
     * @see de.uol.swp.client.ClientConnection
     * @since 2021-04-26
     */
    void exceptionOccurred(Throwable e, String cause);
}

package de.uol.swp.common.message;

import de.uol.swp.common.user.Session;

import java.io.Serializable;
import java.util.Optional;

/**
 * Base interface of all messages
 *
 * @author Marco Grawunder
 * @see java.io.Serializable
 * @since 2019-08-13
 */
public interface Message extends Serializable {

    /**
     * Retrieves the current message context
     *
     * @return Empty optional object or MessageContext
     * @implNote .isPresent() Checks if the MessageContext got set
     * @implNote .get() Gets the MessageContext object
     * @see de.uol.swp.common.message.MessageContext
     * @since 2019-09-09
     */
    Optional<MessageContext> getMessageContext();

    /**
     * Allows to set a MessageContext e.g. for network purposes
     *
     * @param messageContext The MessageContext to be set
     * @see de.uol.swp.common.message.MessageContext
     * @since 2019-08-13
     */
    void setMessageContext(MessageContext messageContext);

    /**
     * Retrieve current session
     *
     * @return Empty optional object or MessageContext
     * @implNote .isPresent() Checks if the Session got set
     * @implNote .get() Gets the Session object
     * @since 2019-09-09
     */
    Optional<Session> getSession();

    /**
     * Sets the current session
     *
     * @param session The current session
     * @see de.uol.swp.common.user.Session
     * @since 2019-08-13
     */
    void setSession(Session session);

    /**
     * Allows to create a new message
     * based on the given one (copy)
     *
     * @param otherMessage Original Message
     * @since 2019-08-13
     */
    void initWithMessage(Message otherMessage);
}

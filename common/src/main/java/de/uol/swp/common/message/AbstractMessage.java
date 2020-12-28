package de.uol.swp.common.message;

import de.uol.swp.common.user.Session;

import java.util.Objects;
import java.util.Optional;

/**
 * Base class of all messages. Basic handling of session information
 *
 * @author Marco Grawunder
 * @see de.uol.swp.common.message.Message
 * @since 2017-03-17
 */
public abstract class AbstractMessage implements Message {

    transient private MessageContext messageContext;
    transient private Session session = null;

    @Override
    public Optional<MessageContext> getMessageContext() {
        return messageContext != null ? Optional.of(messageContext) : Optional.empty();
    }

    @Override
    public void setMessageContext(MessageContext messageContext) {
        this.messageContext = messageContext;
    }

    @Override
    public Optional<Session> getSession() {
        return session != null ? Optional.of(session) : Optional.empty();
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public void initWithMessage(Message otherMessage) {
        otherMessage.getMessageContext().ifPresent(this::setMessageContext);
        otherMessage.getSession().ifPresent(this::setSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(messageContext, session);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractMessage that = (AbstractMessage) o;
        return Objects.equals(messageContext, that.messageContext) &&
                Objects.equals(session, that.session);
    }
}

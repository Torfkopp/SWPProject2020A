package de.uol.swp.common.chat.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request sent by the client to ask for the latest messages
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see AbstractRequestMessage
 * @since 2020-12-17
 */
public class AskLatestChatMessageRequest extends AbstractRequestMessage {
    private final int amount;

    /**
     * Constructor
     *
     * @param amount The amount of messages that should be sent back
     * @since 2020-12-17
     */
    public AskLatestChatMessageRequest(int amount) {
        this.amount = amount;
    }

    /**
     * Getter for the ID attribute
     *
     * @return the ID of the ChatMessage that got deleted
     * @since 2020-12-17
     */
    public int getAmount() {
        return amount;
    }
}

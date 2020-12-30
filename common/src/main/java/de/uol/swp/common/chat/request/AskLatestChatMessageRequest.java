package de.uol.swp.common.chat.request;

/**
 * Request sent by the client to ask for the latest messages
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.request.AbstractChatMessageRequest
 * @since 2020-12-17
 */
public class AskLatestChatMessageRequest extends AbstractChatMessageRequest {
    private final int amount;

    /**
     * Constructor
     *
     * @param amount The amount of messages that should be sent back
     * @since 2020-12-17
     */
    public AskLatestChatMessageRequest(int amount) {
        super(null);
        this.amount = amount;
    }

    public AskLatestChatMessageRequest(int amount, String originLobby) {
        super(originLobby);
        this.amount = amount;
    }

    /**
     * Getter for the amount attribute
     *
     * @return The amount of ChatMessages the client requested
     * @since 2020-12-17
     */
    public int getAmount() {
        return amount;
    }
}

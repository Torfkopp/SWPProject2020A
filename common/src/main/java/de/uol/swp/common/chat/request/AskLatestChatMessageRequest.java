package de.uol.swp.common.chat.request;

import de.uol.swp.common.LobbyName;

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
     * <p>
     * This constructor is used for AskLatestChatMessageRequests originating from
     * the global chat. It sets the inherited originLobby attribute to null.
     *
     * @param amount The amount of messages that should be sent back
     */
    public AskLatestChatMessageRequest(int amount) {
        super(null);
        this.amount = amount;
    }

    /**
     * Constructor
     * <p>
     * This constructor is used for AskLatestChatMessageRequests originating from
     * a lobby chat. It sets the inherited originLobby attribute to the parameter
     * provided upon calling the constructor.
     *
     * @param amount      The amount of messages that should be sent back
     * @param originLobby The Lobby the AskLatestChatMessageRequest originated from
     *
     * @since 2020-12-30
     */
    public AskLatestChatMessageRequest(int amount, LobbyName originLobby) {
        super(originLobby);
        this.amount = amount;
    }

    /**
     * Gets the amount attribute
     *
     * @return The amount of ChatMessages the client requested
     */
    public int getAmount() {
        return amount;
    }
}

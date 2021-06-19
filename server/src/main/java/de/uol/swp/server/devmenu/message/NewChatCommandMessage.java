package de.uol.swp.server.devmenu.message;

import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.user.Actor;
import de.uol.swp.server.message.AbstractServerInternalMessage;

/**
 * Internal Message used by the {@link de.uol.swp.server.chat.ChatService} to
 * notify the {@link de.uol.swp.server.devmenu.CommandService} that a command
 * has been found in the chat.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @since 2021-02-19
 */
public class NewChatCommandMessage extends AbstractServerInternalMessage {

    private final Actor user;
    private final String command;
    private final NewChatMessageRequest originalMessage;

    /**
     * Constructor
     *
     * @param user            The invoking user
     * @param command         The invoked command
     * @param originalMessage The {@link de.uol.swp.common.chat.request.NewChatMessageRequest}
     *                        that was made by the user
     */
    public NewChatCommandMessage(Actor user, String command, NewChatMessageRequest originalMessage) {
        this.user = user;
        this.command = command;
        this.originalMessage = originalMessage;
    }

    /**
     * Gets the command keyword
     *
     * @return The command keyword
     */
    public String getCommand() {
        return command;
    }

    /**
     * Gets the original message
     *
     * @return The original NewChatMessageRequest made by the user
     */
    public NewChatMessageRequest getOriginalMessage() {
        return originalMessage;
    }

    /**
     * Gets the invoking user
     *
     * @return The user invoking the command
     */
    public Actor getUser() {
        return user;
    }
}

package de.uol.swp.server.devmenu.message;

import de.uol.swp.common.chat.request.NewChatMessageRequest;
import de.uol.swp.common.user.User;
import de.uol.swp.server.message.AbstractServerInternalMessage;

public class NewChatCommandMessage extends AbstractServerInternalMessage {

    private final User user;
    private final String command;
    private final NewChatMessageRequest originalMessage;

    public NewChatCommandMessage(User user, String command, NewChatMessageRequest originalMessage) {
        this.user = user;
        this.command = command;
        this.originalMessage = originalMessage;
    }

    public String getCommand() {
        return command;
    }

    public NewChatMessageRequest getOriginalMessage() {
        return originalMessage;
    }

    public User getUser() {
        return user;
    }
}

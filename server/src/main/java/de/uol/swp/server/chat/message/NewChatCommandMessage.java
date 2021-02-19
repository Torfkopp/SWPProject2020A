package de.uol.swp.server.chat.message;

import de.uol.swp.common.user.User;
import de.uol.swp.server.message.AbstractServerInternalMessage;

public class NewChatCommandMessage extends AbstractServerInternalMessage {

    private final User user;
    private final String command;

    public NewChatCommandMessage(User user, String command) {
        this.user = user;
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public User getUser() {
        return user;
    }
}

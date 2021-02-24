package de.uol.swp.common.chat.response;

import de.uol.swp.common.chat.SystemMessage;
import de.uol.swp.common.chat.dto.SystemMessageDTO;
import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * Response message used when the server has to display something in the chat
 * <p>
 * For example: If someone uses a developer command in the chat that will
 * result in some output by the server (like /help), this response will be
 * sent by the server to encapsulate a SystemMessage containing the relevant
 * string to be displayed in the chat as a SystemMessage.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @see de.uol.swp.common.chat.SystemMessage
 * @since 2021-02-22
 */
public class SystemMessageResponse extends AbstractResponseMessage {

    private final String lobbyName;
    private final SystemMessage msg;

    /**
     * Constructor
     *
     * @param lobbyName The name of the lobby to send this SystemMessage to
     * @param content   The content of the SystemMessage
     */
    public SystemMessageResponse(String lobbyName, String content) {
        this.lobbyName = lobbyName;
        this.msg = new SystemMessageDTO(content);
    }

    /**
     * Gets the target lobby's name.
     *
     * @return The name of the target lobby
     */
    public String getLobbyName() {
        return lobbyName;
    }

    /**
     * Gets the SystemMessage object.
     *
     * @return The encapsulated SystemMessage
     */
    public SystemMessage getMsg() {
        return msg;
    }

    /**
     * Check if the ChatMessage message is destined for a lobby chat
     *
     * @return True, if the SystemMessage message is meant for a lobby chat; False if not
     */
    public boolean isLobbyChatMessage() {
        return lobbyName != null;
    }
}

package de.uol.swp.common.chat;

/**
 * Interface to unify SystemMessage objects
 * <p>
 * This interface allows for different types of SystemMessage since
 * not every client needs all information of all messages.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.dto.SystemMessageDTO
 * @see de.uol.swp.common.chat.ChatOrSystemMessage
 * @since 2021-02-22
 */
public interface SystemMessage extends ChatOrSystemMessage {}

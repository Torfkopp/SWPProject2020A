package de.uol.swp.common.chat;

import java.io.Serializable;

/**
 * Interface to unify objects that can appear in the chat
 * <p>
 * This interface serves to differentiate SystemMessage and ChatMessage
 * while unifying them under this interface so chat integration is easier.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.ChatMessage
 * @see de.uol.swp.common.chat.SystemMessage
 * @since 2021-02-22
 */
public interface ChatOrSystemMessage extends Serializable {}

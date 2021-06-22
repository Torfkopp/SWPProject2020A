package de.uol.swp.server.specialisedUtil;

import de.uol.swp.common.chat.request.NewChatMessageRequest;

import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Specialised class
 * for the command mapping
 *
 * @author Mario Fokken
 * @since 2021-06-18
 */
public class CommandMap extends HashMap<String, BiConsumer<List<String>, NewChatMessageRequest>> {}

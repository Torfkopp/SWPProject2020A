package de.uol.swp.common.chat.dto;

import de.uol.swp.common.I18nWrapper;

/**
 * Objects of this class are used to notify the Owner of the ready states
 * of Lobby members.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.dto.SystemMessageDTO
 * @since 2021-04-26
 */
public class ReadySystemMessageDTO extends SystemMessageDTO {

    /**
     * Constructor
     *
     * @param contentWrapper An I18nWrapper with the content of the message
     */
    public ReadySystemMessageDTO(I18nWrapper contentWrapper) {
        super(contentWrapper);
    }
}

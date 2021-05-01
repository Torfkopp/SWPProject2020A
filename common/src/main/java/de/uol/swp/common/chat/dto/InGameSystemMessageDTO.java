package de.uol.swp.common.chat.dto;

import de.uol.swp.common.I18nWrapper;

/**
 * Objects of this class are used to transfer InGameSystemMessages between the server
 * and clients
 *
 * @author Steven Luong
 * @see de.uol.swp.common.chat.dto.SystemMessageDTO
 * @since 2021-04-30
 */
public class InGameSystemMessageDTO extends SystemMessageDTO {

    /**
     * Constructor
     *
     * @param contentWrapper A I18nWrapper with the content of the message
     */
    public InGameSystemMessageDTO(I18nWrapper contentWrapper) {
        super(contentWrapper);
    }
}

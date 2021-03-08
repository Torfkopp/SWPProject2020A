package de.uol.swp.common.chat.dto;

import de.uol.swp.common.I18nWrapper;
import de.uol.swp.common.chat.SystemMessage;

/**
 * Objects of this class are used to transfer SystemMessages between the server
 * and clients
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.chat.SystemMessage
 * @since 2021-02-22
 */
public class SystemMessageDTO implements SystemMessage {

    private final I18nWrapper contentWrapper;

    /**
     * Constructor
     *
     * @param contentWrapper A ContentWrapper with the content of the message
     *
     * @since 2021-03-07
     */
    public SystemMessageDTO(I18nWrapper contentWrapper) {
        this.contentWrapper = contentWrapper;
    }

    @Override
    public String toString() {
        return contentWrapper.toString();
    }
}

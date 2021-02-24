package de.uol.swp.common.devmenu.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * Request used by the client to request a list of all the classes allowed to
 * be used in the /post command.
 * <p>
 * Only posted by the Developer Menu
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractRequestMessage
 * @see de.uol.swp.common.devmenu.response.DevMenuClassesResponse
 * @since 2021-02-22
 */
public class DevMenuClassesRequest extends AbstractRequestMessage {}

package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * A response that the change Password was successful
 * <p>
 * This response is only sent to clients that previously sent a ChangePasswordRequest
 * that was executed successfully, otherwise an ExceptionMessage would be sent.
 *
 * @author Steven Luong
 * @author Eric Vuong
 * @since 2020-12-03
 */
public class ChangePasswordSuccessfulResponse extends AbstractResponseMessage {
}

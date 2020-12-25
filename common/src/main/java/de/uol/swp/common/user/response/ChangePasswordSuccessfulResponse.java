package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * A response that the password was successfully changed
 *
 * This response is only sent to clients that previously sent a
 * successfully executed ChangePasswordRequest.
 * Otherwise, an ExceptionMessage would have been sent.
 *
 * @author Steven Luong
 * @author Eric Vuong
 * @since 2020-12-03
 *
 */

public class ChangePasswordSuccessfulResponse extends AbstractResponseMessage{

}
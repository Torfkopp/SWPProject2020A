package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * A response that the user deletion was successful
 * <p>
 * This response is only sent to clients that previously sent a DeleteUserRequest
 * that was executed successfully, otherwise an ExceptionMessage would be sent.
 *
 * @author Phillip-André Suhr
 * @see de.uol.swp.common.message.AbstractResponseMessage
 * @since 2020-11-02
 */
public class UserDeletionSuccessfulResponse extends AbstractResponseMessage {
}

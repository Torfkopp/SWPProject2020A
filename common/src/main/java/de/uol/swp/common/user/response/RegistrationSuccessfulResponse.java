package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * A response stating that the user registration was successful
 *
 * This response is only sent to clients that previously sent a
 * successfully executed RegisterUserRequest.
 * Otherwise, an ExceptionMessage would have been sent.
 *
 * @author Marco Grawunder
 * @since 2019-09-02
 */
public class RegistrationSuccessfulResponse extends AbstractResponseMessage {
}

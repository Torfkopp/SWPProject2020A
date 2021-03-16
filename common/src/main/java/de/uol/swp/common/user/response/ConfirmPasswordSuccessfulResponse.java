package de.uol.swp.common.user.response;

import de.uol.swp.common.message.AbstractResponseMessage;

/**
 * A response that the password was successfully confirmed
 * <p>
 * This response is only sent to clients that previously sent a
 * successfully executed ConfirmPasswordRequest.
 * Otherwise, an ExceptionMessage would have been sent.
 *
 * @author Alwin Bossert
 * @author Eric Vuong
 * @since 2021-03-16
 */
public class ConfirmPasswordSuccessfulResponse extends AbstractResponseMessage {}

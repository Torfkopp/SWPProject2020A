package de.uol.swp.common.user.request;

import de.uol.swp.common.message.AbstractRequestMessage;

/**
 * A request sent from client to server when a user wants to log out
 *
 * This message should be used when implementing the logout feature
 *
 * @author Marco Grawunder
 * @since 2019-08-07
 */

public class LogoutRequest extends AbstractRequestMessage{
	
	private static final long serialVersionUID = -5912075449879112061L;

	/**
	 * Constructor
	 *
	 * @since 2019-08-07
	 */
	public LogoutRequest() {
		super();
	}

}

package de.uol.swp.common.message;

/**
 * Encapsulates an Exception in a message object
 * 
 * @author Marco Grawunder
 * @since 2017-03-17
 */
public class ExceptionMessage extends AbstractResponseMessage{

	private static final long serialVersionUID = -7739395567707525535L;
	private final String exception;

	/**
	 * Constructor
	 *
	 * @param message String containing the cause of the exception
	 * @since 2017-03-17
	 */
	public ExceptionMessage(String message){
		this.exception = message;
	}

	/**
	 * Getter for the exception message
	 *
	 * @return String containing the cause of the exception
	 * @since 2017-03-17
	 */
	public String getException() {
		return exception;
	}
	
}

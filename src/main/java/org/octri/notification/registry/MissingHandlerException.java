package org.octri.notification.registry;

/**
 * Exception thrown when a required handler is missing.
 */
public class MissingHandlerException extends Exception {

	/**
	 * 
	 * @param message
	 *            the exception message
	 */
	public MissingHandlerException(String message) {
		super(message);
	}

}

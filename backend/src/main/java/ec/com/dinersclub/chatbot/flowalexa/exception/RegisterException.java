package ec.com.dinersclub.chatbot.flowalexa.exception;

public class RegisterException extends Exception {

	/**
	 * Serial generado.
	 */
	private static final long serialVersionUID = 3074968118819369791L;

	public RegisterException() {
	}

	public RegisterException(String message) {
		super(message);
	}

	public RegisterException(Throwable cause) {
		super(cause);
	}

	public RegisterException(String message, Throwable cause) {
		super(message, cause);
	}

	public RegisterException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

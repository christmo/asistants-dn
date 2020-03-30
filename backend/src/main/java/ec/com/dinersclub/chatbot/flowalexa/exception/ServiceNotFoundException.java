package ec.com.dinersclub.chatbot.flowalexa.exception;

public class ServiceNotFoundException extends Exception {

	/**
	 * Serial generado.
	 */
	private static final long serialVersionUID = 8811456208109682824L;

	public ServiceNotFoundException() {
		super();
	}

	public ServiceNotFoundException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

	public ServiceNotFoundException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ServiceNotFoundException(String arg0) {
		super(arg0);
	}

	public ServiceNotFoundException(Throwable arg0) {
		super(arg0);
	}

}

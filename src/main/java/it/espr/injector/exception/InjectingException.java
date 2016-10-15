package it.espr.injector.exception;

public abstract class InjectingException extends Exception {

	private static final long serialVersionUID = 1L;

	public InjectingException(String message, Throwable cause) {
		super(message, cause);
	}
}

package it.espr.injector.exception;

public class BeanException extends Exception {

	private static final long serialVersionUID = 1L;

	public BeanException(String message) {
		super(message);
	}

	public BeanException(String message, Throwable cause) {
		super(message, cause);
	}
}

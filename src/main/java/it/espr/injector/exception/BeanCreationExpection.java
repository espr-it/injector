package it.espr.injector.exception;

public class BeanCreationExpection extends BeanException {

	private static final long serialVersionUID = 1L;

	public BeanCreationExpection(String message) {
		super(message);
	}

	public BeanCreationExpection(String message, Throwable cause) {
		super(message, cause);
	}
}
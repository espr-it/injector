package it.espr.injector.exception;

public class BeanCreationExpection extends InjectingException {

	private static final long serialVersionUID = 1L;

	public BeanCreationExpection(String message, Throwable cause) {
		super(message, cause);
	}
}

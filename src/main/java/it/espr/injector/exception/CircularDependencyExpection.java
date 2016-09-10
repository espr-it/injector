package it.espr.injector.exception;

public class CircularDependencyExpection extends BeanException {

	private static final long serialVersionUID = 1L;

	public CircularDependencyExpection() {
		super();
	}

	public CircularDependencyExpection(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CircularDependencyExpection(String message, Throwable cause) {
		super(message, cause);
	}

	public CircularDependencyExpection(String message) {
		super(message);
	}

	public CircularDependencyExpection(Throwable cause) {
		super(cause);
	}
}

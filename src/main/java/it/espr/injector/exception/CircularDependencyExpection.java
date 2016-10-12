package it.espr.injector.exception;

public class CircularDependencyExpection extends ClassInspectionExpection {

	private static final long serialVersionUID = 1L;

	public CircularDependencyExpection(String message) {
		super(message);
	}

	public CircularDependencyExpection(String message, Throwable cause) {
		super(message, cause);
	}
}

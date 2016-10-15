package it.espr.injector.exception;

public class ClassInspectionExpection extends InjectingException {

	private static final long serialVersionUID = 1L;

	public ClassInspectionExpection(String message) {
		this(message, null);
	}

	public ClassInspectionExpection(String message, Throwable cause) {
		super(message, cause);
	}
}

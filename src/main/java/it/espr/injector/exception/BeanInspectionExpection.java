package it.espr.injector.exception;

public class BeanInspectionExpection extends BeanException {

	private static final long serialVersionUID = 1L;

	public BeanInspectionExpection(String message) {
		super(message);
	}

	public BeanInspectionExpection(String message, Throwable cause) {
		super(message, cause);
	}
}

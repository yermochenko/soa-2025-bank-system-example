package by.vsu.exception;

public class ApplicationException extends Exception {
	public ApplicationException(String message) {
		super(message);
	}

	public ApplicationException(Throwable cause) {
		super(cause);
	}
}

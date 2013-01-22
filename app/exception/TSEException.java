package exception;

public class TSEException extends Exception {

	private boolean tryAgain;

	public TSEException() {

	}

	public TSEException(Exception ex, boolean repeat) {

		super(ex);
		this.setTryAgain(repeat);
	}

	public TSEException(String message, boolean repeat) {
		super(message);
	}

	public TSEException(String message) {
		super(message);
	}

	public boolean isTryAgain() {
		return tryAgain;
	}

	public void setTryAgain(boolean tryAgain) {
		this.tryAgain = tryAgain;
	}

}

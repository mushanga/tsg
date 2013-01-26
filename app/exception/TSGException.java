package exception;

public class TSGException extends Exception {

	private boolean tryAgain;

	public TSGException() {

	}

	public TSGException(Exception ex, boolean repeat) {

		super(ex);
		this.setTryAgain(repeat);
	}

	public TSGException(String message, boolean repeat) {
		super(message);
	}

	public TSGException(String message) {
		super(message);
	}

	public boolean isTryAgain() {
		return tryAgain;
	}

	public void setTryAgain(boolean tryAgain) {
		this.tryAgain = tryAgain;
	}

}

package exception;

public class NoAvailableTokenException extends TSEException {

	public NoAvailableTokenException(){
		super("No available token found!");
	}
}

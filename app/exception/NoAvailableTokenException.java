package exception;

public class NoAvailableTokenException extends TSGException {

	public NoAvailableTokenException(){
		super("No available token found!");
	}
}

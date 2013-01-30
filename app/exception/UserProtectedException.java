package exception;

public class UserProtectedException extends TSGException {

	long protectedUserId;

	public UserProtectedException(String string) {
		super("User's account is protected: " + string);
	}


}

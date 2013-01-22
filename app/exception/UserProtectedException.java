package exception;

public class UserProtectedException extends TSEException {

	long protectedUserId;

	public UserProtectedException(long protectedUserId) {
		super("User's account is protected: " + protectedUserId);
		this.protectedUserId = protectedUserId;
	}


}

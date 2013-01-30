package exception;

public class UserDoesNotExistException extends TSGException {

   public String screenName;
   public Long userId;
   public UserDoesNotExistException(Long userId) {
      super("User does not exist: " + userId);
      this.userId= userId;
   }
   public UserDoesNotExistException(String screenName) {
      super("User does not exist: " + screenName);
      this.screenName= screenName;
   }


}

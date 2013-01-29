package twitter;

import java.util.List;

import exception.NoAvailableTokenException;
import exception.TSGException;
import exception.UserProtectedException;

import models.Reply;
import models.SearchKey;
import models.User;

public interface TwitterProxy {
	
	public List<Long> getFollowingIds(Long id) throws NoAvailableTokenException, UserProtectedException;

	User getUser(Long id) throws NoAvailableTokenException, UserProtectedException;

	User getUser(String screenName) throws NoAvailableTokenException, UserProtectedException;

   List<User> getUsers(List<Long> userIdList) throws NoAvailableTokenException;
   List<User> getUsersSecondary(List<Long> userIdList) throws NoAvailableTokenException;
  
   List<User> searchUser(String query) throws NoAvailableTokenException;
}

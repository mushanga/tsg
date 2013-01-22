package twitter;

import java.util.List;

import exception.NoAvailableTokenException;
import exception.TSEException;
import exception.UserProtectedException;

import models.Reply;
import models.SearchKey;
import models.User;

public interface TwitterProxy {
	
	public List<Long> getFollowingIds(Long id) throws NoAvailableTokenException, UserProtectedException;

	User getUser(Long id) throws NoAvailableTokenException, UserProtectedException;

	User getUser(String screenName) throws NoAvailableTokenException, UserProtectedException;

	List<User> getUsers(List<Long> userIdList) throws NoAvailableTokenException;
}

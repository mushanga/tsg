package twitter;

import play.Logger;
import exception.NoAvailableTokenException;
import exception.TSGException;
import models.User;

public class TwitterProxyFactory {
	
	public static TwitterProxy newInstance(String consKey, String consSecret, String authToken, String authTokenSecret){
		return new TwitterProxyImpl(consKey, consSecret, authToken, authTokenSecret);
	}
	public static TwitterProxy newInstance(User user){
		return new TwitterProxyImpl(user);
	}
	public static TwitterProxy newInstance(Long tokenOwnerId) throws TSGException{
		return new TwitterProxyImpl(tokenOwnerId);
	}
	public static TwitterProxy defaultInstance() throws NoAvailableTokenException{
		return  new TwitterProxyImpl();
	}
}

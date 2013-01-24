package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.User;
import play.Logger;
import play.cache.Cache;
import play.db.jpa.JPA;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;
import exception.NoAvailableTokenException;
import exception.TSEException;
import exception.UserProtectedException;

public class UserLookup {


	private static int MEM = 0;
	private static int DB = 1;
	private static int TWITTER = 2;
	public static User getUser(String screenName) {
		return getUser(screenName, TWITTER);
	}
	public static User getUser(long id) {
		return getUser(id, TWITTER);
	}
	
	private static User getUser(long id, int maxDepth) {
	    User user = Cache.get("user_id_" + id, User.class);
	    if(user == null && maxDepth> MEM) {
	        user = User.findByTwitterId(Long.valueOf(id));
	        if(user==null && maxDepth> DB){
	        	TwitterProxy twitter;
				try {
					twitter = TwitterProxyFactory.defaultInstance();
					user = twitter.getUser(Long.valueOf(id));
					user.save();
					
				} catch (NumberFormatException e) {
					Logger.error(e, e.getMessage());
				} catch (NoAvailableTokenException e) {
					Logger.error(e, e.getMessage());
				} catch (UserProtectedException e) {
					Logger.error(e, e.getMessage());
				} catch (TSEException e) {
					Logger.error(e, e.getMessage());
				}
	        }
	        if(user!=null){
		        Cache.set("user_id_" + id, user, "30mn");	        	
	        }
	    }
	 
		return user;
	}
	public static User getUser(String screenName, int maxDepth) {
		screenName = screenName.toLowerCase();
		
	    User user = Cache.get("user_name_" + screenName, User.class);
	    if(user == null && maxDepth> MEM) {
	        user = User.findByScreenName(screenName);
	        if(user==null && maxDepth> DB){
	        	TwitterProxy twitter;
				try {
					twitter = TwitterProxyFactory.defaultInstance();
					user = twitter.getUser(screenName);
					user.save();
					
				} catch (NumberFormatException e) {
					Logger.error(e, e.getMessage());
				} catch (NoAvailableTokenException e) {
					Logger.error(e, e.getMessage());
				} catch (UserProtectedException e) {
					Logger.error(e, e.getMessage());
				} catch (TSEException e) {
					Logger.error(e, e.getMessage());
				}
	        }
	        if(user!=null){
		        Cache.set("user_name_" + screenName, user, "30mn");	        	
	        }
	    }
	 
		return user;
	}

	public static Set<User> getUsers(Set<Long> userIdList){
	   List<Long> missingUserIds =new ArrayList<Long>();
	   Set<User> users = new HashSet<User>();
	   for(Long userId : userIdList){

		   User user = getUser(userId,DB);
		   if(user==null){
			   missingUserIds.add(userId);
		   }else{
			   users.add(user);
		   }
	   }
	   if(missingUserIds.size()>0){

		   TwitterProxy twitter;
			try {
				twitter = TwitterProxyFactory.defaultInstance();
				List<User> newUsers = twitter.getUsers(missingUserIds);
				int notFlushed = 0;
				
				for(User user : newUsers){
					user.save();
					
				
				}
				
				users.addAll(newUsers);
				
			} catch (NumberFormatException e) {
				Logger.error(e, e.getMessage());
			} catch (TSEException e) {
				Logger.error(e, e.getMessage());
			}
	   }
	return users;
	}
	 

}

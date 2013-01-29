package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.User;
import play.Logger;
import play.cache.Cache;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;
import exception.NoAvailableTokenException;
import exception.TSGException;
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
				} catch (TSGException e) {
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
		screenName = screenName.replace(" ", "");
	    User user = Cache.get("user_name_" + screenName, User.class);
	    if(user == null && maxDepth> MEM) {
	        user = User.findByScreenName(screenName);
	        if(user==null && maxDepth> DB){
	        	TwitterProxy twitter;
				try {
					twitter = TwitterProxyFactory.defaultInstance();
					user = twitter.getUser(screenName);
					if(user!=null){
	               user.save();
					   
					}
					
				} catch (NumberFormatException e) {
					Logger.error(e, e.getMessage());
				} catch (NoAvailableTokenException e) {
					Logger.error(e, e.getMessage());
				} catch (UserProtectedException e) {
					Logger.error(e, e.getMessage());
				} catch (TSGException e) {
               Logger.error(e, e.getMessage());
            } catch (Exception e) {
               Logger.error(e, e.getMessage());
            }
	        }
	        if(user!=null){
		        Cache.set("user_name_" + screenName, user, "30mn");	        	
	        }
	    }
	 
		return user;
	}
	

	public static List<User> getUsers(List<Long> userIdList){
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
				List<User> newUsers = twitter.getUsersSecondary(missingUserIds);
				for(User user : newUsers){
				   
					try {
                  user.save();
               } catch (Exception e) {
                  Logger.error(e, e.getMessage());
               }			
				}
				
				users.addAll(newUsers);
				
			} catch (NumberFormatException e) {
				Logger.error(e, e.getMessage());
			} catch (TSGException e) {
				Logger.error(e, e.getMessage());
			}
	   }
	   ArrayList<User> retUsers = new ArrayList<User>(users);
	   UserOrderComparator userComparator = new UserOrderComparator(userIdList);
	   Collections.sort(retUsers,userComparator);
   
	   return retUsers;
	}

  
	 

}

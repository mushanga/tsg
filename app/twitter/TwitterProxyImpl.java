package twitter;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import jobs.OAuthSettings;
import models.User;
import models.UserToken;

import org.apache.http.message.BasicNameValuePair;

import play.Logger;
import play.Play;
import play.cache.Cache;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import util.UserLookup;
import util.Util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import exception.NoAvailableTokenException;
import exception.TSGException;
import exception.UserDoesNotExistException;
import exception.UserProtectedException;

public class TwitterProxyImpl implements TwitterProxy {
	private static final SimpleDateFormat dt = new SimpleDateFormat(
			"yyyy-mm-dd");
	
	Twitter twitter = null;

	UserToken token; 
	
	public TwitterProxyImpl(User user) {
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(OAuthSettings.getConsumerKey(),
				OAuthSettings.getConsumerSecret());
		twitter.setOAuthAccessToken(new AccessToken(user.authToken,
				user.authTokenSecret));
	}
	public TwitterProxyImpl(String consKey, String consSecret, String authToken, String authTokenSecret) {
		
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(OAuthSettings.getConsumerKey(), OAuthSettings.getConsumerSecret());
		twitter.setOAuthAccessToken(new AccessToken(authToken, authTokenSecret));
	}
	public TwitterProxyImpl(Long tokenOwnerId) throws TSGException {
		
		token = UserToken.getByOwnerId(tokenOwnerId);
		
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(OAuthSettings.getConsumerKey(), OAuthSettings.getConsumerSecret());
		twitter.setOAuthAccessToken(new AccessToken(token.accessToken, token.accessTokenSecret));
	}
	public TwitterProxyImpl() throws NoAvailableTokenException {
		
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(Play.configuration.getProperty("twitstreetConsumerKey"), Play.configuration.getProperty("twitstreetConsumerSecret"));
		findAvailableToken();
	}

	
	

	@Override
	public List<Long> getFollowingIds(Long id) throws NoAvailableTokenException, UserProtectedException, UserDoesNotExistException  {
		ArrayList<Long> followingList = new ArrayList<Long>();
		try {
			for (Long friendId : twitter.getFriendsIDs(id, -1).getIDs()) {
				followingList.add(friendId);
			}
		} catch (TwitterException e1) {
			
			boolean repeat = false;
       
         repeat = handleTwitterException(e1, new String[]{"getFollowingIds",String.valueOf(id)});
         
			if(repeat){
				getFollowingIds(id);
			}else{
			   followingList=null;
			}
		} 
		return followingList;
	}

	@Override
	public User getUserById(Long userId) throws NoAvailableTokenException, UserDoesNotExistException  {
		User user = null;
		try {
			twitter4j.User twuser = twitter.showUser(userId);
			user = new User(twuser);
		} catch (TwitterException e1) {
			
			boolean repeat = false;
			try {
				repeat = handleTwitterException(e1,new String[]{"getUserById",String.valueOf(userId)});
			} catch (UserProtectedException e) {
				Logger.error(e, "This method shouldn't have given 'user protected' error!\n"+e.getMessage());
			}
			if(repeat){
				user = getUserById(userId);
			}
		} catch (Exception e1) {
			Logger.error(e1, e1.getMessage());

		}
		return user;
	}
	

	@Override
	public User getUserByScreenName(String screenName) throws NoAvailableTokenException, UserDoesNotExistException  {
		User user = null;
		try {
			twitter4j.User twuser = twitter.showUser(screenName);
			user = new User(twuser);
		} catch (TwitterException e1) {
			
			boolean repeat = false;
			try {
				repeat = handleTwitterException(e1,new String[]{"getUserByScreenName",screenName});
			} catch (UserProtectedException e) {
				Logger.error(e, "This method shouldn't have given 'user protected' error!\n"+e.getMessage());
			} 
			if(repeat){
				user = getUserByScreenName(screenName);
			}
		} catch (Exception e1) {
			Logger.error(e1, e1.getMessage());

		}
		return user;
	}

	@Override
	public List<User> getUsers(List<Long> idList) throws NoAvailableTokenException  {
		
		List<User> users = new ArrayList<User>();

		if (idList.size() > 100) {
			int size = idList.size();
			users.addAll(getUsers(idList.subList(0, size / 2)));
			users.addAll(getUsers(idList.subList((size / 2), size)));

		} else {

			try {
				
				long[] ids = new long[idList.size()];
				int i = 0;
				for(Long id : idList){
					ids[i] = id;
					i++;
				}
				 ResponseList<twitter4j.User> t4jUsers = twitter.lookupUsers(ids);
				 if(t4jUsers!=null){

					 for(twitter4j.User tu : t4jUsers){
						 users.add(new User(tu));
						 
					 }
				 }
			}catch (TwitterException e1) {
				
				boolean repeat = false;
				try {
					repeat = handleTwitterException(e1,new String[]{"getUsers",getParametersAsString(idList)});
				} catch (UserProtectedException e) {
					Logger.error(e, "This method shouldn't have given 'user protected' error!\n"+e.getMessage());
				} catch (UserDoesNotExistException e) {
               Logger.error(e.getMessage());
            }
				if(repeat){
					users = getUsers(idList);
				}
			} catch (Exception e1) {
				Logger.error(e1, e1.getMessage());

			}
		}

		return users;
	
	}
	

   public List<User> getUsersSecondary(List<Long> idList) {
      List<User> users = new ArrayList<User>();

      if (idList.size() > 100) {
         int size = idList.size();
         users.addAll(getUsersSecondary(idList.subList(0, size / 2)));
         users.addAll(getUsersSecondary(idList.subList((size / 2), size)));

      } else {

         try {
            ArrayList<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();
            String ids = Util.getIdListAsCommaSeparatedString(idList);
            nvps.add(new BasicNameValuePair("user_id", ids));

            String resp = OAuth.getInstance().get(token.accessToken, token.accessTokenSecret, "users/lookup.json", nvps);

            Type listOfTestObject = new TypeToken<List<UserJSONImpl>>() {
            }.getType();

            Gson gson = new Gson();

            List<UserJSONImpl> tusers = ((List<UserJSONImpl>) gson.fromJson(resp, listOfTestObject));
            
            for(UserJSONImpl tu: tusers){
               User user = new User(tu);
               user.save();
               users.add(user);
            }
           
         } catch (Exception e) {
            Logger.error(e.getMessage(), e);
         }
      }

      return users;

   }

	private void findAvailableToken() throws NoAvailableTokenException{
		token = UserToken.getAvailabeToken();
		if (token != null) {
			twitter.setOAuthAccessToken(new AccessToken(token.accessToken, token.accessTokenSecret));
			//token.setInUse();
		
		} else {
		   Logger.info("Setting all tokens to valid");
		   UserToken.setAllInvalidToValid();
			throw new NoAvailableTokenException();

		}
	}

   private String getParametersAsString(String[] params){
      String ret = "";
      if(params!=null){
         for(String param: params){
            ret = ret+","+param;
         }
      }
      if(ret.length()>200){
         ret = ret.substring(0,200);
      }
      return ret;
   }
   private String getParametersAsString(List<Long> params){
      String ret = "";
      if(params!=null){
         for(Long param: params){
            ret = ret+","+param;
         }
      }
      if(ret.length()>200){
         ret = ret.substring(0,200);
      }
      return ret;
   }
	
	private boolean handleTwitterException(TwitterException e1,  String[] parameters) throws NoAvailableTokenException, UserProtectedException, UserDoesNotExistException {
		boolean repeat = false;		
		String paramStr = getParametersAsString(parameters);
		if (e1.exceededRateLimitation()) {
			token.setRateLimited(e1.getRateLimitStatus().getResetTimeInSeconds());
			Logger.info("Rate limited: "+token.ownerScreenName);
			
			repeat = true;
			findAvailableToken();
		
		} else if (e1.getMessage().contains("Invalid or expired token")) {
			token.setInvalid();			
			Logger.info("Invalid token: " + token.ownerScreenName);
			Logger.info(e1.getMessage());
			
			repeat = true;			
			findAvailableToken();
		} else if (e1.getMessage().contains("Not authorized")) {
		
			throw new UserProtectedException(paramStr);
		} else if (e1.getMessage().contains("connect timed out")) {
         Logger.info("Connect timed out: " + token.ownerScreenName);
         repeat = true;    
         findAvailableToken();
      }else if (e1.getMessage().contains("Read-only application cannot POST")) {
         Logger.info("Read-only application cannot POST: " + token.ownerScreenName);
         repeat = true;    
         findAvailableToken();
      } 
      else if (e1.getMessage().contains("such as a user, does not exists")) {
         Logger.info("Something wrong with the user : " + paramStr);
         if(parameters[0].equalsIgnoreCase("getUserById")){
         try{
            throw new UserDoesNotExistException(Long.valueOf(parameters[1]));
         }catch(NumberFormatException ex){
            Logger.info("NumberFormatException : " + parameters[1]);
         }
            
         }else{
            throw new UserDoesNotExistException(parameters[1]);
         }
      }
      else {
			Logger.info("UNKNOWN ERROR: " + token.ownerScreenName);
			Logger.error(e1.getMessage(), e1);

		}
		return repeat;
	}
	
	private boolean handleTwitterException(TwitterException e1) throws NoAvailableTokenException, UserProtectedException, UserDoesNotExistException {
		return handleTwitterException(e1, null);

	}
	public List<User> searchUserThroughTwitter(String query) throws NoAvailableTokenException {
	   List<User> users = new ArrayList<User>();

	   User exactMatch = null;
      try {
         exactMatch = UserLookup.getUser(query);
      } catch (UserDoesNotExistException e3) {
         
      }
	   if(exactMatch!=null){
	      users.add(exactMatch);
	   }
	  
	   ResponseList<twitter4j.User> t4jUsers = null;
	   try {
	      t4jUsers = twitter.searchUsers(query, 1);
	   } catch (TwitterException e) {

	      boolean repeat = false;
	      try {
	         repeat = handleTwitterException(e,new String[]{"searchUserThroughTwitter",query});
	      } catch (UserProtectedException e1) {
	         Logger.error(e1, "This method shouldn't have given 'user protected' error!\n"+e1.getMessage());
	      } catch (UserDoesNotExistException e2) {
            Logger.info(e2.getMessage());
         }
	      if(repeat){
	         users = searchUserThroughTwitter(query);
	      }
	   }
	   if(t4jUsers!=null){

	      for(twitter4j.User tu : t4jUsers){
	         if(exactMatch==null || tu.getId()!=exactMatch.twitterId){
	            users.add(new User(tu));
	            
	         }

	      }
	   }


      return users;
   
   
   }
	
	private class UserSearchResult implements Serializable{
	   
	   List<Long> userIds = new ArrayList<Long>();
	   String value = "";
      
	   public UserSearchResult(List<Long> users) {
         super();
         this.userIds = users;
         if(Util.isValid(this.userIds)){
            
            for(Long us : this.userIds){
               value = value+"-"+us;
            }
            value = value.substring(1);
         }
      }
	   
      
      public UserSearchResult(String dashSeparated) {
         super();
         this.value = dashSeparated;
         String[] userIdStrArr = this.value.split("-");
         if(userIdStrArr.length>0){
            
            for(String us : userIdStrArr){
               try{
                  this.userIds.add(Long.valueOf(us));
                  
               }catch(NumberFormatException ex){
                  
               }
            }
         }
      }


      @Override
	   public String toString() {
	      
	      return value;
	   }
	}

   @Override
	public List<User> searchUser(String query) throws NoAvailableTokenException {
	   String cacheVal = (String) Cache.get("search_query_" + query);
	   List<User> retList = new ArrayList<User>();
      if(cacheVal == null) {
         List<User> resultList = searchUserThroughTwitter(query);
         if(resultList!=null){         
            List<Long> idList = new ArrayList<Long>();
            for(User us : resultList){
               User userInDB = User.findByTwitterId(us.twitterId);
               if(userInDB==null){
                  us.save();
               }
               idList.add(us.twitterId);
            }
            

            UserSearchResult userSR = new UserSearchResult(idList);   
             Cache.set("search_query_"+query,userSR.toString(),"30mn");
             cacheVal = (String) Cache.get("search_query_"+query);
         }
      }
      
      if(cacheVal!=null){

         UserSearchResult userSR = new UserSearchResult(cacheVal);
      
         retList =  UserLookup.getUsers(userSR.userIds);
      }
      
      if(Util.isValid(retList) && retList.size()>10){
         retList = retList.subList(0, 10);
      }
     return retList ;
  }
}

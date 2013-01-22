package controllers;

import java.util.Calendar;

import jobs.OAuthSettings;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import models.User;
import play.cache.Cache;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Router;
import play.mvc.Util;
import play.mvc.Scope.Params;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class Auth extends Controller{
	
	private final static String ORIGINAL_URL_SESSION_KEY = "original_url";
	static ServiceBuilder sb = new ServiceBuilder()
    .provider(TwitterApi.class)
    .apiKey(OAuthSettings.getConsumerKey())
    .apiSecret(OAuthSettings.getConsumerSecret()).callback(Router.getFullUrl("Auth.twitterCallback"));
    
    public static void login() {
        if (Cache.get(session.getId()) != null) {
            redirect("/");
        }
        flash.keep(ORIGINAL_URL_SESSION_KEY);
        if (request.isAjax()) {
            forbidden();
        }
        render();
    }
    
    public static void logout() {
        Long userId = Cache.get(session.getId(), Long.class);
        // see NotificationHelper, get method
        Cache.delete(userId + "_n");
        Cache.delete(session.getId());
        session.clear();
        redirect("/");
    }


    @Util
    private static void afterLogin(String sessionId, Long userId) {
        // cache replaces may be unnecessary ?
        if (Cache.get(sessionId) == null) {
            Cache.add(sessionId, userId);
        } else {
            Cache.replace(sessionId, userId);
        }
        redirect(flash.get(ORIGINAL_URL_SESSION_KEY) != null ? flash.get(ORIGINAL_URL_SESSION_KEY) : "/");
    }
    
    @Util
    public static User getCurrentUser() {
        Long userId = Cache.get(session.getId(), Long.class);
        if (userId == null) {
            session.clear();
            flash.put(ORIGINAL_URL_SESSION_KEY, request.url);
            login();
        }
        return User.findById(userId);
    }
    

    public static void twitterAuthentication(){
        OAuthService service = sb.build();
        Token requestToken = service.getRequestToken();
        Cache.add(requestToken.getToken(), requestToken, "3min");
        String authorizationUrl = service.getAuthorizationUrl(requestToken);
        redirect(authorizationUrl);
    }
    
    public static void twitterCallback(String oauth_token, String oauth_verifier, String denied) throws TwitterException {
        if (denied != null) {
            Cache.delete(oauth_token);
            login();
        }
        Token token = Cache.get(oauth_token, Token.class);
        Cache.delete(oauth_token);
        
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(OAuthSettings.getConsumerKey(),
        		OAuthSettings.getConsumerSecret());
        RequestToken requestToken = new RequestToken(token.getToken(), token.getSecret());
        AccessToken accessToken = twitter.getOAuthAccessToken(requestToken,
        		oauth_verifier);
        
        long userId = accessToken.getUserId();
        String authToken = accessToken.getToken();
		String authTokenSecret = accessToken.getTokenSecret();
		twitter.setOAuthAccessToken(accessToken);
		twitter4j.User twUser =  twitter.showUser(userId);
        User user = User.findByTwitterId(userId);
        if(user==null){
        	user = new User(twUser, authToken, authTokenSecret);
        	user.firstLogin = Calendar.getInstance().getTime();
        	user.lastLogin = user.firstLogin;
        	
        }
        else{
        	user.updateTwData(twUser,authToken,authTokenSecret);
        	user.lastLogin = Calendar.getInstance().getTime();
        }
    	user.save();
    	addFriends(user);
    	afterLogin(session.getId(), user.id);
    }
    
    private static void addFriends(User user){
    	TwitterProxy twitterProxy = TwitterProxyFactory.newInstance(user);
//    	twitterProxy.addFriends();
    }
    
  }

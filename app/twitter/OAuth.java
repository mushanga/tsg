package twitter;

import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.message.BasicNameValuePair;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import util.PropsConfigMgrImpl;
import util.Util;

import com.google.gson.Gson;

public class OAuth {
	private static PropsConfigMgrImpl propsMgr = PropsConfigMgrImpl.getInstance();
	public static String API_KEY = propsMgr.getTwitstreetApiKey();
	public static String API_SECRET =  propsMgr.getTwitstreetApiSecret();


	public static String COOKIE_NAME = "access_token";
	
	private static OAuth instance = new OAuth();
	OAuthService service;


	public static OAuth getInstance() {
		return instance;
	}

	

	private OAuth() {
		service = new ServiceBuilder().provider(TwitterApi.class).apiKey(API_KEY).apiSecret(API_SECRET).build();

	}

	public Token getRequestToken() {
		return service.getRequestToken();
	}

	public String getAuthUrl() {
		String authUrl = service.getAuthorizationUrl(service.getRequestToken());
		return authUrl;
	}
   public String get(String apiKey, String apiSecret, String token, String secret,String url,  List<BasicNameValuePair> nvps) {
      
      OAuthService service = new ServiceBuilder().provider(TwitterApi.class).apiKey(apiKey).apiSecret(apiSecret).build();
      
         OAuthRequest req = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/"+url);
         if(nvps!=null){

            for(BasicNameValuePair nvp : nvps){

               req.addQuerystringParameter(nvp.getName(), nvp.getValue());
            }
         }
         
         service.signRequest(new Token(token, secret), req); // the access token from step
                                       // 4
         Response resp = req.send();
         //System.out.println(resp.getBody());
      
         return resp.getBody();
      
   }
   public String get(String token, String secret, String url,  List<BasicNameValuePair> nvps) {
      
     
         OAuthRequest req = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/"+url);
         if(nvps!=null){

            for(BasicNameValuePair nvp : nvps){

               req.addQuerystringParameter(nvp.getName(), nvp.getValue());
            }
         }
         
         service.signRequest(new Token(token, secret), req); // the access token from step
                                       // 4
         Response resp = req.send();
         //System.out.println(resp.getBody());
      
         return resp.getBody();
      
   }
//	public Announcer handle(HttpServletRequest request, HttpServletResponse response) {
//		Announcer announcer = null;
//
//		String oAuthToken = request.getParameter("oauth_token");
//		String oAuthVerifier = request.getParameter("oauth_verifier");
//		if (Util.stringIsValid(oAuthVerifier)) {
//
//			Verifier v = new Verifier(oAuthVerifier);
//			Token accessToken = service.getAccessToken(new Token(oAuthToken, API_SECRET), v);
//
//
//			Cookie coo= new Cookie(COOKIE_NAME,accessToken.getToken());
//			coo.setMaxAge(Integer.MAX_VALUE);
//			coo.setPath("/");
//			response.addCookie(coo);
//			
//			
//			OAuthRequest req = new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
//			service.signRequest(accessToken, req); // the access token from step
//													// 4
//			Response resp = req.send();
//			System.out.println(resp.getBody());
//			Gson gson = new Gson();
//
//			UserJSONImpl userjson = gson.fromJson(resp.getBody(), UserJSONImpl.class);
//
//			Announcer newAnnouncer = new Announcer();
//			newAnnouncer.setId(userjson.getId());
//			newAnnouncer.setScreenName(userjson.getScreenName());
//			newAnnouncer.setFollower(userjson.getFollowersCount());
//			newAnnouncer.setFollowing(userjson.getFriendsCount());
//			newAnnouncer.setAccessToken(accessToken.getToken());
//			newAnnouncer.setAccessTokenSecret(accessToken.getSecret());
//			newAnnouncer.setPictureUrl(userjson.getPicUrl());
//
//			AnnouncerMgrImpl.getInstance().addAnnouncer(newAnnouncer);
//			
//			announcer = newAnnouncer;
//		}
//		
//		
//		return announcer;
//		
//	}	

}

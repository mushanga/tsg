package plugins;

import java.util.ArrayList;
import java.util.List;

import exception.NoAvailableTokenException;
import exception.TSGException;
import exception.UserProtectedException;

import models.FollowingList;
import models.Link;
import models.UserGraph;
import models.UserToken;
import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;
import twitter4j.TwitterException;
import util.PropsConfigMgrImpl;

public class TSGHiddenPropsPlugin extends PlayPlugin {
	PropsConfigMgrImpl props = PropsConfigMgrImpl.getInstance();

   public static final String TWITSTREET_CONSUMER_KEY = "twitstreetConsumerKey"; 
   public static final String TWITSTREET_CONSUMER_SECRET = "twitstreetConsumerSecret"; 
   public static final String TWITTER_CONSUMER_KEY = "consumerKey"; 
   public static final String TWITTER_CONSUMER_SECRET = "consumerSecret";  
	
	@Override
	public void onConfigurationRead() {
		super.onConfigurationRead();
      Play.configuration.setProperty("db.user", props.getDbUserName());       
      Play.configuration.setProperty("db.pass", props.getDbPassword());
      Play.configuration.setProperty("mail.smtp.user", props.getMailDealer());       
      Play.configuration.setProperty("mail.smtp.pass", props.getMailDealerPassword());
	     Play.configuration.setProperty(TWITTER_CONSUMER_KEY, props.getApiKey());
	     Play.configuration.setProperty(TWITTER_CONSUMER_SECRET, props.getApiSecret());
	     Play.configuration.setProperty(TWITSTREET_CONSUMER_KEY, props.getTwitstreetApiKey());
	     Play.configuration.setProperty(TWITSTREET_CONSUMER_SECRET, props.getTwitstreetApiSecret());
	}

}

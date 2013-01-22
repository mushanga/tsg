package plugins;

import java.util.ArrayList;
import java.util.List;

import exception.NoAvailableTokenException;
import exception.TSEException;
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
	
	@Override
	public void onConfigurationRead() {
		super.onConfigurationRead();
	     Play.configuration.setProperty("db.user", props.getDbUserName());

	     Logger.info("TSGHiddenPropsPlugin: props.getDbUserName(): "+props.getDbUserName());
	     Play.configuration.setProperty("db.pass", props.getDbPassword());
	     Play.configuration.setProperty("consumerKey", props.getApiKey());
	     Play.configuration.setProperty("consumerSecret", props.getApiSecret());
	     Play.configuration.setProperty("twitstreetConsumerKey", props.getTwitstreetApiKey());
	     Play.configuration.setProperty("twitstreetConsumerSecret", props.getTwitstreetApiSecret());
	}

}

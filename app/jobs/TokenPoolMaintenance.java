package jobs;

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
import play.jobs.Every;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;
import twitter4j.TwitterException;

@Every("5s")
public class TokenPoolMaintenance extends Job {

	
	@Override
	public void doJob() {
		try {
			super.doJob();
		} catch (Exception e) {
			Logger.error(e, e.getMessage());
		}
		try {
			UserToken.checkRateLimitedTokens();
			
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
	}

	
}

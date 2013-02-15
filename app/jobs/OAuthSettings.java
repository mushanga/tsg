package jobs;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import plugins.TSGHiddenPropsPlugin;


public class OAuthSettings extends Job{
	private static String consumerKey;
	private static String consumerSecret;
	
	
	public void doJob(){
		consumerKey = Play.configuration.getProperty(TSGHiddenPropsPlugin.TWITTER_CONSUMER_KEY);
		consumerSecret = Play.configuration.getProperty(TSGHiddenPropsPlugin.TWITTER_CONSUMER_SECRET);
	}
	
	public static String getConsumerKey(){return consumerKey;}
	public static String getConsumerSecret(){return consumerSecret;}
}

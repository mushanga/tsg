package jobs;

import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;


public class OAuthSettings extends Job{
	private static String consumerKey;
	private static String consumerSecret;
	
	
	public void doJob(){
		consumerKey = Play.configuration.getProperty("consumerKey");
		consumerSecret = Play.configuration.getProperty("consumerSecret");
	}
	
	public static String getConsumerKey(){return consumerKey;}
	public static String getConsumerSecret(){return consumerSecret;}
}

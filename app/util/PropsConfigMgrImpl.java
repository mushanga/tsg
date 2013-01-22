package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

public class PropsConfigMgrImpl {
	private HashMap<String, String> configMap = new HashMap<String, String>();
	public static final String DB_HOST = "dbhost";
	public static final String DB_PORT = "dbport";
	public static final String DB_ADMIN = "dbadmin";
	public static final String DB_PASSWORD = "dbpassword";
	public static final String DATABASE = "database";
	public static final String MAIL_DEALER = "mailDealer";
	public static final String MAIL_DEALER_PWD = "mailDealerPassword";
	public static final String MAIL_RECIPIENTS = "mailRecipients";
	public static final String STATISTIC_PERIOD = "statisticPeriod";
	public static final String STATISTIC_LOG_NUMBER = "statisticLogNumber";
	public static final String JVM_MONITOR_PERIOD = "JVMMonitorPeriod";
	public static final String JVM_MONITOR_DIFF = "JVMMonitorDiff";
	public static final String STAGE = "stage";
	public static final String MODEM = "modem";
	public static final String IMAGES = "images";
	public static final String API_KEY = "twitterApiKey";
	public static final String API_SECRET = "twitterApiSecret";
	public static final String TWITSTREET_API_KEY = "twitstreetApiKey";
	public static final String TWITSTREET_API_SECRET = "twitstreetApiSecret";
	private static final String GRAPH_DB_PATH = "graphdb";
	
	public static final String APP_PROPERTIES = System
			.getenv("HOME") + "/.tsg/tsg.properties";
	
	private static  PropsConfigMgrImpl instance = null;
	
	private static Logger logger = Logger.getLogger(PropsConfigMgrImpl.class);
	boolean initialized = false;
	public void init(){
		Properties properties = new Properties();
		try {
			properties.load(new FileReader(new File(APP_PROPERTIES)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		configMap.put(DB_ADMIN, properties.getProperty(DB_ADMIN));
		configMap.put(DB_HOST, properties.getProperty(DB_HOST));
		configMap.put(DB_PASSWORD, properties.getProperty(DB_PASSWORD));
		configMap.put(DATABASE, properties.getProperty(DATABASE));
		configMap.put(DB_PORT, properties.getProperty(DB_PORT));
		configMap.put(MAIL_DEALER, properties.getProperty(MAIL_DEALER));
		configMap.put(MAIL_DEALER_PWD, properties.getProperty(MAIL_DEALER_PWD));
		configMap.put(MAIL_RECIPIENTS, properties.getProperty(MAIL_RECIPIENTS));
		configMap.put(JVM_MONITOR_PERIOD, properties.getProperty(JVM_MONITOR_PERIOD));
		configMap.put(JVM_MONITOR_DIFF, properties.getProperty(JVM_MONITOR_DIFF));
		configMap.put(STAGE, properties.getProperty(STAGE));
		configMap.put(STATISTIC_LOG_NUMBER, properties.getProperty(STATISTIC_LOG_NUMBER));
		configMap.put(STATISTIC_PERIOD, properties.getProperty(STATISTIC_PERIOD));
		configMap.put(MODEM, properties.getProperty(MODEM));
		configMap.put(IMAGES, properties.getProperty(IMAGES));
		configMap.put(GRAPH_DB_PATH, properties.getProperty(GRAPH_DB_PATH));
		configMap.put(API_KEY, properties.getProperty(API_KEY));
		configMap.put(API_SECRET, properties.getProperty(API_SECRET));
		configMap.put(TWITSTREET_API_SECRET, properties.getProperty(TWITSTREET_API_SECRET));
		configMap.put(TWITSTREET_API_KEY, properties.getProperty(TWITSTREET_API_KEY));
		initialized = true;
	}
	
	public String getMailDealer() {
		return get(MAIL_DEALER);
	}

	public String getMailDealerPassword() {
		return get(MAIL_DEALER_PWD);
	}

	public String[] getMailRecipients() {
		return get(MAIL_RECIPIENTS).split(",");
		
	}
	public String getModem(){
		return get(MODEM);
	}
	public long getStatisticLogNumber() {
		return Integer.parseInt(get(STATISTIC_LOG_NUMBER));
	}
	public long getStatisticPeriod() {
		return Integer.parseInt(get(STATISTIC_PERIOD));
	}
	public int getJVMMonitorPeriod() {
		return Integer.parseInt(get(JVM_MONITOR_PERIOD));
	}
	public int getJVMMonitorDiff() {
		return Integer.parseInt(get(JVM_MONITOR_DIFF));
	}
	public String getDbUserName() {
		return get(DB_ADMIN);
	}
	public String getDbPassword() {
		return get(DB_PASSWORD);
	}
	public String getDbHost() {
		return get(DB_HOST);
	}
	public String getDbPort() {
		return get(DB_PORT);
	}
	public String getDbName() {
		return get(DATABASE);
	}
	
	public String getImagesPath(){
		return get(IMAGES);
	}
	
	public String get(String parm){
		if(!initialized){
			init();
		}
		String val = configMap.get(parm);
		return val == null ? "" : val;
	}

	public boolean isDev() {
		return Boolean.parseBoolean(get(STAGE));
	}

	public static PropsConfigMgrImpl getInstance() {
		if(instance == null){
			instance = new PropsConfigMgrImpl();
		}
		return instance;
	}

	public String getGraphDbPAth() {
		return get(GRAPH_DB_PATH);
	}

	public String getApiSecret() {

		return get(API_SECRET);
	}

	public String getApiKey() {
		return get(API_KEY);
	}
	public String getTwitstreetApiSecret() {

		return get(TWITSTREET_API_SECRET);
	}

	public String getTwitstreetApiKey() {
		return get(TWITSTREET_API_KEY);
	}
}

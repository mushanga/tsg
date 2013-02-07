package jobs;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import exception.NoAvailableTokenException;
import exception.TSGException;
import exception.UserProtectedException;
import graph.GraphDatabase;
import graph.IMGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.FollowingList;
import models.Item;
import models.Link;
import models.User;
import models.UserGraph;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.db.jpa.NoTransaction;
import play.jobs.Every;
import play.jobs.Job;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;
import util.LinkShortener;
import util.UserLookup;
import util.Util;

@Every("5s")
public class GraphJobBase extends Job {

	
	
public static int USER_PER_PAGE = 50;


public class UserComparator implements Comparator<User>{

	public int incoming = 0;
	public UserComparator(HashMap<Long, Integer> userIncomingCountMap) {
		this.incomingMap = userIncomingCountMap;
	}
	public HashMap<Long, Integer> incomingMap;

	@Override
	public int compare(User o1, User o2) {
		try{
			return incomingMap.get(o1.twitterId) - incomingMap.get(o2.twitterId);
		}catch(Exception ex){
			return 0;
		}
		
	}
	
}
	

public static void saveGraphJson(String name, String content) {
	try {
		FileUtils.writeStringToFile(new File(Start.getGraphJSONDataPath()+name+".json"), content);
	} catch (IOException e) {
		Logger.error(e, e.getMessage());
	}
	
}
public static File getGraphJson(String name) {
	return new File(Start.getGraphJSONDataPath()+name+".json");
	
}

	
	private static HashMap<String,ArrayList<String>> adjacencyList = new HashMap<String, ArrayList<String>>();
	


}

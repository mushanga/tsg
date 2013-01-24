package jobs;

import exception.NoAvailableTokenException;
import exception.UserProtectedException;
import graph.GraphDatabase;
import graph.IMGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
public class ProcessGraphInProgress extends GraphOperationsBase {


	private static Set<String> doneSet = new HashSet<String>(); 
	@Override
	public void doJob() {
		try {
			List<UserGraph> graph = UserGraph.getInProgressList();

			if (Util.isListValid(graph)) {
				for(UserGraph ug : graph){
					if(!doneSet.contains(ug.ownerId+"-"+ug.version)){

						FollowingList fl = FollowingList.getByOwnerId(ug.ownerId);
						if(fl!=null && fl.isCompleted()){
							createGraphForUser(ug, true);
							doneSet.add(ug.ownerId+"-"+ug.version);

						}	
					}

				}
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}

	}

	
	
}
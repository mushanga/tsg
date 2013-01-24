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

@Every("1s")
public class ProcessGraphReadyToBeFinalized extends GraphOperationsBase {


	@Override
	public void doJob() {
		try {
		
			UserGraph graph = UserGraph.getReadyToBeFinalized();
			if (graph != null) {
				graph.setStatusConstructing();

				createGraphForUser(graph,false);
				graph.setStatusCompleted();
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
	}
	
	
}

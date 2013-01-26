package jobs;

import exception.NoAvailableTokenException;
import exception.UserProtectedException;
import graph.GraphDatabase;
import graph.IMGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import play.jobs.Every;
import play.jobs.Job;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;
import util.LinkShortener;
import util.UserLookup;

public class ConstructGraph extends GraphJobBase {


	@Override
	public void doJob() {
		try {
			UserGraph graph = UserGraph.getInProgress();
			if (graph != null) {
			//	createGraphForUser(graph);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
	}
	

}

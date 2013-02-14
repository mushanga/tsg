package jobs;

import exception.NoAvailableTokenException;
import exception.UserDoesNotExistException;
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

import jobs.GraphJobBase.UserComparator;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.ClientUser;
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
import scala.Array;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;
import util.LinkShortener;
import util.UserLookup;
import util.Util;

@Every("1s")
public class GraphReadyJob extends GraphJobBase {


	@Override
	public void doJob() {
		try {
		
			UserGraph graph = UserGraph.getReadyToBeFinalized();
			if (graph != null) {
				graph.setStatusConstructing();

				createGraphForUser(graph,false);
				graph.setStatusSuccessful();
				graph.save();
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
	}
		
	
	
	protected List<Long> getUserListForGraph(UserGraph ug, boolean temp){
	
      List<Long> idList = new ArrayList<Long>(GraphDatabase.getFollowings(ug.ownerId));
      idList.add(0, ug.ownerId);
//      if(temp && idList.size()>50){
//         idList = idList.subList(0, 50);
//      }
      return idList;
      
	}

	public void createGraphForUser(UserGraph ug, boolean temp) throws UserDoesNotExistException {
	   User user = UserLookup.getUser(ug.ownerId);
     
	   Logger.info("Creating"+((temp)?" temp":"")+" graph for user: "+ user.screenName);
	   
      Logger.info("getAllNodesAndLinksForUserGraph - enter: "+user.screenName);
      UserGraphUtil ugUtil = GraphDatabase.getAllNodesAndLinksForUserGraph(ug.ownerId);
      Logger.info("getAllNodesAndLinksForUserGraph - exit: "+user.screenName);
//      UserGraphUtil ugUtil = fillLinksAndNodesForUserSet(ug, graphContentIdList, visibleUsers, visibleLinks, userIncomingCountMap);
      
      Logger.info("paginateLinks for user "+user.screenName); 
	   List<ClientUserGraph> graphs = paginateLinks(ug, USER_PER_PAGE, ugUtil);
	   
	   Gson gson = new GsonBuilder().setPrettyPrinting().create();      
	   for(ClientUserGraph cg : graphs){
	      if(temp){
	         cg.needsReload = true;
	      }

	      String content = gson.toJson(cg, ClientUserGraph.class);
	      saveGraphJson(ug.ownerId+((temp)?"-temp":"-"+ cg.page), content);
	      if(temp){
	         break;
	      }
	   }
	   Logger.info("Created"+((temp)?" temp":"")+" graph for user: "+ user.screenName);

	}

	protected List<ClientUserGraph> paginateLinks(UserGraph ug, int recPerPage,UserGraphUtil lu){
	    int total = ug.total;
	    int completed= ug.completed;
      
      List<User> users = UserLookup.getUsers(lu.nodesList);
	   List<HashSet<Long>> cliques = lu.findMaxCliques();

		List<String> ids = new ArrayList<String>();
		
		List<ClientUserGraph> graphs = new ArrayList<ClientUserGraph>();
		
		ClientUserGraph cg = null;
		for(int i =0; i<users.size(); i++){
			
			if(i%recPerPage == 0){
				
				cg = new ClientUserGraph(ug, total, completed, new HashSet() , new ArrayList(), (i/recPerPage) + 1,new HashMap<Long, Double>(),new HashMap<Long, Integer>());
				cg.cliques = cliques;
				graphs.add(cg);
			}
			User user = users.get(i);
			ids.add(String.valueOf(user.twitterId));
         cg.users.add(new ClientUser(user));
         cg.userNodeSizeMap.put(user.twitterId,lu.userNodeSizeMap.get(user.twitterId));
         cg.userLinkSizeMap.put(user.twitterId,lu.userLinkSizeMap.get(user.twitterId));
			
		}
		
		
		for(String link : lu.linksList){
			String srcId = link.split("-")[0];
			String trgId = link.split("-")[1];
			int nthUser = Math.max(ids.indexOf(srcId), ids.indexOf(trgId));
			int nthArr = nthUser / recPerPage;
			
			graphs.get(nthArr).links.add(link);
		}
		
		return graphs;
			
			
	}
	
}

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

import jobs.GraphJobBase.UserComparator;

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
		
	protected void fillLinksAndNodesForUserSet(UserGraph ug, List<Long> graphIdList,List<User> visibleUsers, List<String> visibleLinks){

	   User user = UserLookup.getUser(ug.ownerId);
      Set<User> visibleUserSet = new HashSet();
      Set<String> visibleLinkSet = new HashSet();
      
	   visibleUserSet.add(user);
      
	   HashMap<Long, Integer> userIncomingCountMap = new HashMap<Long, Integer>();

	   for (Long following : graphIdList) {
	      // graph.addLink(userId, following);
	      if(following==ug.ownerId){
	         continue;
	      }
	      Set<Long> friendsOfFollowing = GraphDatabase.getMutualFriendsIncluding(following,graphIdList);
	      if(!friendsOfFollowing.contains(ug.ownerId)){

	         boolean added = visibleLinkSet.add(ug.ownerId+"-"+following);

	         if(added){
	            if(userIncomingCountMap.get(following) == null){
	               userIncomingCountMap.put(following, 0);
	            }
	            userIncomingCountMap.put(following, userIncomingCountMap.get(following)+1);
	         }

	         visibleUserSet.add(UserLookup.getUser(following));


	      }
	      for (Long friendOfFollowing : friendsOfFollowing) {

	         boolean added = visibleLinkSet.add(friendOfFollowing+"-"+following);
	         if(added){
	            if(userIncomingCountMap.get(following) == null){
	               userIncomingCountMap.put(following, 0);
	            }
	            userIncomingCountMap.put(following, userIncomingCountMap.get(following)+1);
	         }
	         added = visibleLinkSet.add(following+"-"+friendOfFollowing);
	         if(added){
	            if(userIncomingCountMap.get(friendOfFollowing) == null){
	               userIncomingCountMap.put(friendOfFollowing, 0);
	            }
	            userIncomingCountMap.put(friendOfFollowing, userIncomingCountMap.get(friendOfFollowing)+1);
	         }


	         visibleUserSet.add(UserLookup.getUser(friendOfFollowing));
	         visibleUserSet.add(UserLookup.getUser(following));
	      }
	   }
	   List<User> usersList = new ArrayList<User>(visibleUserSet);
	   Collections.sort(usersList, new UserComparator(userIncomingCountMap));
	   Collections.reverse(usersList);

	   visibleLinks.addAll(visibleLinkSet);
	   visibleUsers.addAll(usersList);

	}
	
	protected List<Long> getUserListForGraph(UserGraph ug, boolean temp){
	   
      List<Long> idList = new ArrayList<Long>(GraphDatabase.getFollowings(ug.ownerId));
      idList.add(0, ug.ownerId);
//      if(temp && idList.size()>50){
//         idList = idList.subList(0, 50);
//      }
      return idList;
      
	}

	public void createGraphForUser(UserGraph ug, boolean temp) {
	   User user = UserLookup.getUser(ug.ownerId);
	   Logger.info("Creating"+((temp)?" temp":"")+" graph for user: "+ user.screenName);

	   List<String> visibleLinks = new ArrayList<String>();
	   List<User> visibleUsers = new ArrayList<User>();


	   List<Long> graphIdList = getUserListForGraph(ug, temp);


	   fillLinksAndNodesForUserSet(ug, graphIdList, visibleUsers, visibleLinks);

	   Gson gson = new GsonBuilder().setPrettyPrinting().create();
	   List<ClientGraph> graphs = paginateLinks(ug.ownerId, 50, visibleLinks, visibleUsers, ug.total, ug.completed);


	   for(ClientGraph cg : graphs){
	      if(temp){
	         cg.needsReload = true;
	      }

	      String content = gson.toJson(cg, ClientGraph.class);
	      saveGraphJson(ug.ownerId+((temp)?"-temp":"-"+ cg.page), content);
	      if(temp){
	         break;
	      }
	   }
	   Logger.info("Created"+((temp)?" temp":"")+" graph for user: "+ user.screenName);

	}
	protected List<ClientGraph> paginateLinks(long ownerId, int recPerPage,List<String> visibleLinks, List<User> users, int total, int completed){

	   LinksUtil lu = new LinksUtil(visibleLinks);
	   List<HashSet<Long>> cliques = lu.findMaxCliques();


		List<String> ids = new ArrayList<String>();
		
		List<ClientGraph> graphs = new ArrayList<ClientGraph>();
		
		ClientGraph cg = null;
		for(int i =0; i<users.size(); i++){
			
			if(i%recPerPage == 0){
				
				cg = new ClientGraph(ownerId, total, completed, new HashSet() , new ArrayList(), (i/recPerPage) + 1);
				cg.cliques = cliques;
				graphs.add(cg);
			}
			User user = users.get(i);
			ids.add(String.valueOf(user.twitterId));
			cg.users.add(user);
			
		}
		
		
		for(String link : visibleLinks){
			String srcId = link.split("-")[0];
			String trgId = link.split("-")[1];
			int nthUser = Math.max(ids.indexOf(srcId), ids.indexOf(trgId));
			int nthArr = nthUser / recPerPage;
			
			graphs.get(nthArr).links.add(link);
		}
		
		return graphs;
			
			
	}
	
}

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
		
	protected void fillLinksAndNodesForUserSet(UserGraph ug, List<Long> graphContentIdList,List<User> visibleUsers, List<String> visibleLinks, HashMap<Long, Integer> userIncomingCountMap ) throws UserDoesNotExistException{

	   User user;
    
         user = UserLookup.getUser(ug.ownerId);

         Set<User> visibleUserSet = new HashSet();
         Set<Long> visibleUserIdSet = new HashSet();
      Set<String> visibleLinkSet = new HashSet();
      
	   visibleUserSet.add(user);
      

	   for (Long following : graphContentIdList) {
	   
	      if(following==ug.ownerId){
	         continue;
	      }
//	      visibleUserIdSet.add(following);
	      try {
            visibleUserSet.add(UserLookup.getUser(following));
         } catch (UserDoesNotExistException e) {
            Logger.info(e.getMessage());
            continue;
         }
	      Set<Long> friendsOfFollowing = GraphDatabase.getMutualFriendsIncluding(following,graphContentIdList);
	      
	     
	      if(userIncomingCountMap.get(following) == null){
	         userIncomingCountMap.put(following, 0);
	      }
	      for (Long friendOfFollowing : friendsOfFollowing) {

            try {
               visibleUserSet.add(UserLookup.getUser(friendOfFollowing));
               visibleUserSet.add(UserLookup.getUser(following));
            } catch (UserDoesNotExistException e) {
               Logger.error(e, e.getMessage());
               continue;
            }
	         boolean added = visibleLinkSet.add(friendOfFollowing+"-"+following);
	         if(added){
	            userIncomingCountMap.put(following, userIncomingCountMap.get(following)+1);
	         }
	         added = visibleLinkSet.add(following+"-"+friendOfFollowing);
	         if(added){
	            if(userIncomingCountMap.get(friendOfFollowing) == null){
	               userIncomingCountMap.put(friendOfFollowing, 0);
	            }
	            userIncomingCountMap.put(friendOfFollowing, userIncomingCountMap.get(friendOfFollowing)+1);
	         }


	      }
	   }
	   List<User> usersList = new ArrayList<User>(visibleUserSet);
	   Collections.sort(usersList, new UserComparator(userIncomingCountMap));
	   Collections.reverse(usersList);
	  
	   usersList.remove(user);	   
	   usersList.add(0, user);
	   
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

	public void createGraphForUser(UserGraph ug, boolean temp) throws UserDoesNotExistException {
	   User user = UserLookup.getUser(ug.ownerId);
     
	   Logger.info("Creating"+((temp)?" temp":"")+" graph for user: "+ user.screenName);
	   
      Logger.info("fillLinksAndNodesForUserSet for user "+ug.ownerId);
      UserGraphUtil ugUtil = GraphDatabase.getAllNodesAndLinksForUserGraph(ug.ownerId);
     
      Logger.info("paginateLinks for user "+ug.ownerId); 
	   List<ClientGraph> graphs = paginateLinks(ug, 50, ugUtil);
	   
	   Gson gson = new GsonBuilder().setPrettyPrinting().create();      
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

	protected List<ClientGraph> paginateLinks(UserGraph ug, int recPerPage,UserGraphUtil lu){
	    int total = ug.total;
	    int completed= ug.completed;
      
      List<User> users = UserLookup.getUsers(lu.nodesList);
	   List<HashSet<Long>> cliques = lu.findMaxCliques();

		List<String> ids = new ArrayList<String>();
		
		List<ClientGraph> graphs = new ArrayList<ClientGraph>();
		
		ClientGraph cg = null;
		for(int i =0; i<users.size(); i++){
			
			if(i%recPerPage == 0){
				
				cg = new ClientGraph(ug, total, completed, new HashSet() , new ArrayList(), (i/recPerPage) + 1,new HashMap<Long, Double>());
				cg.cliques = cliques;
				graphs.add(cg);
			}
			User user = users.get(i);
			ids.add(String.valueOf(user.twitterId));
         cg.users.add(user);
         cg.userNodeSizeMap.put(user.twitterId,lu.userNodeSizeMap.get(user.twitterId));
			
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

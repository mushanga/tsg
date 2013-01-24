package jobs;

import exception.NoAvailableTokenException;
import exception.TSEException;
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
public class GraphOperationsBase extends Job {

	
	public void revealGraph(UserGraph graph) {
		Long ownerId = graph.ownerId;

		if (graph.isProtected()) {
			return;
		}
		
		try {
			Set<Long> followingIdSet = null;

			FollowingList fl = FollowingList.getByOwnerId(ownerId);
			if(fl==null){
				fl = new FollowingList(ownerId);
				fl.save();
			}
			if(fl.isCompleted()) {
				followingIdSet = GraphDatabase.getFollowings(ownerId);			
			} else if (fl.isWaiting()) {
				followingIdSet = getFollowings(ownerId,true);
			}

			if (fl.isProtected()) {
				graph.setStatusProtected();
				
				return;
			}
			if(Util.isSetValid(followingIdSet)&& graph.total==0 && graph.total != followingIdSet.size()){
				graph.total = followingIdSet.size();
				graph.save();
			}

			int completed = 0;
			
			if(followingIdSet!=null){
				for (Long followingId : followingIdSet) {

					FollowingList ffl = FollowingList.getByOwnerId(followingId);

					if(ffl==null){
						ffl = new FollowingList(followingId);
						ffl.save();
					}
					//				if (ffl.isWaiting()) {
					//					getFollowings(followingId,true);
					//				}
					if(ffl.isCompleted()||ffl.isProtected()){
						completed++;
						if(completed>graph.completed){
							graph.completed = completed;
							graph.saveImmediately();
						}
					}
				}
			}
		} catch (Exception e) {

			graph.setStatusWaiting();
			Logger.log4j.error(e.getMessage(),e);
		}
		
		if(graph.completed<graph.total){
			graph.setStatusWaiting();
		}
	}
public void createGraphForUser(UserGraph ug, boolean temp) {
		User user = UserLookup.getUser(ug.ownerId);
		Logger.info("Creating graph for user: "+ user.screenName);
		
		Set<String> visibleLinks = new HashSet<String>();
		Set<User> visibleUsers = new HashSet<User>();
		
		visibleUsers.add(user);
		
		Set<Long> followingIdList =  GraphDatabase.getFollowings(ug.ownerId);
		Set<Long> userAndAllFollowings = new HashSet<Long>();

		userAndAllFollowings.addAll(followingIdList);
		userAndAllFollowings.add(ug.ownerId);

		int total = followingIdList.size();


		HashMap<Long, Integer> userIncomingCountMap = new HashMap<Long, Integer>();
		
		int completed = 0;
		for (Long following : followingIdList) {
			// graph.addLink(userId, following);
			Set<Long> friendsOfFollowing = GraphDatabase.getMutualFriendsIncluding(following,userAndAllFollowings);
			if(!friendsOfFollowing.contains(ug.ownerId)){
				
				boolean added = visibleLinks.add(ug.ownerId+"-"+following);
				
				if(added){
					if(userIncomingCountMap.get(following) == null){
						userIncomingCountMap.put(following, 0);
					}
					userIncomingCountMap.put(following, userIncomingCountMap.get(following)+1);
				}
				
				visibleUsers.add(UserLookup.getUser(following));
				

			}
			for (Long friendOfFollowing : friendsOfFollowing) {

				boolean added = visibleLinks.add(friendOfFollowing+"-"+following);
				if(added){
					if(userIncomingCountMap.get(following) == null){
						userIncomingCountMap.put(following, 0);
					}
					userIncomingCountMap.put(following, userIncomingCountMap.get(following)+1);
				}
				added = visibleLinks.add(following+"-"+friendOfFollowing);
				if(added){
					if(userIncomingCountMap.get(friendOfFollowing) == null){
						userIncomingCountMap.put(friendOfFollowing, 0);
					}
					userIncomingCountMap.put(friendOfFollowing, userIncomingCountMap.get(friendOfFollowing)+1);
				}
				
				
				visibleUsers.add(UserLookup.getUser(friendOfFollowing));
				visibleUsers.add(UserLookup.getUser(following));
			}

			FollowingList ffl = FollowingList.getByOwnerId(following);
			if(ffl!=null){
				if(ffl.isCompleted() || ffl.isProtected()){
					completed++;
				}
			}

		}

		ug.total = total;
		ug.completed = completed;
		ug.save();
		
		List<User> usersList = new ArrayList<User>(visibleUsers);
		Collections.sort(usersList, new UserComparator(userIncomingCountMap));
		Collections.reverse(usersList);
		
		

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		if(!temp){

			List<ClientGraph> graphs = paginateLinks(ug.ownerId, 50, visibleLinks, usersList, total, completed);
			
			for(ClientGraph cg : graphs){
				String content = gson.toJson(cg, ClientGraph.class);
				
				saveGraphJson(ug.ownerId+"-"+ cg.page, content);
			}
			
			ug.setStatusCompleted();
		}else{
			ClientGraph cg = new ClientGraph(ug.ownerId, total, completed, visibleLinks, usersList, 1);
			cg.needsReload = true;
			String content = gson.toJson(cg, ClientGraph.class);
			saveGraphJson(ug.ownerId+"-temp", content);
		}
		
	}

private List<ClientGraph> paginateLinks(long ownerId, int recPerPage,Set<String> links, List<User> users, int total, int completed){
	List<String> ids = new ArrayList<String>();
	
	List<ClientGraph> graphs = new ArrayList<ClientGraph>();
	
	ClientGraph cg = null;
	for(int i =0; i<users.size(); i++){
		
		if(i%recPerPage == 0){
			
			cg = new ClientGraph(ownerId, total, completed, new HashSet() , new ArrayList(), (i/recPerPage) + 1);
			graphs.add(cg);
		}
		User user = users.get(i);
		ids.add(String.valueOf(user.twitterId));
		cg.users.add(user);
		
	}
	
	
	for(String link : links){
		String srcId = link.split("-")[0];
		String trgId = link.split("-")[1];
		int nthUser = Math.max(ids.indexOf(srcId), ids.indexOf(trgId));
		int nthArr = nthUser / recPerPage;
		
		graphs.get(nthArr).links.add(link);
	}
	return graphs;
		
		
}

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

	protected Set<Long> getFollowings(long id, boolean force) {
	
		Set<Long> followingList = new HashSet<Long>();
		FollowingList fl = FollowingList.getByOwnerId(id);

		if(fl==null){
			fl = new FollowingList(id);
			fl.save();
		} 
		
		if (!force && fl.isCompleted()) {
			followingList = GraphDatabase.getFollowings(id);
		} else if(!force && fl.isProtected()){
			return followingList;
		}
		else if (force || fl.isWaiting()) {

			fl.setStatusInProgress();

			GraphDatabase.clearFollowings(fl.ownerId);
			followingList = new HashSet<Long>();

			try {
				
				TwitterProxy twitter = TwitterProxyFactory.defaultInstance();
				
				for (Long followingId : twitter.getFollowingIds(id)) {
					
					followingList.add(followingId);
				}
				GraphDatabase.addFriendships(id,followingList);
				
				fl.setStatusCompleted();

			} catch (NoAvailableTokenException e) {	
				fl.setStatusWaiting();
				Logger.error(e, e.getMessage());
			
			} catch (UserProtectedException e) {
				fl.setStatusProtected();
				Logger.info(e.getMessage());
			}

			catch (Exception e1) {
				fl.setStatusWaiting();
				Logger.error(e1, e1.getMessage());

			}
		}
		return followingList;
	}

}

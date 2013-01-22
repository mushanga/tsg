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
public class RevealGraph extends Job {

	TwitterProxy twitter;

	@Override
	public void doJob() {
		try {
			twitter = TwitterProxyFactory.defaultInstance();
			UserGraph graph = UserGraph.getWaiting();
			if (graph != null) {
				revealGraph(graph);
			}
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
		
	}

	public void revealGraph(UserGraph graph) {
		Long ownerId = graph.ownerId;

		if (graph.isProtected()) {
			return;
		}
		
		graph.setStatusInProgress();
		graph.saveImmediately();
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
				followingIdSet = getFollowings(ownerId);
			}

			if (fl.isProtected()) {
				graph.setStatusProtected();
				graph.saveImmediately();
				return;
			}

			for (Long followingId : followingIdSet) {

				FollowingList ffl = FollowingList.getByOwnerId(followingId);

				if(ffl==null){
					ffl = new FollowingList(followingId);
					ffl.save();
				}
				if (ffl.isWaiting()) {
					getFollowings(followingId);
				}
			}

			createGraphForUser(graph);

			graph.setStatusCompleted();
			graph.saveImmediately();
		} catch (Exception e) {

			graph.setStatusWaiting();
			graph.saveImmediately();
			Logger.error(e, e.getMessage());
		}
	}
public void createGraphForUser(UserGraph ug) {

		
		Set<String> visibleLinks = new HashSet<String>();
		Set<User> visibleUsers = new HashSet<User>();
		
		visibleUsers.add(UserLookup.getUser(ug.ownerId));
		
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

		List<User> usersList = new ArrayList<User>(visibleUsers);

		Collections.sort(usersList, new UserComparator(userIncomingCountMap));
		Collections.reverse(usersList);
		ClientGraph cg = new ClientGraph(ug.ownerId, total, completed, visibleLinks, usersList);
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String content = gson.toJson(cg, ClientGraph.class);
		saveGraphJson(ug.ownerId, content);
		
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
	

	public static void saveGraphJson(Long ownerId, String content) {
		try {
			FileUtils.writeStringToFile(new File(Start.getGraphJSONDataPath()+ownerId+".json"), content);
		} catch (IOException e) {
			Logger.error(e, e.getMessage());
		}
		
	}

	private Set<Long> getFollowings(long id) {
		Set<Long> followingList = new HashSet<Long>();
		FollowingList fl = FollowingList.getByOwnerId(id);

		if(fl==null){
			fl = new FollowingList(id);
			fl.save();
		} 
		
		if (fl.isCompleted()) {
			followingList = GraphDatabase.getFollowings(id);
//			followingList = Link.getTargetSet(id);
		} else if (fl.isWaiting()) {
			fl.setStatusInProgress();
			fl.save();

			followingList = new HashSet<Long>();

			try {
				for (Long followingId : twitter.getFollowingIds(id)) {
					
					followingList.add(followingId);
				}
				GraphDatabase.addFriendships(id,followingList);
//				Link.saveIfInexistent(id, followingList);
				
				fl.setStatusCompleted();
				fl.save();

			} catch (NoAvailableTokenException e) {	
				fl.setStatusWaiting();
				fl.save();
				Logger.error(e, e.getMessage());
			
			} catch (UserProtectedException e) {
				fl.setStatusProtected();
				fl.save();
				Logger.info(e.getMessage());
			}

			catch (Exception e1) {
				fl.setStatusWaiting();
				fl.save();
				Logger.error(e1, e1.getMessage());

			}
		}
		return followingList;
	}
}

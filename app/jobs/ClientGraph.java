package jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.User;
import models.UserGraph;



public class ClientGraph{
	
   public ClientGraph(UserGraph ug, int total, int completed, Set<String> visibleLinks, List<User> visibleUsers, int page,HashMap<Long, Double> userNodeSizeMap) {
		super();
		this.ownerId = ug.ownerId;
		this.version = ug.version;
		this.total = total;
		this.completed = completed;
		this.links = visibleLinks;
		this.users = visibleUsers;
		this.page = page;
		this.userNodeSizeMap = userNodeSizeMap;
	}
	
	public List<HashSet<Long>> cliques;
	public Long ownerId;
	public HashMap<Long, Double> userNodeSizeMap;
   public Long version;
	public int total;
	public boolean needsReload;
	public int page;
	public int completed;
	
	Set<String> links = new HashSet<String>();
	List<User> users = new ArrayList<User>();
	
	
}

package jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.User;
import models.UserGraph;



public class ClientUserGraph extends ClientGraph{
	
   public ClientUserGraph(UserGraph ug, int total, int completed, Set<String> links, List<User> users, int page,HashMap<Long, Double> nodeSizeMap,HashMap<Long, Integer> linkSizeMap) {
		super(users, links, nodeSizeMap, linkSizeMap);
		this.ownerId = ug.ownerId;
		this.version = ug.version;
		this.total = total;
		this.completed = completed;
		this.page = page;
      
	}
	
	public List<HashSet<Long>> cliques;
	public Long ownerId;
   public Long version;
	public int total;
	public boolean needsReload;
	public int page;
	public int completed;
	
	
	
}

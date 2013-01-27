package jobs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.User;
import models.UserGraph;



public class ClientGraph{
	
   public ClientGraph(UserGraph ug, int total, int completed, Set<String> visibleLinks, List<User> visibleUsers, int page) {
		super();
		this.ownerId = ug.ownerId;
		this.version = ug.version;
		this.total = total;
		this.completed = completed;
		this.links = visibleLinks;
		this.users = visibleUsers;
		this.page = page;
	}
	
	public List<HashSet<Long>> cliques;
	public Long ownerId;

   public Long version;
	public int total;
	public boolean needsReload;
	public int page;
	public int completed;
	
	Set<String> links = new HashSet<String>();
	List<User> users = new ArrayList<User>();
	
	
}

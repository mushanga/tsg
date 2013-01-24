package jobs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.User;



public class ClientGraph{
	
	public ClientGraph(Long ownerId, int total, int completed, Set<String> visibleLinks, List<User> visibleUsers, int page) {
		super();
		this.ownerId = ownerId;
		this.total = total;
		this.completed = completed;
		this.links = visibleLinks;
		this.users = visibleUsers;
		this.page = page;
	}
	public Long ownerId;

	public int total;
	public boolean needsReload;
	public int page;
	public int completed;
	
	Set<String> links = new HashSet<String>();
	List<User> users = new ArrayList<User>();
	
	
}

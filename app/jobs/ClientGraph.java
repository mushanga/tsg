package jobs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.User;



public class ClientGraph{
	
	
	public ClientGraph(Long ownerId, int total, int completed, Set<String> visibleLinks, List<User> visibleUsers) {
		super();
		this.ownerId = ownerId;
		this.total = total;
		this.completed = completed;
		this.links = visibleLinks;
		this.users = visibleUsers;
	}
	public Long ownerId;

	public int total;

	public int completed;
	
	Set<String> links = new HashSet<String>();
	List<User> users = new ArrayList<User>();
	
	
}

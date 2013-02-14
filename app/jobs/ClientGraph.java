package jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.sun.org.apache.bcel.internal.generic.NEW;

import models.ClientUser;
import models.User;
import models.UserGraph;



public class ClientGraph{
	
   public ClientGraph( List<User> users, Set<String> links, HashMap<Long, Double> nodeSizeMap, HashMap<Long, Integer> linkSizeMap) {
		super();
		
		this.links = links;      
      for(User user : users){
         this.users.add(new ClientUser(user));
      }
		
		if(linkSizeMap== null){

         for(User user : users){
            this.userLinkSizeMap.put(user.twitterId, 1);
         }
		}else{
	      this.userLinkSizeMap = linkSizeMap;
		   
		}
		

		if(nodeSizeMap== null){
		   
		   double max = 0;
		   for(User user : this.users){
	         if(user.followersCount>max){
	            max = user.followersCount;
	         }
	      }
		   for(User user : users){
		      this.userNodeSizeMap.put(user.twitterId, user.followersCount/max);
         }
		   
		   
		}
     
	}	
	Set<String> links = new HashSet<String>();
	List<ClientUser> users = new ArrayList<ClientUser>();

   public HashMap<Long, Double> userNodeSizeMap = new HashMap<Long, Double>();
   public HashMap<Long, Integer> userLinkSizeMap = new HashMap<Long, Integer>();
	
}

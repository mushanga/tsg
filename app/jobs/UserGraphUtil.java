package jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import play.Logger;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class UserGraphUtil extends GraphUtil {
   
   public Long root;
   
   public HashMap<Long,Set<Long>> friendIntersectionWithRoot = new HashMap<Long, Set<Long>>();

   public UserGraphUtil(Long root, Set<String> links) {
      super(links);
      this.root = root;
      normalizeAndGetSizeCoefficient();
   }

   protected void normalizeAndGetSizeCoefficient() {
      sort();
      
      int max = 0;
      for(Long userId: nodesList){
         if(userId==root){
            continue;
         }
         Integer numberOfCommons = friendIntersectionWithRoot.get(userId).size();
         if(numberOfCommons>max){
            max = numberOfCommons;
         }     
      }
      
      double maxSizeOverRoot = 0.75;

      for(Long userId: nodesList){
         Integer intersectCount = friendIntersectionWithRoot.get(userId).size();
         Double coefficient = (Double.valueOf(intersectCount) / (double) max)*maxSizeOverRoot;
         if(coefficient.isNaN()){
            coefficient = 0D;
         }
         userNodeSizeMap.put(userId, coefficient);
      }
      userNodeSizeMap.put(root, 1D);
   }
   public void sort(){
      Set<Long> friends = nodeMutuallyLinkedNodesMap.get(this.root);
      
      friendIntersectionWithRoot.put(this.root, nodeMutuallyLinkedNodesMap.get(this.root));
      if(friends!=null){
         for(Long friend : friends){
            Set<Long> friendsOfFriend = nodeMutuallyLinkedNodesMap.get(friend);
            Set<Long> fof = new HashSet<Long>(friendsOfFriend);
            fof.retainAll(friends);
            this.friendIntersectionWithRoot.put(friend, fof);
         }
      }

      for(Long node : nodesList){
         if(friendIntersectionWithRoot.get(node)==null){
            friendIntersectionWithRoot.put(node, new HashSet<Long>());
         }
      }

      Collections.sort(this.nodesList, new ByCommonFriends(friendIntersectionWithRoot));
      Collections.reverse(this.nodesList);
      

     nodesList.remove(root);
     nodesList.add(0, root);
   }
   private class ByCommonFriends implements Comparator<Long>{

      private HashMap<Long,Set<Long>> friendIntersectionWithRoot;

      public ByCommonFriends(HashMap<Long, Set<Long>> friendIntersectionWithRoot2) {
         super();
         this.friendIntersectionWithRoot = friendIntersectionWithRoot2;
      }

      @Override
      public int compare(Long o1, Long o2) {
         int c1 = 0;
         int c2 = 0;
         

         try {
            c1 = this.friendIntersectionWithRoot.get(o1).size();
         } catch (Exception e) {
            Logger.error(e, e.getMessage());
         }
         try {
            c2 = this.friendIntersectionWithRoot.get(o2).size();
         } catch (Exception e) {
            Logger.error(e, e.getMessage());
         }
         return c1-c2;
      }
      
   }
   
   
   
   
}

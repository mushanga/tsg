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
import util.Util;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class UserGraphUtil extends GraphUtil {
   
   public Long root;

   HashMap<Long, Double> userNodeSizeMap = new HashMap<Long, Double>();
   
   public HashMap<Long,Set<Long>> friendIntersectionWithRoot = new HashMap<Long, Set<Long>>();
   public HashMap<Long,Integer> userLinkSizeMap = new HashMap<Long, Integer>();

   public UserGraphUtil(Long root, List<String> links) {
      super(links);
      this.root = root;
      normalizeAndGetSizeCoefficient();
      calculateLinkStrength();
   }
   public UserGraphUtil(Long root, List<Long> nodes , List<String> links) {
      super(nodes,links);
      this.root = root;
      normalizeAndGetSizeCoefficient();
      calculateLinkStrength();
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
      Set<Long> rootAndFriends = nodeMutuallyLinkedNodesMap.get(this.root);
      rootAndFriends.add(this.root);
      
      friendIntersectionWithRoot.put(this.root, nodeMutuallyLinkedNodesMap.get(this.root));
     
      for(Long node : this.nodesList){

         Set<Long> friendsOfNode = nodeMutuallyLinkedNodesMap.get(node);
         Set<Long> fson = new HashSet<Long>();
         if(Util.isValid(friendsOfNode)){

            fson.addAll(friendsOfNode);
            fson.retainAll(rootAndFriends);
         }
         friendIntersectionWithRoot.put(node,fson);
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
   
   public void calculateLinkStrength(){
      if(Util.isValid(cliques)){
         List<Long> tempNodes = new ArrayList<Long>(this.nodesList);
         
         for(HashSet<Long> clique : this.cliques){
            HashSet<Long> tempClique = new HashSet<Long>(clique);
            tempClique.retainAll(tempNodes);
           for(Long node :tempClique){
              userLinkSizeMap.put(node, clique.size());
              tempNodes.remove(node);
           }
          
            
         }
         for(Long node :tempNodes){
            userLinkSizeMap.put(node, 1);
         }
         
      }
    
      
   }
   
   
   
}

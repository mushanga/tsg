package jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.User;
import util.UserLookup;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class GraphUtil {


   public Set<String> linksSet;
   public List<String> linksList;

   public List<String> undirectedLinksList = new ArrayList<String>();
   public List<Long> undirectedLinksSrcList= new ArrayList<Long>();
   public List<Long> undirectedLinksTrgList= new ArrayList<Long>();

   HashMap<Long, Integer> incomingCountMap = new HashMap<Long, Integer>();
   HashMap<Long, Double> userNodeSizeMap = new HashMap<Long, Double>();
   
   HashMap<Long, List<Long>> incomingMap = new HashMap<Long, List<Long>>();
   HashMap<Long, List<Long>> outgoingMap = new HashMap<Long, List<Long>>();
   HashMap<Long, Set<Long>> nodeMutuallyLinkedNodesMap = new HashMap<Long, Set<Long>>();

   public Set<Long> nodesSet = new HashSet<Long>();
   public List<Long> nodesList = new ArrayList<Long>();

   Map<String,List<String>> adjList = new HashMap<String,List<String>>();
   HashMap<String,List<String>> mutualLinksMap = new HashMap<String,List<String>>();

   public GraphUtil(){
      super();
   }
   public GraphUtil(Set<String> links) {
      super();
      this.linksSet = links;
      process();
   }
   public GraphUtil(List<String> links) {

      super();
      this.linksSet = new HashSet<String>(links);
      process();
   }
   public GraphUtil(List<Long> startIds, List<Long> endIds) {

      super();
      this.linksSet = new HashSet<String>();
      for(int i = 0; i< startIds.size(); i++){
         Long startId = startIds.get(i);
         Long endId = endIds.get(i);
         String link = startId+"-"+endId;
         this.linksSet.add(link);
      }


      process();
   }



   public List<HashSet<Long>> findMaxCliques(){

      UndirectedGraph<String,String> unG = new UndirectedSparseGraph<String, String>();
      for(int i = 0; i<undirectedLinksList.size();i++){
         unG.addEdge(undirectedLinksList.get(i),String.valueOf(undirectedLinksSrcList.get(i)), String.valueOf(undirectedLinksTrgList.get(i)));

      }

      BronKerboschCliqueFinder<String, String> cf = new BronKerboschCliqueFinder<String, String>(unG);
      // Collection<Set<String>> response = cf.getAllMaximalCliques();
      Collection<Set<String>> response = cf.getBiggestMaximalCliques();

      List<HashSet<Long>> cliques = new ArrayList<HashSet<Long>>();

      for(Set<String> s : response){
         HashSet<Long> clique = new HashSet<Long>();
         cliques.add(clique);
         for(String l : s){
            clique.add(Long.valueOf(l));
         }
      }
      return cliques;
   }
   
   private void checkNodesExistence(){
      Set<Long> userIds = new HashSet<Long>();
      for(String link : this.linksSet){
         userIds.add(Long.valueOf(link.split("-")[0]));
         userIds.add(Long.valueOf(link.split("-")[1]));
      }
      List<User> existingUsers = UserLookup.getUsers(new ArrayList<Long>(userIds));
      Set<Long> existingUserIds = new HashSet<Long>();
      if(existingUsers.size()!=userIds.size()){

         for(User u: existingUsers){
            existingUserIds.add(u.twitterId);
         }   
         List<String> temp = new ArrayList<String>(this.linksSet);
         for(int i = 0; i<temp.size(); i++){
            String link = temp.get(i);
            Long id1 = Long.valueOf(link.split("-")[0]);
            Long id2 = Long.valueOf(link.split("-")[1]);
            if(!existingUserIds.contains(id1) || !existingUserIds.contains(id2)){
               temp.remove(i);
               i--;
            }
         }
         this.linksSet.retainAll(temp);
      }
   
   }
   protected void process(){
      
      checkNodesExistence();
      this.linksList = new ArrayList<String>(this.linksSet);
      
      for(int i =0; i<linksList.size(); i++){
         String link = linksList.get(i);
         Long node1 = Long.valueOf(link.split("-")[0]);
         Long node2 = Long.valueOf(link.split("-")[1]);

         nodesSet.add(node1);
         nodesSet.add(node2);

         if(outgoingMap.get(node1)==null){
            outgoingMap.put(node1, new ArrayList<Long>());
         }
         if(incomingMap.get(node2)==null){
            incomingMap.put(node2, new ArrayList<Long>());
         }
         outgoingMap.get(node1).add(node2);
         incomingMap.get(node2).add(node1);


         String reverseLink = node2+"-"+node1;
         if(linksList.contains(reverseLink)){
            if(!undirectedLinksList.contains(reverseLink)){

               if(nodeMutuallyLinkedNodesMap.get(node1)==null){
                  nodeMutuallyLinkedNodesMap.put(node1, new HashSet<Long>());
               }
               if(nodeMutuallyLinkedNodesMap.get(node2)==null){
                  nodeMutuallyLinkedNodesMap.put(node2, new HashSet<Long>());
               }
               nodeMutuallyLinkedNodesMap.get(node1).add(node2);
               nodeMutuallyLinkedNodesMap.get(node2).add(node1);
               
               undirectedLinksList.add(link);
               undirectedLinksSrcList.add(node1);
               undirectedLinksTrgList.add(node2);
            }
         }
      }
      nodesList.addAll(nodesSet);
   }
   
   public void sortByIncomingCount(){
      
   }

//   protected void normalizeAndGetSizeCoefficient(){
//      
//      int max = 0;
//      for(String userId: incomingCountMap.keySet()){
//         Integer incomingCount = incomingCountMap.get(userId);
//         if(incomingCount>max){
//            max = incomingCount;
//         }     
//      }
//      
//
//      for(String userId: incomingCountMap.keySet()){
//         Integer incomingCount = incomingCountMap.get(userId);
//         Double coefficient = Double.valueOf(incomingCount) / (double) max;
//         if(coefficient.isNaN()){
//            coefficient = 0D;
//         }
//         userNodeSizeMap.put(userId, coefficient);
//      }
//      
//   }
}

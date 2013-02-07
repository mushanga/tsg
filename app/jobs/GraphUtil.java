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

import models.User;
import util.UserLookup;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class GraphUtil {

   public List<String> linksList;

   public List<String> undirectedLinksList = new ArrayList<String>();
   public List<Long> undirectedLinksSrcList= new ArrayList<Long>();
   public List<Long> undirectedLinksTrgList= new ArrayList<Long>();

   HashMap<Long, Integer> incomingCountMap = new HashMap<Long, Integer>();
   
   HashMap<Long, List<Long>> incomingMap = new HashMap<Long, List<Long>>();
   HashMap<Long, List<Long>> outgoingMap = new HashMap<Long, List<Long>>();
   HashMap<Long, Set<Long>> nodeMutuallyLinkedNodesMap = new HashMap<Long, Set<Long>>();

   public List<Long> nodesList = new ArrayList<Long>();

   Map<String,List<String>> adjList = new HashMap<String,List<String>>();
   HashMap<String,List<String>> mutualLinksMap = new HashMap<String,List<String>>();
   
   List<HashSet<Long>> cliques = new ArrayList<HashSet<Long>>();

   public GraphUtil(){
      super();
   }
   public GraphUtil(List<String> links) {
      super();
      this.linksList = links;
      process();
   }
   public GraphUtil(List<Long> nodes, List<String> links) {
      super();
      this.nodesList = nodes;
      this.linksList = links;
      process();
   }

   public List<HashSet<Long>> findMaxCliques(){
      cliques = new ArrayList<HashSet<Long>>();
      
      UndirectedGraph<String,String> unG = new UndirectedSparseGraph<String, String>();
      for(int i = 0; i<undirectedLinksList.size();i++){
         unG.addEdge(undirectedLinksList.get(i),String.valueOf(undirectedLinksSrcList.get(i)), String.valueOf(undirectedLinksTrgList.get(i)));

      }

      BronKerboschCliqueFinder<String, String> cf = new BronKerboschCliqueFinder<String, String>(unG);
       Collection<Set<String>> response = cf.getAllMaximalCliques();
       CliqueComparator ccomp =  new CliqueComparator();
      
//      Collection<Set<String>> response = cf.getBiggestMaximalCliques();

     
      for(Set<String> s : response){
         HashSet<Long> clique = new HashSet<Long>();
         cliques.add(clique);
         for(String l : s){
            clique.add(Long.valueOf(l));
         }
      }
      
      Collections.sort(cliques, ccomp);
      Collections.reverse(cliques);
      
      return cliques;
   }

   
   public class CliqueComparator implements Comparator<Collection<? extends Object>>{

      @Override
      public int compare(Collection<? extends Object> o1, Collection<? extends Object> o2) {
         return o1.size()-o2.size();
      }
      
   }
   private void checkNodesExistence(){
      Set<Long> userIds = new HashSet<Long>();
      for(String link : this.linksList){
         userIds.add(Long.valueOf(link.split("-")[0]));
         userIds.add(Long.valueOf(link.split("-")[1]));
      }
      List<User> existingUsers = UserLookup.getUsers(new ArrayList<Long>(userIds));
      if(existingUsers.size()!=userIds.size()){

         Set<Long> existingUserIds = new HashSet<Long>();
         for(User u: existingUsers){
            existingUserIds.add(u.twitterId);
         }   
         for(int i = 0; i<this.linksList.size(); i++){
            String link = this.linksList.get(i);
            Long id1 = Long.valueOf(link.split("-")[0]);
            Long id2 = Long.valueOf(link.split("-")[1]);
            if(!existingUserIds.contains(id1) || !existingUserIds.contains(id2)){
               this.linksList.remove(i);
               i--;
            }
         }
      }
   
   }
   protected void process(){
      
      checkNodesExistence();

      Set<Long> nodesSet = new HashSet<Long>();
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
      for(Long id : nodesSet){
         if(!nodesList.contains(id)){
            nodesList.add(id);
         }
      }
      
      findMaxCliques();
      
      
   }

}

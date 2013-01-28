package jobs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class LinksUtil extends ArrayList<String> {

   

   public Set<String> linksSet;
   public List<String> linksList;
   
   public List<String> undirectedLinksList = new ArrayList<String>();
   public List<String> undirectedLinksSrcList= new ArrayList<String>();
   public List<String> undirectedLinksTrgList= new ArrayList<String>();

   HashMap<String, Integer> incomingCountMap = new HashMap<String, Integer>();
   
   public Set<String> nodesSet = new HashSet<String>();
   public List<String> nodesList = new ArrayList<String>();

   Map<String,List<String>> adjList = new HashMap<String,List<String>>();

   public LinksUtil(Set<String> links) {
      super();
      this.linksSet = links;
      process();
   }
   public LinksUtil(List<String> visibleLinks) {

      super();
      this.linksSet = new HashSet<String>(visibleLinks);
      process();
   }
   
   public static void main(String[] args) {
      
      
      
      Set<String> visibleLinks = new HashSet<String>();
      visibleLinks.add("1-2");
      visibleLinks.add("2-1");
      visibleLinks.add("1-3");
      visibleLinks.add("2-3");
      visibleLinks.add("1-4");
      visibleLinks.add("3-1");
      visibleLinks.add("3-2");
      visibleLinks.add("4-1");
      visibleLinks.add("4-2");
      visibleLinks.add("4-3");
      visibleLinks.add("2-4");
      visibleLinks.add("3-4");
      visibleLinks.add("5-6");
      visibleLinks.add("7-8");
      visibleLinks.add("9-10");
      visibleLinks.add("11-12");
      visibleLinks.add("13-14");
   
      LinksUtil lu = new LinksUtil(visibleLinks );
      List<HashSet<Long>> a = lu.findMaxCliques();
     int b =0;
     b++;
   }
   
   public List<HashSet<Long>> findMaxCliques(){
      
      UndirectedGraph<String,String> unG = new UndirectedSparseGraph<String, String>();
for(int i = 0; i<undirectedLinksList.size();i++){
   unG.addEdge(undirectedLinksList.get(i), undirectedLinksSrcList.get(i), undirectedLinksTrgList.get(i));
 
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
//             Logger.info(node+",");
            
//          Logger.info("\n");
         }
      }
      return cliques;
   }
   private void process(){
      this.linksList = new ArrayList<String>(this.linksSet);
      
      for(int i =0; i<linksList.size(); i++){
         String link = linksList.get(i);
         String node1 = link.split("-")[0];
         String node2 = link.split("-")[1];
         
         nodesSet.add(node1);
         if(adjList.get(node1)==null){
            adjList.put(node1, new ArrayList<String>());
         }

         nodesSet.add(node2);
         if(adjList.get(node2)==null){
            adjList.put(node2, new ArrayList<String>());
         }
         
         boolean added12 = adjList.get(node1).add(node2);
         boolean added21 = adjList.get(node2).add(node1);

         if(added12){
            if(incomingCountMap.get(node1)==null){
               incomingCountMap.put(node1, 0);
            }
            incomingCountMap.put(node1, incomingCountMap.get(node1)+1);
         }
         if(added21){
            if(incomingCountMap.get(node2)==null){
               incomingCountMap.put(node2, 0);
            }
            incomingCountMap.put(node2, incomingCountMap.get(node2)+1);
         }
         
         String reverseLink = node2+"-"+node1;
         if(linksList.contains(reverseLink)){
            if(!undirectedLinksList.contains(reverseLink)){

               undirectedLinksList.add(link);
               undirectedLinksSrcList.add(node1);
               undirectedLinksTrgList.add(node2);
            }
         }
      }

   }


}

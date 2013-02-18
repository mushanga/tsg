package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jobs.GraphUtil;
import jobs.UserGraphUtil;
import jobs.GraphJobBase.UserComparator;

import models.Link;
import models.User;
import models.UserGraph;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Expander;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.kernel.Traversal;
import org.neo4j.kernel.impl.core.RelationshipProxy;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.neo4j.shell.ShellSettings;

import exception.UserDoesNotExistException;

import play.Logger;
import play.cache.Cache;

import scala.collection.JavaConversions;
import scala.collection.convert.Wrappers.SeqWrapper;
import util.UserLookup;
import util.Util;

public class GraphDatabase {
	private static final String FS = System.getProperty("file.separator");
	private static final String USER_ID = "user_id";
	//private static GraphDatabaseService graphDatabase = null;
	private static WrappingNeoServerBootstrapper srv = null;
	private static GraphDatabaseAPI graphDatabase = null;

	private static String GET_GRAPH_FOR_USER = 
	      " START root=node:user_id(user_id='73930194') "+
	     " MATCH p2 = root-->b, pr= b-[?]->root, p = b-[?]->a-->b "+
	      " where root-->a "+
	      " RETURN distinct b.user_id,p,RELATIONSHIPS(pr)";
	private static enum RelTypes implements RelationshipType {
		KNOWS, FOLLOWS
	}
	
	public static void startGraphDatabase() {
		String home = System.getenv("HOME");
		String graphDbPath = home + FS + ".tsg" + FS + "graphdb" + FS;
		graphDatabase = (GraphDatabaseAPI) new GraphDatabaseFactory()
				.newEmbeddedDatabaseBuilder(graphDbPath)
				.setConfig(ShellSettings.remote_shell_enabled, "true")
				.setConfig( GraphDatabaseSettings.node_keys_indexable, USER_ID).
			    setConfig( GraphDatabaseSettings.relationship_keys_indexable, String.valueOf(RelTypes.FOLLOWS )).
			    setConfig( GraphDatabaseSettings.node_auto_indexing, "true" ).
			    setConfig( GraphDatabaseSettings.relationship_auto_indexing, "true" )
				.newGraphDatabase();
		ServerConfigurator config;
		config = new ServerConfigurator(graphDatabase);
		// let the server endpoint be on a custom port
//      config.configuration().setProperty(
//            Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7575);
      config.configuration().setProperty(Configurator.WEBSERVER_ADDRESS_PROPERTY_KEY, "0.0.0.0");

		
		srv = new WrappingNeoServerBootstrapper(graphDatabase, config);
		srv.start();

		registerShutdownHook(graphDatabase);
	}

   public static void registerShutdownHook(
         final GraphDatabaseService graphDatabase) {
      // Registers a shutdown hook for the Neo4j instance so that it
      // shuts down nicely when the VM exits (even if you "Ctrl-C" the
      // running example before it's completed)
      Runtime.getRuntime().addShutdownHook(new Thread() {
         @Override
         public void run() {
            graphDatabase.shutdown();
         }
      });
   }
   public static void main(String[] args){
      
      startGraphDatabase();
      
//      addFriendships(src, followingList);
//      GraphUtil lin = getAllNodesAndLinksForUserGraph(73930194);
      int a = 0;
      a++;
      
      
   }
   private static List<Relationship> processResults(Object obj){
               List<Relationship> rels = new ArrayList<Relationship>();
               
               if(obj instanceof SeqWrapper<?>){
                  SeqWrapper<Relationship> ae = ((SeqWrapper<Relationship>) obj);
                  try {
                     
                     
                     for(Relationship rel : ae){

                        rels.add(rel);  
                     }
                   
                 } catch (Exception e) {
                    System.out.println(e.getMessage());
                 }
               }else if(obj instanceof RelationshipProxy){
                  RelationshipProxy a = (RelationshipProxy)obj;
                 
                  rels.add(a);  
               }
           
//             long startNodeId = (Long) rel.getStartNode().getProperty(USER_ID);
//             long endNodeId =(Long) rel.getEndNode().getProperty(USER_ID);;
//             visibleLinks.add(startNodeId+"-"+endNodeId);
               return rels;
      
   }
   private static Set<Long> getFollowingNodeIds(long userId){
      
      return getRelatedNodes(userId, Direction.OUTGOING,null,null,true);
      
   }
   private static Node getNodeByUserId(long userId){
      Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
      Node node = usersIndex.get(USER_ID, userId).getSingle();
    return node;
      
   }
   private static Long getNodeIdByUserId(long userId){
      Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
      Node node = usersIndex.get(USER_ID, userId).getSingle();
    return node.getId();
      
   }
   private static ArrayList<Relationship> executeAndGetRels(String query, Map<String, Object> params){
      GraphUtil liut = null;
      ArrayList<Relationship> relationships = new ArrayList<Relationship>();
      try {
         ExecutionEngine engine = new ExecutionEngine( graphDatabase );

         Set<String> visibleLinks = null;
         try {
            visibleLinks = new HashSet<String>();
            ExecutionResult result = engine.execute( query,params);
            for ( Map<String, Object> row : result )
            {
               for ( Entry<String, Object> column : row.entrySet() )
               {    
                  if(column.getValue()!=null){
                     relationships.addAll(processResults(column.getValue()));
                  }

               }
            }
         } catch (Exception e) {
            Logger.info(e.getMessage());
         }


      } catch (Exception e) {
        Logger.info(e.getMessage());
      }
      return relationships;
   }
//   public static UserGraphUtil getAllNodesAndLinksForUserGraph(long userId){
//      Set<Long> followings = getFollowings(userId);
//      Set<Long>  ownerAndFollowing =new HashSet<Long>(followings);
//      ownerAndFollowing.add(userId);
//
//      Set<Long> nodes = new HashSet();
//      Set<String> links = new HashSet<String>();
//
//      nodes.add(userId);
//      for (Long following : followings) {
//
//         nodes.add(following);         
//         links.add(userId+"-"+following);
//         
//         Set<Long> friendsOfFollowing = getMutualFriendsIncluding(following,ownerAndFollowing);
//
//         for (Long friendOfFollowing : friendsOfFollowing) {
//            links.add(friendOfFollowing+"-"+following);
//            links.add(following+"-"+friendOfFollowing);
//         }
//      }
//
//      Logger.info(userId+": UserGraphUtil - enter");
//      UserGraphUtil liut = new UserGraphUtil(userId, new ArrayList<Long>(nodes), new ArrayList<String>(links));
//      Logger.info(userId+": UserGraphUtil - exit");
//      return liut;
//   }
   public static UserGraphUtil getAllNodesAndLinksForUserGraph(long userId){
      List<Long> ownerAndFollowings = Link.getTargetsBySrc(userId);
      ownerAndFollowings.add(userId);

      List<String> links =  Link.getLinksRelatedTo(ownerAndFollowings);
      

      Logger.debug(userId+": UserGraphUtil - enter");
      UserGraphUtil liut = new UserGraphUtil(userId, ownerAndFollowings, links);
      Logger.debug(userId+": UserGraphUtil - exit");
      return liut;
   }
   public static UserGraphUtil getAllNodesAndLinksForUserGraphCypher(long userId) {
      Set<Long> followings = getFollowings(userId);
      Set<Long> followingNodeIds = getFollowingNodeIds(userId); 
//       Long rootNodeId = getNodeIdByUserId(userId);
//      followingNodeIds.add(rootNodeId);
      List<Relationship> relationships = new ArrayList<Relationship>();

      List<String> links = new ArrayList<String>();
      
   
      Set<Relationship> relsOfRoot = getRelationships(userId, Direction.BOTH, new ArrayList<Long>(followings), null);
      
      if(Util.isValid(relsOfRoot)){
       
         relationships.addAll(relsOfRoot);
      }      
   
      String query = 
            " start u=node({nodeIdList}), u2= node({nodeIdList2}) "+
            " MATCH u-[r]->u2, u2-[r2]->u "+
            " RETURN distinct r,r2; ";
      Map<String, Object> params = new HashMap<String, Object>();
      params.put( "nodeIdList", followingNodeIds );
      params.put( "nodeIdList2", followingNodeIds );
      
      ArrayList<Relationship> relsOfNeighbors = executeAndGetRels(query, params);

      
      if(Util.isValid(relsOfNeighbors)){
         relationships.addAll(relsOfNeighbors);
      }
      
      for(Relationship rel : relationships){
         links.add(rel.getStartNode().getProperty(USER_ID).toString()+"-"+rel.getEndNode().getProperty(USER_ID).toString());    
      }
      
      UserGraphUtil liut = new UserGraphUtil(userId, links);

      return liut;
   }
   public static GraphUtil getRelsOfUserInSet(long userId, Set<Long> userIdList) {
      Set<Long> processSet = getFollowingNodeIds(userId); 

      List<Relationship> relationships = new ArrayList<Relationship>();

      Node rootNode = getNodeByUserId(userId);
      Iterable<Relationship> relsOfRoot = rootNode.getRelationships(Direction.BOTH);
      for(Relationship rel: relsOfRoot){
         relationships.add(rel);
      }
      String query = 
            " start u=node({nodeIdList}), u2= node({nodeIdList2}) "+
            " MATCH u-[r]-u2 "+
            " RETURN distinct r; ";
      Map<String, Object> params = new HashMap<String, Object>();
      params.put( "nodeIdList", processSet );
      params.put( "nodeIdList2", processSet );
      
      ArrayList<Relationship> relsOfNeighbors = executeAndGetRels(query, params);

      if(Util.isValid(relsOfNeighbors)){
         relationships.addAll(relsOfNeighbors);
      }
      
      ArrayList<String> links = new ArrayList<String>();
      for(Relationship rel : relationships){
         links.add(rel.getStartNode().getProperty(USER_ID).toString()+"-"+rel.getEndNode().getProperty(USER_ID).toString());    
      }
      
      GraphUtil liut = new GraphUtil(links);

      return liut;
   }
	public static void shutDown() {
		if (srv != null) {
			srv.stop();
		}
	}

//   public static synchronized void addFriendships(long src, Set<Long> followingList) {
////    Logger.info("Adding "+followingList.size()+" followings of "+src);
//      Transaction tx = graphDatabase.beginTx();
//      try {
//         addNodeNoTx(src);
//         for (Long trg : followingList) {
//
//            addFriendshipNoTx(src, trg);
//         }
//
//         tx.success();
//
//      } finally {
//         tx.finish();
////       Logger.info("Finished adding "+followingList.size()+" followings of "+src);
//      }
//
//   }
   public static synchronized void addFriendships(long src, Set<Long> followingList) {
//    Logger.info("Adding "+followingList.size()+" followings of "+src);
     
      try {
         Link.add(src, new ArrayList<Long>(followingList));
         

      } finally {
   
//       Logger.info("Finished adding "+followingList.size()+" followings of "+src);
      }

   }
	private static List<Long> getCacheQuery(long userId, List<Long> userSet){
	     List<Long> results = (List<Long>) Cache.get("gdb-"+userId+"-"+Util.getIdListAsCommaSeparatedString(userSet));
	     return results;
	}

   private static void setCacheQuery(long userId, List<Long> userSet, Set<Long> friends) {
      Collections.sort(userSet);
      
      
      Cache.set("gdb-"+userId+"-"+Util.getIdListAsCommaSeparatedString(userSet), friends, "30mn");
   }
   
   
   
   private static List<Long> getCacheQuery(long userId){
      return (List<Long>) Cache.get("gdb-"+userId);
   }
	private  static void addNodeNoTx(long nodeId) {
	   Node node1 = graphDatabase.createNode();
      // user.setDataToGDBNode(node1);
	   Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);

      Node existing = usersIndex.putIfAbsent(node1, USER_ID, nodeId);
      if (existing != null) {
         node1 = existing;
      }

      node1.setProperty(USER_ID, nodeId);
	}
	   

	private  static void addFriendshipNoTx(long src, long trg) {
		if (src < 0 || trg < 0) {
			return;
		}
		Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);

		Node node1 = graphDatabase.createNode();
		// user.setDataToGDBNode(node1);

		Node existing = usersIndex.putIfAbsent(node1, USER_ID, src);
		if (existing != null) {
			node1 = existing;
		}

		node1.setProperty(USER_ID, src);

		Node node2 = graphDatabase.createNode();
		// friend.setDataToGDBNode(node2);

		Node existing2 = usersIndex.putIfAbsent(node2, USER_ID, trg);
		if (existing2 != null) {
			node2 = existing2;
		}

		node2.setProperty(USER_ID, trg);

		Iterable<Relationship> a = node1.getRelationships(RelTypes.FOLLOWS, Direction.OUTGOING);

		boolean found = false;
		for (Relationship rel : a) {
			if (rel.getEndNode().getProperty(USER_ID).equals(trg)) {
				found = true;
				break;
			}
		}

		if (!found) {
			node1.createRelationshipTo(node2, RelTypes.FOLLOWS);

		}

	}
	public static void addUserAndFriends(long announcer, long[] friends) {
		Transaction tx = graphDatabase.beginTx();
		try {
			Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
			Node node = graphDatabase.createNode();
			node.setProperty(USER_ID, announcer);
			Node existing = usersIndex.putIfAbsent(node, USER_ID, announcer);
			if (existing != null) {
				node = existing;
			}

			for (long friend : friends) {
				Node friendNode = graphDatabase.createNode();
				friendNode.setProperty(USER_ID, friend);
				existing = usersIndex.putIfAbsent(friendNode, USER_ID, friend);
				if (existing != null) {
					friendNode = existing;
				}
				node.createRelationshipTo(friendNode, RelTypes.KNOWS);
			}
			tx.success();
		} finally {
			tx.finish();
		}

	}

	public static int findConnectionBetweet(long from, long to) {
		if(graphDatabase == null){
			return -1;
		}
		Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
		Node fromNode = usersIndex.get(USER_ID, from).getSingle();
		Node toNode = usersIndex.get(USER_ID, to).getSingle();
		if (fromNode != null && toNode != null) {
			Expander expander = Traversal.expanderForTypes(RelTypes.KNOWS,
					Direction.OUTGOING);
			PathFinder<Path> pathFinder = GraphAlgoFactory.shortestPath(
					expander, 5);
			Path path = pathFinder.findSinglePath(fromNode, toNode);
			return path == null ? -1 : path.length();
		} else {
			return -1;
		}

	}

//   public static Set<Long> getFollowings(long srcId) {
//      return getFollowings(srcId, null,null);
//   }

   public static Set<Long> getFollowings(long srcId) {
      return new HashSet<Long>(Link.getTargetsBySrc(srcId));
     
   }
	public static Set<Long> getFollowings(long srcId, List<Long> including, List<Long> exluding) {
		return getRelatedNodes(srcId, Direction.OUTGOING,including,exluding,false);
	}

	public static Set<Long> getFollowers(long srcId) {
		return getFollowers(srcId ,null,null);
	}

	public static Set<Long> getFollowers(long srcId, List<Long> including, List<Long> exluding) {
		return getRelatedNodes(srcId, Direction.INCOMING, including, exluding,false);
	}
	private static Set<Long> getRelatedNodes(long srcId, Direction direction, List<Long> including, List<Long> exluding, boolean returnNodeId) {
		Set<Long> friends = new HashSet<Long>();

	
		Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
		Node node = usersIndex.get(USER_ID, srcId).getSingle();
		Iterable<Relationship> rels = node.getRelationships(direction);

		if (rels != null) {
			for (Relationship rel : rels) {
				Node otherNode = rel.getOtherNode(node);
				long otherNodeId = (Long) otherNode.getProperty(USER_ID);
				
				if (!Util.isListValid(including) || (including.contains(otherNodeId))) {
					if (!Util.isListValid(exluding) || (!exluding.contains(otherNodeId))) {
					   if(returnNodeId){
					      friends.add(otherNode.getId());
					   }else{

	                  friends.add(otherNodeId);
					   }
					}
				}
			}
		}

	
		return friends;

	}
	  private static Set<Relationship> getRelationships(long srcId, Direction direction, List<Long> including, List<Long> exluding) {
	      

	   Set<Relationship> relationships = new HashSet<Relationship>();
	      Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
	      Node node = usersIndex.get(USER_ID, srcId).getSingle();
	      Iterable<Relationship> rels = node.getRelationships(direction);

	      if (rels != null) {
	         for (Relationship rel : rels) {
	            Node otherNode = rel.getOtherNode(node);
	            long otherNodeId = (Long) otherNode.getProperty(USER_ID);
	            
	            if (!util.Util.isListValid(including) || (including.contains(otherNodeId))) {
	               if (!Util.isListValid(exluding) || (!exluding.contains(otherNodeId))) {
	                  relationships.add(rel);
	               }
	            }
	         }
	      }

	   
	      return relationships;

	   }
	  
	public static Set<Long> convertRelationsToFriendSet(Node node, Iterable<Relationship> rels){
	   Set<Long> friends = new HashSet<Long>();
	   if (rels != null) {
         for (Relationship rel : rels) {
            
            long id2 = (Long) rel.getOtherNode(node).getProperty(USER_ID);
            friends.add(id2);
            }
      }
	   return friends;
	}
	public static Set<Long> getMutualFriendsIncluding(long srcId, Collection<Long> userAndAllFollowings) {
		

//      Logger.info(srcId+": getMutualFriendsIncluding - enter");
	   Set<Long> friends = new HashSet<Long>();

		Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
		Node node = usersIndex.get(USER_ID, srcId).getSingle();
		Iterable<Relationship> rels = node.getRelationships(Direction.OUTGOING);
      Iterable<Relationship> rels2 = node.getRelationships(Direction.INCOMING);

      Set<Long> outgoingSet = convertRelationsToFriendSet(node, rels);

      Set<Long> incomingSet = convertRelationsToFriendSet(node, rels2);
      
      friends.addAll(incomingSet);
      friends.retainAll(outgoingSet);
		
      friends.retainAll(userAndAllFollowings); 
		


//      Logger.info(srcId+": getMutualFriendsIncluding - exit");
		return friends;

	}

   public static Set<Long> getMutualFriends(long srcId) {
		return getMutualFriendsIncluding(srcId, null);

	}
	private synchronized static void clearRelations(long srcId, Direction direction, List<Long> including, List<Long> exluding) {
		Set<Long> friends = new HashSet<Long>();

		Transaction tx = graphDatabase.beginTx();
		try {

			Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
			Node node = usersIndex.get(USER_ID, srcId).getSingle();
			if (node != null) {

				Iterable<Relationship> rels = node.getRelationships(direction);

				if (rels != null) {
					for (Relationship rel : rels) {
						Node otherNode = rel.getOtherNode(node);
						long otherNodeId = (Long) otherNode.getProperty(USER_ID);

						if (!util.Util.isListValid(including) || (including.contains(otherNodeId))) {
							if (!Util.isListValid(exluding) || (!exluding.contains(otherNodeId))) {

								rel.delete();
							}
						}
					}
				}
			}

			tx.success();

		} finally {
			tx.finish();
		}
		

	
	}
//   public static void clearFollowings(long id) {
//      clearRelations(id, Direction.OUTGOING, null, null);
//   }
   public static void clearFollowings(long id) {
      Link.clearBySrc(id);
   }
}
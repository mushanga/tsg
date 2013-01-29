package graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.neo4j.graphdb.index.Index;
import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.kernel.Traversal;
import org.neo4j.server.WrappingNeoServerBootstrapper;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.neo4j.shell.ShellSettings;

import util.Util;

public class GraphDatabase {
	private static final String FS = System.getProperty("file.separator");
	private static final String USER_ID = "user_id";
	//private static GraphDatabaseService graphDatabase = null;
	private static WrappingNeoServerBootstrapper srv = null;
	private static GraphDatabaseAPI graphDatabase = null;

	private static enum RelTypes implements RelationshipType {
		KNOWS, FOLLOWS
	}
	
	public static void startGraphDatabase() {
		String home = System.getenv("HOME");
		String graphDbPath = home + FS + ".tsg" + FS + "graphdb" + FS;
		graphDatabase = (GraphDatabaseAPI) new GraphDatabaseFactory()
				.newEmbeddedDatabaseBuilder(graphDbPath)
				.setConfig(ShellSettings.remote_shell_enabled, "true")
				.newGraphDatabase();
		ServerConfigurator config;
		config = new ServerConfigurator(graphDatabase);
		// let the server endpoint be on a custom port
		config.configuration().setProperty(
				Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7575);

		
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

	public static void shutDown() {
		if (srv != null) {
			srv.stop();
		}
	}

	public static synchronized void addFriendships(long src, Set<Long> followingList) {
		Transaction tx = graphDatabase.beginTx();
		try {
		   addNodeNoTx(src);
			for (Long trg : followingList) {

				addFriendshipNoTx(src, trg);
			}

			tx.success();

		} finally {
			tx.finish();
		}

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

	public static Set<Long> getFollowings(long srcId) {
		return getFollowings(srcId, null,null);
	}
	public static Set<Long> getFollowings(long srcId, List<Long> including, List<Long> exluding) {
		return getRelatedNodes(srcId, Direction.OUTGOING,including,exluding);
	}

	public static Set<Long> getFollowers(long srcId) {
		return getFollowers(srcId ,null,null);
	}

	public static Set<Long> getFollowers(long srcId, List<Long> including, List<Long> exluding) {
		return getRelatedNodes(srcId, Direction.INCOMING, including, exluding);
	}
	private static Set<Long> getRelatedNodes(long srcId, Direction direction, List<Long> including, List<Long> exluding) {
		Set<Long> friends = new HashSet<Long>();

	
		Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
		Node node = usersIndex.get(USER_ID, srcId).getSingle();
		Iterable<Relationship> rels = node.getRelationships(direction);

		if (rels != null) {
			for (Relationship rel : rels) {
				Node otherNode = rel.getOtherNode(node);
				long otherNodeId = (Long) otherNode.getProperty(USER_ID);
				
				if (!util.Util.isListValid(including) || (including.contains(otherNodeId))) {
					if (!Util.isListValid(exluding) || (!exluding.contains(otherNodeId))) {

						friends.add(otherNodeId);
					}
				}
			}
		}

	
		return friends;

	}
	public static Set<Long> getMutualFriendsIncluding(long srcId, Collection<Long> userAndAllFollowings) {
		Set<Long> friends = new HashSet<Long>();

		Index<Node> usersIndex = graphDatabase.index().forNodes(USER_ID);
		Node node = usersIndex.get(USER_ID, srcId).getSingle();
		Iterable<Relationship> rels = node.getRelationships(Direction.BOTH);

		ArrayList<String> links = new ArrayList<String>();
		if (rels != null) {
			for (Relationship rel : rels) {
				
				long id1 = (Long) rel.getStartNode().getProperty(USER_ID);
				long id2 = (Long) rel.getEndNode().getProperty(USER_ID);
				if (!Util.isValid(userAndAllFollowings) || (userAndAllFollowings.contains(id1) && userAndAllFollowings.contains(id2))) {

					String startId = String.valueOf(id1);
					String endId = String.valueOf(id2);
					links.add(startId + "-" + endId);
				}
			}
		}

		for (int i = 0; i < links.size(); i++) {
			String link = links.get(i);
			Long id1 = Long.valueOf(link.split("-")[0]);
			Long id2 = Long.valueOf(link.split("-")[1]);
			String mirrored = id2 + "-" + id1;

			if (links.contains(mirrored)) {

				if (srcId == id1) {
					friends.add(id2);
				} else {
					friends.add(id1);
				}

				links.remove(mirrored);
			}

		}

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
	public static void clearFollowings(long id) {
		clearRelations(id, Direction.OUTGOING, null, null);
	}
}
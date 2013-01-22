package graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Link;

import org.neo4j.graphdb.Direction;

import util.Util;


public class IMGraphDatabase {
	
	public Set<Link> links = new HashSet<Link>();

	public IMGraphDatabase(Set<Link> links) {
		super();
		this.links = links;
	}

//	public Set<Long> getMutualFriends(long srcId) {
//		return getRelatedNodes(srcId, null , null, null);
//	}

//	private Set<Long> getRelatedNodes(long srcId, Direction direction, List<Long> including, List<Long> exluding) {
//		Set<Long> filteredNodes = new HashSet<Long>();
//
//		Set<Long> relatedNodes = new HashSet<Long>();
//
//		if(Direction.INCOMING.equals(direction)){
//			relatedNodes = Link.getSourceSet(srcId);
//		}
//		else if(Direction.OUTGOING.equals(direction)){
//			relatedNodes = Link.getTargetSet(srcId);
//		}
//		else if(Direction.BOTH.equals(direction)){
//			relatedNodes = Link.getRelated(srcId);
//			
//		}
//		else {
//
//			relatedNodes = Link.getSourceSet(srcId);
//
//			Set<Long> temp2 = Link.getTargetSet(srcId);
//			relatedNodes.retainAll(temp2);
//		}
//		//Link.getBySrcOrTarget(srcId);
//		
//		if (relatedNodes != null) {
//			for (Long related : relatedNodes) {
//				
//				if (!Util.isListValid(including) || (including.contains(related))) {
//					if (!Util.isListValid(exluding) || (!exluding.contains(related))) {
//
//						filteredNodes.add(related);
//					}
//				}
//			}
//		}
//
//	
//		return filteredNodes;
//
//	}
	
	private void includeExclude(List<Long> including, List<Long> exluding){

	}

	
}
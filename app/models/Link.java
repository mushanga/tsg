package models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.JPABase;
import play.db.jpa.Model;

//@Table( name = "link",
//uniqueConstraints = { @UniqueConstraint( columnNames = { "srcId", "trgId" } ) })
public class Link{

	@Version
	public Long version;

	@Required
	public Long srcId;
	
	@Required
	public Long trgId;

//	@ManyToMany(cascade={CascadeType.ALL})
//	@JoinTable(name = "graphLink", joinColumns = { @JoinColumn(name = "id") }, inverseJoinColumns = { @JoinColumn(name = "ownerId")})
//	public Set<UserGraph> graphs = new HashSet<UserGraph>();

//	@ManyToMany(cascade={CascadeType.ALL})
//	@JoinTable(name = "graphLink", joinColumns = { @JoinColumn(name = "linkId") }, inverseJoinColumns = { @JoinColumn(name = "graphId") })
//	public List<UserGraph> graphs = new ArrayList<UserGraph>();
	
	public Link( Long srcId, Long trgId) {
		super();
		this.srcId = srcId;
		this.trgId = trgId;
	}

//	public static List<Link> getBySrc(Long srcId){
//		return Link.find("select l from Link l where l.srcId = ?", srcId).fetch();
//	}
//	public static List<Link> getLinksRelatedTo(Set<Long> ids){
//		String jpql = "select l from Link l where l.srcId in (:ids) and l.trgId in (:ids)";
//
//		Query q = JPA.em().createQuery(jpql);
//		q.setParameter("ids",ids);
//		return q.getResultList();
//	}
//
//	public static Set<Long> getTargetSet(Long srcId){
//		List<Link> links = Link.find("select l from Link l where l.srcId = ?", srcId).fetch();
//		
//		Set<Long> trgSet = new HashSet<Long>();
//		for(Link link : links){
//			trgSet.add(link.trgId);
//		}
//		return trgSet;
//				
//	}
//	public static Set<Long> getSourceSet(Long trgId){
//		List<Link> links = Link.find("select l from Link l where l.trgId = ?", trgId).fetch();
//		
//		Set<Long> srcSet = new HashSet<Long>();
//		for(Link link : links){
//			srcSet.add(link.srcId);
//		}
//		return srcSet;
//				
//	}
//
//	public static Set<Long> getRelated(long id) {
//			List<Link> links = Link.find("select l from Link l where l.srcId = ?", id).fetch();
//		
//		Set<Long> set = new HashSet<Long>();
//		for(Link link : links){
//			set.add(link.trgId);
//			set.add(link.srcId);
//		}
//		set.remove(id);
//		return set;
//	}
//
//	public static List<Link> getByTrg(Long trgId){
//		return Link.find("select l from Link l where l.trgId = ?", trgId).fetch();
//	}
//	public static Link getBySrcTrg(Long srcId, Long trgId){
//		return Link.find("select l from Link l where  l.srcId = ? and l.trgId = ?", srcId, trgId).first();
//	}
//	
//
//	public static void saveIfInexistent(Long srcId, Long trgId){
//		Link existing = getBySrcTrg(srcId, trgId);
//		if(existing==null){
//			Link link = new Link(srcId, trgId);
//			link.save();
//		}
//		
//	}
//	public static void saveIfInexistent(Long srcId, Set<Long> trgIdList){
//		for(Long trgId : trgIdList){
//
//			Link existing = getBySrcTrg(srcId, trgId);
//			if(existing==null){
//				Link link = new Link(srcId, trgId);
//				link.save();
//			}
//		}
//		
//	}
//	
//	public static void saveIfInexistent(List<Link> linkList){
//		for(Link link : linkList){
//
//			Link existing = getBySrcTrg(link.srcId, link.trgId);
//			if(existing==null){
//				link.save();
//			}
//		}
//		
//	}
//	public static void saveIfInexistent(Link link){
//	
//		ArrayList<Link> list = new ArrayList<Link>();
//		list.add(link);
//		saveIfInexistent(list);
//		
//	}


}

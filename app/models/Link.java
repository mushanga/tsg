package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
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

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = { "srcId","trgId" })})
public class Link extends TSGModel{

   public Date updatedAt;

	@Required
	public Long srcId;
	
	@Required
	public Long trgId;
	
	public Link( Long srcId, Long trgId) {
		super();
		this.srcId = srcId;
		this.trgId = trgId;
	}

   
   @PrePersist
   void createdAt() {
      this.updatedAt  = new Date();
   }

   @PreUpdate
   void updatedAt() {
      this.updatedAt = new Date();
   }

   public String toString(){
      return srcId+"-"+trgId;
   }
   
   public static void add(Long srcId, Long trgId){
      Link link = new Link(srcId, trgId);
      link.save();     
   }

   public static void add(Long srcId, List<Long> trgIdList){
      for(Long trgId : trgIdList){
         add(srcId, trgId);
      }
   }

   public static void clearBySrc(Long srcId){
      Link.delete("srcId=?", srcId);
   }

   public static List<Long> getTargetsBySrc(long userId) {
      return Link.find("select trgId from Link l where l.srcId = ?", userId).fetch();
   }
   
   
   public static List<Link> getBySrc(Long srcId){
      return Link.find("select l from Link l where l.srcId = ?", srcId).fetch();
   }
	
   public static List<String> getLinksRelatedTo(List<Long> ownerAndFollowings){
		String jpql = "select l.srcId||'-'||l.trgId from Link l where l.srcId in (:ids) and l.trgId in (:ids)";
		Query q = JPA.em().createQuery(jpql);
		q.setParameter("ids",ownerAndFollowings);
		return q.getResultList();
	}

   public static List<Link> getByTrg(Long trgId){
      return Link.find("select l from Link l where l.trgId = ?", trgId).fetch();
   }
   
   public static Link getBySrcTrg(Long srcId, Long trgId){
      return Link.find("select l from Link l where  l.srcId = ? and l.trgId = ?", srcId, trgId).first();
   }


}

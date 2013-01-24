package models;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.bouncycastle.util.encoders.Base64;


import play.data.validation.Required;
import play.db.jpa.Model;
import util.Common;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = { "ownerId" })})
public class FollowingList extends TSGModel {
	public FollowingList(Long ownerId) {
		super();
		this.ownerId = ownerId;
		this.status = WAITING;
	}
	
	public String ownerScreenName;
	
	public Long ownerId;	

	public String status;

	@Version
	public Long version;
	public Date createdAt;
	public Date updatedAt;

	@PrePersist
	void createdAt() {
		this.createdAt = this.updatedAt = new Date();
	}

	@PreUpdate
	void updatedAt() {
		this.updatedAt = new Date();
	}
//	@ManyToMany(cascade={CascadeType.ALL})
//	@JoinTable(name = "followingLink", joinColumns = { @JoinColumn(name = "ownerId") }, inverseJoinColumns = { @JoinColumn(name = "id")})
//	public Set<Link> links = new HashSet<Link>();

	public static String WAITING = "Waiting...";
	public static String IN_PROGRESS = "In Progress...";
	public static String ERROR = "Error";
	public static String COMPLETED = "Completed";
	public static String PROTECTED = "Protected";

	
	
	public static Long getCompletedFollowingListCount(Set<Long> followings){
		return FollowingList.find("select count(*) from FollowingList fl where (status =? OR status = ?) and ownerId in (:ids)", COMPLETED,PROTECTED).bind("ids", followings).first();
	}
	public static FollowingList getByOwnerId(Long ownerId){
		return FollowingList.find("byOwnerId", ownerId).first();
	}

	public static FollowingList getWaiting(){
		return FollowingList.find("byStatus", WAITING).first();
	}
	public static List<FollowingList> getWaitingList(){
		return FollowingList.find("byStatus", WAITING).fetch();
	}

	public static List<FollowingList> getInProgressList(){
		return FollowingList.find("byStatus", IN_PROGRESS).fetch();
	}

	public boolean isProtected(){
		return this.status.equals(PROTECTED);
	}

	public boolean isCompleted(){
		return this.status.equals(COMPLETED);
	}
	public boolean isWaiting(){
		return this.status.equals(WAITING);
	}

	private void setStatus(String status) {
		this.status = status;
		this.saveImmediately();
		
	}
	


	public void setStatusInProgress() {
		setStatus(IN_PROGRESS);
	}

	public void setStatusCompleted() {
		setStatus(COMPLETED);
	}
	
	public void setStatusProtected() {
		setStatus(PROTECTED);
	}

	public void setStatusWaiting() {
		setStatus(WAITING);	
	}
	
//	public List<Long> getTargetIds(){
//		List<Long> ids = new ArrayList<Long>();
//		for(Link link : links){
//			ids.add(link.trgId);
//		}
//		
//		return ids;
//	}
//
//	public void addLink(Link link){
//		if(!this.links.contains(link)){
//			this.links.add(link);
//		}
//		
//	}
//	public void addLinks(Link link){
//		if(!this.links.contains(link)){
//			this.links.add(link);
//		}
//		
//	}
//	public void addLinks(Set<Long> targets){
//		
//		if(!this.links.addAll(link)){
//			this.links.add(link);
//		}
//		
//	}
	
}

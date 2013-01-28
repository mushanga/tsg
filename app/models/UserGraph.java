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
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.bouncycastle.util.encoders.Base64;


import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;
import util.Common;

@Entity
@Table(uniqueConstraints={@UniqueConstraint(columnNames = { "ownerId" })})
public class UserGraph extends TSGModel {
	
	public int total;
	public int completed;
	public Long ownerId;	

	public String status;

	
	public Long version = 0L;

	public UserGraph(Long ownerId) {
		super();
		this.ownerId = ownerId;
		this.status = WAITING;
	}
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
	
	@Transient
	public Set<User> visibleUsers = new HashSet<User>();

	@Transient
	public Set<User> visibleLinks = new HashSet<User>();
	
	
	public static String WAITING = "Waiting...";
	public static String IN_PROGRESS = "In Progress...";
	public static String ERROR = "Error";
   public static String CONTRUCTING = "Constructing...";
   public static String PROTECTED = "Protected";
   public static String SUCCESSFUL = "Successful";
   public static String COMPLETED = "Completed";

	public static UserGraph getReadyToBeFinalized(){
		return UserGraph.find("select ug from UserGraph ug where completed = total and total>0 and status = ?", IN_PROGRESS).first();
	}
   public static UserGraph getWaiting(){
      return UserGraph.find("byStatus", WAITING).first();
   }
   public static List<UserGraph> getWaitingList(){
      return UserGraph.find("byStatus", WAITING).fetch();
   }
	public static UserGraph getInProgress(){
		return UserGraph.find("byStatus", IN_PROGRESS).first();
	}
	public static List<UserGraph> getInProgressList(){
		return UserGraph.find("byStatus", IN_PROGRESS).fetch();
	}
	public static UserGraph getConstructing(){
		return UserGraph.find("byStatus", CONTRUCTING).first();
	}

	public static UserGraph getByOwnerId(long ownerId){
		return UserGraph.find("byOwnerId", ownerId).first();
	}

   public boolean isProtected(){
      return this.status.equals(PROTECTED);
   }
   public boolean isSuccessful(){
      return this.status.equals(SUCCESSFUL);
   }

	private void setStatus(String status) {
		this.status = status;
//		this.saveImmediately();
		
	}


	public void setStatusInProgress() {
		setStatus(IN_PROGRESS);
	}

//   public void setStatusCompleted() {
//      setStatus(COMPLETED);
//   }
   public void setStatusSuccessful() {
      setStatus(SUCCESSFUL);
   }
	public void setStatusConstructing() {
		setStatus(CONTRUCTING);
	}
	public void setStatusProtected() {
		setStatus(PROTECTED);
	}

	public void setStatusWaiting() {
		setStatus(WAITING);	
	}

	public boolean isCompleted(){
		return SUCCESSFUL.equalsIgnoreCase(this.status) || PROTECTED.equalsIgnoreCase(this.status);
	}
	
}

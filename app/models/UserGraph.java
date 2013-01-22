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
public class UserGraph extends Model {
	
	
	public Long ownerId;	

	public String status;

	@Version
	public Long version;

	public UserGraph(Long ownerId) {
		super();
		this.ownerId = ownerId;
		this.status = WAITING;
	}

	
//	@ManyToMany(cascade={CascadeType.ALL})
//	@JoinTable(name = "graphLink", joinColumns = { @JoinColumn(name = "ownerId") }, inverseJoinColumns = { @JoinColumn(name = "id")})
//	public Set<Link> links = new HashSet<Link>();

	@Transient
	public Set<User> visibleUsers = new HashSet<User>();

	@Transient
	public Set<User> visibleLinks = new HashSet<User>();
	
	
	public static String WAITING = "Waiting...";
	public static String IN_PROGRESS = "In Progress...";
	public static String ERROR = "Error";
	public static String READY_TO_CONSTRUCT = "Ready to Construct";
	public static String COMPLETED = "Completed";
	public static String PROTECTED = "Protected";

	public static UserGraph getWaiting(){
		return UserGraph.find("byStatus", WAITING).first();
	}
	public static UserGraph getInProgress(){
		return UserGraph.find("byStatus", IN_PROGRESS).first();
	}
	public static List<UserGraph> getInProgressList(){
		return UserGraph.find("byStatus", IN_PROGRESS).fetch();
	}
	public static UserGraph getReadyToContruct(){
		return UserGraph.find("byStatus", READY_TO_CONSTRUCT).first();
	}

	public static UserGraph getByOwnerId(long ownerId){
		return UserGraph.find("byOwnerId", ownerId).first();
	}
	
	public boolean isProtected(){
		return this.status.equals(PROTECTED);
	}

	public void setStatusInProgress() {
		this.status = IN_PROGRESS;
	}

	public void setStatusCompleted() {
		this.status = COMPLETED;
	}
	public void setStatusReadyToConstruct() {
		this.status = READY_TO_CONSTRUCT;
	}
	public void setStatusProtected() {
		this.status = PROTECTED;
	}

	public void setStatusWaiting() {
		this.status = WAITING;
	}
	public void saveImmediately() {
		boolean noExistingTx=!JPA.em().getTransaction().isActive();
		if(noExistingTx){

		    JPA.em().getTransaction().begin();
		}
		this.save();
	    JPA.em().flush();
	    JPA.em().getTransaction().commit();
		if(!noExistingTx){

		    JPA.em().getTransaction().begin();
		}
	}
	
	
}

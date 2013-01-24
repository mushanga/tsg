package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.Model;

@Entity
public class Visitor extends TSGModel {
	
	@ManyToOne
	public Item item;
	
	@ManyToOne
	public User user;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date date;
	
	public Visitor(Item item, User user){
		this.item = item;
		this.user = user;
		this.date = new Date();
	}
	
}

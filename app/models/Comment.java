package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.Model;
import util.Common;

@Entity
public class Comment extends Model{
	
	public String commentText;
	
	@ManyToOne
	public User owner;
	
	@ManyToOne
	public Item item;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date date;
	
	public Comment(User owner, String text,Item item){
		this.item = item;
		this.owner = owner;
		this.commentText = text;
		date = new Date();
	}
	
	public String getTime(){
		return Common.dateSince(date.getTime());
	}

}

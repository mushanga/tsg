package models;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import play.db.jpa.Model;

@Entity
public class Reply extends Model{
	public long tweetId;
	public String tweet;
	@ManyToOne
	public Tweet source;
}

package models;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.db.jpa.Model;

@Entity @Table(uniqueConstraints = {@UniqueConstraint(columnNames={"tweetId"})})
public class Tweet extends Model{
	private static final int MAX_DAY_DIFF = 2;
	public long tweetId;
	public String tweet;
	@ManyToOne
	public User owner;
	public boolean responded;
	@ManyToOne
	public User respondedBy;
	@ManyToOne
	public Item item;
	public Date created;
	public Date updated;
	
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="source")
	public List<Reply> replyList;
	
	
	public static Tweet getTweet2Ads(){
		return Tweet.find("responded = false and tweet.item.owner.adsTweetLevel <> ?1 and (tweet.owner.lastResponded is null or (NOW(),tweet.owner.lastResponded) > ?2) and owner <> item.owner order by tweet.item.owner.lastAds asc, created desc", AdsTweetLevel.NONE,MAX_DAY_DIFF).first();
	}
	
	public static Tweet findByTwitterId(Long tweetId){
		return Tweet.find("byTweetId", tweetId).first();
	}
}

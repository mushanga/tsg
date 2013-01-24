package models;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.codec.binary.Hex;

import analysis.AnalysisResult;
import analysis.AnalyzerFactory;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class Item extends TSGModel{
	
	@Required
	@Column(length=4096)
	public String description;
	
	@Required
	public String picture;
	
	@ManyToOne
	public User owner;
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date date;
	
	@ManyToMany
	public List<SearchKey> searchKeys;
	
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="item")
	public List<Tweet> tweets; 
	
	public Date lastAds;
	
	@OneToMany(mappedBy="item")
	public List<Comment> comments;
	
	@OneToMany(mappedBy="item")
	public List<Visitor> visitors;
	
	public String shortLink;
	
	public Item(String description, String picture, User owner){
		this.description = description;
		this.picture = picture;
		this.owner = owner;
		this.date = new Date();
		this.searchKeys = AnalyzerFactory.createAnalyzer().anaylze(this).getSearchKeys();
	}
	
	public static Item findItem2Ads(){
		return null;//return Item.find("owner.adsTweetLevel <> ?1 and (select count(*) from tweet where item = ) order by lastAds", AdsTweetLevel.NONE).first();
	}
	
	public static List<Item> searchTitle(String keyword){
		return Item.find("select i from Item i where i.description like ?", "%"+keyword.toLowerCase()+"%").fetch();
	}
	
	public static int getUniqueVisitorCount(long itemId){
		int count = 0;
		try{
			List<User> visits = User.find("select v.user from Visitor v where v.item.id = ? group by v.user",itemId).fetch();
			count = visits.size();
		}catch(Exception e){
			Logger.error("exception on visitor count ", e);
		}
		return count;
	}
	
	public List<User> getVisitors(){
		List<User> visits = new ArrayList<User>();
		try{
			visits = User.find("select v.user from Visitor v where v.item = ? group by v.user",this).fetch();
		}catch(Exception e){
			Logger.error("exception on visitor count ", e);
		}
		return visits;
	}

	public static List<Item> findItemsByUser(User owner) {
		List<Item> itemList = new ArrayList<Item>();
		itemList = Item.find("byOwner", owner).fetch();
		return itemList;
	}

	public static void deleteById(Long itemId) {
		delete("id = ?", itemId);
	}

	public void update(String description, String fullUrl) {
		if(!description.equals(this.description)){
			this.description = description;
			this.searchKeys = AnalyzerFactory.createAnalyzer().anaylze(this).getSearchKeys();
		}
		if(fullUrl != null){
			this.picture = fullUrl;
		}
		this.save();
	}
	
	public void update(String description){
		update(description, null);
	}

}

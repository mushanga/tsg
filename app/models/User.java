package models;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.bouncycastle.util.encoders.Base64;


import play.data.validation.Required;
import play.db.jpa.Model;
import twitter.UserJSONImpl;
import util.Common;

@Entity
public class User extends TSGModel {

	public String screenName;
	public Long twitterId;
	public String authToken;
	public String authTokenSecret;
	public String picture;
	public String fullName;
	public String description;
	public String webSite;
	public String location;
	public Date lastUsed = null;
	public Date lastAds = null;
	public Date firstLogin = null;
	public Date lastLogin = null;
   public Date lastResponded = null;
   public int friendsCount;
   public int followersCount;
	

   public User(User user) {
      super();
      
      this.screenName = user.screenName;
      this.twitterId = user.twitterId;
      this.authToken = user.authToken;
      this.authTokenSecret = user.authTokenSecret;
      this.picture = user.picture;
      this.fullName = user.fullName;
      this.description = user.description;
      this.webSite = user.webSite;
      this.location = user.location;
      this.lastUsed = user.lastUsed;
      this.lastAds = user.lastAds;
      this.firstLogin = user.firstLogin;
      this.lastLogin = user.lastLogin;
      this.lastResponded = user.lastResponded;
      this.friendsCount = user.friendsCount;
      this.followersCount = user.followersCount;
   }
	public User(twitter4j.User twUser){
		updateTwData(twUser,null,null);
	}
	
	public User(UserJSONImpl twUser){
	
		updateTwData(twUser, authToken, authTokenSecret);
	}
   public User(twitter4j.User twUser, String authToken, String authTokenSecret){
      updateTwData(twUser, authToken, authTokenSecret);
   }

	public User() {
   }

   private void updateTwData(UserJSONImpl twUser, String authToken, String authTokenSecret) {
	   

      this.screenName = twUser.screenName;
      this.picture = twUser.picUrl;
      this.fullName = twUser.name;
      this.authToken = authToken;
      this.authTokenSecret = authTokenSecret;
      this.followersCount = twUser.followersCount;
      this.friendsCount = twUser.friendsCount;
      this.twitterId = twUser.id;
   }

   public static User findByEmailOrUsername(String emailOrUserName) {
		return User.find("email = ?1 or screenName = ?1", emailOrUserName)
				.first();
	}

	public static User findByTwitterId(Long twitterId) {
		return User.find("byTwitterId", twitterId).first();
	}
	public static User findByScreenName(String screenName) {
		return User.find("byScreenName", screenName).first();
	}

	public static User findLeastUsed(){
		return User.find("authToken is not null and authTokenSecret is not null order by lastUsed asc").first();
	}

	public void updateTwData(twitter4j.User twUser, String authToken,
			String authTokenSecret) {
		this.screenName = twUser.getScreenName();
		this.picture = twUser.getProfileImageURLHttps();
		this.fullName = twUser.getName();
		this.authToken = authToken;
		this.authTokenSecret = authTokenSecret;
		this.webSite = twUser.getURL() == null ? "" : twUser.getURL();
		this.location = twUser.getLocation();
		this.description = twUser.getDescription();
		this.twitterId = twUser.getId();
		this.friendsCount = twUser.getFriendsCount();
		this.followersCount = twUser.getFollowersCount();
	}
	
	public String latestVisit(Item item){
		Visitor visit = Visitor.find("item = ? and user = ? order by date desc", item,this).first();
		return Common.dateSince(visit.date.getTime());
	}

   public int visitCount(Item item){
      Long visits = Visitor.count("item = ? and user = ?", item,this);
      return visits.intValue();
   }

}

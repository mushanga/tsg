package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.db.jpa.Model;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint( columnNames = { "ownerId" } ) })
public class UserToken extends TSGModel{

	public static String IN_USE = "In Use";
	public static String FREE = "Free";
	public static String INVALID = "Invalid";
	public static String RATE_LIMITED = "Rate Limited";
		
	
	public String ownerId;
	public String ownerScreenName;
	
	public String accessToken;
	public String accessTokenSecret;
	
	public String status;
	
	public int resetTimeInSecs = 0;
	

	public static UserToken getByAccessToken(String accessToken){
		return UserToken.find("byAccessToken", accessToken).first();
	}

	public static UserToken getByOwnerId(Long ownerId){
		return UserToken.find("byOwnerId", ownerId).first();
	}

	public boolean isInUse(){
		return this.status.equals(IN_USE);
	}
	public boolean isInvalid(){
		return this.status.equals(INVALID);
	}
	public boolean isRateLimited(){
		return this.status.equals(RATE_LIMITED);
	}
	public boolean isFree(){
		return this.status.equals(FREE);
	}
	
	public void setStatus(String status){
		this.status = status;
		this.save();
	}
	public void setInUse(){
		setStatus(IN_USE);
	}
	public void setInvalid(){
		setStatus(INVALID);
	}
	public void setRateLimited(int resetTimeInSecs){

		this.resetTimeInSecs = resetTimeInSecs;
		setStatus(RATE_LIMITED);
	}
	public void setFree(){
		this.resetTimeInSecs = 0;
		setStatus(FREE);
	}

	public static UserToken getAvailabeToken() {
		return UserToken.find("select ut from UserToken ut where status =? order by rand()", FREE).first();
	}
	public static void checkRateLimitedTokens(){
		List<UserToken> tokens = UserToken.find("select ut from UserToken ut where ut.status = ? and ut.resetTimeInSecs < ?", UserToken.RATE_LIMITED,(int) (new Date().getTime()/1000)).fetch();
		for(UserToken ut : tokens){
			ut.setFree();
		}
	}
	
	public static void setAllInvalidToValid(){
	   List<UserToken> invalidTokens = UserToken.find("byStatus", INVALID).fetch();
	   for(UserToken ut : invalidTokens){
         ut.setFree();
      }
	}
	
}

package twitter;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import models.User;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserJSONImpl {

   public String lang;
   public String url;
   public String name;

   @SerializedName("screen_name")
   public String screenName = null;
   
   public long id = -1;

	@SerializedName("followers_count")
	public int followersCount = -1;
	
	@SerializedName("friends_count")
	public int friendsCount = -1;
	
	@SerializedName("profile_image_url_https")
	public String picUrl = null;
}

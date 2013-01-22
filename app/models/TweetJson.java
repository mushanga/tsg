package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TweetJson {
	public String id;
	public String ownerId;
	public String ownerScreenName;
	public String tweetId;
	public String tweet;
	public boolean responded;
	public String respondedBy;
	public String itemId;
	public Date created;
	public int replyCount;
	public List<ReplyJson> replyList;
	public TweetJson(Tweet tweet){
		this.ownerId = tweet.owner.id.toString();
		this.id = tweet.id.toString();
		this.tweetId = String.valueOf(tweet.tweetId);
		this.tweet = tweet.tweet;
		this.responded = tweet.responded;
		this.respondedBy = tweet.respondedBy == null ? null : tweet.respondedBy.id.toString();
		this.itemId = tweet.item.id.toString();
		this.created = new Date(tweet.created.getTime());
		this.ownerScreenName = tweet.owner.screenName;
		this.replyCount = tweet.replyList.size();
		this.replyList = replyJsonList(tweet.replyList);
		
	}
	
	private static List<ReplyJson> replyJsonList(List<Reply> replyList){
		List<ReplyJson> replyJsonList = new ArrayList<ReplyJson>();
		for(Reply reply : replyList){
			replyJsonList.add(new ReplyJson(reply));
		}
		return replyJsonList;
	}
	
	public static List<TweetJson> toTweetJsonList(List<Tweet> tweetList){
		ArrayList<TweetJson> tweetJsonList = new ArrayList<TweetJson>(tweetList.size());
		for(Tweet tweet : tweetList){
			tweetJsonList.add(new TweetJson(tweet));
		}
		return tweetJsonList;
	}
	
}

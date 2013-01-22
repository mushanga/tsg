package models;

public class ReplyJson {
	public String id;
	public String tweet;
	public String sourceId;
	public String tweetId;
	public ReplyJson(Reply reply){
		this.id = String.valueOf(reply.id);
		this.tweet = reply.tweet;
		this.sourceId = String.valueOf(reply.source.id);
		this.tweetId = String.valueOf(reply.tweetId);
	}
}

package util.bitly;

public class BitlyResponseData {
  	private String global_hash;
   	private String hash;
   	private String long_url;
   	private Number new_hash;
   	private String url;

 	public String getGlobal_hash(){
		return this.global_hash;
	}
	public void setGlobal_hash(String global_hash){
		this.global_hash = global_hash;
	}
 	public String getHash(){
		return this.hash;
	}
	public void setHash(String hash){
		this.hash = hash;
	}
 	public String getLong_url(){
		return this.long_url;
	}
	public void setLong_url(String long_url){
		this.long_url = long_url;
	}
 	public Number getNew_hash(){
		return this.new_hash;
	}
	public void setNew_hash(Number new_hash){
		this.new_hash = new_hash;
	}
 	public String getUrl(){
		return this.url;
	}
	public void setUrl(String url){
		this.url = url;
	}
}

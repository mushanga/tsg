package util.bitly;

public class BitlyResponse {
  	private BitlyResponseData data;
   	private Number status_code;
   	private String status_txt;

 	public BitlyResponseData getData(){
		return this.data;
	}
	public void setData(BitlyResponseData data){
		this.data = data;
	}
 	public Number getStatus_code(){
		return this.status_code;
	}
	public void setStatus_code(Number status_code){
		this.status_code = status_code;
	}
 	public String getStatus_txt(){
		return this.status_txt;
	}
	public void setStatus_txt(String status_txt){
		this.status_txt = status_txt;
	}
}

package util;

import java.util.Date;

public class Common {
	public static long NOW = 1000*5;
	public static long MIN_MS = 1000*60;
	public static long HOUR_MS = 1000*60*60;
	public static long DAY_MS = 1000*60*60*24;
	public static long WEEK_MS = 1000*60*60*24*7;

	public static String dateSince(Long date){
		Date now = new Date();
		long diff = now.getTime() - date;
		if(diff>WEEK_MS){
			return ((int)diff/WEEK_MS)+"w ago";
		}
		if(diff>DAY_MS){
			return ((int)diff/DAY_MS)+"d ago";
		}
		if(diff>HOUR_MS){
			return ((int)diff/HOUR_MS)+"h ago";
		}
		if(diff>MIN_MS){
			return ((int)diff/MIN_MS)+"m ago";
		}
		if(diff>NOW){
			return ((int)diff/1000)+"s ago";
		}
		return "now";
	}
}

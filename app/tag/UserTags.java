package tag;

import groovy.lang.Closure;

import java.io.PrintWriter;
import java.util.Map;

import models.AdsTweetLevel;
import models.User;

import play.templates.FastTags;
import play.templates.GroovyTemplate.ExecutableTemplate;

@FastTags.Namespace("tc.user")
public class UserTags extends FastTags {
//	public static void _name(Map<?, ?> args, Closure body, PrintWriter out,
//			ExecutableTemplate template, int fromLine) {
//		User user = (User) args.get("arg");
//		if (user.fullName == null) {
//			out.println(user.screenName);
//		} else {
//			out.println(user.fullName);
//		}
//	}
//	
//	public static void _adslevel(Map<?,?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine){
//		StringBuilder sb = new StringBuilder();
//		User user = (User) args.get("arg");
//		sb.append("<select name=\"adsTweetLevel\">");
//		for(AdsTweetLevel tweetLevel : AdsTweetLevel.values()){
//			sb.append("<option value=\"").append(tweetLevel).append("\"");
//			if(tweetLevel.equals(user.adsTweetLevel)){
//				sb.append(" selected = \"selected\"");
//			}
//			sb.append(">").append(tweetLevel.description).append("</option>");
//		}
//		sb.append("</select>");
//		out.println(sb.toString());
//	}
//	
//	public static void _shortenDesc(Map<?,?> args, Closure body, PrintWriter out, ExecutableTemplate template, int fromLine){
//		String description = (String) args.get("arg");
//		if(description != null){
//			if( description.length() > 120){
//				out.println(description.substring(0,120) + "...");
//			}
//			else{
//				out.println(description);
//			}
//			
//		}
//		else{
//			out.println("");
//		}
//	}
}
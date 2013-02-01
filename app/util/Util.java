package util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


import models.User;

import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.encoders.Base64;

import exception.NoAvailableTokenException;
import exception.TSGException;
import exception.UserProtectedException;

import play.Logger;
import play.cache.Cache;
import play.db.jpa.JPA;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;

public class Util {

	public static boolean isListValid(List<?> list) {

		return isCollectionValid(list);

	}
	private static boolean isCollectionValid(Collection<?> coll) {

		return coll != null && coll.size() > 0;

	}
   public static boolean isSetValid(Set<?> set) {
      return isCollectionValid(set);

   }
   public static boolean isValid(Collection<?> col) {
      return isCollectionValid(col);

   }
	
	public static boolean isStringValid(String str) {

		return str != null && str.length() > 0;

	}
   public static String getIdListAsCommaSeparatedString(List<Long> idList){
      String idListStr = "";
      for(int i= 0;i <idList.size(); i++){
         if(i!=0){
            idListStr = idListStr +",";
         }
         idListStr = idListStr + String.valueOf(idList.get(i) );
      }
      return idListStr;
   }

//   public static synchronized long startTimer(){
//      long startTime = new Date().getTime();
//      
//      return idListStr;
//   }
//
//   public static long getDiff(){
//      long startTime = new Date().getTime();
//      
//      return idListStr;
//   }

	
	
}

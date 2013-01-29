package util;

import java.util.Comparator;
import java.util.List;

import models.User;


	public class UserOrderComparator implements Comparator<User>{
	   private List<Long> idList;
      @Override
	   public int compare(User o1, User o2) {
         
	      return idList.indexOf(o1.twitterId) - idList.indexOf(o2.twitterId);
	   }
      public UserOrderComparator(List<Long> userIdList){
	      this.idList = userIdList;
	   }
	}
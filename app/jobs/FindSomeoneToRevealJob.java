package jobs;

import exception.NoAvailableTokenException;
import exception.UserProtectedException;
import graph.GraphDatabase;
import graph.IMGraphDatabase;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.FollowingList;
import models.Item;
import models.Link;
import models.User;
import models.UserGraph;
import play.Logger;
import play.Play;
import play.cache.Cache;
import play.db.jpa.JPA;
import play.db.jpa.NoTransaction;
import play.jobs.Every;
import play.jobs.Job;
import twitter.TwitterProxy;
import twitter.TwitterProxyFactory;
import util.LinkShortener;
import util.UserLookup;
import util.Util;

@Every("10s")
public class FindSomeoneToRevealJob extends GraphJobBase {

   public int MIN_FOLLOWER = 1000000;
   
   @Override
   public void doJob() {
      try {
         if(!Play.configuration.get("application.mode").equals("dev")){
            List<UserGraph> graphs = UserGraph.getWaitingList();
            if (!Util.isValid(graphs)) {
               User someone = UserGraph.getSomeoneToReveal(MIN_FOLLOWER);
               if(someone==null){
                  Logger.info("Couldn't find someone to reveal.\nMIN_FOLLOWER: "+MIN_FOLLOWER);
                  MIN_FOLLOWER = (int) (0.75 * MIN_FOLLOWER);
                
               }else{

                  UserGraph ug = new UserGraph(someone.twitterId);
                  ug.save();  
               }
            }
         }
       
      } catch (Exception e) {
         try {
            JPA.em().getTransaction().rollback();
         } catch (Exception e1) {
            Logger.error(e1.getMessage(),e1);
         }
         Logger.error(e.getMessage(),e);
      }

   }


}

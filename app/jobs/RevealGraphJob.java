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

@Every("1s")
public class RevealGraphJob extends GraphJobBase {
   UserGraph graph;

   @Override
   public void doJob() {
      try {
         List<UserGraph> graphs = UserGraph.getWaitingList();
         if (Util.isValid(graphs)) {
            for(UserGraph graph : graphs){
               revealGraph(graph);
            }
         }
      } catch (Exception e) {
         try {

            JPA.em().getTransaction().rollback();
         } catch (Exception e1) {
         }

         graph.setStatusWaiting();
         Logger.error(e.getMessage(),e);
      }

   }
   public void revealGraph(UserGraph graph) {
      try{

         Long ownerId = graph.ownerId;

         if (graph.isProtected()) {
            return;
         }
         Set<Long> followingIdSet = new HashSet<Long>();

         try {
           
            FollowingList fl = FollowingList.getByOwnerId(ownerId);

            if(fl.isSuccessful()) {
               followingIdSet = GraphDatabase.getFollowings(ownerId);	
             
            } 
        
            if (fl.isProtected()) {
               graph.setStatusProtected();
               return;
            }
            if(Util.isSetValid(followingIdSet)&& graph.total==0 && graph.total != followingIdSet.size()){
               graph.total = followingIdSet.size();
            }

            int completed = 0;

            if(followingIdSet!=null){
               for (Long followingId : followingIdSet) {

                  FollowingList ffl = FollowingList.getByOwnerId(followingId);

                  //					if (ffl.isWaiting()) {
                  //						ffl.setStatusInProgress();
                  //						GetFollowingsJob gfj = new GetFollowingsJob(ffl.ownerId);
                  //						gfj.now();
                  //						
                  //					}
                  if(ffl.isCompleted()){
                     completed++;
                     if(completed>graph.completed){
                        graph.completed = completed;
                        graph.version++;
                     }
                  }
               }
            }
         } catch (Exception e) {

            Logger.error(e.getMessage(),e);
            graph.setStatusWaiting();
         }

         if(Util.isValid(followingIdSet)&& (graph.completed==0 || graph.completed<graph.total)){
            graph.setStatusWaiting();
         }else{
            graph.setStatusInProgress();
         }

      }finally{

         graph.save();
      }
   }

}

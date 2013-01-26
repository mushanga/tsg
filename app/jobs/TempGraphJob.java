package jobs;

import graph.GraphDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jobs.GraphJobBase.UserComparator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.FollowingList;
import models.User;
import models.UserGraph;
import play.Logger;
import play.jobs.Every;
import util.UserLookup;
import util.Util;

@Every("10s")
public class TempGraphJob extends GraphReadyJob {

   private static Set<String> doneSet = new HashSet<String>();

   @Override
   public void doJob() {
      try {
         UserGraph ug = UserGraph.getWaiting();

         if (ug!=null) {
//            for (UserGraph ug : graph) {
               if (!doneSet.contains(ug.ownerId + "-" + ug.version)) {

                  FollowingList fl = FollowingList.getByOwnerId(ug.ownerId);
                  if (fl != null && fl.isCompleted()) {
                     createGraphForUser(ug, true);
                     doneSet.add(ug.ownerId + "-" + ug.version);
                     //
                  }
               }

//            }
         }
      } catch (Exception e) {
         Logger.error(e.getMessage());
      }

   }

}

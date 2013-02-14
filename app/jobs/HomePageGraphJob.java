package jobs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import models.FollowingList;
import models.User;
import models.UserGraph;
import play.Logger;
import play.jobs.Every;
import util.Util;

@Every("1h")
public class HomePageGraphJob extends GraphReadyJob {

   public static String MAIN_GRAPH = "mainGraph";
   @Override
   public void doJob() {
      try {
         

         List<User> users = UserGraph.getCelebrityGraphs();
         
         ClientGraph cg = new ClientGraph(users, null, null, null);
        
         Gson gson = new GsonBuilder().setPrettyPrinting().create();
         String content = gson.toJson(cg, ClientGraph.class);
         
         GraphJobBase.saveGraphJson(MAIN_GRAPH, content);
         
         
      } catch (Exception e) {
         Logger.error(e,e.getMessage());
      }

   }

}

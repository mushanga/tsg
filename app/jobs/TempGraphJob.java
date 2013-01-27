package jobs;

import java.util.HashMap;
import java.util.Map;

import models.FollowingList;
import models.UserGraph;
import play.Logger;
import play.jobs.Every;

@Every("10s")
public class TempGraphJob extends GraphReadyJob {
private static Long versionDiffToCreateNew = 20L;
   private static Map<Long,Long> doneMap = new HashMap<Long,Long>();

   @Override
   public void doJob() {
      try {
         UserGraph ug = UserGraph.getWaiting();

         if (ug!=null) {
//            for (UserGraph ug : graph) {
               if (!doneMap.containsKey(ug.ownerId)){
                  doneMap.put(ug.ownerId, -versionDiffToCreateNew);
               }
               if (ug.version - doneMap.get(ug.ownerId) > versionDiffToCreateNew){
                  FollowingList fl = FollowingList.getByOwnerId(ug.ownerId);
                  if (fl != null && fl.isCompleted()) {
                     createGraphForUser(ug, true);
                     doneMap.put(ug.ownerId, ug.version);
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

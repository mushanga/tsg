package jobs;

import java.awt.ItemSelectable;

import models.Item;
import models.User;

import play.Logger;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;

@OnApplicationStart
public class DataFill extends Job {
	public void doJob(){
//		if(User.count() == 0) {
//            Fixtures.deleteDatabase();
//            Logger.info("Loading datafill");
//            Fixtures.loadModels("datafill.yml");
//		}
	}
}

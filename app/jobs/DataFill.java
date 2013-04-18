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
//            Fixtures.loadModels("data.yml");
	}
}

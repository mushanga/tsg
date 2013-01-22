package jobs;

import graph.GraphDatabase;
import play.jobs.Job;
import play.jobs.OnApplicationStop;

@OnApplicationStop
public class Stop extends Job{
	
	@Override
	public void doJob(){
		GraphDatabase.shutDown();
	}
}

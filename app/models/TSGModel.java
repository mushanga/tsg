package models;

import play.db.jpa.JPA;
import play.db.jpa.Model;

public class TSGModel extends Model{
	
	
	public void saveImmediately() {
		boolean noExistingTx=!JPA.em().getTransaction().isActive();
		if(noExistingTx){

		    JPA.em().getTransaction().begin();
		}
		this.save();
	    JPA.em().flush();
	    JPA.em().getTransaction().commit();
		if(!noExistingTx){

		    JPA.em().getTransaction().begin();
		}
	}
}

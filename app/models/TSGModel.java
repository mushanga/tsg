package models;

import javax.persistence.OptimisticLockException;

import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.JPABase;
import play.db.jpa.JPAPlugin;
import play.db.jpa.Model;

public class TSGModel extends Model{
	
//	@Override
//	public <T extends JPABase> T save() {
//		JPAPlugin.startTx(false);
//		T t = super.save();
//		JPAPlugin.closeTx(false);
//		
//		
//		return t;
//		
//	}
	
	public void saveImmediately() {
//		JPAPlugin.startTx(false);
		this.save();

//		JPAPlugin.closeTx(false);
//		boolean noExistingTx=!em().getTransaction().isActive();
//		if(noExistingTx){
//
//		    em().getTransaction().begin();
//		}else{
//		    em().getTransaction().commit();
//		    em().getTransaction().begin();
//			
//		}
//		this.save();
//	    em().getTransaction().commit();
	}
	
	public String getCacheName(){
		return this.getClass().getSimpleName();
	}
	
	
}

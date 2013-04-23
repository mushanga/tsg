package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import play.db.jpa.Model;

@Entity
public class CurrentTermin extends TSGModel{

   public int currentDate;
  
}

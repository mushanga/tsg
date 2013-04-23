package models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import play.db.jpa.Model;

@Entity
public class Report extends TSGModel{

   public String text;
   public Report(String text) {
      super();
      this.text = text;
   }
   public Date createdAt;
   @PrePersist
   void createdAt() {
      this.createdAt = new Date();
   }
}

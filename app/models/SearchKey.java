package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;


import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
public class SearchKey extends TSGModel{
	
	@Required 
	public String keyName;
	
	public Date lastSearch;
	
	@ManyToMany(cascade={CascadeType.ALL}, mappedBy="searchKeys")
	public List<Item> items = new ArrayList<Item>();
	
	public SearchKey(String name){
		this.keyName = name;
	}
	
	public static SearchKey getLeastUsed(){
		return SearchKey.find("order by lastSearch asc").first();
	}
}

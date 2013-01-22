package models;

public enum AdsTweetLevel {
	NONE(-1, "No One"), 
	FIRST(1, "My 1. Level Connections"), 
	SECOND(2, "My 2. Level Connections"), 
	THIRD(3, "My 3. Level Connections"), 
	FOURTH(4, "My 4. Level Connections"),
	EVERYONE(Integer.MAX_VALUE, "Everyone");
	
	public int level;
	public String description;
	AdsTweetLevel(int level,String description){
		this.level = level;
		this.description = description;
	}
	public int getLevel(){return level;}
	public String getDescription(){return description;}
}

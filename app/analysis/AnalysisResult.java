package analysis;

import java.util.ArrayList;
import java.util.List;

import models.SearchKey;

public class AnalysisResult {
	List<SearchKey> searchKeys = new ArrayList<SearchKey>();
	
	public void addSearchKey(SearchKey searchKey){
		this.searchKeys.add(searchKey);
	}
	
	public List<SearchKey> getSearchKeys(){
		return searchKeys;
	}
}

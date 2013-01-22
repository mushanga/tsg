function Util(){
	
	this.getListAsCommaSeparated = function (list) {
    	var str = '';
    	for(var i=0;i<list.length; i++){
    		if(i!=0){
    			str = str + ',';
    		}
    		str = str + list[i];
    	}
    	return str;
    }
	
	
}

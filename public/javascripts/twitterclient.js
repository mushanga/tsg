function TwitterClient(){
	
	

	function twitterGet(url,callback){
		
		$.ajax({

		    url : url,
		    dataType : "jsonp",
		    success : function(data)
		    {
		    	callback(data);
		    },
		    error : function()
		    {
		        //alert("Failure!");
		    },

		});

	}      
	this.showUserById = function(id,callback){
		twitterGet("http://api.twitter.com/1/users/show.json?user_id="+id,callback);	
	}       
	this.showUserByName = function(userName,callback){
		twitterGet("http://api.twitter.com/1/users/show.json?screen_name="+userName,callback);	
	}     
	function getUserById(list,id){
		for(var i =0; i<list.length; i++){
			if(list[i].id===id){
				return list[i];
			}
		}
	}
	
	this.getFriendListById = function(id,callback){
		twitterGet("https://api.twitter.com/1/friends/ids.json?user_id="+id,function(idList){
	
			while(idList.ids.length > 100){
				var idsStr = util.getListAsCommaSeparated(idList.ids.splice(0,100));
				
				twitterGet("https://api.twitter.com/1/users/lookup.json?user_id="+idsStr,function(users){
					callback(users);
				});
				
			}
			var idsStr = util.getListAsCommaSeparated(idList.ids);
			
			twitterGet("https://api.twitter.com/1/users/lookup.json?user_id="+idsStr,function(users){
				callback(users);
			});
			
			
		});	
	}   
	this.getFriendListByName = function(userName,callback){
		twitterGet("https://api.twitter.com/1/friends/ids.json?screen_name="+userName,function(idList){
			var idsStr = util.getListAsCommaSeparated(idList.ids.slice(0,99));
			twitterGet("https://api.twitter.com/1/users/lookup.json?user_id="+idsStr,callback);
		});		
	}         
	this.showUsersByIdList = function(idList,callback){
		var ids = util.getListAsCommaSeparated(idList);
		twitterGet("https://api.twitter.com/1/users/lookup.json?user_id="+ids,callback);
	}


	
	
}

function setProgressBarMessage(msg){
	$('#progressbar-message').html(msg);
	
}
function setProgressBarValue(val){
	$("#progressbar").progressbar({ value: val });

}

function resetProgressBar(){
	setProgressBarMessage('');
	setProgressBarValue(0);
}

var lastUser;
var graphVersion = -1;
function getUserGraph(){
	var url = getUserGraphLinkUrl();
	var userName = $('.navbar-search').val();
	if(lastUser!=userName){
		graph.clear();
	}
	setProgressBarMessage('Loading the graph of '+userName+'...');
	$.ajax({

		url : "userfriend/?user="+userName+"&version="+graphVersion,
		dataType : "json",
		success :  function(data)
		{
			
			
			if(data.reloadTimeInSecs && data.reloadTimeInSecs>0){

				window.setTimeout(getUserGraph,data.reloadTimeInSecs * 1000);
			}

			if(data.version <= graphVersion){
				return;
			}else{
				graphVersion = data.version;
			}
			lastUser = userName;
			
			graph.centerNodeId = data.user;
			
			var srcIdList = new Array();
			var trgIdList = new Array();
			for ( var i = 0; i < data.users.length; i++) {
				
				var newNode = data.users[i];
					
				graph.addNode(newNode);

			}

			for ( var key in data.links) {
				for ( var i in data.links[key]) {
					srcIdList.push(parseInt(key));
					trgIdList.push(data.links[key][i]);
				}

			}

			graph.addLinks(srcIdList, trgIdList);
			
			var val = parseInt(((data.total-data.left)/data.total)*100);
			var msg = val + '% of '+userName+'\'s friends are revealed. The graph will be completed soon. Please wait...';
			if(val==100){
				msg = 'Completed';
			}else if(data.protectedGraph){
				val = 0;
				msg = userName+'\'s account is protected...';
			}
			setProgressBarMessage(msg);
			setProgressBarValue(val);
			graph.update();
		
		},
		error : function(er)
		{
			expanding = false;

			setProgressBarMessage('The account of '+userName+' is protected. ');
		},

	});
}

function clearGraph(){
//	started = false;
//	paused = false;
//	document.getElementById('operateBtnId').value = getButtonValue();
	resetProgressBar();
	graph.clear();
}


var started = false;
var paused = false;
function getButtonValue(){
	if(!started) {
		return 'Start';
	}else if(paused){
		return 'Continue';
	}
	else {
		return 'Pause';
	}
}

function toggle(){
	if(!started){
		clearGraph();
		paused = false;
		started = true;
		getUserAsRoot(document.getElementById('usernameinput').value);		
		
	}else if(paused){
		getNextFriendList();
		paused = false;
	}else{
		paused = true;
	}
	document.getElementById('operateBtnId').value = getButtonValue();
	
}

var userMap = {};
function getUserAsRoot(userName){
	
	tw.showUserByName(userName, function(user) {
		 graph.addNode(user,true);
		 userMap[user.id] = user;
		 totalFriendCount = user.friends_count;
		 expandNode(user);
		 
		 tw.getFriendListById(user.id,function(users){
			 for(var i in users){
				 var u = users[i];
				 userMap[u.id] = u;
				 
			 }
		 });
  	});
}       
function startRetrieving(){
	
}
var expanding = false;

var userFriendListCursor = {};

function expandCursor(cursor){
	
	
	
	var user = cursor.user;
	
	if(!expanding){

		expanding = true;
		
		showLoading();
		$.ajax({

			url : "userfriend/?user="+user.screen_name+"&cursor="+cursor.id,
			dataType : "json",
			success :  function(data)
			{
				
				handleResponse(data,user);
			},
			error : function(er)
			{
				expanding = false;
				alert("Bisey oldu ya da kullanicinin bilgileri gizli");
			},

		});

	}

	
}
function showLoading(){
	$("#loadingDiv").html("Loading...");
}
function hideLoading(){
	$("#loadingDiv").html('');
}
function showErrorMessage(msg){
	$("#loadingDiv").html(msg);
}
function hideErrorMessage(){
	$("#loadingDiv").html();
}

function expandNode(user, doNotSetActive){
	
	
	if(!expanding){
		showLoading();
		expanding = true;
	
		if(!doNotSetActive){
			graph.addToPathNodes(user);				
		}

		if(!user.next_cursor){
			user.next_cursor = "-1";
		}
		if(user.next_cursor == "0"){
			graph.update();
			hideLoading();
			expanding = false;
		}else{	
			$.ajax({

			url : "userfriend/?user="+user.screen_name+"&cursor="+user.next_cursor,
			dataType : "json",
			success :  function(data)
			{
				
				handleResponse(data,user);
			},
			error : function(er)
			{
				expanding = false;
				hideLoading();
				showErrorMessage("Friends of "+user.screen_name+" are hidden...");
			},

		});
			
		}
	
		
	
		
			

	}
}  
var retrieved = 0;
var total = -1;

function handleResponse(data, user){

	hideLoading();
	var srcIdList = new Array();
	var trgIdList = new Array();
	for(var i=0; i<data.friends.length; i++){
		var ele = data.friends [i];

		if(userMap[ele.id]){
			data.friends.splice(i,1,userMap[ele.id]);
		}
		
		srcIdList.push(user.id);
		trgIdList.push(ele.id);

	}
	
	//data.friends'i userMap'ten bulup replace et...

	graph.addNode(data.user);
	
	
	graph.addNodes(data.friends);
	graph.addLinks(srcIdList, trgIdList);
	
	var oldCursor = graph.getCursorByUserId(user.id);
	var leftCount = user.friends_count- data.friends.length;
	if(oldCursor){
		graph.removeCursor(oldCursor.id);
		leftCount =  oldCursor.left_count - data.friends.length;
	}
	
	if(data.next_cursor > 0){
		
		graph.addCursor(user,data.next_cursor);
		var newCursor =  graph.getCursorByUserId(user.id);
		newCursor.left_count =leftCount;
		user.next_cursor = data.next_cursor;
	}else{
		user.next_cursor = "0";
	}
	
	expanding = false;

	if(graph.activeNode){
		if (user.id == graph.activeNode.id) {
			for ( var i in data.friends) {
				var friend = data.friends[i];

				var friendNode = graph.getNodeById(friend.id);

				if (friendNode.next_cursor != "0") {
					usersToExpand.splice(0, 0, friendNode);
				}
			}
			if(user.next_cursor!="0"){
				usersToExpand.splice(0, 0, user);
			}
		}
	}
	
	if(started && !paused){

		window.setTimeout(getNextFriendList,300);
	}
	refreshSlider();
	
	
	var unknownFriendsCountExists = false; 
	for(var i in usersToExpand){
		if(usersToExpand[i].friends_count < 0){
			
			var us = userMap[usersToExpand[i].id];
			if(us && us.friends_count>-1){
				graph.addNode(us);
				usersToExpand[i] = us;
			}else{

				unknownFriendsCountExists = true;
				break;
			}
		}
	}

	retrieved = retrieved + data.friends.length;
	
	if(!unknownFriendsCountExists){
		
		if(total<0){
			total = 0;
			
			for(var i in usersToExpand){
				total = total + usersToExpand[i].friends_count;
			}

		} 
		
		$(function() {
		    $("#progressbar").progressbar({ value: 100 * Math.max(0,(finishedFriendCount/totalFriendCount)) });
		});
	}
}
function refreshSlider(){

	$("#sliderId").slider("option", "max", graph.maxIncoming);
}

function userComparatorByFriendCount(user1, user2){
	return user1.friends_count-user2.friends_count;
}

var finishedFriendCount = 0;
var totalFriendCount = 0;


var lastProcessed;
function getNextFriendList(){
	usersToExpand.sort(userComparatorByFriendCount);
	
	var index = parseInt(Math.random()*5);
	index = Math.min(usersToExpand.length-1, index);
	var userToExpand = usersToExpand[index];
	
	if(userToExpand){
		usersToExpand.splice(index,1);
		if(userToExpand.next_cursor && userToExpand.next_cursor!="0"){

			expandCursor(graph.getCursorByUserId(userToExpand.id));
			usersToExpand.push(userToExpand);
		}else if(!userToExpand.next_cursor){
			expandNode(userToExpand,true);
			usersToExpand.push(userToExpand);
		}else{

			graph.update();
			finishedFriendCount++;
			window.setTimeout(getNextFriendList,300);
		}
	}else{
		$(function() {
		    $("#progressbar").progressbar({ value:100 });
		});
	}
}
var usersToExpand = new Array();
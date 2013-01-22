
$(document).ready(function() {
	$(".productEditBtn").click(
			function(e){
				$('#updateProductModal').modal('show');
				var data = $(this).attr('data');
				$.postJSON("/application/itemData",{id: data}, itemData_CallBack);
			}
	);
});

$.postJSON = function(url, data, callback) {
    return $.ajax({
        'type' : 'POST',
        'url' : url,
        'data' : data,
        'dataType' : 'json',
        'success' : callback,
        'error' : error_callback
    });
};

function showAddProductModal(){
	$('#addProductModal').modal('show');
}

function error_callback(XMLHttpRequest, textStatus, errorThrown){
	alert(errorThrown);
}

function itemData_CallBack(data){
	console.log(data);
	$("#product-description").val(data.description);
	$("#product-keywords").val(data.searchKey);
	$("#product-id").val(data.id);
}

function getCookie(c_name)
{
var i,x,y,ARRcookies=document.cookie.split(";");
for (i=0;i<ARRcookies.length;i++)
{
  x=ARRcookies[i].substr(0,ARRcookies[i].indexOf("="));
  y=ARRcookies[i].substr(ARRcookies[i].indexOf("=")+1);
  x=x.replace(/^\s+|\s+$/g,"");
  if (x==c_name)
    {
    return unescape(y);
    }
  }
}

function signout(signoutUrl) {
	deletecookie("id");
	deletecookie("oauthtoken");
	document.location = signoutUrl;
}

function deletecookie(cookieName) {
	document.cookie = cookieName + "=;expires=Thu, 01-Jan-1970 00:00:01 GMT;";
}
function getCookie(c_name) {
	if (document.cookie.length > 0) {
		c_start = document.cookie.indexOf(c_name + "=");
		if (c_start != -1) {
			c_start = c_start + c_name.length + 1;
			c_end = document.cookie.indexOf(";", c_start);
			if (c_end == -1)
				c_end = document.cookie.length;
			return unescape(document.cookie.substring(c_start, c_end));
		}
	}
	return "";
}

function Comment(comment, itemId){
	this.text = comment;
	this.itemId = itemId;
	
	this.add = function(){
		var data = {itemId:itemId,'text':comment};
		$.postJSON("/addComment", data, this.add_CallBack);
	};
	
	this.add_CallBack = function (data){
		if(data.error){
			alert(data.error);	
		}else{
			//console.log("comment : \""+data.text+"\" added to item : "+data.item);
			$("#noComment").hide();
			var html = parseTemplate($("#commentTemplate").html(), {comment: data});
			
			$(html).fadeIn("slow").appendTo(".commentContainer");
			$("#commentText").val("");
		}
		
	};
}

function Item(){
	this.getTweets = function(itemId){
		$.postJSON("/application/showProductTweets",{productId: itemId}, this.tweet_CallBack);
	}
	
	this.tweet_CallBack = function(data){
		displayTweets(data);
	}
	
	function displayTweets(tweets){
		var tweetListHtml = "";
		for(i in tweets){
			var html = parseTemplate($("#tweetTemplate").html(), {tweet: tweets[i]});
			tweetListHtml += html;
		}
		if(tweets.length > 0){
			$("#tweetList").html(tweetListHtml);
		}
		else{
			var html = parseTemplate($("#noTweetTemplate").html(), {data: ""});
			$("#tweetList").html(html);
		}
	}
}

function addComment(item){
	var text = $("#commentText").val();
	var comment = new Comment(text,item);
	comment.add();
}
function showProductTweets(productId, current){
	var item = new Item();
	item.getTweets(productId);
	$(".customerSideBar > .active").attr("class","");
	$(current).parent().attr("class","active");
	
}

function sendReply(){
	var text = $('#replyTweetTextArea').val();
	var tweetId = $("#replyTweetModal_tweetId").val();
	$.postJSON("/application/replyTweet",{tweetId: tweetId, text: text}, sendReply_CallBack);
	$("#replyTweetModal").modal("hide");
}

function sendReply_CallBack(data){
	showHideReplies(data.sourceId);
}


function openReplyTweetDialog(tweetId, screenName, userId){
	var limit = 110;
	$("#replyTweetModalHeader").html("Reply to <a href=\"/profile/"+userId+"\" target=\"_blank\">@" + screenName + "</a>" )
	$('#replyTweetModal').modal();
	var twScreenName ="@"+screenName + " ";
	$('#replyTweetTextArea').text(twScreenName);
	$('#replyTweetModal_tweetId').val(tweetId);
	$("#tweetLengthLabel").html(limit - twScreenName.length + "/110 characters left");
	$('#replyTweetTextArea').keyup(limitInput);
	$('#replyTweetTextArea').bind('paste', limitInput);
}

function limitInput(){
	var limit = 110;
	if($(this).val().length > 110){
        $(this).val($(this).val().substr(0, limit));
    }
    $("#tweetLengthLabel").html(limit - $(this).val().length + "/110 characters");
    if(limit - $(this).val().length < 11){
    	$("#tweetLengthLabel").css('color', 'red');
    }
    else{
    	$("#tweetLengthLabel").css('color', 'black');
    }
}

function showHideReplies(divId){
	if( $('#replyList_'+divId).is(':hidden') ) {		
		$('#replyList_'+divId).show();
		$("#replyStateTriange_" + divId).html("&#9660;");
		
	}
	else {
		$('#replyList_'+divId).hide();
		$("#replyStateTriange_" + divId).html("&#9654;");
	}

}


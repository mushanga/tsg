
$(function() {
	
	$('#graphSearchId').autocomplete({
	      source: "/application/autocomplete",
	      select: function( event, ui ) {
		        window.location = getGraph(ui.item.value);
		        return false;
	      },
		  search: function(event, ui) {
			  
			  $('#autocompleteSpinnerId').show();
		  },
		   response: function(event, ui) {
			   $('#autocompleteSpinnerId').hide();
		    },
		  
		  
	      minChars: 2,
		  width: 200,
		  delay: 400,
		  cacheLength: 1,
		  scroll: true,
		  scrollHeight: 100
		}).data( "autocomplete" )._renderItem = function( ul, user ) {
	      var li = $( "<li>" )
	        var a = $( "<a>").appendTo(li);
	      	
	        a.append( "<img class='ui-corner-all' width='48' height='48' src='"+ user.picture+"'></img>" )
	        a.append("<span style=\"margin-left:10px;font-family: 'Helvetica Neue',Helvetica,Arial,sans-serif;font-size: 13px;font-weight: normal;line-height: 1;\">@<b>"+user.value+"</b> ("+user.fullName+")"+"</span>");		     
	        return li.appendTo( ul );
	    };


    $("#slider").slider({ min:ginfo.recPerPage, value:ginfo.recPerPage, step: 1, stop: function(event, ui) {
    	
    	var page = ui.value / 50;
    	
    	
    	if(page>ginfo.getPage()){
    		ginfo.incrementPage();
    		getUserGraph();
    	}else{
        	graph.update(); 
    	}  	
    }});
    
	$("#slider").hide();
	
    graph = new Graph("#screen");
//    util = new Util(); 
    var slider;
  });

$(function() {
	$(function() {
	    $("#progressbar").progressbar({ value: 0 });
	    $("#progressbar").hide();
	});
})

function GraphInfo(){
	
	this.page = 1;
	this.version = -1;
	this.recPerPage = 50;
	this.activeUserId = '';
	this.needsReload = false;
	this.getPage = function(){
		return Math.max(this.page,1);
	}
	this.incrementPage = function(){
		this.page = this.page + 1;
	}
}

var ginfo = new GraphInfo();
function getUserGraph(id){
	
	if(id){
		ginfo.activeUserId = id;
	}
//	var userName = $('#graphSearchId').val();
 
		
	$("#slider").slider({ disabled: true });
	
	$.ajax({

		url : getGraphUrlForUser(ginfo.activeUserId, ginfo.getPage()),
		dataType : "json",
		success :  function(data)
		{
			
			var switchedFromTempToPermanent = ginfo.needsReload && !data.needsReload;
			ginfo.needsReload = data.needsReload;
			
			if(data.ownerId!=ginfo.activeUserId){
				ginfo = new GraphInfo();
				graph.clear();
			}
			ginfo.activeUserId = data.ownerId;
			
		
				
			ginfo.page = data.page;
			
			if(data.completed<data.total){
				graph.retainNodes(data.users);
				window.setTimeout(getUserGraph,10 * 1000 + Math.sqrt(data.total));
				$("#progressbar").progressbar({ value: (data.completed/data.total) * 100 });	
				$("#progressbar").show();	
				$("#spinnerId").show();	
				$("#slider").hide();
				if(data.version==ginfo.version){
					return;
				}else{
					ginfo.version = data.version;
				}

			}else{	
				if(switchedFromTempToPermanent){
					graph.clear();
				}
				$("#progressbar").hide();
				$("#spinnerId").hide();	
				if(data.total>ginfo.recPerPage){
					var currValue = $('#slider').slider("value");
					$("#slider").slider({max: data.total, value:Math.min(currValue, (ginfo.recPerPage*ginfo.getPage())+1)});
					$("#slider").slider({ disabled: false });
					$("#slider").show();
				}
			}
			
			
			graph.centerNodeId = data.ownerId;
			
			for ( var i = 0; i < data.users.length; i++) {
				
				var newNode = data.users[i];
				
					newNode.id = parseInt(newNode.twitterId);
					if(newNode.id == data.ownerId){

						$('#graphSearchId').val(newNode.screenName);
					}
				graph.addNode(newNode);

			}
			graph.addLinks(data.links);
			graph.cliques = data.cliques;
			var val = parseInt((data.completed/data.total)*100);
	
			graph.update();
 
		},
		error : function(er)
		{
			expanding = false;
		},

	});
}
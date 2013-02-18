var InfoWinMgr = Class.extend({
	delegate: null,
	visibleInfoWinData : null,	
	init: function(delegate){
		this.delegate = delegate;
	},
	tick : function tick() {

		var thisObj = this;
		var delegateObj = this.delegate;

		delegateObj.txt.attr("transform", function(d) {			

			return "translate(" + d.x + "," + d.y + ")";
		});
		
		
		thisObj.setInfoWinPosition();
		
	
	},
	openInfoWin : function openInfoWin(d){
		var delegateObj = this.delegate;
		var thisObj = this;

		if(thisObj.visibleInfoWinData){			
			thisObj.closeInfoWin(thisObj.visibleInfoWinData);		
		}		
		
		thisObj.visibleInfoWinData = d;
		
		thisObj.tick();
		$("#tsg-node-info-win-"+d.id).stop(true).fadeTo(500,0.8,function(){thisObj.tick()});
	
		
	},

	persistInfoWin : function persistInfoWin(d){
		var delegateObj = this.delegate;
		var thisObj = this;
		
		thisObj.visibleInfoWinData = d;
		d3.select("#tsg-node-info-win-"+d.id).selectAll(".details-text").style("display","block");
		$("#tsg-node-info-win-"+d.id).stop(true).show();
		
		thisObj.tick();
		$("#tsg-node-info-win-"+d.id).fadeTo(50,1,function(){thisObj.tick()});
	},

	closeInfoWin : function closeInfoWin(d){
		var delegateObj = this.delegate;
		var thisObj = this;
		
		thisObj.visibleInfoWinData = null;
		d3.select("#tsg-node-info-win-"+d.id).selectAll(".details-text").style("display","none");
		$("#tsg-node-info-win-"+d.id).stop(true).fadeOut(100);
	},

	margin: 10,
	setInfoWinPosition : function setInfoWinPosition(){

		var thisObj = this;	
		var delegateObj = this.delegate;
		
		if(thisObj.visibleInfoWinData){
			delegateObj.txt.selectAll(".tsg-node-info-g"+thisObj.visibleInfoWinData.id).attr("transform", function(d) {
				var height = this.getBBox().height;
				var width = this.getBBox().width;
				var tx = "";
				var ty = "";
				var objRadius = delegateObj.nodeMgr.nodeSizeMap[d.id]/2;
				if(d.x + objRadius + width >delegateObj.w){
					tx = delegateObj.w - (d.x + objRadius+width);
					
				}else{
					tx = objRadius;
				}
				
				if(d.y + objRadius + height >delegateObj.h){
					ty = delegateObj.h - (d.y + objRadius + height);
				}else{
					ty = 0;
				}	
					
				return "translate("+tx+","+ty+")";
			
			});
			delegateObj.txt.selectAll(".tsg-info-win-rec"+thisObj.visibleInfoWinData.id)
		    .attr("width", function(d) {
		
			    	return this.nextSibling.getBBox().width+2*thisObj.margin;   
		    })
		    .attr("height", function(d) {

		    	return this.nextSibling.getBBox().height+2*thisObj.margin;   
		    })
		}
		
		
	},
	lineHeight: 20,
	createInfoWin : function createInfoWin(){
		var thisObj = this;

		var delegateObj = this.delegate;
		
		delegateObj.txt = delegateObj.textGroup.selectAll(".tsg-node-info-win").data(delegateObj.activeNodes, function(d) { 
												return d.id;
											});
		delegateObj.txt.exit().remove();
		
		var txtEnter = delegateObj.txt.enter();

		var root = txtEnter.append("svg:g")
		.style("display","none")
		.attr("id",function(d){return "tsg-node-info-win-"+d.id})
		.attr("class","tsg-node-info-win")
		
		var parent = root.append("svg:g").attr("class",function(d){return "tsg-node-info-g"+d.id})
//		.attr("transform", function(d) {
//			
//	    	return "translate("+ thisObj.nodeSizeMap[d.id]/2+",0)";
//		})
//		
		 parent.append("svg:rect")
		.attr("x", 0)
		.attr("y", 0)
		.attr("rx", 20)
		.attr("ry", 20)
	    .attr("class","tsg-info-win-rec")
		.on("mouseover",function(d){   
			thisObj.persistInfoWin(d);
		})	
		.on("mouseout",function(d){
			thisObj.closeInfoWin(d);
				
		})
		
		var innerg = parent.append("g")
		.attr("transform", function(d) {
			
	    	return "translate("+thisObj.margin+","+thisObj.margin/2+")";
		})
//		
		
		var lineIndent = 0;
		var y = 0;
		
		y+=thisObj.lineHeight;
//		
		innerg.append("svg:text")		
		.attr("x", lineIndent)
		.attr("y", y)
		.attr("class", function(d) { return "shadow title text"+d.id; })
		.text(function(d) { return getDescription(d); });

		innerg.append("svg:text")
		.attr("x", lineIndent)
		.attr("y", y)
		.attr("class", function(d) { return "text title text"+d.id; })
		.text(function(d) { return getDescription(d); });

		y+=thisObj.lineHeight;
		y+=thisObj.lineHeight;
		
		innerg.append("svg:text")		
		.attr("x", lineIndent)
		.attr("y", y)
		.style("display","none")
		.attr("class", function(d) { return "shadow details-text text"+d.id; })
		.text(function(d) {  return 'Following: '+d.friendsCount; });

		innerg.append("svg:text")
		.attr("x", lineIndent)
		.attr("y", y)
		.attr("class", function(d) { return "details-text text"+d.id; })
		.style("display","none")
		.text(function(d) { return 'Following: '+d.friendsCount; });

		y+=thisObj.lineHeight;

		innerg.append("svg:text")		
		.attr("x", lineIndent)
		.attr("y", y)
		.style("display","none")
		.attr("class", function(d) { return "shadow details-text text"+d.id; })
		.text(function(d) { return 'Followers: '+d.followersCount; });
		
		innerg.append("svg:text")
		.attr("x", lineIndent)
		.attr("y", y)
		.attr("class", function(d) { return "details-text text"+d.id; })
		.style("display","none")
		.text(function(d) { return 'Followers: '+d.followersCount; });

		y+=thisObj.lineHeight;
		
		var a = innerg.append("a")
		.attr("xlink:href",function(d){
			return getGraph(d.screenName)
		})
		a.append("svg:text")		
		.attr("x", lineIndent)
		.attr("y", y)
		.style("display","none")
		.attr("class", function(d) { return "shadow details-text text"+d.id; })
		.text(function(d) {  return 'Show Graph' });
		
		a.append("text")
		.attr("x", lineIndent)
		.attr("y", y)
		.style("display","none")
		.attr("class", function(d) { return "details-text text"+d.id; })
		.on("mouseover",function(d){
			thisObj.persistInfoWin(d);
		})	
		.text(function(d) { return 'Show Graph'});
		

	
		
		
//		.on("click", function(d) { 
//			window.location = "/user?screenName="+d.screenName 
//			})
//		.style("pointer", "cursor")

		y+=thisObj.lineHeight;
		
		a = innerg.append("a")
		.attr("xlink:href",function(d){
			return "https://www.twitter.com/"+d.screenName;
		})
		.attr("target","_blank")
		
		a.append("svg:text")		
		.attr("x", lineIndent)
		.attr("y", y)
		.style("display","none")
		.attr("class", function(d) { return "shadow details-text text"+d.id; })
		.text(function(d) {  return 'Show Profile'});
		
		a.append("text")
		.style("display","none")
		.attr("x", lineIndent)
		.attr("y", y)
		.attr("class", function(d) { return "details-text text"+d.id; })
		.on("mouseover",function(d){
			thisObj.persistInfoWin(d);
		})	
		.text(function(d) { return 'Show Profile'});
//
//		y+=thisObj.lineHeight;
//		
//		innerg.append("svg:a").attr("xlink:href",function(d){"https://twitter.com/"+d.screenName}).append("svg:text")
//		.style("display","none")
//		.attr("x", lineIndent)
//		.attr("y", y)
//		.attr("class", function(d) { return "text text"+d.id; })
//		.text(function(d) { return 'Go to Twitter Profile'});

		function getDescription(d){
			return '@'+d.screenName+' ('+d.fullName+') '//+d.id;	
		}

		
		thisObj.updateInfoWin();
		    	
	    
	},	
	updateInfoWin : function updateInfoWin(){
		var delegateObj = this.delegate;
		var thisObj = this;
		
		var rects = delegateObj.textGroup.selectAll(".tsg-info-win-rec");

	    rects.attr("class",function(d){   
			return d.tsgClass + " tsg-info-win-rec tsg-info-win-rec"+d.id;
		})	
	},
	appendAnchor : function appendAnchor(parent,url, target, text){
		var delegateObj = this.delegate;
		var thisObj = this;
		
		var a = parent.append("a")
		.attr("href",url)
		.append("text")
		.attr("class","text")
		.on("mouseover",function(d){
			thisObj.persistInfoWin(d);
		})	
		.style("cursor", "pointer")
		.text(function(d) { return 'Show Graph'});
		
		if(target){

			a.attr("target",target)
		}
		return a;
	}
});

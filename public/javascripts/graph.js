function Graph(el) {
	var mLinks = [];
	var dLinks = [];
	var thisObj = this;
	var clickedImageId;
	var nodes;
	var cursors = [];
	var links;
	var nodeIncomingMap = {};
	var nodeOutgoingMap = {};
	var pathNodes = new Array();
	var newNodes = [];
	this.centerNodeId = {};
	
	this.activeNode = {};
	this.maxIncoming = 2;
	this.threshold = 2;
	// Add and remove elements on the graph object
	this.addToPathNodes = function (obj) {
	
		if(pathNodes.indexOf(obj)<0){
			pathNodes.push(obj);
		}
		this.activeNode = obj;
		for(var i in nodes){
			nodes[i].fixed = false;
		}
		this.activeNode.x = secViewBottomRight.x/2;
		this.activeNode.y = secViewBottomRight.y/2;
		this.activeNode.fixed = true;
	}	// Add and remove elements on the graph object
	this.addNode = function (obj) {
		
		var existing = this.getNodeById(obj.id);
		if(!existing){
			
			nodes.push(obj);
		}else{
			existing.friends_count = obj.friends_count;
			existing.followers_count= obj.followers_count;
		}
	
	}
	this.getCursorByUserId = function(userId){
		for(var i =0; i<cursors.length; i++){
			if(userId==cursors[i].user.id){
				return cursors[i];
			}
		}
	}
	this.addCursor = function (user,cursor) {

				
		var cursorObj = this.getCursorByUserId(user.id);
		if (!cursorObj) {

			var cursorObj = {
				"user" : user,
				"id" : cursor
			};

			cursors.push(cursorObj);	
			this.addLink(user.id,cursorObj.id);
		}
	
		
	}
	this.removeCursor = function (cursorId) {
		
		for(var i =0; i<cursors.length; i++){
			if(cursorId==cursors[i].id){
			
				cursors.splice(i,1);
				
				for(var j in links){
					if(cursorId==links[i].source.id){
						links[i].splice(j,1);
						
					}
				}
			}
		}
	}
	this.addNodes = function (objList) {
		
		for(var i = 0; i<objList.length; i++){				
			var obj = objList[i];
			this.addNode(obj);
			
		}		
	}

	this.removeNode = function (id) {
		var i = 0;
		var n = this.getNodeById(id);
	
		cursors.splice(findNodeIndex(id),1);
	}


	this.removeLink = function (sourceId,targetId) {
		var i = 0;
		var n = this.getNodeById(targetId);
		while (i < links.length) {
			if ((links[i]['source'] == n)||(links[i]['target'] == n)) links.splice(i,1);
			else i++;
		}
		
	}

	this.clear = function () {
		thisObj.centerNodeId = -1;
		nodes.length = 0;
		links.length = 0;
		mLinks.length = 0;
		dLinks.length = 0;
//		cursors.length = 0;
		nodeIncomingMap = {};
        nodeOutgoingMap = {};
//		pathNodes = new Array();
		
		this.update();
	}

	this.addLink = function (sourceId, targetId) {

		if (!this.getLinkBySrcTrgId(sourceId, targetId)) {
			var srcObj = this.getNodeById(sourceId);
			var trgObj = this.getNodeById(targetId);
			links.push({
				"source" : srcObj,
				"target" : trgObj
			});

			increaseOutgoing(srcObj);
			increaseIncoming(trgObj);
		}

	}

	
	this.addLinks = function(srcDashTrgList) {
		
		for(var i = 0; i<srcDashTrgList.length; i++){
			var srcDashTrg = srcDashTrgList[i];
			var src = srcDashTrg.split("-")[0];
			var trg = srcDashTrg.split("-")[1];
			this.addLink(parseInt(src), parseInt(trg));
		}
			
		
	}
	var click = function(id) {
		for (var i in nodes) {if (nodes[i]["id"] === id) return nodes[i]};
	}
	this.getNodeById = function getNodeById(id) {
		for (var i in nodes) {
			if (nodes[i]["id"] === id){
				return nodes[i]
			}
		}	
		for (var i in cursors) {
			if (cursors[i]["id"] === id){
				return cursors[i]
			}
		}
	}

	
	this.getLinkBySrcTrgId = function(srcId,trgId) {
		for (var i in links) {
			if (links[i].source["id"] === srcId && links[i].target["id"] === trgId){
				return links[i];
			}
		}
	}

	this.getLinksBySrcId = function(srcId) {
		var ls = [];
		for (var i in links) {
			
			if (links[i].source["id"] === srcId){
				ls.push(links[i]);
			}
		}
		return ls;
	}

	this.getLinksByTrgId = function(trgId) {
		var ls = [];
		for (var i in links) {
			
			if (links[i].target["id"] === trgId){
				ls.push(links[i]);
			}
		}
		return ls;
	}
	var findNodeIndex = function(id) {
		for (var i in nodes) {if (nodes[i]["id"] === id) return i};
		for (var i in cursors) {if (cursors[i]["id"] === id) return i};
	}

	// set up the D3 visualisation in the specified element
	w = $(el).innerWidth();
	h = $(el).innerHeight();

	firstViewHeight = 50;
	thirdViewHeight = 200;
	secViewHeight = h-firstViewHeight-thirdViewHeight;
	
	firstViewTopLeft = {"x":0,"y":0};
	firstViewTopRight = {"x":w,"y":0};
	firstViewBottomLeft = {"x":0,"y":h-secViewHeight-thirdViewHeight};
	firstViewBottomRight = {"x":w,"y":h-secViewHeight-thirdViewHeight};

	secViewTopLeft = firstViewBottomLeft;
	secViewTopRight = firstViewBottomRight;
	secViewBottomLeft = {"x":0,"y":h-thirdViewHeight};
	secViewBottomRight = {"x":w,"y":h-thirdViewHeight};

	thirdViewTopLeft = secViewBottomLeft;
	thirdViewTopRight = secViewBottomRight;
	thirdViewBottomLeft = {"x":0,"y":h};
	thirdViewBottomRight = {"x":w,"y":h};


	var force = d3.layout.force()
	.size([w, h])
	.linkDistance(function(d){
		if(mLinks.indexOf(d)>-1){
			return 150;
		}	else{
			return 350;
		}
	})
	.linkStrength(function(d){
		if(mLinks.indexOf(d)>-1){
			return 1;
		}	else{
			return 0.1;
		}
	})
	.charge(function(d){
		if(d.id == thisObj.centerNodeId){
			return -800;
		}	else{
			return -200
		}
	})
//	.friction(0.1)
	
	.gravity(0.1);

	var vis = this.vis = d3.select(el).append("svg:svg")
	.attr("width", w)
	.attr("height", h);

	vis.append("svg:rect")
//	.attr("x",firstViewTopLeft.x)
	.attr("y",firstViewTopLeft.y)
    .attr("width", w)
    .attr("height",h)
	.on("click",function(d){
		thisObj.nodeClicked(clickedImageId);
	})
    .style("stroke", "#000");
	
//	Per-type markers, as they don't inherit styles.
	vis.append("svg:defs").selectAll("marker")
	.data(["suit", "licensing", "resolved"])
	.enter().append("svg:marker")
	.attr("id", String)
	.attr("viewBox", "0 -5 10 10")
	.attr("refX", 40)
	.attr("refY", -1.5)
	.attr("markerWidth", 6)
	.attr("markerHeight", 6)
	.attr("orient", "auto")
	.append("svg:path")
	.attr("d", "M0,-5L10,0L0,5");
	
//	<rect id="rect" x="25%" y="25%" width="50%" height="50%" rx="15"/>
//    <clipPath id="clip">
//      <use xlink:href="#rect"/>
//    </clipPath>

   
	var pathGroup = vis.append("svg:g");
	var circleGroup = vis.append("svg:g")
//	.on("click",function(d){
//		thisObj.update.nodeClicked(clickedImageId);
//	})
//	;
	var crsrGroup = vis.append("svg:g");
	var textGroup = vis.append("svg:g");
	var cursorTextGroup = vis.append("svg:g");

	visibleNodes = force.nodes();
	visibleLinks = force.links();
	
	nodes = new Array();
	links= new Array();
	
	
	
	function addAll(from,to){

		
		for(var i = 0; i<from.length; i++){				
			var item = from[i];
			var found = false;
			for(var j = 0; j<to.length; j++){				
				var it = to[j];
				if(it.id==item.id){
					found = true;
				}
				
			}
			if(!found){
				to.push(item);
			}
		}
	}
	
	
	
	function add(from,to){

		
		var arr = new Array();
		arr.push(from);
		addAll(arr, to);
	}

	this.separateLinks = function separateLinks(visibleLinks){
		
		
		mLinks.length = 0;
		dLinks.length = 0;
		for(var i in visibleLinks){
			var ln = visibleLinks[i];
			
			var reverseLn = this.getLinkBySrcTrgId(ln.target.id, ln.source.id);
			if(visibleLinks.indexOf(reverseLn)>-1){
				if(mLinks.indexOf(reverseLn)<0){

					mLinks.push(ln);
				}
			}
			else{
				dLinks.push(ln);
			}
		}


	}
	
	this.getVisibleLinks = function getVisibleLinks(visibleLinks,visibleNodes){
		
		var visibleNodeIds = [];
		
		for(var i = 0; i<visibleNodes.length; i++){				
			var visibleNode = visibleNodes[i];
			visibleNodeIds.push(visibleNode.id);
		}
		
		for(var i = 0; i<links.length; i++){				
			var link = links[i];
			
//			var reverseLink = this.getLinkBySrcTrgId(link.target.id, link.source.id);
//			if(reverseLink && visibleLinks.indexOf(reverseLink)<0){
//				if(visibleLinks.indexOf(link)<0){
			if(visibleNodeIds.indexOf(link.source.id)>-1 && visibleNodeIds.indexOf(link.target.id)>-1){
				visibleLinks.push(link);
			}
//				}
//				
//			}
		}
		return visibleLinks;

	}
	
	function nodesHaveARelation(node1Id, node2Id){
		for(var i in links){
			if(links[i].source.id== node1Id && links[i].target.id == node2Id ||
					links[i].source.id== node2Id && links[i].target.id == node1Id 	){
				return true;
			}
		}
		return false;
	}
	
	this.update = function update() {
		var maxUser = $('#slider').slider("option", "value");
		
		var visibleNodeIds = new Array();
		visibleNodes.length = 0;
		visibleLinks.length = 0;

		var activeNodes = [];
		
		var unlinkedNodes = [];		

		addAll(nodes.slice(0,maxUser), activeNodes);
		//addAll(cursors, visibleNodes);
		addAll(activeNodes, visibleNodes);
		addAll(nodes, visibleNodes);
		
		this.getVisibleLinks(visibleLinks,activeNodes);
		this.separateLinks(visibleLinks);
		
		

		var imageHeight = 32;
		var imageWidth = 32;
		
		

		force.charge(function(d){
			if (d.id == thisObj.centerNodeId) {
				return 30 * -imageHeight;
			} else {
				return 20*(-(nodeIncomingMap[d.id]  + imageHeight)/2);				
			}
		})
		
		var roundedRects= circleGroup.selectAll("rect")
		.data(activeNodes, function(d) { return d.id;});
		
		var roundedRectsEnter = roundedRects.enter();
		var defs = roundedRectsEnter.append("defs");
		defs.append("svg:rect")
	    .attr("id",function(d) { return 'image-clip-'+d.id;})	 
	    .attr("x", function(d) {
	    	if (d.id == thisObj.centerNodeId) {
				return - imageWidth;
			} else {
				return -(nodeIncomingMap[d.id] + imageWidth)/2;				
			}
		})
	    .attr("y",function(d) {
	    	if (d.id == thisObj.centerNodeId) {
				return -imageHeight;
			} else {
				return -(nodeIncomingMap[d.id]  + imageHeight)/2;				
			}
		})
	    .attr("width", function(d) {
	    	if (d.id == thisObj.centerNodeId) {
				return 2 * imageWidth;
			} else {
				return nodeIncomingMap[d.id] + imageWidth;				
			}
		})
		.attr("height", function(d) {
			if (d.id == thisObj.centerNodeId) {
				return 2 * imageHeight
			} else {
				return nodeIncomingMap[d.id] + imageHeight;
			}
		})		
	    .attr("rx", function(d) {
			if (d.id == thisObj.centerNodeId) {
				return imageHeight
			} else {
				return (nodeIncomingMap[d.id]  + imageHeight)/2;
			}
		});
		
		roundedRects.exit().remove();
		

		defs.append("svg:clipPath")
	    .attr("id", function(d) { return 'image-clip-path-'+d.id;})
	    .append("svg:use")
	    .attr("xlink:href", function(d) { return '#image-clip-'+d.id;})
	    
		var images = circleGroup.selectAll("image")
		.data(activeNodes, function(d) { return d.id;});

		images.exit().remove();
		var imagesEnter = images.enter();
		
		
		imagesEnter.append("image")
		.attr("clip-path",function(d) { return 'url(#image-clip-path-'+d.id+')';})
	    .attr("xlink:href", function(d) { return d.picture;})
	    
	    .attr("x", function(d) {
	    	if (d.id == thisObj.centerNodeId) {
				return - imageWidth;
			} else {
				return -(nodeIncomingMap[d.id] + imageWidth)/2;				
			}
		})
	    .attr("y",function(d) {
	    	if (d.id == thisObj.centerNodeId) {
				return -imageHeight;
			} else {
				return -(nodeIncomingMap[d.id]  + imageHeight)/2;				
			}
		})
	    .attr("width", function(d) {
	    	if (d.id == thisObj.centerNodeId) {
				return 2 * imageWidth;
			} else {
				return nodeIncomingMap[d.id] + imageWidth;				
			}
		})
		.attr("height", function(d) {
			if (d.id == thisObj.centerNodeId) {
				return 2 * imageHeight
			} else {
				return nodeIncomingMap[d.id] + imageHeight;
			}
		})		
		
		.on("click",  function(ele) {
			
		thisObj.nodeClicked(ele.id);
		})
		.on("mouseover",function(ele){
			
			vis.selectAll(".text"+ele.id).style("display","block");
			
		})
		.on("mouseout",function(ele){
			vis.selectAll(".text"+ele.id).style("display","none");
		})
		.call(force.drag)
		.style("cursor","pointer");

		if(thisObj.centerNodeId>0){

			var rootNode = thisObj.getNodeById(thisObj.centerNodeId);
			
			rootNode.fixed = true;
			rootNode.x = w/2;
			rootNode.y = h/2;
		}
	
		var pth = pathGroup.selectAll(".link")
		.data(mLinks, function(d) { return d.source.id + "-" + d.target.id; });

		pth.exit().remove();
		pth.enter().append("svg:path")
		.attr("class", function(d) { return "link " + "suit"; })
		
		var dpth = pathGroup.selectAll(".dlink")
		.data(dLinks, function(d) { return d.source.id + "-" + d.target.id; });

		dpth.exit().remove();
		dpth.enter().append("svg:path")
		.attr("class", function(d) { return "dlink " + "directed"; })
		.attr("marker-end", function(d) { return "url(#" +  "suit" + ")"; });
		
		
		function getDescription(d){
			//return d.screen_name+" (Following: "+d.friends_count+", Followed by: "+d.followers_count+")";
			return '@'+d.screenName+' ('+d.fullName+')';
		}
		
		var txt = textGroup.selectAll("g").data(activeNodes, function(d) { 
												return getDescription(d);
											});
		txt.exit().remove();
		
		var txtEnter = txt.enter();

		var txtCont = txtEnter.append("svg:g");
		txtCont.append("svg:text")
		.style("display","none")		
		.attr("x", 8)
		.attr("y", ".31em")
		.attr("class", function(d) { return "shadow text"+d.id; })
		.text(function(d) { return getDescription(d); });

		txtCont.append("svg:text")
		.style("display","none")
		.attr("x", 8)
		.attr("y", ".31em")
		.attr("class", function(d) { return "text text"+d.id; })
		.text(function(d) { return getDescription(d); });

		force.on("tick", function() {

			pth.attr("d", function(d) {
				var dx = d.target.x - d.source.x,
				dy = d.target.y - d.source.y,
				dr = Math.sqrt(dx * dx + dy * dy);
				return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
			});

			dpth.attr("d", function(d) {
				var dx = d.target.x - d.source.x,
				dy = d.target.y - d.source.y,
				dr = Math.sqrt(dx * dx + dy * dy);
				return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
			});

			txt.attr("transform", function(d) {
				return "translate(" + d.x + "," + d.y + ")";
			});

			images.attr("transform", function(d) {
				d.x = Math.max(imageWidth/2, Math.min(w - imageWidth/2, d.x));
				d.y = Math.max(imageHeight/2, Math.min(thirdViewBottomLeft.y - imageHeight/2, d.y)); 
					
				

				return "translate(" + d.x + "," + d.y + ")";
			});

			
			
			
//			cursorTxt.attr("transform", function(d) {
//				return "translate(" + d.x + "," + d.y + ")";
//			});
		});
		
	
		// Restart the force layout.
		force.start();
	}

	// Make it all go
	this.update();

	
	this.nodeClicked = function(id){
		if(id == clickedImageId){
			circleGroup.selectAll("image").each(function(d,i){
				this.style.visibility = "visible";
				this.style.display = "block";
			
			});
			pathGroup.selectAll("path").each(function(d,i){
				this.style.visibility = "visible";
				this.style.display = "block";
			
			});
			clickedImageId = -1;
		}else{
			
			var tempVisibleArr = new Array();
		
			clickedImageId = id;
			circleGroup.selectAll("image").each(function(d,i){
				if(id == d.id || nodesHaveARelation(id, d.id) || thisObj.activeNode && d.id == thisObj.activeNode.id){
					this.style.visibility = "visible";
					this.style.display = "block";
					tempVisibleArr.push(d);
				}else{
					this.style.visibility = "hidden";
					this.style.display = "none";
				}
			});
			pathGroup.selectAll(".link").each(function(link,i){
				var srcNode = link.source;
				var trgNode = link.target;
				
				if(tempVisibleArr.indexOf(srcNode) >-1 && tempVisibleArr.indexOf(trgNode) >-1){
					this.style.visibility = "visible";
					this.style.display = "block";
				}else{
					this.style.visibility = "hidden";
					this.style.display = "none";
				}
			});
			pathGroup.selectAll(".dlink").each(function(link,i){
				var srcNode = link.source;
				var trgNode = link.target;
				
				if(tempVisibleArr.indexOf(srcNode) >-1 && tempVisibleArr.indexOf(trgNode) >-1){
					this.style.visibility = "visible";
					this.style.display = "block";
				}else{
					this.style.visibility = "hidden";
					this.style.display = "none";
				}
			});
		}
	
	}
//////////////////
	function incomingCount(node){
		if(!nodeIncomingMap[node.id]){
			nodeIncomingMap[node.id] = 0;
		}
		return nodeIncomingMap[node.id];
	}
	function increaseIncoming(node){
		if(!nodeIncomingMap[node.id]){
			nodeIncomingMap[node.id] = 0;
		}
		nodeIncomingMap[node.id] = nodeIncomingMap[node.id] +1;
	}
	function decreaseIncoming(node){
		nodeIncomingMap[node.id] = nodeIncomingMap[node.id] -1;
	}
	function outgoingCount(node){
		if(!nodeOutgoingMap[node.id]){
			nodeOutgoingMap[node.id] = 0;
		}
		return nodeOutgoingMap[node.id];
	}
	function increaseOutgoing(node){
		if(!nodeOutgoingMap[node.id]){
			nodeOutgoingMap[node.id] = 0;
		}
		nodeOutgoingMap[node.id] = nodeOutgoingMap[node.id] +1;
	}
	function decreaseOutgoing(node){
		nodeOutgoingMap[node.id] = nodeOutgoingMap[node.id] -1;
	}

	function comparatorByIncoming(a, b) {
		if (incomingCount(a) < incomingCount(b))
			return -1;
		if (incomingCount(a)  > incomingCount(b) )
			return 1;
		return 0;
	}



	function comparatorByOutgoing(a, b) {
		if (outgoingCount(a) < outgoingCount(b))
			return -1;
		if (outgoingCount(a)  > outgoingCount(b) )
			return 1;
		return 0;
	}

//	Use elliptical arc path segments to doubly-encode directionality.

}

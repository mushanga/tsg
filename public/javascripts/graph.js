
		var imageHeight = 25;
		var imageWidth = 25;
var Graph = GraphDataMgr.extend({
	uLinks : [],
	dLinks : [],
	thisObj : this,
	clickedImageId : -1,
	centerNodeId : -1,
	cliques : [],	
	nodeSizeMap : {},	
	imageHeight : 32,	
	imageWidth : 32,	
	clear : function () {
		
		this._super();
		 $("#slider").slider({value:50});
		this.centerNodeId = -1;
		this.clickedImageId = -1,
		this.uLinks.length=0;
		this.dLinks.length=0;
		this.cliques.length=0;
		this.nodeSizeMap = {};	

		this.update();
	},
	init : function (el) {
		
		
		this._super();
		 $("#slider").slider({value:50});
		var thisObj  = this;
		
		
		// set up the D3 visualisation in the specified element
		this.w = $(el).innerWidth();
		this.h = $(el).innerHeight();

		this.firstViewHeight = 50;
		this.thirdViewHeight = 200;
		this.secViewHeight = this.h-this.firstViewHeight-this.thirdViewHeight;
		
		this.firstViewTopLeft = {"x":0,"y":0};
		this.firstViewTopRight = {"x":this.w,"y":0};
		this.firstViewBottomLeft = {"x":0,"y":this.h-this.secViewHeight-this.thirdViewHeight};
		this.firstViewBottomRight = {"x":this.w,"y":this.h-this.secViewHeight-this.thirdViewHeight};

		this.secViewTopLeft = this.firstViewBottomLeft;
		this.secViewTopRight = this.firstViewBottomRight;
		this.secViewBottomLeft = {"x":0,"y":this.h-this.thirdViewHeight};
		this.secViewBottomRight = {"x":this.w,"y":this.h-this.thirdViewHeight};

		this.thirdViewTopLeft = this.secViewBottomLeft;
		this.thirdViewTopRight = this.secViewBottomRight;
		this.thirdViewBottomLeft = {"x":0,"y":this.h};
		this.thirdViewBottomRight = {"x":this.w,"y":this.h};
		
		
		this.force = d3.layout.force()
		.size([this.w, this.h])
//		.gravity(0.1)
		.linkDistance(function(d){			
			var src = d.source.id;
			var trg = d.target.id;
			if(src == thisObj.centerNodeId || trg == thisObj.centerNodeId){
				return 40 + (thisObj.nodeSizeMap[src]+thisObj.nodeSizeMap[trg])/2;
			}else{
				return 40 + (thisObj.nodeSizeMap[src]+thisObj.nodeSizeMap[trg])/2;
			}
			
		})
		.linkStrength(function(d){			
			var src = d.source.id;
			var trg = d.target.id;
			if(src == thisObj.centerNodeId || trg == thisObj.centerNodeId){
				return 0.2;
			}else{
				return 0.02;
			}
			
		})
		.charge(-300)
		.friction(0.2)

		

		this.vis = d3.select(el).append("svg:svg")
		.attr("width", this.w)
		.attr("height", this.h);

		this.vis.append("svg:rect")
//		.attr("x",firstViewTopLeft.x)
		.attr("y",this.firstViewTopLeft.y)
	    .attr("width", this.w)
	    .attr("height",this.h)
		.on("click",function(d){
			thisObj.nodeClicked(thisObj.clickedImageId);
		})
	    .style("stroke", "#000");
		
		this.vis.append("svg:defs").selectAll("marker")
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
		
		this.pathGroup = this.vis.append("svg:g");
		this.circleGroup = this.vis.append("svg:g");

		this.bgGroup = this.vis.append("svg:g");
		this.textGroup = this.vis.append("svg:g");

		this.visibleNodes = this.force.nodes();
		this.visibleLinks = this.force.links();

		this.update();
	},
	addNodeSizeMap : function addNodeSizeMap(pnodeSizeMap){

		var keys=		Object.keys(pnodeSizeMap);
		for(var key in keys ){				
			var size = pnodeSizeMap[keys[key]];
			if(!size){
				size = 0;
			}
			this.nodeSizeMap[keys[key]]= parseInt((size*imageHeight)+imageHeight);
		}
	},
	addAll : function(from,to){

		
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
	},
	separateLinks : function separateLinks(visibleLinks){
		this.uLinks.length = 0;
		this.dLinks.length = 0;
		for(var i in visibleLinks){
			var ln = visibleLinks[i];
			
			var reverseLn = this.getLinkBySrcTrgId(ln.target.id, ln.source.id);
			if(visibleLinks.indexOf(reverseLn)>-1){
				if(this.uLinks.indexOf(reverseLn)<0){

					this.uLinks.push(ln);
				}
			}
			else{
//				this.dLinks.push(ln);
			}
		}


	},
	getVisibleLinks : function getVisibleLinks(visibleLinks,visibleNodes){
		
		var visibleNodeIds = [];
		
		for(var i = 0; i<visibleNodes.length; i++){				
			var visibleNode = visibleNodes[i];
			visibleNodeIds.push(visibleNode.id);
		}
		
		for(var i = 0; i<this.links.length; i++){				
			var link = this.links[i];
			
			if(visibleNodeIds.indexOf(link.source.id)>-1 && visibleNodeIds.indexOf(link.target.id)>-1){
				if(this.getLinkBySrcTrgId(link.target.id,link.source.id)){
					visibleLinks.push(link);
				}
			}
		}
		return visibleLinks;

	},
	nodesHaveARelation : function(node1Id, node2Id){
		for(var i in this.links){
			if(this.links[i].source.id== node1Id && this.links[i].target.id == node2Id ||
					this.links[i].source.id== node2Id && this.links[i].target.id == node1Id 	){
				return true;
			}
		}
		return false;
	},
	update : function update() {
		
		var thisObj = this;
		
		var maxUser = $('#slider').slider("option", "value");
		
		var visibleNodeIds = new Array();
		this.visibleNodes.length = 0;
		this.visibleLinks.length = 0;

		this.activeNodes = [];
		

		this.addAll(this.nodes.slice(0,maxUser), this.activeNodes);
		this.addAll(this.activeNodes, this.visibleNodes);
		this.addAll(this.nodes, this.visibleNodes);
		
		this.getVisibleLinks(this.visibleLinks,this.activeNodes);
		this.separateLinks(this.visibleLinks);
		
	
		var roundedRects= this.circleGroup.selectAll("rect")
		.data(this.activeNodes, function(d) { return d.id;});
		
		var roundedRectsEnter = roundedRects.enter();
		var defs = roundedRectsEnter.append("defs");
		defs.append("svg:rect")
	    .style("fill","black")
	    .style("stroke","rgba(255,255,255,0.1)")
	    .style("stroke-width",function(d) {
			return thisObj.nodeSizeMap[d.id]/10;
		})
	    .attr("id",function(d) { return 'image-clip-'+d.id;})	 
	    .attr("x", function(d) {
				return - thisObj.nodeSizeMap[d.id]/2;
		})
	    .attr("y",function(d) {
	    	return - thisObj.nodeSizeMap[d.id]/2;
		})
	    .attr("width", function(d) {
	    	return thisObj.nodeSizeMap[d.id];
		})
		.attr("height", function(d) {
			return thisObj.nodeSizeMap[d.id];
		})		
	    .attr("rx", function(d) {
	    	return thisObj.nodeSizeMap[d.id]/2;
		});
		
		roundedRects.exit().remove();
		

		defs.append("svg:clipPath")
	    .attr("id", function(d) { return 'image-clip-path-'+d.id;})
	    .append("svg:use")
	    .attr("xlink:href", function(d) { return '#image-clip-'+d.id;})
	   
	    
		this.images = this.circleGroup.selectAll("g")
		.data(this.activeNodes, function(d) { return d.id;});

		this.images.exit().remove();
		var imagesEnterc = this.images.enter();
		

//		var node_drag = d3.behavior.drag()
//	    .on("dragstart", function(d,i){	    	
//	    	thisObj.dragStart(d,i);
//	    })
//	    .on("drag", function(d,i){	    	
//	    	thisObj.dragMove(d,i);
//	    })
//	    .on("dragend", function(d,i){	    	
//	    	thisObj.dragEnd(d,i);
//	    })

		var imagesEnterg = imagesEnterc.append("svg:g")
			.on("click",  function(d) {
			  thisObj.nodeClicked(d.id);
			})
			.on("mouseover",function(d){
				thisObj.vis.selectAll(".text"+d.id).style("display","block");
				
			})
			.on("mouseout",function(d){
				thisObj.vis.selectAll(".text"+d.id).style("display","none");
			})
			.call(this.force.drag)
			.style("cursor","pointer")
		imagesEnterg.append("use")
	    .attr("xlink:href", function(d) { return '#image-clip-'+d.id;})
	    .attr("stroke","black")
	    .attr("stroke-width","6")
	    
		imagesEnterg.append("image")
//		.style("border-color","black").style("border-width","5px")
		.attr("clip-path",function(d) { return 'url(#image-clip-path-'+d.id+')';})
		.attr("xlink:href", function(d) { return d.picture;})
		 .attr("x", function(d) {
				return - thisObj.nodeSizeMap[d.id]/2;
		})
	    .attr("y",function(d) {
	    	return - thisObj.nodeSizeMap[d.id]/2;
		})  
		.attr("width", function(d) {
			d.radius =  (thisObj.nodeSizeMap[d.id]*2)/3;
	    	return thisObj.nodeSizeMap[d.id];
		})
		.attr("height", function(d) {
			d.radius =  (thisObj.nodeSizeMap[d.id]*2)/3;
	    	return thisObj.nodeSizeMap[d.id];
		});
		
		if(this.centerNodeId>0){

			var rootNode = this.getNodeById(this.centerNodeId);
			
			rootNode.fixed = true;
			rootNode.x = this.w/2;
			rootNode.y = this.h/2;
		}
	
		this.pth = this.pathGroup.selectAll(".link")
		.data(this.uLinks, function(d) { return d.source.id + "-" + d.target.id; });

		this.pth.exit().remove();
		this.pth.enter().append("svg:path")
		.attr("class", function(d) { return "link " + "suit"; })
		
//		this.dpth = this.pathGroup.selectAll(".dlink")
//		.data(this.dLinks, function(d) { return d.source.id + "-" + d.target.id; });
//
//		this.dpth.exit().remove();
//		this.dpth.enter().append("svg:path")
//		.attr("class", function(d) { return "dlink " + "directed"; })
//		.attr("marker-end", function(d) { return "url(#" +  "suit" + ")"; });
		
		
		function getDescription(d){
			
			return '@'+d.screenName+' ('+d.fullName+') '+d.id;
		}
		
		this.txt = this.textGroup.selectAll("g").data(this.activeNodes, function(d) { 
												return getDescription(d);
											});
		this.txt.exit().remove();
		
		var txtEnter = this.txt.enter();

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

		this.force.on("tick",function(){
			thisObj.tick();
		});
		this.force.start();
		
	},
	getPositionOfLink : function(d){
		var dx = d.target.x - d.source.x,
		dy = d.target.y - d.source.y,
		dr = Math.sqrt(dx * dx + dy * dy);
		return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
	},
	tick : function tick() {
		
		var thisObj = this;
	
		var alpha = this.force.alpha();
		if(alpha<0.03){
			this.force.stop();
			return;
		}
		var rootNode = this.activeNodes[0];
		if(rootNode){	

			var node = {};
			node.x = rootNode.x;
			node.y = rootNode.y;
			node.radius =rootNode.radius + Math.sqrt(this.getMutualLinks(rootNode.id).length) * 50 ;


			var friends = [];
			var others = [];
			for (var i in this.activeNodes) {
				if(i==0){
					continue;
				}
				if(this.checkMutualLink(rootNode.id,  this.activeNodes[i].id)){
					friends.push(this.activeNodes[i]);
				}else{
					others.push(this.activeNodes[i]);
				}
			}
			friends.push(rootNode);
			var q = d3.geom.quadtree(friends);

			q.visit(collide(rootNode));

			for (var i in friends) {

				q.visit(collide(friends[i]));
			}

			q = d3.geom.quadtree(others);
			q.visit(collide(node));

			for (var i in others) {

				q.visit(collide(others[i]));
			}

		}
	
		this.pth.attr("d", this.getPositionOfLink);
//		this.dpth.attr("d", this.getPositionOfLink);

		this.txt.attr("transform", function(d) {
			return "translate(" + d.x + "," + d.y + ")";
		});

		this.images.attr("transform", function(d) {
			d.x = Math.max(d.radius, Math.min(thisObj.w - d.radius, d.x));
			d.y = Math.max(d.radius, Math.min(thisObj.thirdViewBottomLeft.y - d.radius, d.y)); 

			return "translate(" + d.x + "," + d.y + ")";
		});

	},
//	dragStart : function dragStart(d, i) 
//	{
////		this.force.friction(0);
//	},	 
//	dragMove : function dragMove(d, i) 
//	{
//		d.px += d3.event.dx;
//		d.py += d3.event.dy;
//		d.x += d3.event.dx;
//		d.y += d3.event.dy; 
//		this.tick();
//	},	 
//	dragEnd : function dragEnd(d, i) 
//	{
//		d.fixed = !d.fixed ;
////		this.update();
////		this.force.friction(0.01);
//		this.force.resume();
//	},
	nodeClicked : function(id){
		var thisObj = this;
		if(id == this.clickedImageId){
			this.circleGroup.selectAll("g").each(function(d,i){
//				this.style.visibility = "visible";
				this.style.display = "block";
			
			});
			this.pathGroup.selectAll("path").each(function(d,i){
//				this.style.visibility = "visible";
				this.style.display = "block";
			
			});
			this.clickedImageId = -1;
		}else{
			
			var tempVisibleArr = new Array();
		
			this.clickedImageId = id;
			this.circleGroup.selectAll("g").each(function(d,i){
				if(id == d.id || thisObj.nodesHaveARelation(id, d.id)){
//					this.style.visibility = "visible";
					this.style.display = "block";
					tempVisibleArr.push(d);
				}else{
//					this.style.visibility = "hidden";
					this.style.display = "none";
				}
			});
			this.pathGroup.selectAll(".link").each(function(link,i){
				var srcNode = link.source;
				var trgNode = link.target;
				
				if(tempVisibleArr.indexOf(srcNode) >-1 && tempVisibleArr.indexOf(trgNode) >-1){
//					this.style.visibility = "visible";
					this.style.display = "block";
				}else{
//					this.style.visibility = "hidden";
					this.style.display = "none";
				}
			});
//			this.pathGroup.selectAll(".dlink").each(function(link,i){
//				var srcNode = link.source;
//				var trgNode = link.target;
//				
//				if(tempVisibleArr.indexOf(srcNode) >-1 && tempVisibleArr.indexOf(trgNode) >-1){
//					this.style.visibility = "visible";
//					this.style.display = "block";
//				}else{
//					this.style.visibility = "hidden";
//					this.style.display = "none";
//				}
//			});
		}
	
	},
//	comparatorByIncoming : function(a, b) {
//		if (this.incomingCount(a) < this.incomingCount(b))
//			return -1;
//		if (this.incomingCount(a)  > this.incomingCount(b) )
//			return 1;
//		return 0;
//	},
//	comparatorByOutgoing : function(a, b) {
//		if (this.outgoingCount(a) < this.outgoingCount(b))
//			return -1;
//		if (this.outgoingCount(a)  > this.outgoingCount(b) )
//			return 1;
//		return 0;
//	}

});
	


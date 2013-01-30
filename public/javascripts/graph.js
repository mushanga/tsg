
		var imageHeight = 32;
		var imageWidth = 32;
var Graph = GraphDataMgr.extend({
	uLinks : [],
	dLinks : [],
	thisObj : this,
	clickedImageId : -1,
	centerNodeId : -1,
	cliques : [],	
	clear : function () {
		
		this._super();
		 $("#slider").slider({value:50});
		this.centerNodeId = -1;
		this.clickedImageId = -1,
		this.uLinks = [];
		this.dLinks = [];
		this.cliques = [];
		
	},
	init : function (el) {
		
		
		this._super();
		 $("#slider").slider({value:50});
		var thisObj  = this;
		this.nodes.length = 0;
		this.links.length = 0;
		this.nodeIncomingMap = {};
		
		
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
		.linkDistance(function(d){
			for(var i in thisObj.cliques){
				var clique = thisObj.cliques[i];
				if(clique.indexOf(d.source.id)>-1 &&
						clique.indexOf(d.target.id)>-1 ){
					return 200;
				}
				
			}
			var revLink = thisObj.getLinkBySrcTrgId(d.target.id,d.source.id);
			if(thisObj.uLinks.indexOf(d)>-1 || thisObj.uLinks.indexOf(revLink)>-1){
				return 200;
			}	else{
				return 300;
			}
		})
		.linkStrength(function(d){
			for(var i in thisObj.cliques){
				var clique = thisObj.cliques[i];
				if(clique.indexOf(d.source.id)>-1 &&
						clique.indexOf(d.target.id)>-1 ){
					return 0.5;
				}
				
			}
			var revLink = thisObj.getLinkBySrcTrgId(d.target.id,d.source.id);
			if(thisObj.uLinks.indexOf(d)>-1 || thisObj.uLinks.indexOf(revLink)>-1){
				return 0.5;
			}	else{
				return 1;
			}
		})
//		.gravity(0.03);
//		.charge(function(d){
//			if(d.id == thisObj.centerNodeId){
//				return -800;
//			}	else{
//				return -200
//			}
//		})
//		.friction(0.1)
		

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

		this.textGroup = this.vis.append("svg:g");

		this.visibleNodes = this.force.nodes();
		this.visibleLinks = this.force.links();

		
		
		this.update();
		
		
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
	separateLinks : function(visibleLinks){
		

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
				this.dLinks.push(ln);
			}
		}


	},
	getVisibleLinks : function(visibleLinks,visibleNodes){
		
		var visibleNodeIds = [];
		
		for(var i = 0; i<visibleNodes.length; i++){				
			var visibleNode = visibleNodes[i];
			visibleNodeIds.push(visibleNode.id);
		}
		
		for(var i = 0; i<this.links.length; i++){				
			var link = this.links[i];
			
			if(visibleNodeIds.indexOf(link.source.id)>-1 && visibleNodeIds.indexOf(link.target.id)>-1){
				visibleLinks.push(link);
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
	update : function() {
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
		
		

		
		

		this.force.charge(function(d){
			return -500;
		})
		
		var roundedRects= this.circleGroup.selectAll("rect")
		.data(this.activeNodes, function(d) { return d.id;});
		
		var roundedRectsEnter = roundedRects.enter();
		var defs = roundedRectsEnter.append("defs");
		defs.append("svg:rect")
	    .style("fill","none")
	    .style("stroke",function(d){
			for(var i in thisObj.cliques){
				var clique = thisObj.cliques[i];
				if(clique.indexOf(d.id)>-1 ){
					return "black";
					//return cliqueColor[i];
				}
				
			}
			return "black"
		})
	    .style("stroke-width","3")
	    .attr("id",function(d) { return 'image-clip-'+d.id;})	 
	    .attr("x", function(d) {
	    	if (d.id == thisObj.centerNodeId) {
				return - imageWidth;
			} else {
				return -(thisObj.nodeIncomingMap[d.id].length + imageWidth)/2;				
			}
		})
	    .attr("y",function(d) {
	    	if (d.id == thisObj.centerNodeId) {
				return -imageHeight;
			} else {
				return -(thisObj.nodeIncomingMap[d.id].length  + imageHeight)/2;				
			}
		})
	    .attr("width", function(d) {
	    	if (d.id == thisObj.centerNodeId) {
				return 2 * imageWidth;
			} else {
				return thisObj.nodeIncomingMap[d.id].length + imageWidth;				
			}
		})
		.attr("height", function(d) {
			if (d.id == thisObj.centerNodeId) {
				return 2 * imageHeight
			} else {
				return thisObj.nodeIncomingMap[d.id].length + imageHeight;
			}
		})		
	    .attr("rx", function(d) {
			if (d.id == thisObj.centerNodeId) {
				return imageHeight
			} else {
				return (thisObj.nodeIncomingMap[d.id].length  + imageHeight)/2;
			}
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
		

		var imagesEnterg = imagesEnterc.append("svg:g")
			.on("click",  function(ele) {
			  thisObj.nodeClicked(ele.id);
			})
			.on("mouseover",function(ele){
				thisObj.vis.selectAll(".text"+ele.id).style("display","block");
				
			})
			.on("mouseout",function(ele){
				thisObj.vis.selectAll(".text"+ele.id).style("display","none");
			})
			.call(this.force.drag)
			.style("cursor","pointer")
		imagesEnterg.append("use")
	    .attr("xlink:href", function(d) { return '#image-clip-'+d.id;})
	    .attr("stroke","black")
	    .attr("stroke-width","3")
	    
		imagesEnterg.append("image")
		.style("border-color","black").style("border-width","5px")
		.attr("clip-path",function(d) { return 'url(#image-clip-path-'+d.id+')';})
		.attr("xlink:href", function(d) { return d.picture;})
		.attr("x", function(d) {
			if (d.id == thisObj.centerNodeId) {
				return - imageWidth;
			} else {
				return -(thisObj.nodeIncomingMap[d.id].length + imageWidth)/2;				
			}
		})
		.attr("y",function(d) {
			if (d.id == thisObj.centerNodeId) {
				return -imageHeight;
			} else {
				return -(thisObj.nodeIncomingMap[d.id].length  + imageHeight)/2;				
			}
		})
		.attr("width", function(d) {
			if (d.id == thisObj.centerNodeId) {
				d.radius =  imageWidth;
				return 2*d.radius;
			} else {
				d.radius =   (thisObj.nodeIncomingMap[d.id].length + imageWidth)/2;
				return 2*d.radius; 			
			}
		})
		.attr("height", function(d) {
			if (d.id == thisObj.centerNodeId) {
				d.radius = imageHeight
				return  2 *d.radius 
			} else {
				d.radius = (thisObj.nodeIncomingMap[d.id].length + imageHeight)/2;
				return 2*d.radius;
			}
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
		
		this.dpth = this.pathGroup.selectAll(".dlink")
		.data(this.dLinks, function(d) { return d.source.id + "-" + d.target.id; });

		this.dpth.exit().remove();
		this.dpth.enter().append("svg:path")
		.attr("class", function(d) { return "dlink " + "directed"; })
		.attr("marker-end", function(d) { return "url(#" +  "suit" + ")"; });
		
		
		function getDescription(d){
			//return d.screen_name+" (Following: "+d.friends_count+", Followed by: "+d.followers_count+")";
			return '@'+d.screenName+' ('+d.fullName+')';
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
	tick : function() {
		var thisObj = this;
		this.pth.attr("d", this.getPositionOfLink);
		this.dpth.attr("d", this.getPositionOfLink);

		this.txt.attr("transform", function(d) {
			return "translate(" + d.x + "," + d.y + ")";
		});


	
		
		var rootNode = this.activeNodes[0];
		if(rootNode){	

			var node = {};
			node.x = rootNode.x;
			node.y = rootNode.y;
			node.radius = Math.sqrt(this.getMutualLinks(rootNode.id).length) * 50 ;


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
	

		this.images.attr("transform", function(d) {
			d.x = Math.max(d.radius, Math.min(thisObj.w - d.radius, d.x));
			d.y = Math.max(d.radius, Math.min(thisObj.thirdViewBottomLeft.y - d.radius, d.y)); 

			return "translate(" + d.x + "," + d.y + ")";
		});

	},
	nodeClicked : function(id){
		var thisObj = this;
		if(id == this.clickedImageId){
			this.circleGroup.selectAll("g").each(function(d,i){
				this.style.visibility = "visible";
				this.style.display = "block";
			
			});
			this.pathGroup.selectAll("path").each(function(d,i){
				this.style.visibility = "visible";
				this.style.display = "block";
			
			});
			this.clickedImageId = -1;
		}else{
			
			var tempVisibleArr = new Array();
		
			this.clickedImageId = id;
			this.circleGroup.selectAll("g").each(function(d,i){
				if(id == d.id || thisObj.nodesHaveARelation(id, d.id)){
					this.style.visibility = "visible";
					this.style.display = "block";
					tempVisibleArr.push(d);
				}else{
					this.style.visibility = "hidden";
					this.style.display = "none";
				}
			});
			this.pathGroup.selectAll(".link").each(function(link,i){
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
			this.pathGroup.selectAll(".dlink").each(function(link,i){
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
	
	},
	comparatorByIncoming : function(a, b) {
		if (this.incomingCount(a) < this.incomingCount(b))
			return -1;
		if (this.incomingCount(a)  > this.incomingCount(b) )
			return 1;
		return 0;
	},
	comparatorByOutgoing : function(a, b) {
		if (this.outgoingCount(a) < this.outgoingCount(b))
			return -1;
		if (this.outgoingCount(a)  > this.outgoingCount(b) )
			return 1;
		return 0;
	}

});
	


var NodeMgr = Class.extend({
	delegate: null,
	clickedImageId : -1,
	init: function(delegate){
		this.delegate = delegate;
	},
	clear : function () {
		var thisObj = this;

		thisObj.clickedImageId = -1;

	},

	tick : function tick() {

		var thisObj = this;
		var delegateObj = this.delegate;
		
		thisObj.detectCollisions();

		delegateObj.images.attr("transform", function(d) {
			
			d.x = Math.max(d.radius, Math.min(delegateObj.w - d.radius, d.x));
			d.y = Math.max(d.radius, Math.min(delegateObj.thirdViewBottomLeft.y - d.radius, d.y)); 

			return "translate(" + d.x + "," + d.y + ")";
		});
	
	},
	createNodes : function createNodes(){

		var delegateObj = this.delegate;
		var thisObj = this;

		var roundedRects= delegateObj.circleGroup.selectAll("rect")
		.data(delegateObj.activeNodes, function(d) { return d.id;});

		var roundedRectsEnter = roundedRects.enter();
		var defs = roundedRectsEnter.append("defs");
		defs.append("svg:rect")
		.attr("class",function(d) {

			if(delegateObj.centerNodeId == d.id){
				d.tsgClass ="tsg-root-nc"; 

			}else if(delegateObj.checkMutualLink(delegateObj.centerNodeId, d.id)){

				d.tsgClass = "tsg-root-friend-nc";
			}else{
				d.tsgClass = "tsg-nc";
			}
			return d.tsgClass;
		})
		.style("stroke-width",function(d) {
			return delegateObj.nodeSizeMap[d.id]/5;
		})
		.attr("id",function(d) { return 'nc-'+d.id;})	 
		.attr("x", function(d) {
			return - delegateObj.nodeSizeMap[d.id]/2;
		})
		.attr("y",function(d) {
			return - delegateObj.nodeSizeMap[d.id]/2;
		})
		.attr("width", function(d) {
			return delegateObj.nodeSizeMap[d.id];
		})
		.attr("height", function(d) {
			return delegateObj.nodeSizeMap[d.id];
		})		
		.attr("rx", function(d) {
			return delegateObj.nodeSizeMap[d.id]/2;
		});

		roundedRects.exit().remove();


		defs.append("svg:clipPath")
		.attr("id", function(d) { return 'nc-path-'+d.id;})
		.append("svg:use")
		.attr("xlink:href", function(d) { return '#nc-'+d.id;})


		delegateObj.images = delegateObj.circleGroup.selectAll("g")
		.data(delegateObj.activeNodes, function(d) { return d.id;});

		delegateObj.images.exit().remove();
		var imagesEnterc = delegateObj.images.enter();

		var imagesEnterg = imagesEnterc.append("svg:g")
		.call(delegateObj.force.drag)
		.style("cursor","pointer")
		.on("click",  function(d) {
			thisObj.nodeClicked(d.id);
		})
		.on("mouseover",function(d){
			delegateObj.infoWinMgr.openInfoWin(d);	
		})	
		.on("mouseout",function(d){
			delegateObj.infoWinMgr.closeInfoWin(d);				
		})	
		imagesEnterg.append("use")
		.attr("xlink:href", function(d) { return '#nc-'+d.id;})
//		.attr("stroke","black")
//		.attr("stroke-width","6")

		imagesEnterg.append("image")
//		.style("border-color","black").style("border-width","5px")
		.attr("clip-path",function(d) { return 'url(#nc-path-'+d.id+')';})
		.attr("xlink:href", function(d) { return d.picture;})
		.attr("x", function(d) {
			return - delegateObj.nodeSizeMap[d.id]/2;
		})
		.attr("y",function(d) {
			return - delegateObj.nodeSizeMap[d.id]/2;
		})  
		.attr("width", function(d) {
			d.radius = (delegateObj.nodeSizeMap[d.id]/2) * 1.5;
			return delegateObj.nodeSizeMap[d.id];
		})
		.attr("height", function(d) {
			d.radius =  (delegateObj.nodeSizeMap[d.id]/2) * 1.5;
			return delegateObj.nodeSizeMap[d.id];
		});

		if(delegateObj.centerNodeId>0){

			var rootNode = delegateObj.getNodeById(delegateObj.centerNodeId);

			rootNode.fixed = true;
			rootNode.x = delegateObj.w/2;
			rootNode.y = delegateObj.h/2;
		}

	},	
	deselectNode : function deselectNode(){
		var thisObj = this;
		var delegateObj = this.delegate;
		
		thisObj.nodeClicked(thisObj.clickedImageId);		
	},
	nodeClicked : function nodeClicked(id){

		var thisObj = this;
		var delegateObj = this.delegate;
		
		if(id==-1){
			return;
		}
		

		delegateObj.circleGroup.selectAll("rect").each(function(d,i){
			removeClass(this, "tsg-selected-nc");
		
		});
		
		if(id == thisObj.clickedImageId){
			delegateObj.circleGroup.selectAll("g").each(function(d,i){
				this.style.display = "block";
			
			});
			delegateObj.pathGroup.selectAll("path").each(function(d,i){
				this.style.display = "block";
			
			});
			thisObj.clickedImageId = -1;
		}else{

			delegateObj.circleGroup.selectAll("rect").each(function(d,i){
				if(id == d.id){

					addClass(this, "tsg-selected-nc");
				}
			
			});
			var tempVisibleArr = new Array();
		
			thisObj.clickedImageId = id;
			delegateObj.circleGroup.selectAll("g").each(function(d,i){
				if(id == d.id || delegateObj.nodesHaveARelation(id, d.id)){
					this.style.display = "block";
					tempVisibleArr.push(d);
				}else{
//					this.style.visibility = "hidden";
					this.style.display = "none";
				}
			});
			
			delegateObj.pathGroup.selectAll(".link").each(function(link,i){
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
	detectCollisions : function detectCollisions(){
		var thisObj = this;
		var delegateObj = this.delegate;
//		var q = d3.geom.quadtree(this.activeNodes);
//
//
//		for (var i in this.activeNodes) {
//
//			q.visit(collide(this.activeNodes[i]));
//		}

		var rootNode = delegateObj.getNodeById(delegateObj.centerNodeId);
		if(rootNode){	

			var node = {};
			node.x = rootNode.x;
			node.y = rootNode.y;
			var srcSize = delegateObj.linkSizeMap[rootNode.id];
			node.radius =rootNode.radius + 100+ Math.sqrt(srcSize) * 20 ;


			var friends = [];
			var others = [];
			for (var i in delegateObj.activeNodes) {
				if(rootNode==delegateObj.activeNodes[i]){
					continue;
				}
				if(delegateObj.checkMutualLink(rootNode.id,  delegateObj.activeNodes[i].id)){
					friends.push(delegateObj.activeNodes[i]);
				}else{
					others.push(delegateObj.activeNodes[i]);
				}
			}
			
			friends.push(rootNode);
			var q = d3.geom.quadtree(delegateObj.activeNodes);

			q.visit(collide(rootNode));

			for (var i in friends) {

				q.visit(collide(friends[i]));
			}
			for (var i in others) {

				q.visit(collide(others[i]));
			}
			
			others.push(node);
			var q2 = d3.geom.quadtree(others);	
			q2.visit(collide(node));
			for (var i in others) {

				q2.visit(collide(others[i]));
			}
			

			

		}
	},
});

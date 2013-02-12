
var Graph = GraphDataMgr.extend({
	centerNodeId : -1,
	cliques : [],	
	nodeSizeMap : {},	
	linkSizeMap : {},	
	imageMin : 32,		
	imageMax : 60,	
	userPerPage : 50,	
	nodeMgr : null,
	infoWinMgr : null,
	linkMgr : null,
	clear : function () {
		
		this._super();
		
		 $("#slider").slider({value:this.userPerPage});
		
		this.linkMgr.clear();
		 
		this.centerNodeId = -1;
		this.cliques.length=0;
		this.nodeSizeMap = {};	
		this.linkSizeMap = {};
		
		this.update();
	},
	init : function (el) {
		
		this._super();
		

		this.nodeMgr = new NodeMgr(this);
		this.linkMgr = new LinkMgr(this);
		this.infoWinMgr = new InfoWinMgr(this);
		
		
		 $("#slider").slider({value:this.userPerPage});
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
		.gravity(0.1)
		.charge(-300)
//		.linkDistance(function(d){			
//			var src = d.source.id;
//			var trg = d.target.id;
////			var srcSize = thisObj.linkSizeMap[src];
////			var trgSize = thisObj.linkSizeMap[trg];
//			var srcSize = thisObj.getMutualLinks(src).length;
//			var trgSize = thisObj.getMutualLinks(trg).length;
//			var max = (srcSize>trgSize)?srcSize:trgSize;
//			return Math.sqrt(max)*30;
//			
//		})
		.linkStrength(function(d){			
			var src = d.source.id;
			var trg = d.target.id;
			var srcSize = thisObj.linkSizeMap[src];
			var trgSize = thisObj.linkSizeMap[trg];
			var max = (srcSize>trgSize)?srcSize:trgSize;
			if(!max){
				max = 1;
			}
			
			return 0.5/max;
		})
		

		this.svg = d3.select(el).append("svg:svg")
		.attr("width", this.w)
		.attr("height", this.h);

		this.svg.append("svg:rect")
//		.attr("x",firstViewTopLeft.x)
		.attr("y",this.firstViewTopLeft.y)
	    .attr("width", this.w)
	    .attr("height",this.h)
		.on("click",function(d){
			thisObj.nodeMgr.deselectNode();
		})
	    .style("stroke", "#000");
		
		this.svg.append("svg:defs").selectAll("marker")
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
		
		this.pathGroup = this.svg.append("svg:g");
		this.circleGroup = this.svg.append("svg:g");

		this.bgGroup = this.svg.append("svg:g");
		this.textGroup = this.svg.append("svg:g");

		this.visibleNodes = this.force.nodes();
		this.visibleLinks = this.force.links();

		this.update();
	},
	addNodeSizeMap : function addNodeSizeMap(pnodeSizeMap){

		var keys=		Object.keys(pnodeSizeMap);
		
		var delta = this.imageMax - this.imageMin;
		for(var key in keys ){				
			var size = pnodeSizeMap[keys[key]];
			if(!size){
				size = 0;
			}
			this.nodeSizeMap[keys[key]]= parseInt((size*delta)+this.imageMin);
		}
	},
	addLinkSizeMap : function addLinkSizeMap(plinkSizeMap){

		var keys=		Object.keys(plinkSizeMap);

		for(var key in keys ){				
			var size = plinkSizeMap[keys[key]];
			if(!size){
				size = 1;
			}
			this.linkSizeMap[keys[key]]= plinkSizeMap[keys[key]];
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
	
	nodesHaveARelation : function nodesHaveARelation(node1Id, node2Id){
		
		return this.checkMutualLink(node1Id, node2Id);
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
		
		this.linkMgr.getVisibleLinks(this.visibleLinks,this.activeNodes);
		this.linkMgr.separateLinks(this.visibleLinks);
		
		this.linkMgr.createLinks();
		this.nodeMgr.createNodes();
		this.infoWinMgr.createInfoWin();
		
		this.force.on("tick",function(){
			thisObj.tick();
		});
		this.force.start();
		
	},
	
	
	
	
	tick : function tick() {

		var thisObj = this;

		thisObj.nodeMgr.tick();
		thisObj.linkMgr.tick();
		thisObj.infoWinMgr.tick();

	
	}
});
	

var LinkMgr = Class.extend({
	delegate: null,
	uLinks : [],
	dLinks : [],
	linkSizeMap : {},	
	init: function(delegate){
		this.delegate = delegate;
	},
	clear : function () {
		var thisObj = this;
		
		thisObj.uLinks.length=0;
		thisObj.dLinks.length=0;
		this.linkSizeMap = {};

		
	},
	tick : function tick() {
		var delegateObj = this.delegate;
		var thisObj = this;

		delegateObj.pth.attr("d", thisObj.getPositionOfLink);
		
	
	},
	addLinkSizeMap : function addLinkSizeMap(plinkSizeMap){
		var delegateObj = this.delegate;
		var thisObj = this;

		var keys=		Object.keys(plinkSizeMap);

		for(var key in keys ){				
			var size = plinkSizeMap[keys[key]];
			if(!size){
				size = 1;
			}
			thisObj.linkSizeMap[keys[key]]= plinkSizeMap[keys[key]];
		}
	},
	separateLinks : function separateLinks(visibleLinks){
		
		var delegateObj = this.delegate;
		var thisObj = this;
		
		thisObj.uLinks.length = 0;
		thisObj.dLinks.length = 0;
		for(var i in visibleLinks){
			var ln = visibleLinks[i];
			
			var reverseLn = delegateObj.getLinkBySrcTrgId(ln.target.id, ln.source.id);
			if(visibleLinks.indexOf(reverseLn)>-1){
				if(thisObj.uLinks.indexOf(reverseLn)<0){

					thisObj.uLinks.push(ln);
				}
			}
			else{
//				this.dLinks.push(ln);
			}
		}


	},
	getVisibleLinks : function getVisibleLinks(visibleLinks,visibleNodes){

		var delegateObj = this.delegate;
		var thisObj = this;
		
		var visibleNodeIds = [];
		
		for(var i = 0; i<visibleNodes.length; i++){				
			var visibleNode = visibleNodes[i];
			visibleNodeIds.push(visibleNode.id);
		}
		
		for(var i = 0; i<delegateObj.links.length; i++){				
			var link = delegateObj.links[i];

			if(visibleNodeIds.indexOf(link.source.id)>-1 && visibleNodeIds.indexOf(link.target.id)>-1){
				if(delegateObj.getLinkBySrcTrgId(link.target.id,link.source.id)){

					visibleLinks.push(link);

				}
			}
		}
		return visibleLinks;

	},	
	createLinks : function createLinks(){
		var delegateObj = this.delegate;
		var thisObj = this;
		
		

		delegateObj.pth = delegateObj.pathGroup.selectAll(".link")
		.data(thisObj.uLinks, function(d) { return d.source.id + "-" + d.target.id; });

		delegateObj.pth.exit().remove();
		delegateObj.pth.enter().append("svg:path")
		.attr("class", function(d) { return "link " + "suit"; })
		
//		this.dpth = this.pathGroup.selectAll(".dlink")
//		.data(this.dLinks, function(d) { return d.source.id + "-" + d.target.id; });
//
//		this.dpth.exit().remove();
//		this.dpth.enter().append("svg:path")
//		.attr("class", function(d) { return "dlink " + "directed"; })
//		.attr("marker-end", function(d) { return "url(#" +  "suit" + ")"; });
		
		
	},
	getStrengthOfLink	: function getStrengthOfLink(d){	
		var delegateObj = this.delegate;
		var thisObj = this;
		
		var src = d.source.id;
		var trg = d.target.id;
		var srcSize = thisObj.linkSizeMap[src];
		var trgSize = thisObj.linkSizeMap[trg];
		var max = (srcSize>trgSize)?srcSize:trgSize;
		if(!max){
			max = 1;
		}
		
		return 0.5/max;
	},
	getLengthOfLink	: function getLengthOfLink(d){	
		var delegateObj = this.delegate;
		var thisObj = this;
		return 1 / thisObj.getStrengthOfLink(d);
	},
	getPositionOfLink : function getPositionOfLink(d){

		var delegateObj = this.delegate;
		var thisObj = this;
		
		var dx = d.target.x - d.source.x,
		dy = d.target.y - d.source.y,
		dr = Math.sqrt(dx * dx + dy * dy);
		if(!d.target || !d.source){
			console.log ( 'node not found for link: '+d );
		}
//		console.log ( 'link: '+d.source.screenName+"-"+ d.target.screenName);
		return "M" + d.source.x + "," + d.source.y + "A" + dr + "," + dr + " 0 0,1 " + d.target.x + "," + d.target.y;
	},
});

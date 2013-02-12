var LinkMgr = Class.extend({
	delegate: null,
	uLinks : [],
	dLinks : [],
	init: function(delegate){
		this.delegate = delegate;
	},
	clear : function () {
		var thisObj = this;
		
		thisObj.uLinks.length=0;
		thisObj.dLinks.length=0;

		
	},
	tick : function tick() {
		var delegateObj = this.delegate;
		var thisObj = this;

		delegateObj.pth.attr("d", thisObj.getPositionOfLink);
		
	
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
//					if(link.source.id!=delegateObj.centerNodeId && link.target.id!=delegateObj.centerNodeId ){
						visibleLinks.push(link);
						
//					}
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

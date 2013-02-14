var GraphDataMgr = Class.extend({

	init: function(){

		this.nodes = new Array();
		this.links= new Array();		
		this.nodeIncomingMap = {};
		this.nodeOutgoingMap = {};
	},
	clear : function () {
		this.nodes.length = 0;
		this.links.length = 0;
		this.nodeIncomingMap = {};
		this.nodeOutgoingMap = {};

	},
	addNode : function addNode(obj) {
	
		var existing = this.getNodeById(obj.id);
		if(!existing){
			
			this.nodes.push(obj);
			this.nodeIncomingMap[obj.id] = [];
			this.nodeOutgoingMap[obj.id] = [];
		}else{
			existing.friends_count = obj.friends_count;
			existing.followers_count= obj.followers_count;
		}
	},	
	addNodes : function addNodes(nodes) {

		for(var i in nodes){
			this.addNode(nodes[i]);
		}
	},	
	retainNodes : function retainNodes(list) {
		var missingList = [];
		for(var i in this.nodes){
			var missing = true;
			for(var j in list){
				if(this.nodes[i].id==list[j].twitterId){
					missing = false;
				}
			}
			if(missing){
				missingList.push(this.nodes[i].id);
			}

		}
		for(var i in missingList){
			this.removeNode(missingList[i]);

		}


	},
	removeNode : function removeNode(id) {

		var n = this.getNodeById(id);

		var linkArr = this.getLinksBySrcOrTrg(id);
		for(var i in linkArr){
			this.removeLink(linkArr[i].source.id, linkArr[i].target.id);
		}
		this.nodes.splice(this.findNodeIndex(id),1);
	},
	removeLink : function removeLink(sourceId,targetId) {
		var i = 0;
		var trg = this.getNodeById(targetId);
		var src = this.getNodeById(sourceId);
		while (i < this.links.length) {
			if ((this.links[i]['source'] == src) && (this.links[i]['target'] == trg)){
				this.links.splice(i,1);

				var trgIncomingArr = this.nodeIncomingMap[trg.id];
				var iSrc = trgIncomingArr.indexOf(src.id);
				trgIncomingArr.splice(iSrc,1);

				var srcOutgoingArr = this.nodeOutgoingMap[src.id];
				iSrc = srcOutgoingArr.indexOf(trg.id);
				srcOutgoingArr.splice(iSrc,1);
				break;
			}
			else{
				i++;
			}
		}

	},
	addLink : function addLink(sourceId, targetId) {

		if (!this.getLinkBySrcTrgId(sourceId, targetId)) {
			var srcObj = this.getNodeById(sourceId);
			var trgObj = this.getNodeById(targetId);
			this.links.push({
				"source" : srcObj,
				"target" : trgObj
			});
			if(!srcObj || !trgObj){
				console.log ( 'node not found for link\n src: '+sourceId+'/'+srcObj+'\n trg: '+targetId+'/'+trgObj );
			}
			this.nodeOutgoingMap[srcObj.id].push(trgObj.id);
			this.nodeIncomingMap[trgObj.id].push(srcObj.id);
		}

	},
	addLinks : function addLinks(srcDashTrgList) {

		for(var i in srcDashTrgList){
			var srcDashTrg = srcDashTrgList[i];
			var src = srcDashTrg.split("-")[0];
			var trg = srcDashTrg.split("-")[1];
			this.addLink(parseInt(src), parseInt(trg));
		}
	},
	getNodeById : function getNodeById(id) {
		for (var i in this.nodes) {
			if (this.nodes[i]["id"] === id){
				return this.nodes[i]
			}
		}	
	},
	getLinkBySrcTrgId : function getLinkBySrcTrgId(srcId,trgId) {
		for (var i in this.links) {
			if (this.links[i].source["id"] === srcId && this.links[i].target["id"] === trgId){
				return this.links[i];
			}
		}
	},
	getLinksBySrcId : function getLinksBySrcId(srcId) {
		var ls = [];
		for (var i in this.links) {

			if (this.links[i].source["id"] === srcId){
				ls.push(this.links[i]);
			}
		}
		return ls;
	},
	getLinksByTrgId : function getLinksByTrgId(trgId) {
		var ls = [];
		for (var i in this.links) {

			if (this.links[i].target["id"] === trgId){
				ls.push(this.links[i]);
			}
		}
		return ls;
	},
	getLinksBySrcOrTrg : function getLinksBySrcOrTrg(id) {
		var ls = [];
		for (var i in this.links) {

			if (this.links[i].target["id"] === id || this.links[i].source["id"] === id ){
				ls.push(this.links[i]);
			}
		}
		return ls;
	},
	checkMutualLink : function checkMutualLink(id1, id2) {

		return this.nodeIncomingMap[id1].indexOf(id2)>-1 && this.nodeOutgoingMap[id1].indexOf(id2)>-1;
	},
	getMutualLinks : function getMutualLinks(id1) {

		return intersect(this.nodeIncomingMap[id1], this.nodeOutgoingMap[id1]);
	},
	intersectMutualLinksOfNodes : function intersectMutualLinksOfNodes(id1,id2) {
		var mls1 = this.getMutualLinks(id1);
		var mls2 = this.getMutualLinks(id2);
		return intersect(mls1, mls2);
	},
	findNodeIndex : function findNodeIndex(id) {
		for (var i in this.nodes) {if (this.nodes[i]["id"] === id) return i};
	},
	nodesHaveARelation : function nodesHaveARelation(node1Id, node2Id){

		return this.checkMutualLink(node1Id, node2Id);
	},
	addAll : function addAll(from,to){


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

});

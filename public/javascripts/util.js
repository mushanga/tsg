function Util(){
	
	this.getListAsCommaSeparated = function (list) {
    	var str = '';
    	for(var i=0;i<list.length; i++){
    		if(i!=0){
    			str = str + ',';
    		}
    		str = str + list[i];
    	}
    	return str;
    }
	
	
}
function collide(node) {
	  var r = node.radius + 16,
	      nx1 = node.x - r,
	      nx2 = node.x + r,
	      ny1 = node.y - r,
	      ny2 = node.y + r;
	  return function(quad, x1, y1, x2, y2) {
	    if (quad.point && (quad.point !== node)) {
	      var x = node.x - quad.point.x,
	          y = node.y - quad.point.y,
	          l = Math.sqrt(x * x + y * y),
	          r = node.radius + quad.point.radius;
	      if (l < r) {
	        l = (l - r) / l * .5;
	        node.x -= x *= l;
	        node.y -= y *= l;
	        quad.point.x += x;
	        quad.point.y += y;
	      }
	    }
	    return x1 > nx2
	        || x2 < nx1
	        || y1 > ny2
	        || y2 < ny1;
	  };
	}

function intersect(a, b)
{
  var ai=0, bi=0;
  var result = new Array();

  var copya = a.slice(0);
  var copyb = b.slice(0);

  copya.sort();
  copyb.sort();
  
  while( ai < copya.length && bi < copyb.length )
  {
     if      (copya[ai] < copyb[bi] ){ ai++; }
     else if (copya[ai] > copyb[bi] ){ bi++; }
     else /* they're equal */
     {
       result.push(copya[ai]);
       ai++;
       bi++;
     }
  }

  return result;
}

function remove(arr, obj)
{
	var i = arr.indexOf(obj)
	if(i>-1){
		arr.splice(i,1);
	}
}


function contains(arr, obj)
{
	return arr.indexOf(obj)>-1;
}

function getObjectOutOfArea(object, area){
	
	
	
}
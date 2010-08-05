var frontPoint, backPoint, currentPoint;

var data = [null,null];




/**
 * Create a new LinkedList of DivId Elements
 */
function createList(){
	//The DivId element
	function LinkedListNode(prev,numId){
		this.prev = prev;
		this.next = null;
		this.numId = numId;
		this.data = null;
	}
	//make the front node/element
	currentPoint = new LinkedListNode(null,0);
	var prev = currentPoint, cur;
	for (var i = 1; i < 16; i++){	//make a list of nodes, each node.prev = the last node
		cur = new LinkedListNode(prev,i);
		prev.next = cur;	//and the last node's next node is the current node
		prev = cur;			//and do it again with the current node
	}
	prev.next = currentPoint;		//make sure the last points to the front
	currentPoint.prev = prev;		//and that the front's prev points to end
	frontPoint = backPoint = currentPoint;	//and they're all equal right about now
	
}
/**
 * Object with methods called by java
 */
var readee = {
	onEvent : function(type,moreInfo){
		
		switch(type){
			//perform initialization
			case "init":
				info.scriptWaiting = true;
				info.userWaiting = true;
				info.init = true;
				loadDiv('loading');
				window.reader.requestMoreItems("");
				methods.startTimer();
				
				break;	
			//the feed has been changed	
			case "itemSwitched":
				if (moreInfo)	methods.nextItem();
					else		methods.prevItem();
								
				break;
			//the feed has been scrolled
			case "itemScrolled":
				if (moreInfo)	methods.scrollUp();
					else		methods.scrollDown();
				break;
			//the new feed has been downloaded
			case "feedDownloaded":
					methods.nextFeedDownloaded();
				break;
			
			case "itemRead":
				if (currentPoint.item)
					currentPoint.item.itemRead = moreInfo;
					log('read' + moreInfo);
				break;
			case "itemSave":
				if (currentPoint.item)
					currentPoint.item.itemSave = moreInfo;
					log('save' + moreInfo);
				break;
			default:
				return false;
		}
		
		return true;		
	}
}

var methods = {
	
	/**
	 * Next item has been clicked
	 */
	nextItem : function(){
		if (info.earlyEnd){
			loadDiv('end');
		} else 	if (currentPoint.next != backPoint){
			currentPoint = currentPoint.next;
			
			methods.setItemNumber(currentPoint.numId);
			
			info.distanceFromCurFront++;
			
			while (info.distanceFromCurFront >= 5){
				frontPoint = frontPoint.next;
				info.distanceFromCurFront--;
			}
		} else if (info.init){	//they're equal, but we're initializing
			loadDiv('loading');
			info.userWaiting = true;
			window.reader.setInfo(null,null);
		} else if (!info.noMore){	//they're equal, not init, and we have more	
			loadDiv('loading');
			info.userWaiting = true;
			window.reader.setInfo(null,null);
		} else {	//We have no more and we're at the end.  We're screwed
			loadDiv('end');
			window.reader.setInfo(null,null);
		}
		
	},
	
	/**
	 * Previous item has been clicked
	 */
	prevItem : function(){
		if (currentPoint != frontPoint) {
			if (info.earlyEnd)
				return;
			if (info.currentDivId == 'loading' || info.currentDivId == 'end'){
				methods.setItemNumber(currentPoint.numId);
				info.userWaiting = false;
			} else {
				currentPoint = currentPoint.prev;
				methods.setItemNumber(currentPoint.numId);
				info.distanceFromCurFront--;
				info.userWaiting = false;
			}			
		}
	},
	
	/**
	 * The next items have been downloaded
	 * TODO: test functionality with 0 items (after loading 15).
	 */
	nextFeedDownloaded : function(){
		var it = window.reader.retrieveItems();
		var newData = eval("(" + it.toString() + ")");
		
		info.waitingMore = false;
		log("dl" + it.toString().length());
		
		//todo: what if we're at loading div, and now we have no more items?
		if (newData.items.length < 15) {
			info.noMore = true;
			if (newData.items.length == 0 && !info.init){
				info.nextLoaded = false;
				return;
			}	
		}
		
		if (info.init) {
			if (newData.items.length > 0){
				data[0] = newData;
				info.init = false;
							
				info.scriptWaiting = false;
				info.userWaiting = false;
				methods.pushBackward();		
				methods.setItemNumber(0);
				
				//not sure why we need this at the moment, but we do.
				document.getElementById('loading').style.display = 'none';
			} else {
				info.noMore = true;
				info.nextLoaded = false;
				methods.stopTimer();
				loadDiv('end');
				document.getElementById('loading').style.display = 'none';
				info.earlyEnd = true;
			}
			
		} else {
			var nextIndex = (info.curMajorIndex + 1) % 2;
			data[nextIndex] = newData;
			
			info.scriptWaiting = false;
			info.nextLoaded = true;
			
			if (info.userWaiting){
				info.userWaiting = false;
				methods.pushBackward();
				methods.nextItem();
			}
			
		}
	},
	
	
	pushBackward : function(){
		
		if (info.scriptWaiting)	return;		
		
		var id = backPoint.numId;
		
		var nextMajorIndex = info.curMajorIndex;
		var nextMinorIndex = info.curMinorIndex + 1;
		
		//if the index is passed the furthest index in our current data
		if (nextMinorIndex >= data[info.curMajorIndex].items.length){
			if (!info.nextLoaded){
				info.scriptWaiting = true;
				return;
			}
			
			nextMajorIndex = (info.curMajorIndex + 1) % 2;
			nextMinorIndex = 0;	
			
			info.nextLoaded = false;			
		}
		
		var nextItem = data[nextMajorIndex].items[nextMinorIndex];
		
		document.getElementById("title" + id).innerHTML		= nextItem.title;
		
		document.getElementById("author" + id).innerHTML	= nextItem.author ? nextItem.author : "";
		
		/*
		var extra = "0";
		
		for (var i = 1; i < 50; i++)
			extra += "\n" + i;
		*/
		
		
		if (nextItem.summary)
			document.getElementById("content" + id).innerHTML	= nextItem.summary.content // + "<pre>" + extra + "</pre>";
		else
			document.getElementById("content" + id).innerHTML	= nextItem.content.content // + "<pre>" + extra + "</pre>";
		
		backPoint.item = nextItem;
		nextItem.itemRead = false;
		nextItem.itemSaved = false;
		
		info.curMajorIndex = nextMajorIndex;
		info.curMinorIndex = nextMinorIndex;
		
		backPoint = backPoint.next;
		
		if (methods.computeNumberLeft() < 3 && !info.noMore){
			methods.requestMore();
			info.nextLoaded = false;
		}
			
		
		log("p" + backPoint.prev.numId);
	},
	
	computeNumberLeft : function(){
		var left = data[info.curMajorIndex].items.length - info.curMinorIndex - 1;
		if (info.nextLoaded)
			left += data[(info.curMajorIndex + 1) % 2].items.length;
			
		return left;
	},
	
	startTimer : function(){
		info.timer = setInterval(methods.onTimerForPushing,1000);
	},
	
	stopTimer : function(){
		if (info.timer)
			clearInterval(info.timer);
	},
	
	onTimerForPushing : function(){
		
		if (backPoint.next != frontPoint)
			methods.pushBackward();
	},
	
	requestMore : function(){
		if (!info.waitingMore && !info.noMore){
			window.reader.requestMoreItems(data[info.curMajorIndex].continuation);
			info.waitingMore = true;
			log('requestedMore');
		}
		
	},
	
	setItemNumber : function(id){
		window.reader.setInfo(currentPoint.item.origin.streamId,currentPoint.item.id,""+currentPoint.item.itemRead,""+currentPoint.item.itemSave);
		loadDiv('item' + id);
		log("item" + id);
	},
	
	scrollUp: function(){
		var element = document.getElementById('item' + currentPoint.numId);
		var num = Number(element.style.top.substring(0,element.style.top.length - 2));
		if (num < 10)
			element.style.top = (num + 680) + "px";
	},
	
	scrollDown : function(){
		var element = document.getElementById('item' + currentPoint.numId);
		var num = Number(element.style.top.substring(0,element.style.top.length - 2));
		if (num + element.offsetHeight > 680)
			element.style.top = (num  - 680) + "px";
	}
	
	
}

function log(what){
	/*document.getElementById('log').innerHTML += " " + what;*/
}

/**
 * Show one div, and hide the currently shown div
 * @param {String} divId the id of the div to show
 */
function loadDiv(divId){
	document.getElementById(info.currentDivId).style.display = 'none';
	info.currentDivId = divId;
	var element = document.getElementById(divId);
	element.style.display = 'block';
	element.style.top = '10px';
}

var info = {
	distanceFromCurFront : 0,
	noMore : false,
	userWaiting : false,
	scriptWaiting : false,
	currentDivId : "loading",
	curMajorIndex : 0,
	curMinorIndex : -1,
	nextLoaded : false,
	init : true,
	waitingMore : false,
	timer : null,
	earlyEnd : false
}



var global = {
    isEndOfFeed: false,
    isScriptWaiting: true,
    continuation: "",
	isFirstData : true,
	isRequestPending : false,
	itemCountFromLastRequest : 0
}

var options = {	itemsPerDownload : 15,	numberOfDivs : 16,	maxFrontDistance : 5,	requestBuffer : 3, scrollBy : 680}

var itemsDownloaded = [];

var divs = {
	show : function(id){document.getElementById(id).style.display = 'block';},
	hide : function(id){document.getElementById(id).style.display = 'none';}
}



function onPageLoad(){
	connectorTouch.init("touchView","onData");
	position.init();
}

function onData(theData){
	switch (theData.type){
		case "scrollUp":
			helper.scroll(true);
			break;
		case "scrollDown":
			helper.scroll(false);
			break;
		case "itemNext":
			position.advance();
			break;
		case "itemPrev":
			position.withdrawl();
			break;
		case "itemSave":
			//TODO
			break;
		case "itemRead":
			//TODO
			break;
		case "init":
			helper.init();
			break;
	}	
}
//object for java item send
var readee = {
	onDownloaded : function(data){helper.nextFeedDownloaded(data);},
	onItemDownloaded : function(item){helper.nextFeedItemDownload(item);}
}

var helper = {
	
	timerId : 0,
	
	init : function(){
		helper.requestMore();		
		helper.startTimer();
	},

	
	scroll : function(isUp){
		
		if (position.isInSpecialNode)	return;
		
		var element = document.getElementById('item-'+position.current.divId);
		var num = Number(element.style.top.substring(0,element.style.top.length - 2));
		
		if (isUp && num < 0)		//if we're going up 
			element.style.top = (num + options.scrollBy) + "px";
		else if (!isUp && num + element.offsetHeight > options.scrollBy)
			element.style.top = (num  - options.scrollBy) + "px";
	},
	
	
	requestMore : function(){
		
			
		touchView.postData({
			type:"more",
			continuationString:global.continuation
		});
		
		global.isRequestPending = true;

		log('requestedMore');
	
		
	}, 
	
	nextFeedDownloaded : function(newData){
		log('item downloaded');
		global.isRequestPending = false;
		//put new data into the data array
		
		for (var i in newData.items)
			itemsDownloaded.push(newData.items[i]);
		
		
		
		if (newData.items.length === 0){
			global.isEndOfFeed = true;
		} else if (newData.items.length < options.itemsPerDownload){
			info.noMore = true;	//TODO
			global.isEndOfFeed = true;
			log("no more");
		}	
		
		//if the user is waiting, load up the next one and then show it
		if (position.isInSpecialNode){
			position.loadNext();
			position.advance();
		}
		
		global.requestPending = false;
		global.continuation = newData.continuation ? newData.continuation : "";
	},
	
	nextFeedItemDownload : function (newItem){
		
		
		
		global.itemCountFromLastRequest++;
		
		if (global.itemCountFromLastRequest >= options.itemsPerDownload){
			global.itemCountFromLastRequest = 0;			
			global.isRequestPending = false;
		}
		
		itemsDownloaded.push(newItem);
		
		//if the user is waiting, load up the next one and then show it
		if (position.isInSpecialNode){
			position.loadNext();
			position.advance();
		}
	},
	
	
	
	startTimer : function(){
		this.timerId = setInterval(function(){
			if (position.back.next != position.front)
				position.loadNext();
				
				if (position.isInSpecialNode){
					position.advance();
				}
		},300);
	},
	
	stopTimer : function(){
		if (this.timerId > 0){
			clearInterval(this.timerId);
			this.timerId = 0;
		}
			
	},
	
	updateInfo : function(theData){
		
		touchView.postData({
			"type" : "info",
			"data" : theData
		})
	}
	
}

function log(what){
	
};


/*//DEBUG!
function log(what){
	window.parent.postMessage('l ' +what,"*");
}



var connectorTouch = {init : function(){}};

var touchView = {
	postData : function (data){
		window.parent.postMessage('f ' + JSON.stringify(data),"*");
	}
}

window.addEventListener("message", function(event){
	
		if (event.data == "data")
			readee.onDownloaded(testData);
		else {
			var data = JSON.parse(event.data);
			onData(data);
		}
		
	
	

}, false);
*/




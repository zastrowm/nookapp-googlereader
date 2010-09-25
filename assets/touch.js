/**
 * @author zastrowm
 */
var currentFeed,currentItem;

/**
 * Runs when the page is done loading
 */
function onPageLoad(){
	connectorFeed.init("einkView","onData");
	java.init("onJavaToMe");	
}

/**
 * Message received from the java program
 * @param {String} message
 */
function onJavaToMe(message){
	switch(message){
		case "keyLeftBottom":
			sendEink("itemNext");
			break;
		case "keyLeftTop":
			sendEink("itemPrev");
			break;
		case "keyRightTop":
			sendEink("scrollUp");
			break;
		case "keyRightBottom":
			sendEink("scrollDown");
			break;
		case "init":
			sendEink("init");
			break;	
	}
}

/**
 * When a message is received from the feed display
 * @param {JsonObject} data
 */
function onData(data){
	
	if (data.type == "more") {
		log('more ' + data.continuationString);
		window.reader.getFeedBasedOnLabel("nook", data.continuationString);
	}else if (data.type == "info") {
		
		
		if (data.data != null){
			log('info for ' + data.data.itemId);
			currentFeed = data.data.feedUrl;
			currentItem = data.data.itemId;
		} else {
			log('info for null');
			currentFeed = "";
			currentItem = "";
		}
		
		
		markRead();
	}	
}

function markUnread(){
	if (isValid()) {
		window.reader.markAsUnread(currentFeed, currentItem);
		log("add unread tag | " + currentFeed + " " + currentItem);	
	}				
}

function markRead(){
	window.reader.markAsRead(currentFeed, currentItem);
}

function isValid(){
	return currentFeed && currentItem;
}

function markSaved(){
	if (isValid()){
		window.reader.removeLabel(currentFeed, currentItem, "nook");
		window.reader.addLabel(currentFeed, currentItem, "nookRead");
		log("add save tag | " + currentFeed + " " + currentItem);	
	}				
}

function sendEink(theType){
	einkView.postData({type:theType});
}

function quit(){
	java.sendMessage("quit");
}

function quitAndDelete(){
	java.sendMessage("delete");
}

function onButtonPress(id){
	var element = document.getElementById(id);
	element.style.backgroundColor = "#35A099";
	setTimeout(function(){
		element.style.backgroundColor = "#356AA0";
	},300);
}

function log(what){
	
	java.sendMessage("log",what)
	
};

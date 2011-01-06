/**
 * LinkedListNode class
 * @param {Object} prev the previous node
 * @param {Object} numId the numeric Id of the node
 */
function LinkedListNode(prev,numId){
	this.prev = prev;
	this.next = null;
	this.divId = numId;
	this.data = {};
}


var position = {
	
	endCounter : 0,
	isInSpecialNode : true,
	specialNode : 'special-start',
	
	distanceBetweenFrontAndCurrent : 0,
	
	current :null,
	front : null,
	back : null,
	
	
	_advanceRaw : function(dontHide){		
		
		if (!dontHide)
			divs.hide('item-'+position.current.divId);
		
		position.current = position.current.next;
		this.distanceBetweenFrontAndCurrent++;
		
		{//move front forward
			if (this.distanceBetweenFrontAndCurrent >= options.maxFrontDistance){
				position.front = position.front.next;
				this.distanceBetweenFrontAndCurrent--;
			}
			
			//if we're at the maxDifference between the two
			if (this.distanceBetweenFrontAndCurrent >= options.maxFrontDistance){
				position.front = position.front.next;
				this.distanceBetweenFrontAndCurrent--;
			}
			
			divs.show('item-'+position.current.divId);	
		}
		

	},
	
	advance : function(){
		if (!this.isInSpecialNode){
			log('moveNext')
			//if we're at the end, displayy div.end
			if (position.current == position.back){
				divs.hide('item-'+position.current.divId);
				this._moveToSpecial(global.isEndOfFeed ? 'end' : 'loading');
			} else {	
				this._advanceRaw();		//push current back and display it				
			}
			
			this._updateStats();	
		} else if (position.current != position.back){	//if this is a special node and we're not at the end
			this._moveFromSpecial();
			if (position.specialNode != "special-start")
				this._advanceRaw(true);			//push current back and display it
			else
				divs.show('item-'+position.current.divId);		
				
			this._updateStats();	
		}
		
		
	},
	
	
	withdrawl : function(){
		//If we're in special and have room behind us
		if (this.isInSpecialNode && position.front != position.current){
			this._moveFromSpecial();
			divs.show('item-'+position.current.divId);
			this._updateStats();	
		} else if  (position.front != position.current){	//If we're not special and have room
			
			divs.hide('item-'+position.current.divId);
			this.distanceBetweenFrontAndCurrent--;
			position.current = position.current.prev;
			divs.show('item-'+position.current.divId);
			this._updateStats();	
		}
		
			
	},
	
	loadNext : function(){
		if (itemsDownloaded.length == 0 || position.back.next == position.current)
			return;
		
		var where = position.back.next;
		if (global.isFirstData)
			where = where.prev;
		
		
		var item = itemsDownloaded.shift();
			
		var author = document.getElementById("author-" + where.divId);
		var title = document.getElementById("title-" + where.divId);
		var content = document.getElementById("content-" + where.divId);
		//TODO: add feed source	
		
		title.innerHTML = item.title ? item.title : "&lt;&lt;UNTITLED&gt;&gt;";
		author.innerHTML = item.author ? item.author : "&lt;&lt;NO AUTHOR&gt;&gt;";
		
		if (item.summary)		content.innerHTML = item.summary.content;
		else if (item.content)	content.innerHTML = item.content.content;
		else					content.innerHTML = "&lt;&lt;NO CONTENT&gt;&gt;";
		
		//TODO: add states
		
		var divItem = document.getElementById('item-'+where.divId);
		this._saveDataToElement(divItem,item);
		divItem.style.top = "0px";	//reset the scroll		
	
		position.back = position.back.next;
		
		if (global.isFirstData){
			global.isFirstData = false;
			position.back = position.back.prev;
		}
		
		if (!global.isRequestPending && itemsDownloaded.length <= options.requestBuffer){
			helper.requestMore();
			
			if (itemsDownloaded.length == 0)	global.isScriptWaiting = true;
		}
		
		log('loadedNext ' + where.divId);
	
	},

	_saveDataToElement : function(where,item){
		
		where.data = {
			itemId : item.id,
			feedUrl : item.origin.streamId,
			feedTitle : item.origin.title,
			read : false, 
			saved : false};
			
			
	},	

	
	init : function(){
		this._createList();
		this._moveToSpecial('start');
	},
	
	_moveToSpecial : function(type){
		if (this.isInSpecialNode)
			divs.hide(this.specialNode);
			
		this.isInSpecialNode = true;
		this.specialNode = 'special-' + type;
		divs.show(this.specialNode);
		log('inSpecial(' + this.isInSpecialNode + ')')
	},
	
	_moveFromSpecial : function(type){
		this.isInSpecialNode = false;
		divs.hide(this.specialNode);
		log('inSpecial(' + this.isInSpecialNode + ')')
	},
	
	_updateStats : function(){
		if (this.isInSpecialNode){
			helper.updateInfo(null);
		} else {
			helper.updateInfo(document.getElementById('item-' + this.current.divId).data);
		}
	},
	
		
	_createList : function(){
		
		//make the front node/element
		position.current = new LinkedListNode(null,0);
		var prev = position.current, cur;
		for (var i = 1; i < options.numberOfDivs; i++){	//make a list of nodes, each node.prev = the last node
			cur = new LinkedListNode(prev,i);
			prev.next = cur;	//and the last node's next node is the current node
			prev = cur;			//and do it again with the current node
		}
		
		prev.next = position.current;		//make sure the last points to the front
		
		position.current.prev = prev;		//and that the front's prev points to end
		position.front = position.back = position.current;	//and they're all equal right about now
		
	},
}

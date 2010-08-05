/*******************************************************************************
 * 	Filename:	ManagedThreadedReader.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Aug 2, 2010
 ********************************************************************************/
package net.nookapps.googlereader;

import net.nookapps.googlereader.googleAPI.AbstractReaderMethodHelper;
import net.nookapps.googlereader.googleAPI.ThreadedReader;

/**
 * @author zastrowm
 *
 */
public class ManagedThreadedReader{
	
	private ThreadedReader reader;
	private boolean isMarkedUnread, isSaved;
	private String itemId, owner;
	private String labels;

	public ManagedThreadedReader(ThreadedReader it){
		this.reader = it;
	}
	
	public void setOwner(String theOwner){
		this.owner = theOwner;
	}
	
	public void setItem(String theItemId){
		this.itemId = theItemId;
	}
	
	public void setStatus(boolean isItRead,boolean isItSaved,String theLabels){
		this.isMarkedUnread = isItRead;
		this.isSaved = isItSaved;
		this.labels = theLabels;
	}
	
	public void setInfo(String theOwner, String theItemId, boolean isItRead,boolean isItSaved,String theLabels){
		this.owner = theOwner;
		this.itemId = theItemId;
		this.isMarkedUnread = isItRead;
		this.isSaved = isItSaved;
		this.labels = theLabels;
	}
	
	public void markRead(boolean apply){
		if (canDo()){
			if (apply)	reader.markAsRead(this.owner, this.itemId);
			else		reader.markAsUnread(this.owner,this.itemId);
		}
	}
	
	public void readLater(boolean apply){
		if (canDo()){
			if (apply){
				reader.addLabel(this.owner, this.itemId,"nookRead");
				reader.removeLabel(this.owner, this.itemId,"nook");
			} else {
				reader.removeLabel(this.owner,this.itemId,"nookRead");
				reader.addLabel(this.owner, this.itemId,"nook");
			}
		}
	}
	
	public void readOnPC(boolean apply){
		if (canDo()){
			if (apply)	reader.addLabel(this.owner, this.itemId,"nookRead");
			else		reader.removeLabel(this.owner,this.itemId,"nookRead");
		}
	}
	
	public void addLabel(String label){
		if (canDo())
			reader.addLabel(this.owner, this.itemId,label);
	}
	
	public void removeLabel(String label){
		if (canDo())
			reader.removeLabel(this.owner, this.itemId,label);
	}
	
	private boolean canDo(){
		return itemId != null && owner != null;
	}
	
}

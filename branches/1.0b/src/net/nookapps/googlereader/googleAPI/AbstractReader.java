/*******************************************************************************
 * 	Filename:	InterfaceReader.java
 * 	Author:		Mackenzie Zastrow
 * 	Use:		nookReader
 * 	Date:		Jul 25, 2010
 ********************************************************************************/
package net.nookapps.googlereader.googleAPI;

import net.nookapps.googlereader.network.NetManager;

/**
 * @author zastrowm
 *
 */
public abstract class AbstractReader {
	
	protected final NetManager network;
	protected final String username,password,sortOrder;
	protected String currentToken;
	
	protected AbstractReader(String user,String pass,boolean ascending){
		this.network = new NetManager();
		this.username = user;
		this.password = pass;
		this.sortOrder = !ascending ? "a" : "d";
	}
	
	protected AbstractReader(AbstractReader other){
		this.network = other.network;
		this.username = other.username;
		this.password = other.password;
		this.sortOrder = other.sortOrder;
	}
	
	/**
	 * Get the token for the current session
	 * @return the token
	 */
	public String getToken(){
		if (this.currentToken == null)
			this.currentToken = requestToken();
		
		return this.currentToken;
	}
	
	/**
	 * Request the token for the Google API POST commands
	 * @return the new token
	 */
	public String requestToken(){
		return network.httpGet("http://www.google.com/reader/api/0/token");
	}
	
	/**
	 * Place a raw label on an item
	 * @param owner the owner of the item that's being marked
	 * @param item the identifier of the item
	 * @param rawLabel the raw label to add
	 * @param add is this an add operation?
	 * @return whether or not it succeeded
	 */
	protected abstract String rawAddRemoveLabel(String owner,String item,String rawLabel,boolean add);
	
	/**
	 * Remove specific label from an item
	 * @param owner the owner of the item that's being marked
	 * @param item the identifier of the item
	 * @param label the label to remove
	 * @return whether or not it succeeded
	 */
	public String addLabel(String owner,String item,String label){
		return rawAddRemoveLabel(owner,item,"user/-/label/" + label,true);
	}

	/**
	 * Remove specific label from an item
	 * @param owner the owner of the item that's being marked
	 * @param item the identifier of the item
	 * @param label the label to apply
	 * @return whether or not it succeeded
	 */
	public String removeLabel(String owner,String item,String label){
		return rawAddRemoveLabel(owner,item,"user/-/label/" + label,false);
	}
	
	/**
	 * Mark an item as unread
	 * @param owner the owner of the item that's being marked
	 * @param item the identifier of the item
	 * @return whether or not it succeeded
	 */
	public String markAsUnread(String owner,String item){
		return rawAddRemoveLabel(owner,item,"user/-/state/com.google/read",false);
	}
	
	/**
	 * Mark an item as read
	 * @param owner the owner of the item that's being marked
	 * @param item the identifier of the item
	 * @return whether or not it succeeded
	 */
	public String markAsRead(String owner,String item){
		return rawAddRemoveLabel(owner,item,"user/-/state/com.google/read",true);
	}
	
	

	/**
	 * Helper function for the Google login to retreive a specific portion of a string
	 * @param content the string to search
	 * @param startPart1 the first string that serves as a position to start searching from for string2
	 * @param startPart2 the second string that serves as the "start" of the specific string
	 * @param endString the "end" of the string that we want
	 * @return a substring of content, between startPart2 and endString, after the first occurance of startPart1
	 */
	static String getSurroundedString(String content,String startPart1, String startPart2, String endString){
		int start, end;
		
		start = content.indexOf(startPart2,content.indexOf(startPart1)) + 7;
		end = content.indexOf(endString,start);
		return content.substring(start, end);
	}
	
	

}
